package ru.ratanov.movix.api

import android.util.Log
import com.google.gson.Gson
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import ru.ratanov.movix.model.Film
import ru.ratanov.movix.model.PurchaseResponse
import ru.ratanov.movix.model.SearchResult
import ru.ratanov.movix.model.Video
import java.io.IOException
import okhttp3.RequestBody



object RequestExecutor {

    private val TAG = this::class.java.simpleName

    private val client = OkHttpClient.Builder()
        .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()


    fun getVideoFile(url: String, onSuccess: (videoUrl: String?) -> Unit, onError: () -> Unit) {
        val request = Request.Builder()
            .url(url)
            .addHeader("View", "stb3")
            .addHeader("X-Auth-Token", TOKEN)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d(TAG, "Search error")
                onError.invoke()
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d(TAG, "Get Video success")

                if (response.isSuccessful) {
                    val searchResult = Gson().fromJson(response.body()?.charStream(), Video::class.java)
                    onSuccess.invoke(searchResult.url)
                } else {
                    onError.invoke()
                }
            }
        })
    }

    fun doSearch(query: String, onSuccess: (ArrayList<Film>) -> Unit, onError: () -> Unit) {

        val request = Request.Builder()
            .url("$SEARCH_URL?text=$query&limit=10")
            .addHeader("View", "stb3")
            .addHeader("X-Auth-Token", TOKEN)
            .build()


        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d(TAG, "Search error")
                onError.invoke()
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d(TAG, "Search success")

                if (response.isSuccessful) {
                    val searchResult = Gson().fromJson(response.body()?.charStream(), SearchResult::class.java)

                    val films = ArrayList<Film>()

                    searchResult.data.showcases.forEach { showcase ->
                        when (showcase.type) {
                            "movies", "serials" -> {
                                Log.d("Result", "${showcase.title} = ${showcase.total}")

                                showcase.items.forEach {
                                    println(it.title)

                                    val posterId = it.resources.find { it.type == "poster_blueprint" }?.id
                                    val streamId = it.resources.find { it.type == "hls" }?.id

                                    films.add(
                                        Film(
                                            id = it.id, title = it.title, description = it.description,
                                            posterUrl = "http://er-cdn.ertelecom.ru/content/public/r$posterId",
                                            streamId = "https://discovery-stb3.ertelecom.ru/resource/get_url/${it.id}/$streamId",
                                            offer = it.offer.id
                                        )
                                    )
                                }
                            }
                        }
                    }

                    onSuccess.invoke(films)
                } else {
                    onError.invoke()
                }
            }
        })

    }

    fun doPurchase(filmId: Int, offerId: Int, onSuccess: () -> Unit, onError: () -> Unit) {

        val body = RequestBody.create(null, byteArrayOf())

        val request = Request.Builder()
            .url("https://discovery-stb3.ertelecom.ru/er/billing/purchase?asset_id=$filmId&offer_id=$offerId")
            .addHeader("View", "stb3")
            .method("POST", body)
            .addHeader("X-Auth-Token", TOKEN)
            .build()

        client.newCall(request).enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {
                Log.d(TAG, "Purchase error")
                onError.invoke()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val purchaseResponse = Gson().fromJson(response.body()?.charStream(), PurchaseResponse::class.java)
                    if (purchaseResponse.result == 1) {
                        onSuccess.invoke()
                    } else {
                        onError.invoke()
                    }


                } else {
                    onError.invoke()
                }
            }

        })
    }

}