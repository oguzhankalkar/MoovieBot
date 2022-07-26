package com.example.mooviebot.data.movie_search

import com.google.gson.annotations.SerializedName


class SearchModel {
    @SerializedName("page")
    var page: Int? = null

    @SerializedName("results")
    var results: List<Result>? = null

    @SerializedName("total_pages")
    var totalPages: Int? = null

    @SerializedName("total_results")
    var totalResults: Int? = null
}