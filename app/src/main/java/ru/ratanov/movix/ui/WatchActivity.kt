package ru.ratanov.movix.ui

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import kotlinx.android.synthetic.main.activity_watch.*
import ru.ratanov.movix.R

class WatchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_watch)

        val exoPlayer = ExoPlayerFactory.newSimpleInstance(DefaultRenderersFactory(this), DefaultTrackSelector(), DefaultLoadControl())
        val userAgent = "exoplayer-codelab"
        val urlSource = "" // todo: get from activity
        val mediaSource = HlsMediaSource.Factory(DefaultHttpDataSourceFactory(userAgent)).createMediaSource(Uri.parse(urlSource))
        exoPlayer.prepare(mediaSource)
        exoPlayer.playWhenReady = true
        player_view.player = exoPlayer
    }
}
