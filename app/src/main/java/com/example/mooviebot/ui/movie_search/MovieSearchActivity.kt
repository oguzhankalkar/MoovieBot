package com.example.mooviebot.ui.movie_search

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.DefaultItemAnimator
import android.content.Intent
import android.view.View
import com.example.mooviebot.adapter.MovieAdapter
import com.example.mooviebot.adapter.OnItemClickListener
import com.example.mooviebot.databinding.ActivityMovieSearchBinding
import com.example.mooviebot.ui.movie_detail.MovieDetailActivity
import com.example.mooviebot.util.Constants
import com.example.mooviebot.data.movie_search.Result
import com.huawei.hms.ads.AdParam
import com.huawei.hms.ads.BannerAdSize
import com.huawei.hms.ads.HwAds
import com.huawei.hms.ads.banner.BannerView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.mooviebot.R

class MovieSearchActivity : AppCompatActivity() ,OnItemClickListener{
    private var binding: ActivityMovieSearchBinding? = null
    private var mViewModel: MovieSearchViewModel? = null
    private lateinit var movieAdapter: MovieAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieSearchBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        mViewModel = ViewModelProviders.of(this).get(MovieSearchViewModel::class.java)

        HwAds.init(this)
        loadBannerAd()

        initComponents()
        initClicks()
        initObservers()
    }

    private fun loadBannerAd() {
        val adParam = AdParam.Builder().build()
        val topBannerView = BannerView(this)
        topBannerView.adId = "testw6vs28auh3"
        topBannerView.bannerAdSize = BannerAdSize.BANNER_SIZE_360_57
        topBannerView.loadAd(adParam)

        val rootView = findViewById<ConstraintLayout>(R.id.root_view)
        rootView.addView(topBannerView)
    }

    private fun initComponents() {
        binding!!.rvMovies.layoutManager = LinearLayoutManager(this)
        binding!!.rvMovies.itemAnimator = DefaultItemAnimator()
        movieAdapter = MovieAdapter(this,this)
        binding!!.rvMovies.adapter = movieAdapter
    }

    private fun initClicks() {
        binding!!.btnSearch.setOnClickListener { v: View? ->
            mViewModel!!.search(
                binding!!.etSearch.text.toString()
            )
        }
       // movieAdapter!!.setOnClickListener { pos: Int, movie: Result ->
       //     val intent = Intent(this@MovieSearchActivity, MovieDetailActivity::class.java)
       //     intent.putExtra(Constants.ARG_MOVIE_ID, movie.id)
       //     startActivity(intent)
       //  }
    }

    private fun initObservers() {
        mViewModel!!.searchList.observe(this, { models: List<Result> -> prepareRecycler(models) })
        mViewModel!!.searchControl.observe(
            this,
            { aBoolean: Boolean ->
                if (aBoolean) Toast.makeText(
                    this,
                    "You should enter at least one letter",
                    Toast.LENGTH_SHORT
                ).show()
            })
    }

    /**
     * Set the data to the RecyclerView's adapter
     *
     * @param models as List<Result>
    </Result> */
    private fun prepareRecycler(models: List<Result>) {
        movieAdapter!!.setAdapterList(models)
    }

    override fun onItemClicked(position: Int, movie: Result?) {
        val intent = Intent(this@MovieSearchActivity, MovieDetailActivity::class.java)
        if (movie != null) {
            intent.putExtra(Constants.ARG_MOVIE_ID, movie.id)
        }
        startActivity(intent)
    }

}