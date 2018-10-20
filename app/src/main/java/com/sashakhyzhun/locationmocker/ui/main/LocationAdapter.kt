package com.sashakhyzhun.locationmocker.ui.main

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.sashakhyzhun.locationmocker.R
import com.sashakhyzhun.locationmocker.data.model.MockLocation

class LocationAdapter(
        private val context: Context,
        private val mockLocations: List<MockLocation>
) : RecyclerView.Adapter<LocationAdapter.ViewHolderMockLocation>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, type: Int): ViewHolderMockLocation {
        return ViewHolderMockLocation(LayoutInflater.from(context)
                .inflate(R.layout.item_mock_location, viewGroup, false))
    }

    override fun onBindViewHolder(vh: ViewHolderMockLocation, position: Int) {
        val item = mockLocations[position]
        vh.tvTitle.text = item.title
        vh.tvLat.text = item.latitude.toString()
        vh.tvLong.text = item.longitude.toString()
    }

    override fun getItemCount(): Int = mockLocations.size

    class ViewHolderMockLocation(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.text_view_title)
        val tvLat: TextView = view.findViewById(R.id.text_view_lat)
        val tvLong: TextView = view.findViewById(R.id.text_view_long)
    }

}