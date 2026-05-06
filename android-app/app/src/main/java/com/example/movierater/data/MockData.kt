package com.example.movierater.data

private const val TMDB = "https://image.tmdb.org/t/p/w500"

object MockData {

    val actors: List<Actor> = listOf(
        Actor(1, "Leonardo DiCaprio", "$TMDB/wo2hJpn04vbtmh0B9utCFdsQhxM.jpg"),
        Actor(2, "Joseph Gordon-Levitt", "$TMDB/4U9G4YwTlIEbAymBaseltS38eH4.jpg"),
        Actor(3, "Elliot Page", "$TMDB/5UkzNSOK561c2QRy2Zr4AkADzLT.jpg"),
        Actor(4, "Christian Bale", "$TMDB/qCpZn2e3dimwbryLnqxZuI88PTi.jpg"),
        Actor(5, "Heath Ledger", "$TMDB/p9eDSq0FA1ucTluLdkRBp4LK0EX.jpg"),
        Actor(6, "Aaron Eckhart", "$TMDB/5dXrJv8U86bmYj6cnvIimNB6QPK.jpg"),
        Actor(7, "Matthew McConaughey", "$TMDB/wJiGedOCZhwMx9DezY8uwbNxmAY.jpg"),
        Actor(8, "Anne Hathaway", "$TMDB/tLelKoPNiyJCSEtQTz1FGv4TLGc.jpg"),
        Actor(9, "Jessica Chastain", "$TMDB/lodMzLKSdrPcBry6TdoDsMN3Vge.jpg"),
        Actor(10, "Keanu Reeves", "$TMDB/4D0PpNI0kmP58hgrwGC3wCjxhnm.jpg"),
        Actor(11, "Carrie-Anne Moss", "$TMDB/8iATAc5z5XOKFFARLsvaawa8MTY.jpg"),
        Actor(12, "Laurence Fishburne", "$TMDB/iA0pPGgUDqJSWioizPZQO5IdsLR.jpg"),
        Actor(13, "Tom Hanks", "$TMDB/xndWFsBlClOJFRdhSt4NBwiPq2o.jpg"),
        Actor(14, "Robin Wright", "$TMDB/8ZSeEFhZBJWwI0PChsbbtfcOR9b.jpg"),
        Actor(15, "Sally Field", "$TMDB/n0LtpnIxvKNlHj3fRmyspIBu3l9.jpg"),
    )

    private fun actorsByIds(ids: List<Int>): Triple<List<String>, List<String>, List<Int>> {
        val selected = ids.mapNotNull { id -> actors.firstOrNull { it.id == id } }
        return Triple(
            selected.map { it.name },
            selected.map { it.profileImage ?: "" },
            selected.map { it.id },
        )
    }

    private fun movie(
        id: Int,
        title: String,
        poster: String,
        description: String,
        year: Int,
        rating: Double,
        link: String,
        actorIds: List<Int>,
        trailerUrl: String? = null,
    ): Movie {
        val (names, images, ids) = actorsByIds(actorIds)
        return Movie(
            id = id,
            title = title,
            image = "$TMDB$poster",
            description = description,
            actors = names,
            actorImages = images,
            actorIds = ids,
            year = year,
            rating = rating,
            link = link,
            trailerUrl = trailerUrl,
        )
    }

    val movies: List<Movie> = listOf(
        movie(
            id = 27205,
            title = "Inception",
            poster = "/9gk7adHYeDvHkCSEqAvQNLV5Uge.jpg",
            description = "Cobb, a skilled thief who commits corporate espionage by infiltrating the subconscious of his targets, is offered a chance to regain his old life as payment for a task considered to be impossible.",
            year = 2010,
            rating = 8.4,
            link = "https://www.themoviedb.org/movie/27205",
            actorIds = listOf(1, 2, 3),
            trailerUrl = "https://www.youtube.com/watch?v=YoHD9XEInc0",
        ),
        movie(
            id = 155,
            title = "The Dark Knight",
            poster = "/qJ2tW6WMUDux911r6m7haRef0WH.jpg",
            description = "Batman raises the stakes in his war on crime. With the help of Lt. Jim Gordon and District Attorney Harvey Dent, Batman sets out to dismantle the remaining criminal organizations that plague the streets.",
            year = 2008,
            rating = 9.0,
            link = "https://www.themoviedb.org/movie/155",
            actorIds = listOf(4, 5, 6),
            trailerUrl = "https://www.youtube.com/watch?v=EXeTwQWrcwY",
        ),
        movie(
            id = 157336,
            title = "Interstellar",
            poster = "/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg",
            description = "The adventures of a group of explorers who make use of a newly discovered wormhole to surpass the limitations on human space travel and conquer the vast distances involved in an interstellar voyage.",
            year = 2014,
            rating = 8.6,
            link = "https://www.themoviedb.org/movie/157336",
            actorIds = listOf(7, 8, 9),
            trailerUrl = "https://www.youtube.com/watch?v=zSWdZVtXT7E",
        ),
        movie(
            id = 603,
            title = "The Matrix",
            poster = "/p96dm7sCMn4VYAStA6siNz30G1r.jpg",
            description = "Set in the 22nd century, The Matrix tells the story of a computer hacker who joins a group of underground insurgents fighting the vast and powerful computers who now rule the earth.",
            year = 1999,
            rating = 8.7,
            link = "https://www.themoviedb.org/movie/603",
            actorIds = listOf(10, 11, 12),
            trailerUrl = "https://www.youtube.com/watch?v=vKQi3bBA1y8",
        ),
        movie(
            id = 13,
            title = "Forrest Gump",
            poster = "/arw2vcBveWOVZr6pxd9XTd1TdQa.jpg",
            description = "A man with a low IQ has accomplished great things in his life and been present during significant historic events—in each case, far exceeding what anyone imagined he could do.",
            year = 1994,
            rating = 8.5,
            link = "https://www.themoviedb.org/movie/13",
            actorIds = listOf(13, 14, 15),
            trailerUrl = "https://www.youtube.com/watch?v=bLvqoHBptjg",
        ),
        movie(
            id = 49026,
            title = "The Dark Knight Rises",
            poster = "/hr0L2aueqlP2BYUblTTjmtn0hw4.jpg",
            description = "Following the death of District Attorney Harvey Dent, Batman assumes responsibility for Dent's crimes to protect the late attorney's reputation and is subsequently hunted by the Gotham City Police Department.",
            year = 2012,
            rating = 7.8,
            link = "https://www.themoviedb.org/movie/49026",
            actorIds = listOf(4, 8),
        ),
    )

    fun movieById(id: Int): Movie? = movies.firstOrNull { it.id == id }

    fun actorById(id: Int): Actor? = actors.firstOrNull { it.id == id }

    fun moviesForActor(actorId: Int): List<Movie> = movies.filter { actorId in it.actorIds }
}
