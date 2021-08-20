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
import com.udacity.asteroidradar.database.toAsteroids
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false) as FragmentMainBinding

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        var adapter = AsteroidRecyclerAdapter(AsteroidRecyclerAdapter.OnClickListener {
            findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
        })

        binding.asteroidRecycler.adapter = adapter

        viewModel.dbAsteroids.observe(viewLifecycleOwner, Observer {
            if(it.any()){
                adapter.submitList(it)
            }else{
                Toast.makeText(context, "Unable to retrieve asteroids at this time.", Toast.LENGTH_SHORT).show()
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

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return true
    }
}
