package com.sashakhyzhun.locationmocker.data.prefs

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.sashakhyzhun.locationmocker.data.model.MockLocation


object PreferencesHelper : IPreferencesHelper {

    private val PREFER_NAME = "location_mocker_prefs"
    private val KEY_PREF_VERSION = "pref_version"

    private val KEY_USER_IS_REGISTERED = "key_user_is_registered"
    private val KEY_USER_NAME = "key_user_name"

    private val PRIVATE_MODE = 0
    private val PREF_VERSION = 0

    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor


    @SuppressLint("CommitPrefEdits")
    fun init(ctx: Context) {
        preferences = ctx.getSharedPreferences(PREFER_NAME, PRIVATE_MODE)
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }


    override fun storeMockLocation(item: MockLocation) {
        preferences.edit {

        }
    }

    override fun retrieve(title: String): MockLocation {
        TODO("not implemented")
    }

    override fun retrieveAll(): List<MockLocation> {
        TODO("not implemented")
    }

}