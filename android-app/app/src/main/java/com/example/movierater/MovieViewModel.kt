package com.example.movierater

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movierater.data.Movie
import com.example.movierater.data.MovieService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

class MovieViewModel : ViewModel() {

    private val _allMovies = MutableStateFlow<List<Movie>>(emptyList())
    val allMovies: StateFlow<List<Movie>> = _allMovies.asStateFlow()

    private val _favoriteIds = MutableStateFlow<Set<Int>>(emptySet())
    val favoriteIds: StateFlow<Set<Int>> = _favoriteIds.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _allMoviesMap = MutableStateFlow<Map<Int, Movie>>(emptyMap())
    val allMoviesMap: StateFlow<Map<Int, Movie>> = _allMoviesMap.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadPopularMovies()
    }

    private fun loadPopularMovies() {
        viewModelScope.launch {
            _isLoading.value = true
            val movies = MovieService.getPopularMovies()
            _allMovies.value = movies
            _allMoviesMap.value = movies.associateBy { it.id }
            _isLoading.value = false
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()

        if (query.isNotEmpty()) {
            searchJob = viewModelScope.launch {
                delay(300)
                searchMovies(query)
            }
        } else {
            loadPopularMovies()
        }
    }

    private fun searchMovies(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val movies = MovieService.searchMovies(query)
            _allMovies.value = movies
            _allMoviesMap.value = movies.associateBy { it.id }
            _isLoading.value = false
        }
    }

    fun getMovieById(movieId: Int): Movie? {
        return _allMoviesMap.value[movieId]
    }

    fun cacheMovies(movies: List<Movie>) {
        _allMoviesMap.update { current -> current + movies.associateBy { it.id } }
    }

    fun searchedMovies(): List<Movie> = _allMovies.value

    fun toggleFavorite(movieId: Int) {
        _favoriteIds.update { current ->
            if (movieId in current) current - movieId else current + movieId
        }
    }

    fun isFavorite(movieId: Int): Boolean = movieId in _favoriteIds.value

    fun favoriteMovies(): List<Movie> =
        _allMovies.value.filter { it.id in _favoriteIds.value }
}
