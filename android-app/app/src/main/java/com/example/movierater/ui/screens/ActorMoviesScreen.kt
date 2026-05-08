package com.example.movierater.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.movierater.MovieViewModel
import com.example.movierater.data.Actor
import com.example.movierater.data.Movie
import com.example.movierater.data.MovieService
import com.example.movierater.ui.components.ActorLarge
import com.example.movierater.ui.components.MovieCardLarge

@Composable
fun ActorMoviesScreen(
    actorId: Int,
    viewModel: MovieViewModel,
    onBack: () -> Unit,
    onMovieClick: (Int) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    var actor by remember { mutableStateOf<Actor?>(null) }
    var movies by remember { mutableStateOf<List<Movie>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val favorites by viewModel.favoriteIds.collectAsStateWithLifecycle()

    LaunchedEffect(actorId) {
        isLoading = true
        actor = MovieService.getActor(actorId)
        movies = MovieService.getActorMovies(actorId)
        viewModel.cacheMovies(movies)
        isLoading = false
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = contentPadding.calculateTopPadding() + 8.dp,
                bottom = contentPadding.calculateBottomPadding() + 100.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.4f)),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                    )
                }
            }
            if (isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 80.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
            } else if (actor == null) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 80.dp), contentAlignment = Alignment.Center) {
                        Text("Actor not found", color = Color.White)
                    }
                }
            } else {
                item {
                    val currentActor = actor
                    if (currentActor != null) {
                        ActorLarge(actor = currentActor, movieCount = movies.size)
                    }
                }
                if (movies.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(top = 32.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = "No movies found for this actor.",
                                color = Color.White.copy(alpha = 0.8f),
                            )
                        }
                    }
                } else {
                    items(movies, key = { it.id }) { movie ->
                        MovieCardLarge(
                            movie = movie,
                            isFavorited = movie.id in favorites,
                            onClick = { onMovieClick(movie.id) },
                            onFavoriteClick = { viewModel.toggleFavorite(movie.id) },
                            onActorClick = null,
                        )
                    }
                }
            }
        }
    }
}
