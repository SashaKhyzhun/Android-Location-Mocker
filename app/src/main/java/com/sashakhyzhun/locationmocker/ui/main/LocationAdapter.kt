package com.sashakhyzhun.locationmocker.ui.main

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.sashakhyzhun.locationmocker.R
import com.sashakhyzhun.locationmocker.data.model.MockLocation

class LocationAdapter
    constructor(
            private val context: Context,
            private val callback: AdapterCallback)
    : RecyclerView.Adapter<LocationAdapter.ViewHolderMockLocation>() {

    private var mockLocations: List<MockLocation>? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, type: Int): ViewHolderMockLocation {
        return ViewHolderMockLocation(LayoutInflater.from(context)
                .inflate(R.layout.item_mock_location, viewGroup, false))
    }

    override fun onBindViewHolder(vh: ViewHolderMockLocation, position: Int) {
        if (mockLocations != null) {
            val item = mockLocations!![position]
            vh.tvTitle.text = item.title
            vh.tvLat.text = item.latitude.toString()
            vh.tvLong.text = item.longitude.toString()

            vh.layout.setOnLongClickListener {
                Toast.makeText(context, "Long Click", Toast.LENGTH_SHORT).show()
                callback.onLongClicked(item)
                true
            }

            vh.tvStart.setOnClickListener {
                item.enabled = true
                vh.tvStart.isEnabled = false
                vh.tvStop.isEnabled = true
                callback.onStartClicked(item)
            }
            vh.tvStop.setOnClickListener {
                item.enabled = false
                vh.tvStart.isEnabled = true
                vh.tvStop.isEnabled = false
                callback.onStopClicked(item)
            }
        }
    }


    override fun getItemCount(): Int = mockLocations?.size ?: 0

    fun setMockLocations(locations: List<MockLocation>?) {
        mockLocations = locations
        notifyDataSetChanged()
    }

    class ViewHolderMockLocation(view: View) : RecyclerView.ViewHolder(view) {
        val layout: ConstraintLayout = view.findViewById(R.id.layout_mock_location)
        val tvTitle: TextView = view.findViewById(R.id.text_view_title)
        val tvLat: TextView = view.findViewById(R.id.text_view_lat)
        val tvLong: TextView = view.findViewById(R.id.text_view_long)

        val tvStart: TextView = view.findViewById(R.id.text_view_start)
        val tvStop: TextView = view.findViewById(R.id.text_view_stop)
    }

}