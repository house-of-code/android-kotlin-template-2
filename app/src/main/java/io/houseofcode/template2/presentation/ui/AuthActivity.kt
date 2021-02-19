package io.houseofcode.template2.presentation.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import io.houseofcode.template2.TemplateApp
import io.houseofcode.template2.presentation.viewmodel.SharedPreferencesViewModel
import timber.log.Timber

/**
 * Base activity that handles authentication.
 * This activity should only be used for activities that expect authorized requests.
 */
abstract class AuthActivity: AppCompatActivity() {

    // View model for SharedPreferences, which we access saved login token from.
    private lateinit var sharedPreferencesViewModel: SharedPreferencesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        // Get view model.
        sharedPreferencesViewModel = ViewModelProvider(
            this,
            SharedPreferencesViewModel.Factory(TemplateApp.instance.sharedPreferencesRepository)
        ).get(SharedPreferencesViewModel::class.java)

        // Check if is login token exists.
        if (sharedPreferencesViewModel.getLoginToken().isNullOrBlank()) {
            Timber.w("Login token not found, logging out ...")

            // Logout and send user to login.
            TemplateApp.instance.logout(this)
        }

        super.onCreate(savedInstanceState)
    }
}