package ru.ratanov.movix.model

data class Film(
    val id: Int,
    val title: String,
    val posterUrl: String?,
    val streamId: String
)