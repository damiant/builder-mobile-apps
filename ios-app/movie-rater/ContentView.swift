import SwiftUI

struct ContentView: View {
    var body: some View {
        TabView {
            MoviesView()
                .tabItem {
                    Label("Movies", systemImage: "film")
                }

            FavoritesView()
                .tabItem {
                    Label("Favorites", systemImage: "star.fill")
                }
        }
    }
}

#Preview {
    ContentView()
        .environmentObject(MovieService.shared)
}
