package com.example.movierater.data

data class Movie(
    val id: Int,
    val title: String,
    val image: String,
    val description: String,
    val actors: List<String>,
    val actorImages: List<String>,
    val actorIds: List<Int>,
    val year: Int,
    val rating: Double, // 0..10
    val link: String,
    val trailerUrl: String? = null,
)

data class Actor(
    val id: Int,
    val name: String,
    val profileImage: String?,
)
