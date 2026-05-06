import Foundation
import Combine

@MainActor
final class MovieService: ObservableObject {
    static let shared = MovieService()

    private let apiKey = "47b9a9b5aea7b85e093e203b33d41878"
    private let baseURL = "https://api.themoviedb.org/3"
    private let imageBaseURL = "https://image.tmdb.org/t/p/w500"

    @Published private(set) var movies: [Movie] = []
    @Published private(set) var isLoaded = false
    @Published private(set) var savedMovieIdsPublished: Set<Int> = []

    private init() {
        if let arr = UserDefaults.standard.array(forKey: "savedMovies") as? [Int] {
            savedMovieIdsPublished = Set(arr)
        }
        Task { await loadMovies() }
    }

    // MARK: - Favorites

    func isSaved(_ movieId: Int) -> Bool {
        savedMovieIdsPublished.contains(movieId)
    }

    func toggleSave(_ movieId: Int) {
        if savedMovieIdsPublished.contains(movieId) {
            savedMovieIdsPublished.remove(movieId)
        } else {
            savedMovieIdsPublished.insert(movieId)
        }
        UserDefaults.standard.set(Array(savedMovieIdsPublished), forKey: "savedMovies")
    }

    func savedMovies() -> [Movie] {
        movies.filter { savedMovieIdsPublished.contains($0.id) }
    }

    // MARK: - Search

    nonisolated func searchMovies(query: String) async -> [Movie] {
        guard !query.trimmingCharacters(in: .whitespaces).isEmpty else { return [] }
        let encoded = query.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? query
        let urlStr = "https://api.themoviedb.org/3/search/movie?api_key=47b9a9b5aea7b85e093e203b33d41878&language=en-US&query=\(encoded)&page=1&include_adult=false"
        guard let url = URL(string: urlStr) else { return [] }
        do {
            let (data, _) = try await URLSession.shared.data(from: url)
            let response = try JSONDecoder().decode(TMDbMovieResponse.self, from: data)
            var results: [Movie] = []
            for tmdb in response.results.prefix(20) {
                let movie = await Self.enrichMovie(tmdb, imageBaseURL: "https://image.tmdb.org/t/p/w500")
                results.append(movie)
            }
            return results
        } catch {
            return []
        }
    }

    // MARK: - Actor data

    nonisolated func getActorMovies(actorId: Int) async -> [Movie] {
        let urlStr = "https://api.themoviedb.org/3/person/\(actorId)/movie_credits?api_key=47b9a9b5aea7b85e093e203b33d41878"
        guard let url = URL(string: urlStr) else { return [] }
        do {
            let (data, _) = try await URLSession.shared.data(from: url)
            let response = try JSONDecoder().decode(TMDbActorCredits.self, from: data)
            let filtered = response.cast
                .filter { $0.poster_path != nil && ($0.release_date ?? "").isEmpty == false }
                .sorted { ($0.release_date ?? "") > ($1.release_date ?? "") }
            var results: [Movie] = []
            for tmdb in filtered {
                let movie = await Self.enrichMovie(tmdb.asMovieItem(), imageBaseURL: "https://image.tmdb.org/t/p/w500")
                results.append(movie)
            }
            return results
        } catch {
            return []
        }
    }

    nonisolated func getActor(actorId: Int) async -> Actor? {
        let urlStr = "https://api.themoviedb.org/3/person/\(actorId)?api_key=47b9a9b5aea7b85e093e203b33d41878"
        guard let url = URL(string: urlStr) else { return nil }
        do {
            let (data, _) = try await URLSession.shared.data(from: url)
            let person = try JSONDecoder().decode(TMDbPerson.self, from: data)
            let profileImage = person.profile_path.map { "https://image.tmdb.org/t/p/w500\($0)" } ?? ""
            return Actor(id: person.id, name: person.name, profileImage: profileImage)
        } catch {
            return nil
        }
    }

    // MARK: - Private load

    private func loadMovies() async {
        let urlStr = "\(baseURL)/movie/popular?api_key=\(apiKey)&language=en-US&page=1"
        guard let url = URL(string: urlStr) else { isLoaded = true; return }
        do {
            let (data, _) = try await URLSession.shared.data(from: url)
            let response = try JSONDecoder().decode(TMDbMovieResponse.self, from: data)
            var enriched: [Movie] = []
            for tmdb in response.results.prefix(20) {
                let movie = await Self.enrichMovie(tmdb, imageBaseURL: imageBaseURL)
                enriched.append(movie)
            }
            movies = enriched
        } catch {
            movies = []
        }
        isLoaded = true
    }

