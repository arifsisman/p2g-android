package vip.yazilim.p2g.android.util.data

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences


object SharedPrefSingleton {
    private var mSharedPref: SharedPreferences? = null

    fun init(context: Context, name: String) {
        if (mSharedPref == null) mSharedPref =
            context.getSharedPreferences(name, Activity.MODE_PRIVATE)
    }

    fun read(key: String?, defValue: String?): String? {
        return mSharedPref!!.getString(key, defValue)
    }

    fun write(key: String?, value: String?) {
        val prefsEditor = mSharedPref!!.edit()
        prefsEditor.putString(key, value)
        prefsEditor.apply()
    }

    fun read(key: String?, defValue: Boolean): Boolean {
        return mSharedPref!!.getBoolean(key, defValue)
    }

    fun write(key: String?, value: Boolean) {
        val prefsEditor = mSharedPref!!.edit()
        prefsEditor.putBoolean(key, value)
        prefsEditor.apply()
    }

    fun read(key: String?, defValue: Int): Int {
        return mSharedPref!!.getInt(key, defValue)
    }

    fun write(key: String?, value: Int?) {
        val prefsEditor = mSharedPref!!.edit()
        prefsEditor.putInt(key, value!!).apply()
    }

    fun write(key: String, value: Long?) {
        val prefsEditor = mSharedPref!!.edit()
        prefsEditor.putLong(key, value!!).apply()
    }

    fun contains(key: String): Boolean? {
        return mSharedPref?.contains(key)
    }

    fun remove(key: String?) {
        val prefsEditor = mSharedPref!!.edit()
        prefsEditor.remove(key)
        prefsEditor.apply()
    }

}