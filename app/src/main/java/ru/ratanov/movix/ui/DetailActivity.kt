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
import ru.ratanov.movix.utils.Util
import ru.yandex.speechkit.*
import ru.yandex.speechkit.gui.RecognizerActivity

class DetailActivity : AppCompatActivity() {

    private val REQUEST_CODE = 31

    private lateinit var film: Film

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        film = intent.getParcelableExtra("film")

        film_title.text = film.title
        film_desc.text = film.description

        Picasso.get()
            .load(film.posterUrl)
            .into(film_poster)

        fab.setOnClickListener {
            App.shutUp()
            val intent = Intent(this, RecognizerActivity::class.java)
            intent.putExtra(RecognizerActivity.EXTRA_MODEL, OnlineModel.QUERIES.name)
            intent.putExtra(RecognizerActivity.EXTRA_LANGUAGE, Language.RUSSIAN.value)

            startActivityForResult(intent, REQUEST_CODE)
        }

        film_btn_watch.setOnClickListener {
            playVideo()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RecognizerActivity.RESULT_OK && data != null) {
                val result = data.getStringExtra(RecognizerActivity.EXTRA_RESULT) // FULL QUERY
                val correctResult = Util.getCorrectQuery(result)
                val action = Util.getAction(result)

                when (action) {
                    Util.ACTION_FIND -> {
                        val intent = Intent(this, MainActivity::class.java).apply {
                            putExtra("query", correctResult)
                        }
                        startActivity(intent)
                    }
                    Util.ACTION_WATCH -> {}
                }


            } else if (resultCode == RecognizerActivity.RESULT_ERROR) {
                val error = data?.getSerializableExtra(RecognizerActivity.EXTRA_ERROR).toString()
                App.speakMessage(error)
            }
        }
    }

    private fun playVideo() {
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
