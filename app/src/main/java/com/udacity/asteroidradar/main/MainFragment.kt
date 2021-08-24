package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    private lateinit var asteroidAdapter: AsteroidRecyclerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false) as FragmentMainBinding

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        asteroidAdapter = AsteroidRecyclerAdapter(AsteroidRecyclerAdapter.OnClickListener {
            findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
        })

        binding.asteroidRecycler.adapter = asteroidAdapter

        viewModel.asteroids.observe(viewLifecycleOwner, Observer {
            try {
                if(it.any()){
                    asteroidAdapter.submitList(it)
                }else{
                    Toast.makeText(context, "Unable to retrieve asteroids at this time.", Toast.LENGTH_SHORT).show()
                }
            }catch(exception: Exception){
                Toast.makeText(context, exception.message, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.status.observe(viewLifecycleOwner, Observer {
            if(it.equals(NasaApiStatus.ERROR)){
                val snackbar = Snackbar
                    .make(requireActivity().findViewById(R.id.constraint_layout), "Unable to retrieve Asteroids", Snackbar.LENGTH_LONG)
                    .setAction("Retry", View.OnClickListener {
                        CoroutineScope(Dispatchers.Default).launch{
                            viewModel.getAsteroids()
                        }
                    })
                snackbar.show()
            }
        })

        viewModel.picture.observe(viewLifecycleOwner, Observer {
            if(it == null){
                Toast.makeText(context, "Unable to get picture from database'", Toast.LENGTH_SHORT).show()
            }else{
                Timber.i(it.toString())
                Toast.makeText(context, it.title, Toast.LENGTH_SHORT).show()
            }
        })

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        // Remove observers from dbAsteroids to avoid updating the database while the user
        // is viewing data from an online request. Otherwise if the worker(not yet implemented)
        // updates the database, the dbAsteroids will also be updated from the Transformations.
        viewModel.dbAsteroids.removeObservers(viewLifecycleOwner)

        return when(item.itemId){
            R.id.show_saved_asteroids -> observeAsteroidsFromDatabase()
            R.id.show_today_asteroids -> viewModel.getTodaysAsteroids()
            else -> viewModel.getWeekAsteroids()
        }
    }

    private fun observeAsteroidsFromDatabase(): Boolean {

        viewModel.dbAsteroids.observe(viewLifecycleOwner, Observer {
            asteroidAdapter.submitList(it)
        })

        return true
    }
}
