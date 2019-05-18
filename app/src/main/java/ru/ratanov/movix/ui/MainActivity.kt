package ru.ratanov.movix.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import ru.ratanov.movix.R
import ru.ratanov.movix.api.RequestExecutor
import ru.ratanov.movix.list.FilmAdapter
import ru.ratanov.movix.list.FilmClickListener
import ru.ratanov.movix.model.Film
import ru.ratanov.movix.utils.Util
import ru.yandex.speechkit.*
import ru.yandex.speechkit.gui.RecognizerActivity


class MainActivity : AppCompatActivity(), FilmClickListener, VocalizerListener {


    companion object {
        private val REQUEST_CODE = 31
    }

    private var vocalizer: Vocalizer? = null

    private var films = ArrayList<Film>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        greet()

        fab.setOnClickListener {
            val intent = Intent(this, RecognizerActivity::class.java)
            intent.putExtra(RecognizerActivity.EXTRA_MODEL, OnlineModel.QUERIES.name)
            intent.putExtra(RecognizerActivity.EXTRA_LANGUAGE, Language.RUSSIAN.value)

            startActivityForResult(intent, REQUEST_CODE)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        vocalizer?.cancel()
        vocalizer?.destroy()
        vocalizer = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RecognizerActivity.RESULT_OK && data != null) {
                val result = data.getStringExtra(RecognizerActivity.EXTRA_RESULT) // FULL QUERY
                val correctResult = Util.getCorrectQuery(result)
                val action = Util.getAction(result)

                when (action) {
                    Util.ACTION_FIND -> doSearch(correctResult)
                    Util.ACTION_SELECT -> doSelect(correctResult)
                    Util.ACTION_WATCH -> {
                    }
                }


            } else if (resultCode == RecognizerActivity.RESULT_ERROR) {
                val error = data?.getSerializableExtra(RecognizerActivity.EXTRA_ERROR).toString()
//                updateTextResult(error)
            }
        }
    }

    private fun doSearch(query: String) {
        RequestExecutor.doSearch(query,
            onSuccess = { result ->
                runOnUiThread {
                    if (result.isEmpty()) {
                        vocalizer?.synthesize(
                            "Ничего не найдено",
                            Vocalizer.TextSynthesizingMode.INTERRUPT
                        )
                        return@runOnUiThread
                    }

                    films.clear()
                    films = result

                    with(recycler_view) {
                        layoutManager = LinearLayoutManager(this@MainActivity)
                        addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
                        setHasFixedSize(true)
                        adapter = FilmAdapter(films, this@MainActivity)
                    }
                }

            },
            onError = {
                vocalizer?.synthesize(
                    "Произошла ошибка. Попробуйте еще.",
                    Vocalizer.TextSynthesizingMode.INTERRUPT
                )
            }
        )
    }

    private fun doSelect(query: String) {
        val selected = films.find { it.title.contains(query, true) }
        selected?.let { onFilmSelected(it) }

    }


    override fun onFilmSelected(film: Film) {
        Toast.makeText(this, film.title, Toast.LENGTH_SHORT).show()

        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("film", film)
        startActivity(intent)
    }

    private fun greet() {
        vocalizer = OnlineVocalizer.Builder(Language.RUSSIAN, this@MainActivity)
            .setVoice(Voice.ALYSS)
            .setAutoPlay(true)
            .build()

        vocalizer?.synthesize(
            getString(R.string.greeting),
            Vocalizer.TextSynthesizingMode.INTERRUPT
        )
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
