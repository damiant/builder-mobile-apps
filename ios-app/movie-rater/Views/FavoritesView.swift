import SwiftUI

struct FavoritesView: View {
    @EnvironmentObject private var movieService: MovieService
    @State private var selectedMovie: Movie? = nil

    private let columns = [GridItem(.flexible()), GridItem(.flexible())]

    var body: some View {
        NavigationStack {
            Group {
                let saved = movieService.savedMovies()
                if saved.isEmpty {
                    VStack(spacing: 16) {
                        Spacer()
                        Image(systemName: "star.slash")
                            .font(.system(size: 60))
                            .foregroundColor(.secondary)
                        Text("No favorites yet")
                            .font(.headline)
                        Text("Tap the star on any movie to save it here.")
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                            .multilineTextAlignment(.center)
                            .padding(.horizontal, 40)
                        Spacer()
                    }
                } else {
                    ScrollView {
                        LazyVGrid(columns: columns, spacing: 16) {
                            ForEach(saved) { movie in
                                favoriteCard(movie)
                                    .onTapGesture { selectedMovie = movie }
                            }
                        }
                        .padding()
                    }
                }
            }
            .navigationTitle("Favorites")
            .navigationDestination(item: $selectedMovie) { movie in
                MovieDetailView(movie: movie)
            }
        }
    }

    @ViewBuilder
    private func favoriteCard(_ movie: Movie) -> some View {
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
            .frame(height: 200)
            .clipped()
            .cornerRadius(12)

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
}

#Preview {
    FavoritesView()
        .environmentObject(MovieService.shared)
}
