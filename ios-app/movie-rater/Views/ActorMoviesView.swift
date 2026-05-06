import SwiftUI

struct ActorMoviesView: View {
    let actorId: Int
    @EnvironmentObject private var movieService: MovieService

    @State private var actor: Actor? = nil
    @State private var movies: [Movie] = []
    @State private var isLoading = true
    @State private var selectedMovie: Movie? = nil

    private let columns = [GridItem(.flexible()), GridItem(.flexible())]

    var body: some View {
        Group {
            if isLoading {
                VStack {
                    Spacer()
                    ProgressView("Loading...")
                    Spacer()
                }
            } else {
                ScrollView {
                    VStack(spacing: 20) {
                        // Actor header
                        if let actor {
                            actorHeader(actor)
                        }

                        // Movies grid
                        if movies.isEmpty {
                            Text("No movies found for this actor.")
                                .foregroundColor(.secondary)
                                .padding()
                        } else {
                            LazyVGrid(columns: columns, spacing: 16) {
                                ForEach(movies) { movie in
                                    movieGridItem(movie)
                                        .onTapGesture { selectedMovie = movie }
                                }
                            }
                            .padding(.horizontal)
                        }
                    }
                    .padding(.bottom, 20)
                }
            }
        }
        .navigationTitle(actor?.name ?? "Actor")
        .navigationBarTitleDisplayMode(.inline)
        .navigationDestination(item: $selectedMovie) { movie in
            MovieDetailView(movie: movie)
        }
        .task {
            await loadData()
        }
    }

    @ViewBuilder
    private func actorHeader(_ actor: Actor) -> some View {
        VStack(spacing: 12) {
            AsyncImage(url: URL(string: actor.profileImage)) { phase in
                switch phase {
                case .success(let image):
                    image.resizable().scaledToFill()
                default:
                    Circle()
                        .fill(Color.gray.opacity(0.3))
                        .overlay(
                            Text(initials(for: actor.name))
                                .font(.largeTitle)
                                .foregroundColor(.white)
                        )
                }
            }
            .frame(width: 100, height: 100)
            .clipShape(Circle())
            .shadow(radius: 6)

            Text(actor.name)
                .font(.title2)
                .fontWeight(.bold)

            Text("\(movies.count) films")
                .font(.subheadline)
                .foregroundColor(.secondary)
        }
        .padding(.top, 20)
    }

    @ViewBuilder
    private func movieGridItem(_ movie: Movie) -> some View {
        VStack(alignment: .leading, spacing: 6) {
            AsyncImage(url: URL(string: movie.image)) { phase in
                switch phase {
                case .success(let image):
                    image.resizable().scaledToFill()
                default:
                    Rectangle()
                        .fill(Color.gray.opacity(0.2))
                        .overlay(ProgressView())
                }
            }
            .frame(height: 180)
            .clipped()
            .cornerRadius(10)

            Text(movie.title)
                .font(.caption)
                .fontWeight(.semibold)
                .lineLimit(2)

            HStack(spacing: 4) {
                Text(String(movie.year))
                    .font(.caption2)
                    .foregroundColor(.blue)

                Spacer()

                Label(movie.displayRating, systemImage: "star.fill")
                    .font(.caption2)
                    .foregroundColor(.orange)
            }
        }
        .contentShape(Rectangle())
    }

    @MainActor
    private func loadData() async {
        async let actorTask = movieService.getActor(actorId: actorId)
        async let moviesTask = movieService.getActorMovies(actorId: actorId)
        let (fetchedActor, fetchedMovies) = await (actorTask, moviesTask)
        actor = fetchedActor
        movies = fetchedMovies
        isLoading = false
    }

    private func initials(for name: String) -> String {
        name.components(separatedBy: " ")
            .compactMap { $0.first.map(String.init) }
            .prefix(2)
            .joined()
    }
}

#Preview {
    NavigationStack {
        ActorMoviesView(actorId: 6193)
            .environmentObject(MovieService.shared)
    }
}
