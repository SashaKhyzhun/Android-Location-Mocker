package com.sashakhyzhun.locationmocker.utils

import android.app.AlertDialog
import android.app.Activity
import android.widget.EditText
import android.widget.TextView
import com.sashakhyzhun.locationmocker.R


object DialogHelper {

    fun addNewLocation(activity: Activity, action: (title: String, lat: Double, long: Double) -> Unit) {
        val mBuilder = AlertDialog.Builder(activity)
        mBuilder.setTitle("Add mock location")

        val view = activity.layoutInflater.inflate(R.layout.dialog_login, null)

        val etTitle = view.findViewById(R.id.et_title) as EditText
        val etLat = view.findViewById(R.id.et_latitude) as EditText
        val etLong = view.findViewById(R.id.et_longitude) as EditText
        val tvCancel = view.findViewById(R.id.button_cancel) as TextView
        val tvAdd = view.findViewById(R.id.button_add) as TextView

        mBuilder.setView(view)
        val dialog = mBuilder.create()
        dialog.show()

        tvAdd.setOnClickListener {
            action(etTitle.text.toString(), etLat.text.toString().toDouble(), etLong.text.toString().toDouble())
            dialog.cancel()
        }
        tvCancel.setOnClickListener {
            dialog.dismiss()
        }
    }



}
