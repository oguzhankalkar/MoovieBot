package com.example.mooviebot.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.example.mooviebot.R
import java.util.ArrayList
import com.example.mooviebot.data.movie_search.Result
import com.example.mooviebot.databinding.ItemMovieBinding
import com.example.mooviebot.util.Constants

open class MovieAdapter(private val context: Context,
private val onItemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<MovieAdapter.MovieViewHolders>(){
    private val movies: MutableList<Result>

    /**
     * Setting the list data & notify the adapter for render list
     *
     * @param movies as List<Result>
    </Result> */
    fun setAdapterList(movies: List<Result>?) {
        this.movies.clear()
        this.movies.addAll(movies!!)
        notifyDataSetChanged()
    }

    /**
     * Default onCreate method.
     * Connect Adapter & layout
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolders {
        val binding: ItemMovieBinding =
            ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolders(binding)
    }

    /**
     * Prepare row item with data
     */
    override fun onBindViewHolder(holder: MovieViewHolders, position: Int) {
        val movie: Result = movies[position]
        if (!TextUtils.isEmpty(movie.posterPath)) {
            val posterPath: String = Constants.POSTER_BASE_PATH + movie.posterPath
            Glide.with(context)
                .load(posterPath)
                .placeholder(R.drawable.ic_movie)
                .into(holder.binding.ivPoster)
        }
        if (!TextUtils.isEmpty(movie.originalTitle)) holder.binding.tvTitle.setText(movie.originalTitle)
        if (!TextUtils.isEmpty(movie.popularity.toString())) {
            val popularity = "Popularity: " + movie.popularity.toString()
            holder.binding.tvPopularity.setText(popularity)
        }

        holder.binding.ivPoster.setOnClickListener{
            onItemClickListener.onItemClicked(position,movies[position])
        }
    }

    private fun getItem(pos: Int): Result {
        return movies[pos]
    }

    /**
     * @return the data list count
     */
    override fun getItemCount(): Int {
        return movies.size
    }
    //region Click Listener
    /*
    /**
     * This method is used for te interface in the adapter to communicate with the View
     *
     * @param itemClickListener as ItemClickListener
     */
    open fun setOnClickListener(itemClickListener: ItemClickListener?) {
        this.itemClickListener = itemClickListener
    }
    /**
     * This interface is used to catch the item click event
     */
    interface ItemClickListener {
        fun onClick(pos: Int, movie: Result?)
    }

    //endregion
    */
    inner class MovieViewHolders(binding: ItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var binding: ItemMovieBinding = binding
        //init {
        //    binding.root.setOnClickListener(this)
        //}
    }

    /**
     * Constructor for MovieAdapter class
     *
     * @param context as Context for Glide
     */
    init {
        movies = ArrayList<Result>()
    }
}