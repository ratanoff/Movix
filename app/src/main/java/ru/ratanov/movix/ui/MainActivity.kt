package ru.ratanov.movix.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import ru.ratanov.movix.App
import ru.ratanov.movix.R
import ru.ratanov.movix.api.RequestExecutor
import ru.ratanov.movix.list.FilmAdapter
import ru.ratanov.movix.list.FilmClickListener
import ru.ratanov.movix.model.Film
import ru.ratanov.movix.utils.Util
import ru.yandex.speechkit.Language
import ru.yandex.speechkit.OnlineModel
import ru.yandex.speechkit.gui.RecognizerActivity


class MainActivity : AppCompatActivity(), FilmClickListener {

    private val REQUEST_CODE = 31

    private var films = ArrayList<Film>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab.setOnClickListener {
            App.shutUp()
            val intent = Intent(this, RecognizerActivity::class.java)
            intent.putExtra(RecognizerActivity.EXTRA_MODEL, OnlineModel.QUERIES.name)
            intent.putExtra(RecognizerActivity.EXTRA_LANGUAGE, Language.RUSSIAN.value)

            startActivityForResult(intent, REQUEST_CODE)
        }

        intent.getStringExtra("query")?.let {
            doSearch(it)
            return
        }

        greet()
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
                App.speakMessage(error)
            }
        }
    }

    private fun doSearch(query: String) {
        RequestExecutor.doSearch(query,
            onSuccess = { result ->
                if (result.isEmpty()) {
                    App.speakMessage("Ничего не найдено")
                } else {
                    App.speakMessage(String.format(getString(R.string.found_count_message), result.size))
                    runOnUiThread {
                        films.clear()
                        films = result

                        with(recycler_view) {
                            layoutManager = LinearLayoutManager(this@MainActivity)
                            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
                            setHasFixedSize(true)
                            adapter = FilmAdapter(films, this@MainActivity)
                        }
                    }
                }
            },
            onError = {
                App.speakMessage(R.string.try_later_message)
            }
        )
    }

    private fun doSelect(query: String) {
        val trimmedQuery = Util.removePunctuation(query)
        val selected = films.find { it.title.contains(trimmedQuery, true) }
        selected?.let { onFilmSelected(it) }
    }


    override fun onFilmSelected(film: Film) {
        Toast.makeText(this, film.title, Toast.LENGTH_SHORT).show()

        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("film", film)
        startActivity(intent)
    }

    private fun greet() {
        App.speakMessage(R.string.greeting)
    }

}
