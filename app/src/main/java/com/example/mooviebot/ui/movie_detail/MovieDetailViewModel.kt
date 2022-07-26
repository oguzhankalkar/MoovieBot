package com.example.mooviebot.ui.movie_detail

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mooviebot.data.movie_detail.MovieDetailModel
import com.example.mooviebot.data.movie_search.SearchModel
import com.example.mooviebot.service.ClientMovie
import com.example.mooviebot.service.IRequest
import com.example.mooviebot.util.Constants
import com.example.mooviebot.util.Constants.CUSTOM_TAG
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MovieDetailViewModel : ViewModel() {
    /**
     * This method is used to observe the Live Data for movie detail
     */
    val movieDetail = MutableLiveData<MovieDetailModel?>()

    /**
     * Send HTTP Request for getting the movie detail via movieId
     */
    fun getMovieDetail(movieId: Int) {
        val request: IRequest = ClientMovie().getApiClient()!!.create(IRequest::class.java)
        val call = request.getMovieDetail(movieId, Constants.TEST_API_KEY, Constants.API_LANGUAGE)
        call.enqueue(object : Callback<MovieDetailModel?> {
            override fun onResponse(
                call: Call<MovieDetailModel?>,
                response: Response<MovieDetailModel?>
            ) {
                if (response.isSuccessful) {
                    movieDetail.postValue(response.body())
                }
            }

            override fun onFailure(call: Call<MovieDetailModel?>, t: Throwable) {
                Log.d(CUSTOM_TAG, "onFailure: ")
            }
        })
    }
}