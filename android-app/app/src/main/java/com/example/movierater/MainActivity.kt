package com.example.movierater

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.movierater.ui.components.FooterTab
import com.example.movierater.ui.components.MovieFooter
import com.example.movierater.ui.screens.ActorMoviesScreen
import com.example.movierater.ui.screens.FavoritesScreen
import com.example.movierater.ui.screens.MovieDetailsScreen
import com.example.movierater.ui.screens.MoviesScreen
import com.example.movierater.ui.theme.MovieRaterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MovieRaterTheme {
                MovieRaterApp()
            }
        }
    }
}

object Routes {
    const val MOVIES = "movies"
    const val FAVORITES = "favorites"
    const val MOVIE_DETAILS = "movie-details/{movieId}?fromFavorites={fromFavorites}"
    const val ACTOR = "actor/{actorId}"
    fun movieDetails(movieId: Int, fromFavorites: Boolean = false) =
        "movie-details/$movieId?fromFavorites=$fromFavorites"
    fun actor(actorId: Int) = "actor/$actorId"
}

@Composable
fun MovieRaterApp() {
    val viewModel: MovieViewModel = viewModel()
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val favorites by viewModel.favoriteIds.collectAsStateWithLifecycle()

    val currentTab = when (currentRoute) {
        Routes.FAVORITES -> FooterTab.Favorites
        else -> FooterTab.Movies
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            MovieFooter(
                currentTab = currentTab,
                favoritesCount = favorites.size,
                onTabSelected = { tab ->
                    val target = when (tab) {
                        FooterTab.Movies -> Routes.MOVIES
                        FooterTab.Favorites -> Routes.FAVORITES
                    }
                    if (currentRoute != target) {
                        navController.navigate(target) {
                            popUpTo(Routes.MOVIES) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                },
            )
        },
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopStart) {
            NavHost(
                navController = navController,
                startDestination = Routes.MOVIES,
                modifier = Modifier.fillMaxSize(),
            ) {
                composable(Routes.MOVIES) {
                    MoviesScreen(
                        viewModel = viewModel,
                        onMovieClick = { id -> navController.navigate(Routes.movieDetails(id)) },
                        onActorClick = { id -> navController.navigate(Routes.actor(id)) },
                        contentPadding = innerPadding,
                    )
                }
                composable(Routes.FAVORITES) {
                    FavoritesScreen(
                        viewModel = viewModel,
                        onMovieClick = { id ->
                            navController.navigate(Routes.movieDetails(id, fromFavorites = true))
                        },
                        contentPadding = innerPadding,
                    )
                }
                composable(
                    route = Routes.MOVIE_DETAILS,
                    arguments = listOf(
                        navArgument("movieId") { type = NavType.IntType },
                        navArgument("fromFavorites") {
                            type = NavType.BoolType
                            defaultValue = false
                        },
                    ),
                ) { entry ->
                    val movieId = entry.arguments?.getInt("movieId") ?: 0
                    val fromFavorites = entry.arguments?.getBoolean("fromFavorites") ?: false
                    MovieDetailsScreen(
                        movieId = movieId,
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onActorClick = { id -> navController.navigate(Routes.actor(id)) },
                        fromFavorites = fromFavorites,
                        contentPadding = PaddingValues(bottom = innerPadding.calculateBottomPadding()),
                    )
                }
                composable(
                    route = Routes.ACTOR,
                    arguments = listOf(navArgument("actorId") { type = NavType.IntType }),
                ) { entry ->
                    val actorId = entry.arguments?.getInt("actorId") ?: 0
                    ActorMoviesScreen(
                        actorId = actorId,
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onMovieClick = { id -> navController.navigate(Routes.movieDetails(id)) },
                        contentPadding = PaddingValues(
                            top = innerPadding.calculateTopPadding(),
                            bottom = innerPadding.calculateBottomPadding(),
                        ),
                    )
                }
            }
        }
    }
}
