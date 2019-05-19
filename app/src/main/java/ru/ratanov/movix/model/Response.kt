package ru.ratanov.movix.model


data class SearchResult(
    val data: Data
)

data class Data(
    val showcases: List<Showcase>
)

data class Showcase(
    val items: List<Item>,
    val links: List<Any>,
    val recommendationId: String,
    val title: String,
    val total: Int,
    val type: String,
    val urn: String
)

data class Item(
    val id: Int,
    val title: String,
    val resources: List<Resources>,
    val description: String,
    val offer: Offer
)

data class Resources(
    val id: Int,
    val type: String
)

data class Offer(
    val id: Int
)

data class Video(
    val url: String?,
    val closeTime: Long,
    val drmVersion: Int,
    val result: Int
)

data class PurchaseResponse(
    val result: Int
)