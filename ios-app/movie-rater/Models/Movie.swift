import Foundation

struct Movie: Identifiable, Equatable, Hashable {
    let id: Int
    let title: String
    let image: String
    let description: String
    let actors: [String]
    let actorImages: [String]
    let actorIds: [Int]
    let year: Int
    let rating: Double
    let link: String
    let trailerUrl: String?

    var displayRating: String {
        String(format: "%.1f", rating / 2.0)
    }

    static func == (lhs: Movie, rhs: Movie) -> Bool {
        lhs.id == rhs.id
    }
    
    func hash(into hasher: inout Hasher) {
        hasher.combine(id)
    }
}

struct Actor: Identifiable {
    let id: Int
    let name: String
    let profileImage: String
}
