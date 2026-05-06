import SwiftUI

struct ActorCardView: View {
    let name: String
    let imageUrl: String
    var whiteText: Bool = false
    var onTap: (() -> Void)? = nil

    var body: some View {
        VStack(spacing: 4) {
            AsyncImage(url: URL(string: imageUrl)) { phase in
                switch phase {
                case .success(let image):
                    image
                        .resizable()
                        .scaledToFill()
                default:
                    Circle()
                        .fill(Color.gray.opacity(0.3))
                        .overlay(
                            Text(initials)
                                .font(.caption)
                                .fontWeight(.semibold)
                                .foregroundColor(.white)
                        )
                }
            }
            .frame(width: 52, height: 52)
            .clipShape(Circle())
            .onTapGesture { onTap?() }

            Text(name)
                .font(.caption2)
                .lineLimit(2)
                .multilineTextAlignment(.center)
                .foregroundColor(whiteText ? .white : .primary)
                .frame(width: 60)
        }
        .contentShape(Rectangle())
        .onTapGesture { onTap?() }
    }

    private var initials: String {
        name.components(separatedBy: " ")
            .compactMap { $0.first.map(String.init) }
            .prefix(2)
            .joined()
    }
}

#Preview {
    HStack {
        ActorCardView(name: "Tom Hanks", imageUrl: "")
        ActorCardView(name: "Meryl Streep", imageUrl: "", whiteText: true)
    }
    .padding()
    .background(Color.black)
}
