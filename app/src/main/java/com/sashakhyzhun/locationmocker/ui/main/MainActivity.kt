package com.sashakhyzhun.locationmocker.ui.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.sashakhyzhun.locationmocker.R
import com.sashakhyzhun.locationmocker.data.model.MockLocation
import com.sashakhyzhun.locationmocker.utils.DialogHelper
import timber.log.Timber


class MainActivity : AppCompatActivity(), AdapterCallback {

    private lateinit var fab: FloatingActionButton
    private lateinit var rv: RecyclerView

    private lateinit var adapter: LocationAdapter
    private lateinit var viewModel: ViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProviders.of(this).get(ViewModel::class.java)
        adapter = LocationAdapter(this, this)

        rv = findViewById(R.id.recycler_view)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(this)

        fab = findViewById(R.id.fab)
        fab.setOnClickListener {
            DialogHelper.addNewLocation(this) { title, lat, long ->
                viewModel.insert(MockLocation(title, lat, long))
            }
        }

        viewModel.getAllLocations().observe(this, Observer {
            adapter.setMockLocations(it)
        })

    }


    override fun onLongClicked(mockLocation: MockLocation) {
        Timber.d("called")
        DialogHelper.removeLocation(this, mockLocation) {
            viewModel.delete(mockLocation)
        }
    }

    override fun onStartClicked(mockLocation: MockLocation) {
        Timber.d("called")
        viewModel.applyLocation(this, mockLocation)
    }

    override fun onStopClicked(mockLocation: MockLocation) {
        Timber.d("called")
        viewModel.stopMockingLocation()
    }

}
