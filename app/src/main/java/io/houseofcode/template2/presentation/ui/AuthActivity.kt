package io.houseofcode.template2.presentation.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.houseofcode.template2.TemplateApp
import timber.log.Timber

/**
 * Base activity that handles authentication.
 * This activity should only be used for activities that expect authorized requests.
 */
abstract class AuthActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Check if is login token exists.
        if (TemplateApp.pref.loginToken?.token.isNullOrBlank()) {
            Timber.w("Login token not found, logging out ...")

            // Logout and send user to login.
            TemplateApp.logout(this)
        }

        super.onCreate(savedInstanceState)
    }
}