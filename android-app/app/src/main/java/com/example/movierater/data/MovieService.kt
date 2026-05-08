package com.example.movierater.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

private const val TMDB_API_KEY = "47b9a9b5aea7b85e093e203b33d41878"
private const val TMDB_BASE_URL = "https://api.themoviedb.org/3/"
private const val TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"

private val json = Json { ignoreUnknownKeys = true }

private val httpClient = OkHttpClient.Builder()
    .addInterceptor(HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    })
    .build()

private val retrofit = Retrofit.Builder()
    .baseUrl(TMDB_BASE_URL)
    .client(httpClient)
    .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
    .build()

private val tmdbApi = retrofit.create(TMDbApi::class.java)

interface TMDbApi {
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String = TMDB_API_KEY,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1,
    ): MovieResponse

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String = TMDB_API_KEY,
        @Query("language") language: String = "en-US",
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("include_adult") includeAdult: Boolean = false,
    ): MovieResponse

    @GET("movie/{movieId}/credits")
    suspend fun getCredits(
        @Path("movieId") movieId: Int,
        @Query("api_key") apiKey: String = TMDB_API_KEY,
    ): Credits

    @GET("movie/{movieId}/videos")
    suspend fun getVideos(
        @Path("movieId") movieId: Int,
        @Query("api_key") apiKey: String = TMDB_API_KEY,
    ): VideoResponse

    @GET("person/{personId}")
    suspend fun getPerson(
        @Path("personId") personId: Int,
        @Query("api_key") apiKey: String = TMDB_API_KEY,
    ): Person

    @GET("person/{personId}/movie_credits")
    suspend fun getActorMovies(
        @Path("personId") personId: Int,
        @Query("api_key") apiKey: String = TMDB_API_KEY,
    ): ActorCredits
}

@Serializable
data class MovieResponse(
    val results: List<TMDbMovie>,
)

@Serializable
data class TMDbMovie(
    val id: Int,
    val title: String,
    @SerialName("poster_path")
    val posterPath: String?,
    val overview: String?,
    @SerialName("release_date")
    val releaseDate: String?,
    @SerialName("vote_average")
    val voteAverage: Double?,
)

@Serializable
data class Credits(
    val cast: List<CastMember>,
)

@Serializable
data class CastMember(
    val id: Int,
    val name: String,
    @SerialName("profile_path")
    val profilePath: String?,
)

@Serializable
data class VideoResponse(
    val results: List<Video>,
)

@Serializable
data class Video(
    val key: String,
    val type: String,
    val official: Boolean? = false,
)

@Serializable
data class Person(
    val id: Int,
    val name: String,
    @SerialName("profile_path")
    val profilePath: String?,
)

@Serializable
data class ActorCredits(
    val cast: List<ActorMovie>,
)

@Serializable
data class ActorMovie(
    val id: Int,
    val title: String,
    @SerialName("poster_path")
    val posterPath: String?,
    val overview: String?,
    @SerialName("release_date")
    val releaseDate: String?,
    @SerialName("vote_average")
    val voteAverage: Double?,
)

object MovieService {
    suspend fun getPopularMovies(): List<Movie> {
        return try {
            val response = tmdbApi.getPopularMovies()
            response.results.take(20).map { tmdbMovie ->
                enrichMovie(tmdbMovie)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun searchMovies(query: String): List<Movie> {
        if (query.isBlank()) return emptyList()
        return try {
            val response = tmdbApi.searchMovies(query = query)
            response.results.take(20).map { tmdbMovie ->
                enrichMovie(tmdbMovie)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getActorMovies(actorId: Int): List<Movie> {
        return try {
            val response = tmdbApi.getActorMovies(actorId)
            response.cast
                .filter { it.posterPath != null && it.releaseDate?.isNotEmpty() == true }
                .sortedByDescending { it.releaseDate }
                .take(20)
                .map { actorMovie ->
                    enrichMovie(
                        TMDbMovie(
                            id = actorMovie.id,
                            title = actorMovie.title,
                            posterPath = actorMovie.posterPath,
                            overview = actorMovie.overview,
                            releaseDate = actorMovie.releaseDate,
                            voteAverage = actorMovie.voteAverage,
                        )
                    )
                }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getActor(actorId: Int): Actor? {
        return try {
            val person = tmdbApi.getPerson(actorId)
            Actor(
                id = person.id,
                name = person.name,
                profileImage = person.profilePath?.let { "$TMDB_IMAGE_BASE_URL$it" },
            )
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun enrichMovie(tmdbMovie: TMDbMovie): Movie {
        val (credits, videos) = coroutineScope {
            val creditsDeferred = async {
                try {
                    tmdbApi.getCredits(tmdbMovie.id)
                } catch (e: Exception) {
                    null
                }
            }
            val videosDeferred = async {
                try {
                    tmdbApi.getVideos(tmdbMovie.id)
                } catch (e: Exception) {
                    null
                }
            }
            Pair(creditsDeferred.await(), videosDeferred.await())
        }

        val cast = credits?.cast?.take(10) ?: emptyList()
        val trailerUrl = videos?.results?.let { videoList ->
            (videoList.firstOrNull { it.type == "Trailer" && (it.official ?: false) }
                ?: videoList.firstOrNull { it.type == "Trailer" }
                ?: videoList.firstOrNull { it.type == "Teaser" })?.let {
                "https://www.youtube.com/embed/${it.key}"
            }
        }

        val year = extractYear(tmdbMovie.releaseDate)

        return Movie(
            id = tmdbMovie.id,
            title = tmdbMovie.title,
            image = tmdbMovie.posterPath?.let { "$TMDB_IMAGE_BASE_URL$it" } ?: "",
            description = tmdbMovie.overview ?: "No description available.",
            actors = cast.map { it.name },
            actorImages = cast.map { it.profilePath?.let { path -> "$TMDB_IMAGE_BASE_URL$path" } ?: "" },
            actorIds = cast.map { it.id },
            year = year,
            rating = tmdbMovie.voteAverage ?: 0.0,
            link = "https://www.themoviedb.org/movie/${tmdbMovie.id}",
            trailerUrl = trailerUrl,
        )
    }

    private fun extractYear(dateStr: String?): Int {
        if (dateStr?.isNotEmpty() == true) {
            return try {
                val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val date = formatter.parse(dateStr)
                if (date != null) {
                    val calendar = Calendar.getInstance().apply { time = date }
                    calendar.get(Calendar.YEAR)
                } else {
                    Calendar.getInstance().get(Calendar.YEAR)
                }
            } catch (e: Exception) {
                Calendar.getInstance().get(Calendar.YEAR)
            }
        }
        return Calendar.getInstance().get(Calendar.YEAR)
    }
}
