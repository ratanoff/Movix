package ru.ratanov.movix.list

import ru.ratanov.movix.model.Film

interface FilmClickListener {
    fun onFilmSelected(film: Film)
}