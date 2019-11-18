package io.houseofcode.template2.domain.live

import android.content.SharedPreferences

/**
 * Subtype of LiveData for wrapping SharedPreference String entity.
 */
class SharedPreferenceStringLiveData(sharedPrefs: SharedPreferences, key: String, defValue: String?):
    SharedPreferenceLiveData<String?>(sharedPrefs, key, defValue) {
    override fun getValueFromPreferences(key: String, defValue: String?): String? = sharedPrefs.getString(key, defValue)
    override fun setValueInPreferences(key: String, value: String?) = sharedPrefs.edit().putString(key, value).apply()
}