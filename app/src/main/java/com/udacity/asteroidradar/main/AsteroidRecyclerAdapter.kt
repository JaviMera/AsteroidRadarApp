package com.udacity.asteroidradar.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.R
import org.w3c.dom.Text

class AsteroidRecyclerAdapter() : ListAdapter<Asteroid, AsteroidRecyclerAdapter.AsteroidViewHolder>(DiffCallback) {

    class AsteroidViewHolder(asteroidView: View) : RecyclerView.ViewHolder(asteroidView){

        val codeName: TextView = asteroidView.findViewById(R.id.asteroid_code_name)
        val closeApproachDate: TextView = asteroidView.findViewById(R.id.asteroid_close_approach_date)
        val hazardousImage:ImageView = asteroidView.findViewById(R.id.asteroid_is_hazardous_image)

        fun bind(asteroid: Asteroid){
            codeName.text = asteroid.codename
            closeApproachDate.text = asteroid.closeApproachDate
            hazardousImage.setImageResource(when(asteroid.isPotentiallyHazardous){
                true -> R.drawable.ic_status_normal
                else -> R.drawable.ic_status_potentially_hazardous
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsteroidViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.asteroid_list_item, parent, false)
        return AsteroidViewHolder(view)
    }

    override fun onBindViewHolder(holder: AsteroidViewHolder, position: Int) {
        var asteroid = getItem(position)
        holder.bind(asteroid)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Asteroid>(){
        override fun areItemsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem == newItem
        }
    }
}