package ru.ratanov.movix.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail.*
import ru.ratanov.movix.R
import ru.ratanov.movix.model.Film

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val film = intent.getParcelableExtra<Film>("film")

        film_title.text = film.title
        film_desc.text = film.description

        Picasso.get()
            .load(film.posterUrl)
            .into(film_poster)


    }
}
