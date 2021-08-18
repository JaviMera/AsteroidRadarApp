package com.udacity.asteroidradar.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.AsteroidListItemBinding

class AsteroidRecyclerAdapter() : ListAdapter<Asteroid, AsteroidRecyclerAdapter.AsteroidViewHolder>(DiffCallback) {

    class AsteroidViewHolder(private val binding: AsteroidListItemBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(asteroid: Asteroid){
            binding.asteroid = asteroid
            binding.asteroidIsHazardousImage.setImageResource(when(asteroid.isPotentiallyHazardous){
                true -> R.drawable.ic_status_normal
                else -> R.drawable.ic_status_potentially_hazardous
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsteroidViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate(inflater, R.layout.asteroid_list_item, parent, false) as AsteroidListItemBinding
        return AsteroidViewHolder(binding)
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