    // MARK: - Static helpers (nonisolated by being static)

    private static func enrichMovie(_ tmdb: TMDbMovie, imageBaseURL: String) async -> Movie {
        async let creditsTask: TMDbCredits? = fetchCredits(movieId: tmdb.id)
        async let trailerTask: String? = fetchTrailerUrl(movieId: tmdb.id)
        let (credits, trailerUrl) = await (creditsTask, trailerTask)
        return makeMovie(from: tmdb, credits: credits, trailerUrl: trailerUrl, imageBaseURL: imageBaseURL)
    }

    private static func fetchCredits(movieId: Int) async -> TMDbCredits? {
        let urlStr = "https://api.themoviedb.org/3/movie/\(movieId)/credits?api_key=47b9a9b5aea7b85e093e203b33d41878"
        guard let url = URL(string: urlStr),
              let (data, _) = try? await URLSession.shared.data(from: url) else { return nil }
        return try? JSONDecoder().decode(TMDbCredits.self, from: data)
    }

    private static func fetchTrailerUrl(movieId: Int) async -> String? {
        let urlStr = "https://api.themoviedb.org/3/movie/\(movieId)/videos?api_key=47b9a9b5aea7b85e093e203b33d41878"
        guard let url = URL(string: urlStr),
              let (data, _) = try? await URLSession.shared.data(from: url),
              let videos = try? JSONDecoder().decode(TMDbVideoResponse.self, from: data) else { return nil }
        let trailer = videos.results.first { $0.type == "Trailer" && ($0.official ?? false) }
            ?? videos.results.first { $0.type == "Trailer" }
            ?? videos.results.first { $0.type == "Teaser" }
        return trailer.map { "https://www.youtube.com/embed/\($0.key)" }
    }

    private static func makeMovie(from tmdb: TMDbMovie, credits: TMDbCredits?, trailerUrl: String?, imageBaseURL: String) -> Movie {
        let year: Int
        if let dateStr = tmdb.release_date, !dateStr.isEmpty {
            let formatter = DateFormatter()
            formatter.dateFormat = "yyyy-MM-dd"
            year = formatter.date(from: dateStr).flatMap { Calendar.current.dateComponents([.year], from: $0).year } ?? Calendar.current.component(.year, from: Date())
        } else {
            year = Calendar.current.component(.year, from: Date())
        }
        let cast = credits?.cast.prefix(10) ?? []
        return Movie(
            id: tmdb.id,
            title: tmdb.title,
            image: tmdb.poster_path.map { "\(imageBaseURL)\($0)" } ?? "",
            description: tmdb.overview ?? "No description available.",
            actors: cast.map { $0.name },
            actorImages: cast.map { $0.profile_path.map { "\(imageBaseURL)\($0)" } ?? "" },
            actorIds: cast.map { $0.id },
            year: year,
            rating: tmdb.vote_average ?? 0,
            link: "https://www.themoviedb.org/movie/\(tmdb.id)",
            trailerUrl: trailerUrl
        )
    }
}

// MARK: - TMDb DTOs

private struct TMDbMovieResponse: Decodable {
    let results: [TMDbMovie]
}

private struct TMDbMovie: Decodable {
    let id: Int
    let title: String
    let poster_path: String?
    let overview: String?
    let release_date: String?
    let vote_average: Double?
}

private struct TMDbCredits: Decodable {
    let cast: [TMDbCastMember]
}

private struct TMDbCastMember: Decodable {
    let id: Int
    let name: String
    let profile_path: String?
}

private struct TMDbVideoResponse: Decodable {
    let results: [TMDbVideo]
}

private struct TMDbVideo: Decodable {
    let key: String
    let type: String
    let official: Bool?
}

private struct TMDbActorCredits: Decodable {
    let cast: [TMDbActorMovie]
}

private struct TMDbActorMovie: Decodable {
    let id: Int
    let title: String
    let poster_path: String?
    let overview: String?
    let release_date: String?
    let vote_average: Double?

    func asMovieItem() -> TMDbMovie {
        TMDbMovie(id: id, title: title, poster_path: poster_path, overview: overview, release_date: release_date, vote_average: vote_average)
    }
}

private struct TMDbPerson: Decodable {
    let id: Int
    let name: String
    let profile_path: String?
}
