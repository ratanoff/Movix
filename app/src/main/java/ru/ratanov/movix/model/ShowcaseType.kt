package ru.ratanov.movix.model

enum class ShowcaseType(
    val title: String
) {

    FILMS("Фильмы"),
    SERIALS("Сериалы")
}

/*
    "title": "Фильмы",
    "urn": "showcases/search/movies",
    "type": "movies",
    "total": 437

    "title": "Сериалы",
    "urn": "showcases/search/serials",
    "type": "serials",
    "total": 84
*/
