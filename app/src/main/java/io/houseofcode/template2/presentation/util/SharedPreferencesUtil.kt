package io.houseofcode.template2.presentation.util

import android.content.Context
import android.content.SharedPreferences
import io.houseofcode.template2.domain.model.LoginToken

/**
 * Utilities for saving and retrieving simple values from [SharedPreferences].
 */
class SharedPreferencesUtil(context: Context) {

    private val pref: SharedPreferences

    companion object {
        private const val PREF_PACKAGE_NAME = "io.houseofcode.template2.preferences"

        private const val PREF_LOGIN_TOKEN = "login_token"
    }

    init {
        pref = context.getSharedPreferences(PREF_PACKAGE_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Get or set token from login.
     */
    var loginToken: LoginToken?
        get() = LoginToken(
            pref.getString(
                PREF_LOGIN_TOKEN,
                null
            )
        )
        set(loginToken) = pref.edit().putString(PREF_LOGIN_TOKEN, loginToken?.token).apply()

    /**
     * Logout by clearing user data.
     */
    fun logout() {
        pref.edit()
            .remove(PREF_LOGIN_TOKEN)
            .apply()
    }
}