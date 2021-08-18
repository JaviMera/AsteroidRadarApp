package com.udacity.asteroidradar.main

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.udacity.asteroidradar.R

@BindingAdapter("hazardousIcon")
fun bindAsteroidHazardousImage(imageView: ImageView, isHazardous: Boolean){
    when(isHazardous){
        true -> imageView.setImageResource(R.drawable.ic_status_potentially_hazardous)
        else -> imageView.setImageResource(R.drawable.ic_status_normal)
    }
}