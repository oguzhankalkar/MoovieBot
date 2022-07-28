package com.example.mooviebot.ui.movie_detail

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.drawToBitmap
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.mooviebot.R
import com.example.mooviebot.data.movie_detail.MovieDetailModel
import com.example.mooviebot.databinding.ActivityMovieDetailBinding
import com.example.mooviebot.ui.custom_dialog.CustomMLActivity
import com.example.mooviebot.ui.movie_search.MovieSearchActivity
import com.example.mooviebot.ui.profile.ProfileActivity
import com.example.mooviebot.util.Constants
import com.google.android.gms.ads.*
import com.huawei.agconnect.crash.AGConnectCrash
import com.huawei.hmf.tasks.Task
import com.huawei.hms.mlsdk.classification.MLImageClassification
import com.huawei.hms.mlsdk.classification.MLImageClassificationAnalyzer
import com.huawei.hms.mlsdk.classification.MLLocalClassificationAnalyzerSetting
import com.huawei.hms.mlsdk.MLAnalyzerFactory
import com.huawei.hms.mlsdk.common.MLFrame
import java.io.IOException
import java.lang.StringBuilder
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors

class MovieDetailActivity : AppCompatActivity() {
    private var binding: ActivityMovieDetailBinding? = null
    private var mViewModel: MovieDetailViewModel? = null
    var frameML: MLFrame? = null
    private var analyzer: MLImageClassificationAnalyzer? = null
    private var posterBitmap: Bitmap? = null
    private val TAG = "ImageDetectionFragment"

    lateinit var hAdView: AdView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        mViewModel = ViewModelProviders.of(this)[MovieDetailViewModel::class.java]
        checkArguments()
        initObservers()
        loadMediationAd()
    }

    private fun loadMediationAd() {
        MobileAds.initialize(this) {}
        hAdView = findViewById(R.id.mediationAdView)
        val adRequest = AdRequest.Builder().build()
        hAdView.loadAd(adRequest)

        //listener for the ad
        hAdView.adListener = object : AdListener() {

            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                Toast.makeText(
                    this@MovieDetailActivity,
                    "Failed to load Huawei ad, please check your internet connection.",
                    Toast.LENGTH_LONG
                ).show()
                Log.v("adError", adError.toString())
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        }
    }

    override fun onPause() {
        hAdView.pause()
        super.onPause()

    }

    override fun onResume() {
        hAdView.resume()
        super.onResume()

    }

    override fun onDestroy() {
        hAdView.destroy()
        super.onDestroy()

    }

    private fun checkArguments() {
        val bundle = intent.extras
        if (bundle != null) {
            val movieId = bundle.getInt(Constants.ARG_MOVIE_ID)
            mViewModel!!.getMovieDetail(movieId)
        } else finish()
    }

    private fun initObservers() {
        mViewModel!!.movieDetail.observe(this) { movie: MovieDetailModel? ->
            if (movie != null) {
                prepareComponents(
                    movie
                )
            }
        }
    }

    private fun prepareComponents(movie: MovieDetailModel) {
        if (!TextUtils.isEmpty(movie.backdropPath)) {
            val posterPath: String = Constants.BACKDROP_BASE_PATH + movie.backdropPath
            val executor = Executors.newSingleThreadExecutor()
            val handler = Handler(Looper.getMainLooper())
            executor.execute {
                try {
                    posterBitmap = Glide.with(applicationContext)
                        .asBitmap()
                        .load(posterPath)
                        .placeholder(R.drawable.ic_movie)
                        .submit().get()
                } catch (e: ExecutionException) {
                    e.printStackTrace()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                handler.post {
                    binding?.ivPosterML?.setImageBitmap(posterBitmap)

                    analyzer = createImageAnalyzer()

                    frameML = MLFrame.fromBitmap(posterBitmap)

                    val imageClassificationButton =
                        findViewById<Button>(R.id.image_classification_btn)

                    imageClassificationButton.setOnClickListener { view1: View? ->
                        val task: Task<List<MLImageClassification>> =
                            analyzer!!.asyncAnalyseFrame(frameML)

                        task.addOnSuccessListener { classifications ->
                            val sb = StringBuilder()

                            sb.append("\n")
                            for (i in classifications.indices) {
                                sb.append("[")
                                    .append(i)
                                    .append("] ")
                                    .append(classifications[i].name)
                                    .append("\n")
                            }
                            if (classifications.isNotEmpty()) {
                                val result = sb.toString()
                                val i = Intent(baseContext, CustomMLActivity::class.java).also {
                                    it.putExtra("ML RESULT", result)
                                }
                                startActivity(i)
                            } else {
                                val resultEmpty = """
                                    [0] Others
                                    """.trimIndent()

                                val i = Intent(baseContext, CustomMLActivity::class.java).also {
                                    it.putExtra("ML RESULT", resultEmpty)
                                }
                                startActivity(i)
                            }

                            releaseImageDetectionResources()

                        }.addOnFailureListener { e ->
                            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
        if (TextUtils.isEmpty(movie.backdropPath)) Toast.makeText(
            this,
            "Image could not be found",
            Toast.LENGTH_SHORT
        ).show()
        if (!TextUtils.isEmpty(movie.overview)) binding?.tvOverview?.setText(movie.overview)
        if (movie.genres!!.isNotEmpty() and (movie.genres!!.size != 0)) {
            var genres = ""
            for (genre in movie.genres!!) {
                if (genres == "") genres =
                    genre.name.toString() else genres += ", " + genre.name.toString()
            }
            binding?.tvCategory?.text = genres
        }
        if (!TextUtils.isEmpty(movie.releaseDate)) binding?.tvDate?.text = movie.releaseDate
        if (!movie.voteAverage?.equals(0)!!) binding?.tvScore?.text = movie.voteAverage.toString()
    }

    private fun createImageAnalyzer(): MLImageClassificationAnalyzer {
        val setting: MLLocalClassificationAnalyzerSetting =
            MLLocalClassificationAnalyzerSetting.Factory()
                .setMinAcceptablePossibility(0.8f)
                .create()
        return MLAnalyzerFactory.getInstance().getLocalImageClassificationAnalyzer(setting)
    }

    private fun releaseImageDetectionResources() {
        try {
            analyzer?.stop()
        } catch (e: IOException) {
            Log.e(TAG, e.localizedMessage)
        }
    }
}