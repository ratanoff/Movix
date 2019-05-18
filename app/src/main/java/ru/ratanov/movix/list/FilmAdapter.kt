package ru.ratanov.movix.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_film.view.*
import ru.ratanov.movix.R
import ru.ratanov.movix.model.Film

class FilmAdapter(private val films: List<Film>, private val clickListener: FilmClickListener) : RecyclerView.Adapter<FilmHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_film, parent, false)
        return FilmHolder(view)
    }

    override fun onBindViewHolder(holder: FilmHolder, position: Int) {
        holder.bindItem(films[position])

        holder.itemView.setOnClickListener {
            clickListener.onFilmSelected(films[position])
        }
    }

    override fun getItemCount() = films.size
}


class FilmHolder(private val contentView: View) : RecyclerView.ViewHolder(contentView) {

    fun bindItem(film: Film) {
        contentView.item_title.text = film.title

        Picasso.get()
            .load(film.posterUrl)
            .into(contentView.item_poster)

        contentView.setOnClickListener {

        }
    }

}