package com.vlaksuga.mymo

import android.content.Context
import android.content.SharedPreferences

class AppSharedPreferences(context: Context) {

    private val preferences : SharedPreferences = context.getSharedPreferences(PREFS_FILE_NAME, 0)

    var introPassed : Boolean
        get() = preferences.getBoolean(PREF_KEY_INTRO_PASSED, false)
        set(value) {
            preferences.edit().putBoolean(PREF_KEY_INTRO_PASSED, value).apply()
        }

    var instantMemoTitle : String?
        get() = preferences.getString(PREF_KEY_MEMO_TITLE, "")
        set(title) {
            preferences.edit().putString(PREF_KEY_MEMO_TITLE, title).apply()
        }

    var instantMemoContent  : String?
        get() = preferences.getString(PREF_KEY_MEMO_CONTENT, "")
        set(content) {
            preferences.edit().putString(PREF_KEY_MEMO_CONTENT, content).apply()
        }

    var instantMemoImportance  : Boolean
        get() = preferences.getBoolean(PREF_KEY_MEMO_IMPORTANCE, false)
        set(importance) {
            preferences.edit().putBoolean(PREF_KEY_MEMO_IMPORTANCE, importance).apply()
        }

    var instantMemoGroupId : Int
        get() = preferences.getInt(PREF_KEY_MEMO_GROUP_ID, 1)
        set(id) {
            preferences.edit().putInt(PREF_KEY_MEMO_GROUP_ID, id).apply()
        }

    companion object {
        const val PREFS_FILE_NAME = "prefs"
        const val PREF_KEY_INTRO_PASSED = "intro"
        const val PREF_KEY_MEMO_TITLE = "memoTitle"
        const val PREF_KEY_MEMO_CONTENT = "memoContent"
        const val PREF_KEY_MEMO_IMPORTANCE = "memoImportance"
        const val PREF_KEY_MEMO_GROUP_ID = "memoGroupId"
    }
}