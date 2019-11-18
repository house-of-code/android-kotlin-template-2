package io.houseofcode.template2.domain.live

import android.content.SharedPreferences

/**
 * Subtype of LiveData for wrapping SharedPreference Boolean entity.
 */
class SharedPreferenceBooleanLiveData(sharedPrefs: SharedPreferences, key: String, defValue: Boolean):
    SharedPreferenceLiveData<Boolean>(sharedPrefs, key, defValue) {
    override fun getValueFromPreferences(key: String, defValue: Boolean): Boolean = sharedPrefs.getBoolean(key, defValue)
    override fun setValueInPreferences(key: String, value: Boolean) = sharedPrefs.edit().putBoolean(key, value).apply()
}