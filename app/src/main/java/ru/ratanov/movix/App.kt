package ru.ratanov.movix

import android.app.Application
import ru.yandex.speechkit.*
import java.util.*

class App : Application(), VocalizerListener {

    private val API_KEY_FOR_TESTS_ONLY = "069b6659-984b-4c5f-880e-aaedcfd84102"


    companion object {
        private var instance: Application? = null
        fun instance() = instance!!
        var vocalizer: Vocalizer? = null

        fun speakMessage(messageResId: Int) = speakMessage(instance?.getString(messageResId))

        fun speakMessage(message: String?) {
            message ?: return
            vocalizer?.synthesize(message, Vocalizer.TextSynthesizingMode.INTERRUPT)
        }

        fun shutUp() = vocalizer?.cancel()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        try {
            SpeechKit.getInstance().init(this, API_KEY_FOR_TESTS_ONLY)
            SpeechKit.getInstance().uuid = UUID.randomUUID().toString()
        } catch (ex: Exception) {

        }

        vocalizer = OnlineVocalizer.Builder(Language.RUSSIAN, this@App)
            .setVoice(Voice.ALYSS)
            .setAutoPlay(true)
            .build()
    }

    override fun onTerminate() {
        super.onTerminate()
        vocalizer?.cancel()
        vocalizer?.destroy()
        vocalizer = null
    }

    override fun onPlayingBegin(p0: Vocalizer) {

    }

    override fun onVocalizerError(p0: Vocalizer, p1: Error) {

    }

    override fun onSynthesisDone(p0: Vocalizer) {

    }

    override fun onPartialSynthesis(p0: Vocalizer, p1: Synthesis) {

    }

    override fun onPlayingDone(p0: Vocalizer) {

    }

}