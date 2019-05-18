package ru.ratanov.movix.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail.*
import ru.ratanov.movix.App
import ru.ratanov.movix.R
import ru.ratanov.movix.api.RequestExecutor
import ru.ratanov.movix.model.Film
import ru.yandex.speechkit.*

class DetailActivity : AppCompatActivity() {

    private var vocalizer: Vocalizer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val film = intent.getParcelableExtra<Film>("film")

        film_title.text = film.title
        film_desc.text = film.description

        Picasso.get()
            .load(film.posterUrl)
            .into(film_poster)

        film_btn_watch.setOnClickListener {
            RequestExecutor.getVideoFile(film.streamId,
                onSuccess = { videoUrl ->
                    if (videoUrl == null) {
                        App.speakMessage(R.string.access_error_message)
                        return@getVideoFile
                    }

                    val intent = Intent(this, WatchActivity::class.java)
                    intent.putExtra("urlSource", videoUrl)
                    startActivity(intent)
                }, onError = {
                    App.speakMessage(R.string.try_later_message)
                })
        }
    }
}
