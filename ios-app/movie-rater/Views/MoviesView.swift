import SwiftUI

struct MoviesView: View {
    @EnvironmentObject private var movieService: MovieService

    @State private var searchText = ""
    @State private var searchResults: [Movie]? = nil
    @State private var isSearching = false
    @State private var searchTask: Task<Void, Never>? = nil
    @State private var displayMovies: [Movie] = []
    @State private var selectedMovie: Movie? = nil
    @State private var selectedActorLink: ActorLink? = nil

    var body: some View {
        NavigationStack {
            Group {
                if isSearching {
                    VStack {
                        Spacer()
                        ProgressView()
                        Spacer()
                    }
                } else if let results = searchResults {
                    if results.isEmpty {
                        VStack(spacing: 12) {
                            Spacer()
                            Image(systemName: "magnifyingglass")
                                .font(.largeTitle)
                                .foregroundColor(.secondary)
                            Text("No results found")
                                .font(.headline)
                            Text("Try a different search term.")
                                .font(.subheadline)
                                .foregroundColor(.secondary)
                            Spacer()
                        }
                    } else {
                        moviesList(results)
                    }
                } else {
                    if !movieService.isLoaded {
                        VStack {
                            Spacer()
                            ProgressView("Loading movies...")
                            Spacer()
                        }
                    } else if displayMovies.isEmpty {
                        VStack(spacing: 12) {
                            Spacer()
                            Image(systemName: "film")
                                .font(.largeTitle)
                                .foregroundColor(.secondary)
                            Text("No movies available")
                                .font(.headline)
                            Spacer()
                        }
                    } else {
                        moviesList(displayMovies)
                    }
                }
            }
            .navigationTitle("Movie Rater")
            .searchable(text: $searchText, prompt: "Search for movies...")
            .onChange(of: searchText) { _, newValue in
                handleSearch(query: newValue)
            }
            .onAppear {
                if movieService.isLoaded && displayMovies.isEmpty {
                    loadRandomMovies()
                }
            }
            .onChange(of: movieService.isLoaded) { _, loaded in
                if loaded && displayMovies.isEmpty {
                    loadRandomMovies()
                }
            }
            .navigationDestination(item: $selectedMovie) { movie in
                MovieDetailView(movie: movie)
            }
            .navigationDestination(item: $selectedActorLink) { link in
                ActorMoviesView(actorId: link.id)
            }
        }
    }

    @ViewBuilder
    private func moviesList(_ movies: [Movie]) -> some View {
        ScrollView {
            LazyVStack(spacing: 16) {
                ForEach(movies) { movie in
                    MovieCardView(
                        movie: movie,
                        isFavorited: movieService.isSaved(movie.id),
                        limitActors: 3,
                        onFavoriteTap: { movieService.toggleSave(movie.id) },
                        onActorTap: { actorId in selectedActorLink = ActorLink(id: actorId) }
                    )
                    .padding(.horizontal)
                    .contentShape(Rectangle())
                    .onTapGesture { selectedMovie = movie }
                }
            }
            .padding(.vertical)
        }
    }

    private func loadRandomMovies() {
        let all = movieService.movies
        displayMovies = Array(all.shuffled().prefix(10))
    }

    private func handleSearch(query: String) {
        searchTask?.cancel()
        guard !query.trimmingCharacters(in: .whitespaces).isEmpty else {
            searchResults = nil
            isSearching = false
            return
        }
        isSearching = true
        searchTask = Task { @MainActor in
            try? await Task.sleep(nanoseconds: 500_000_000) // 0.5s debounce
            guard !Task.isCancelled else { return }
            let results = await movieService.searchMovies(query: query)
            guard !Task.isCancelled else { return }
            searchResults = results
            isSearching = false
        }
    }
}

#Preview {
    MoviesView()
        .environmentObject(MovieService.shared)
}
