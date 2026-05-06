import SwiftUI

struct MovieCardView: View {
    let movie: Movie
    var isFavorited: Bool = false
    var limitActors: Int = 3
    var onFavoriteTap: (() -> Void)? = nil
    var onActorTap: ((Int) -> Void)? = nil

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            // Poster
            ZStack(alignment: .topTrailing) {
                AsyncImage(url: URL(string: movie.image)) { phase in
                    switch phase {
                    case .success(let image):
                        image
                            .resizable()
                            .scaledToFill()
                    default:
                        Rectangle()
                            .fill(Color.gray.opacity(0.2))
                            .overlay(ProgressView())
                    }
                }
                .frame(maxWidth: .infinity)
                .frame(height: 260)
                .clipped()

                // Favorite button
                Button(action: { onFavoriteTap?() }) {
                    Image(systemName: isFavorited ? "star.fill" : "star")
                        .foregroundColor(isFavorited ? .yellow : .white)
                        .padding(8)
                        .background(Color.black.opacity(0.5))
                        .clipShape(Circle())
                }
                .padding(12)
            }

            VStack(alignment: .leading, spacing: 8) {
                // Title + meta
                Text(movie.title)
                    .font(.headline)
                    .lineLimit(2)

                HStack(spacing: 8) {
                    Text(String(movie.year))
                        .font(.caption)
                        .padding(.horizontal, 8)
                        .padding(.vertical, 3)
                        .background(Color.blue.opacity(0.15))
                        .foregroundColor(.blue)
                        .cornerRadius(6)

                    Label(movie.displayRating, systemImage: "star.fill")
                        .font(.caption)
                        .foregroundColor(.orange)
                        .padding(.horizontal, 8)
                        .padding(.vertical, 3)
                        .background(Color.orange.opacity(0.1))
                        .cornerRadius(6)
                }

                // Description
                Text(movie.description)
                    .font(.subheadline)
                    .foregroundColor(.secondary)
                    .lineLimit(3)

                // Cast
                if !movie.actors.isEmpty {
                    VStack(alignment: .leading, spacing: 6) {
                        Text("Cast")
                            .font(.caption)
                            .fontWeight(.semibold)
                            .foregroundColor(.secondary)

                        ScrollView(.horizontal, showsIndicators: false) {
                            HStack(spacing: 12) {
                                ForEach(Array(movie.actors.prefix(limitActors).enumerated()), id: \.offset) { index, actor in
                                    ActorCardView(
                                        name: actor,
                                        imageUrl: index < movie.actorImages.count ? movie.actorImages[index] : "",
                                        onTap: {
                                            if index < movie.actorIds.count {
                                                onActorTap?(movie.actorIds[index])
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            .padding(12)
        }
        .background(Color(.systemBackground))
        .cornerRadius(16)
        .shadow(color: .black.opacity(0.1), radius: 8, x: 0, y: 2)
    }
}

#Preview {
    ScrollView {
        MovieCardView(
            movie: Movie(
                id: 1,
                title: "Inception",
                image: "https://image.tmdb.org/t/p/w500/oYuLEt3zVCKq57qu2F8dT7NIa6f.jpg",
                description: "A thief who steals corporate secrets through the use of dream-sharing technology.",
                actors: ["Leonardo DiCaprio", "Joseph Gordon-Levitt"],
                actorImages: [],
                actorIds: [6193, 24045],
                year: 2010,
                rating: 8.4,
                link: "https://www.themoviedb.org/movie/27205",
                trailerUrl: nil
            ),
            isFavorited: false
        )
        .padding()
    }
}
