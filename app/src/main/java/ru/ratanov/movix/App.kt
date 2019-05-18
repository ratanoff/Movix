package ru.ratanov.movix

import android.app.Application
import ru.yandex.speechkit.SpeechKit
import java.util.*

class App : Application() {

    private val API_KEY_FOR_TESTS_ONLY = "069b6659-984b-4c5f-880e-aaedcfd84102"

    companion object {
        private var instance: Application? = null
        fun instance() = instance!!
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        try {
            SpeechKit.getInstance().init(this, API_KEY_FOR_TESTS_ONLY)
            SpeechKit.getInstance().uuid = UUID.randomUUID().toString()
        } catch (ex: Exception) {

        }
    }

}