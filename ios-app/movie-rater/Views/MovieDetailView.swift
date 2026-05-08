import SwiftUI
import SafariServices

struct ActorLink: Identifiable, Hashable {
    let id: Int // actor ID
}

struct MovieDetailView: View {
    let movie: Movie
    @EnvironmentObject private var movieService: MovieService
    @State private var showTrailer = false
    @State private var selectedActorId: Int? = nil
    @State private var isActorNavigationActive = false

    var isSaved: Bool { movieService.isSaved(movie.id) }

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 0) {
                posterImage

                VStack(alignment: .leading, spacing: 16) {
                    titleSection
                    descriptionSection
                    actionButtons
                    castSection
                }
                .padding(20)
            }
        }
        .background(actorNavigationLink)
        .navigationTitle(movie.title)
        .navigationBarTitleDisplayMode(.inline)
        .sheet(isPresented: $showTrailer) {
            trailerSheet
        }
    }

    // MARK: - Subviews

    private var posterImage: some View {
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
        .frame(maxWidth: .infinity)
        .frame(height: 360)
        .clipped()
    }

    private var titleSection: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text(movie.title)
                .font(.title2)
                .fontWeight(.bold)

            HStack(spacing: 10) {
                Text(String(movie.year))
                    .font(.caption)
                    .padding(.horizontal, 10)
                    .padding(.vertical, 4)
                    .background(Color.blue.opacity(0.15))
                    .foregroundColor(.blue)
                    .cornerRadius(8)

                Label(movie.displayRating + "/5", systemImage: "star.fill")
                    .font(.caption)
                    .foregroundColor(.orange)
                    .padding(.horizontal, 10)
                    .padding(.vertical, 4)
                    .background(Color.orange.opacity(0.1))
                    .cornerRadius(8)
            }
        }
    }

    private var descriptionSection: some View {
        Text(movie.description)
            .font(.body)
            .foregroundColor(.secondary)
    }

    private var actionButtons: some View {
        VStack(spacing: 10) {
            if movie.trailerUrl != nil {
                Button(action: { showTrailer = true }) {
                    Label("Watch Trailer", systemImage: "play.circle.fill")
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 12)
                }
                .buttonStyle(.borderedProminent)
                .tint(.orange)
                .clipShape(Capsule())
            }

            Button(action: { openURL(movie.link) }) {
                Label("Open on TMDb", systemImage: "arrow.up.right.square")
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 12)
            }
            .buttonStyle(.borderedProminent)
            .tint(.orange)
            .clipShape(Capsule())

            Button(action: { movieService.toggleSave(movie.id) }) {
                Label(isSaved ? "Favorited" : "Add to Favorites",
                      systemImage: isSaved ? "star.fill" : "star")
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 12)
            }
            .buttonStyle(.borderedProminent)
            .tint(.blue)
            .clipShape(Capsule())
        }
    }

    @ViewBuilder
    private var castSection: some View {
        if !movie.actors.isEmpty {
            VStack(alignment: .leading, spacing: 10) {
                Text("Cast")
                    .font(.headline)

                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(spacing: 16) {
                        ForEach(Array(movie.actors.enumerated()), id: \.offset) { index, actor in
                            let actorId = index < movie.actorIds.count ? movie.actorIds[index] : 0
                            let imageUrl = index < movie.actorImages.count ? movie.actorImages[index] : ""
                            if actorId != 0 {
                                Button {
                                    selectedActorId = actorId
                                    isActorNavigationActive = true
                                } label: {
                                    ActorCardView(name: actor, imageUrl: imageUrl)
                                }
                                .buttonStyle(.plain)
                            } else {
                                ActorCardView(name: actor, imageUrl: imageUrl)
                            }
                        }
                    }
                }
            }
        }
    }

    private var actorNavigationLink: some View {
        NavigationLink(isActive: $isActorNavigationActive) {
            if let selectedActorId {
                ActorMoviesView(actorId: selectedActorId)
            } else {
                EmptyView()
            }
        } label: {
            EmptyView()
        }
        .hidden()
    }

    @ViewBuilder
    private var trailerSheet: some View {
        if let urlString = movie.trailerUrl, let url = URL(string: urlString) {
            SafariView(url: url)
                .ignoresSafeArea()
        }
    }

    private func openURL(_ urlString: String) {
        guard let url = URL(string: urlString) else { return }
        UIApplication.shared.open(url)
    }
}

// MARK: - SafariView wrapper

struct SafariView: UIViewControllerRepresentable {
    let url: URL

    func makeUIViewController(context: Context) -> SFSafariViewController {
        SFSafariViewController(url: url)
    }

    func updateUIViewController(_ uiViewController: SFSafariViewController, context: Context) {}
}

#Preview {
    NavigationStack {
        MovieDetailView(
            movie: Movie(
                id: 27205,
                title: "Inception",
                image: "https://image.tmdb.org/t/p/w500/oYuLEt3zVCKq57qu2F8dT7NIa6f.jpg",
                description: "A thief who steals corporate secrets through the use of dream-sharing technology.",
                actors: ["Leonardo DiCaprio", "Joseph Gordon-Levitt", "Elliot Page"],
                actorImages: [],
                actorIds: [6193, 24045, 27578],
                year: 2010,
                rating: 8.4,
                link: "https://www.themoviedb.org/movie/27205",
                trailerUrl: "https://www.youtube.com/embed/YoHD9XEInc0"
            )
        )
        .environmentObject(MovieService.shared)
    }
}
