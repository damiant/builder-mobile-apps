package com.example.movierater

import androidx.lifecycle.ViewModel
import com.example.movierater.data.MockData
import com.example.movierater.data.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MovieViewModel : ViewModel() {

    private val _allMovies = MutableStateFlow(MockData.movies)
    val allMovies: StateFlow<List<Movie>> = _allMovies.asStateFlow()

    private val _favoriteIds = MutableStateFlow<Set<Int>>(emptySet())
    val favoriteIds: StateFlow<Set<Int>> = _favoriteIds.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun searchedMovies(): List<Movie> {
        val q = _searchQuery.value.trim().lowercase()
        val all = _allMovies.value
        return if (q.isEmpty()) all else all.filter { it.title.lowercase().contains(q) }
    }

    fun toggleFavorite(movieId: Int) {
        _favoriteIds.update { current ->
            if (movieId in current) current - movieId else current + movieId
        }
    }

    fun isFavorite(movieId: Int): Boolean = movieId in _favoriteIds.value

    fun favoriteMovies(): List<Movie> =
        _allMovies.value.filter { it.id in _favoriteIds.value }
}
