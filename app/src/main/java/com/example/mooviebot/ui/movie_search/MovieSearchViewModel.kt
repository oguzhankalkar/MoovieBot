package com.example.mooviebot.ui.movie_search

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mooviebot.data.movie_search.SearchModel
import com.example.mooviebot.service.ClientMovie
import com.example.mooviebot.service.IRequest
import com.example.mooviebot.util.Constants
import com.example.mooviebot.data.movie_search.Result
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MovieSearchViewModel : ViewModel() {
    /**
     * This method is used to observe the Live Data for search
     */
    val searchList = MutableLiveData<List<Result>>()

    /**
     * This method is used to observe the Live Data for null check
     */
    val searchControl = MutableLiveData<Boolean>()

    /**
     * Null check
     *
     * @param query as String
     */
    fun search(query: String) {
        if (!query.isEmpty()) {
            searchControl.postValue(false)
            searchMovies(query)
        } else searchControl.postValue(true)
    }

    /**
     * Send HTTP Request for searching movies
     */
    private fun searchMovies(query: String) {
        val request = ClientMovie().getApiClient()!!.create(IRequest::class.java)
        val call = request.searchMovie(Constants.TEST_API_KEY, query)
        call.enqueue(object : Callback<SearchModel> {
            override fun onResponse(call: Call<SearchModel>, response: Response<SearchModel>) {
                if (response.isSuccessful) {
                    searchList.postValue(response.body()!!.results)
                }
            }

            override fun onFailure(call: Call<SearchModel>, t: Throwable) {
                Log.d(Constants.CUSTOM_TAG, "onFailure: ")
            }
        })
    }
}