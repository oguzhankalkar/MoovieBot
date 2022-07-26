package com.example.mooviebot.adapter

import com.example.mooviebot.data.movie_search.Result

interface OnItemClickListener {

    fun onItemClicked(position: Int, movie: Result?)
}