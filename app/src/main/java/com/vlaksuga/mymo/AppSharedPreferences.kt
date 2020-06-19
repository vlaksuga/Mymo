package com.vlaksuga.mymo

import android.content.Context
import android.content.SharedPreferences

class AppSharedPreferences(context: Context) {

    val PREFS_FILE_NAME = "prefs"
    val PREF_KEY_INTRO_PASSED = "intro"
    val prefes : SharedPreferences = context.getSharedPreferences(PREFS_FILE_NAME, 0)

    var introPassed : Boolean
        get() = prefes.getBoolean(PREF_KEY_INTRO_PASSED, false)
        set(value) {
            prefes.edit().putBoolean(PREF_KEY_INTRO_PASSED, value).apply()
        }
}