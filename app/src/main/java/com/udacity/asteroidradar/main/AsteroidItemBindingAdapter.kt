package com.udacity.asteroidradar.main

import android.opengl.Visibility
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.databinding.BindingAdapter
import com.udacity.asteroidradar.R

@BindingAdapter("hazardousIcon")
fun bindAsteroidHazardousImage(imageView: ImageView, isHazardous: Boolean){
    when(isHazardous){
        true -> imageView.setImageResource(R.drawable.ic_status_potentially_hazardous)
        else -> imageView.setImageResource(R.drawable.ic_status_normal)
    }
}

@BindingAdapter("showProgress")
fun bindAsteroidRequestProgressBar(progressBar: ProgressBar, nasaApiStatus: NasaApiStatus?){
    when(nasaApiStatus){
        NasaApiStatus.ERROR -> progressBar.visibility = View.GONE
        NasaApiStatus.DONE -> progressBar.visibility = View.GONE
        NasaApiStatus.LOADING -> progressBar.visibility = View.VISIBLE
    }
}