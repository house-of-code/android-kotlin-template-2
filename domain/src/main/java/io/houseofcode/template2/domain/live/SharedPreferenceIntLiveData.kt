package io.houseofcode.template2.domain.live

import android.content.SharedPreferences

/**
 * Subtype of LiveData for wrapping SharedPreference Int entity.
 */
class SharedPreferenceIntLiveData(sharedPrefs: SharedPreferences, key: String, defValue: Int):
    SharedPreferenceLiveData<Int>(sharedPrefs, key, defValue) {
    override fun getValueFromPreferences(key: String, defValue: Int): Int = sharedPrefs.getInt(key, defValue)
    override fun setValueInPreferences(key: String, value: Int) = sharedPrefs.edit().putInt(key, value).apply()
}