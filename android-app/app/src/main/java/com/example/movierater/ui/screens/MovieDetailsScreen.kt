package com.example.movierater.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.movierater.MovieViewModel
import com.example.movierater.data.MockData
import com.example.movierater.ui.components.ActorCard
import com.example.movierater.ui.components.RatingBadge

@Composable
fun MovieDetailsScreen(
    movieId: Int,
    viewModel: MovieViewModel,
    onBack: () -> Unit,
    onActorClick: (Int) -> Unit,
    fromFavorites: Boolean,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val movie = remember(movieId) { MockData.movieById(movieId) }
    val favorites by viewModel.favoriteIds.collectAsStateWithLifecycle()
    val context = LocalContext.current

    if (movie == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Movie not found", color = MaterialTheme.colorScheme.onBackground)
        }
        return
    }

    var showPosterDialog by rememberSaveable { mutableStateOf(false) }
    val isFavorited = movie.id in favorites

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                bottom = contentPadding.calculateBottomPadding() + 100.dp,
            ),
        ) {
            item {
                // Poster header — clickable to open dialog
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(420.dp)
                        .clickable { showPosterDialog = true },
                ) {
                    AsyncImage(
                        model = movie.image,
                        contentDescription = movie.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                    // Back button overlay
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .padding(top = contentPadding.calculateTopPadding() + 8.dp, start = 8.dp)
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.55f)),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                        )
                    }
                }
            }
            item {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            text = movie.year.toString(),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.titleMedium,
                        )
                        RatingBadge(rating = movie.rating)
                    }
                    Text(
                        text = movie.description,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        if (movie.trailerUrl != null) {
                            OutlinedButton(
                                onClick = {
                                    context.startActivity(
                                        Intent(Intent.ACTION_VIEW, movie.trailerUrl.toUri())
                                    )
                                },
                                modifier = Modifier.weight(1f),
                            ) {
                                Icon(Icons.Filled.PlayArrow, contentDescription = null)
                                Text("  Trailer")
                            }
                        }
                        OutlinedButton(
                            onClick = {
                                context.startActivity(
                                    Intent(Intent.ACTION_VIEW, movie.link.toUri())
                                )
                            },
                            modifier = Modifier.weight(1f),
                        ) {
                            Text("IMDB")
                        }
                        Button(
                            onClick = { viewModel.toggleFavorite(movie.id) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isFavorited) MaterialTheme.colorScheme.secondary
                                else MaterialTheme.colorScheme.primary,
                            ),
                            modifier = Modifier.weight(1f),
                        ) {
                            Icon(Icons.Filled.Star, contentDescription = null)
                            Text(if (isFavorited) "  Favorited" else "  Favorite")
                        }
                    }
                    if (movie.actors.isNotEmpty()) {
                        Text(
                            text = "Cast",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(top = 8.dp),
                        )
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(movie.actors.size) { idx ->
                                val name = movie.actors[idx]
                                val image = movie.actorImages.getOrNull(idx)
                                val id = movie.actorIds.getOrNull(idx)
                                ActorCard(
                                    name = name,
                                    image = image,
                                    onClick = if (!fromFavorites && id != null) {
                                        { onActorClick(id) }
                                    } else null,
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showPosterDialog) {
        PosterDialog(
            imageUrl = movie.image,
            title = movie.title,
            onDismiss = { showPosterDialog = false },
        )
    }
}

@Composable
private fun PosterDialog(
    imageUrl: String,
    title: String,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.95f)
                .background(Color.Black.copy(alpha = 0.92f), RoundedCornerShape(12.dp)),
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Full poster for $title",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
            )
            // Close FAB top-right
            FloatingActionButton(
                onClick = onDismiss,
                containerColor = Color(0xFF222222),
                contentColor = Color.White,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .size(44.dp),
            ) {
                Icon(Icons.Filled.Close, contentDescription = "Close full poster")
            }
        }
    }
}
