import SwiftUI

@main
struct movie_raterApp: App {
    var body: some Scene {
        WindowGroup {
            ContentView()
                .environmentObject(MovieService.shared)
        }
    }
}
