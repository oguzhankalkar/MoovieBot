package com.example.mooviebot.data.movie_detail

import com.google.gson.annotations.SerializedName


class ProductionCompany {
    @SerializedName("id")
    var id: Int? = null

    @SerializedName("logo_path")
    var logoPath: Any? = null

    @SerializedName("name")
    var name: String? = null

    @SerializedName("origin_country")
    var originCountry: String? = null
}