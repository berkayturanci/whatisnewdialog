package com.nonzeroapps.whatisnewdialog.util

import android.content.Context
import android.content.SharedPreferences

object SharedPrefHelper {
    private const val SHARED_PREF_FILE_NAME = "what_is_new_dialog_pref_file"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(SHARED_PREF_FILE_NAME, Context.MODE_PRIVATE)
    }

    private fun getPreferencesEditor(context: Context): SharedPreferences.Editor {
        return getPreferences(context).edit()
    }

    @JvmStatic
    fun clearSharedPreferences(context: Context) {
        getPreferencesEditor(context).clear().apply()
    }

    @JvmStatic
    fun setSeenBefore(context: Context, versionName: String, isSeenBefore: Boolean) {
        getPreferencesEditor(context).putBoolean(versionName, isSeenBefore).apply()
    }

    @JvmStatic
    fun isSeenBefore(context: Context, versionName: String): Boolean {
        return getPreferences(context).getBoolean(versionName, false)
    }
}
