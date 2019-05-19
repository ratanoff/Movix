package ru.ratanov.movix.ui

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_watch.*
import kotlinx.android.synthetic.main.activity_watch.fab
import ru.ratanov.movix.App
import ru.ratanov.movix.App.Companion.REQUEST_CODE
import ru.ratanov.movix.R
import ru.ratanov.movix.api.RequestExecutor.doSearch
import ru.ratanov.movix.utils.Util
import ru.yandex.speechkit.Language
import ru.yandex.speechkit.OnlineModel
import ru.yandex.speechkit.gui.RecognizerActivity

class WatchActivity : AppCompatActivity() {

    private lateinit var exoPlayer: ExoPlayer

    private val userAgent = "exoplayer-codelab"
    private lateinit var urlSource: String
    private lateinit var mediaSource: HlsMediaSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_watch)

        fab.setOnClickListener {
            App.shutUp()
            val intent = Intent(this, RecognizerActivity::class.java)
            intent.putExtra(RecognizerActivity.EXTRA_MODEL, OnlineModel.QUERIES.name)
            intent.putExtra(RecognizerActivity.EXTRA_LANGUAGE, Language.RUSSIAN.value)

            startActivityForResult(intent, REQUEST_CODE)
        }

        urlSource = intent.getStringExtra("urlSource")
        mediaSource =
            HlsMediaSource.Factory(DefaultHttpDataSourceFactory(userAgent)).createMediaSource(Uri.parse(urlSource))
        exoPlayer = ExoPlayerFactory.newSimpleInstance(
            DefaultRenderersFactory(this),
            DefaultTrackSelector(),
            DefaultLoadControl()
        )
        exoPlayer.prepare(mediaSource)
        exoPlayer.playWhenReady = true
        player_view.player = exoPlayer
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RecognizerActivity.RESULT_OK && data != null) {
                val result = data.getStringExtra(RecognizerActivity.EXTRA_RESULT) // FULL QUERY
                val correctResult = Util.getCorrectQuery(result)
                val action = Util.getAction(Util.removePunctuation(result))

                when (action) {
                    Util.ACTION_FIND -> {
                        val intent = Intent(this, MainActivity::class.java).apply {
                            putExtra("query", correctResult)
                        }
                        startActivity(intent)
                    }
                    Util.ACTION_VIDEO -> {
                        when (correctResult) {
                            "пауза" -> {
                                if (exoPlayer.playWhenReady && exoPlayer.playbackState == Player.STATE_READY) {
                                    exoPlayer.release()
                                }
                            }
                            "плей" -> {
                                exoPlayer.prepare(mediaSource)
                                exoPlayer.playWhenReady = true
                            }
                            "стоп" -> startActivity(Intent(this, MainActivity::class.java))
                        }
                    }
                }


            } else if (resultCode == RecognizerActivity.RESULT_ERROR) {
                val error = data?.getSerializableExtra(RecognizerActivity.EXTRA_ERROR).toString()
                App.speakMessage(error)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        exoPlayer.release()
    }
}
