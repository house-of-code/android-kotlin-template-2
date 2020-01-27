package io.houseofcode.template2.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.houseofcode.template2.R
import io.houseofcode.template2.domain.model.LoginToken
import io.houseofcode.template2.presentation.feature.login.LoginContract
import io.houseofcode.template2.presentation.feature.login.LoginPresenter
import kotlinx.android.synthetic.main.activity_login.*
import timber.log.Timber

class LoginActivity: AppCompatActivity(), LoginContract.View {

    companion object {
        /**
         * Check if login activity active.
         * When the activity is resumed and paused we set this as a guidance for whether the login activity is shown,
         * so we do not send user to login screen multiple times. This logic also counts on only once instance of the
         * login activity being used, which is why the launch mode singleTask is used.
         */
        var isActive = false
    }

    private lateinit var presenter: LoginContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        presenter = LoginPresenter(this)
        presenter.attach(this)

        loginSubmitButton.setOnClickListener {
            presenter.login(loginEmailEditText.text.toString(), loginPasswordEditText.text.toString())
        }
    }

    override fun onResume() {
        super.onResume()

        // Activity is shown.
        isActive = true
    }

    override fun onPause() {
        super.onPause()

        // Activity is paused.
        isActive = false
    }

    override fun onLoginSuccess(loginToken: LoginToken) {
        // Successfully performed login, token is received and already saved in persistent storage.
        Timber.d("onLoginSuccess { loginToken: ${loginToken.token} }")

        // Redirect user.
        val mainIntent = Intent(this, MainActivity::class.java)
        mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(mainIntent)
    }

    override fun onLoginError(errorMessage: String) {
        Timber.w("onLoginError { message: $errorMessage }")
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }
}