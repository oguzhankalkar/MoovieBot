package com.example.mooviebot.ui.movie_search

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.DefaultItemAnimator
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
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
import com.example.mooviebot.ui.profile.ProfileActivity
import com.huawei.hms.analytics.HiAnalytics
import com.huawei.hms.analytics.HiAnalyticsInstance
import com.huawei.hms.analytics.HiAnalyticsTools
import com.huawei.hms.analytics.type.HAEventType.*
import com.huawei.hms.analytics.type.HAParamType.*
import java.text.SimpleDateFormat
import java.util.*


class MovieSearchActivity : AppCompatActivity(), OnItemClickListener {
    private var binding: ActivityMovieSearchBinding? = null
    private var mViewModel: MovieSearchViewModel? = null
    private lateinit var movieAdapter: MovieAdapter
    lateinit var instance: HiAnalyticsInstance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieSearchBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        mViewModel = ViewModelProviders.of(this)[MovieSearchViewModel::class.java]

        HwAds.init(this)
        loadBannerAd()

        initComponents()
        initClicks()
        initObservers()

        HiAnalyticsTools.enableLog()

        instance = HiAnalytics.getInstance(this)

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

    private fun reportMovieDetails(movieSearch: String, movie: Result?) {
        //movieSearch
        //searchTime
        //movieID clicked
        val bundle = Bundle()
        bundle.putString("movieSearch", movieSearch)
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
        bundle.putString("searchTime", sdf.format(Date()))
        if (movie != null) {
            bundle.putString("movieID", movie.id.toString())
        }
        // Report a custom event.
        instance.onEvent("MovieDetails", bundle)
    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.app_bar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.miProfile -> {
                val loginTypeName = intent.getStringExtra("LOGIN TYPE NAME")
                val loginType = intent.getStringExtra("LOGIN TYPE")
                val intent = Intent(this, ProfileActivity::class.java).also {
                    it.putExtra("LOGIN TYPE NAME", loginTypeName)
                    it.putExtra("LOGIN TYPE", loginType)
                }
                startActivity(intent)
            }
        }
        return true
    }


    private fun initComponents() {
        binding!!.rvMovies.layoutManager = LinearLayoutManager(this)
        binding!!.rvMovies.itemAnimator = DefaultItemAnimator()
        movieAdapter = MovieAdapter(this, this)
        binding!!.rvMovies.adapter = movieAdapter
    }

    private fun initClicks() {
        binding!!.btnSearch.setOnClickListener { v: View? ->
            mViewModel!!.search(
                binding!!.etSearch.text.toString()
            )
        }
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

    private fun prepareRecycler(models: List<Result>) {
        movieAdapter.setAdapterList(models)
    }

    override fun onItemClicked(position: Int, movie: Result?) {
        val movieSearched = binding?.etSearch?.text.toString()

        if(movieSearched != null){
            reportMovieDetails(movieSearched,movie)
        }else{
            reportMovieDetails("",movie)
        }

        val loginTypeName = intent.getStringExtra("LOGIN TYPE NAME")
        if(loginTypeName != null){
            instance.setUserProfile("login_type",loginTypeName)
        }

        val intent = Intent(this@MovieSearchActivity, MovieDetailActivity::class.java)
        if (movie != null) {
            intent.putExtra(Constants.ARG_MOVIE_ID, movie.id)
        }
        startActivity(intent)
    }

}