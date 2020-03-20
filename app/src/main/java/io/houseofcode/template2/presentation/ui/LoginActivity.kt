package io.houseofcode.template2.presentation.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.houseofcode.template2.R
import io.houseofcode.template2.presentation.feature.login.LoginContract
import io.houseofcode.template2.presentation.feature.login.LoginPresenter
import kotlinx.android.synthetic.main.activity_login.*
import timber.log.Timber

class LoginActivity: AppCompatActivity(), LoginContract.View {

    companion object {
        /**
         * Get intent to start login activity.
         * The new activity will become the new stack root, clearing any previous back history.
         */
        fun newIntent(context: Context): Intent {
            return Intent(context, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }
    }

    private lateinit var presenter: LoginContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        title = getString(R.string.activity_label_login)

        presenter = LoginPresenter(this)
        presenter.attach(this)

        loginSubmitButton.setOnClickListener {
            presenter.login(loginEmailEditText.text.toString(), loginPasswordEditText.text.toString())
        }
    }

    override fun onLoginSuccess(loginToken: String) {
        // Successfully performed login, token is received and already saved in persistent storage.
        Timber.d("onLoginSuccess { loginToken: $loginToken }")

        // Redirect user.
        startActivity(
            MainActivity.newIntent(this)
        )
    }

    override fun onLoginError(errorMessage: String) {
        Timber.w("onLoginError { message: $errorMessage }")
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }
}