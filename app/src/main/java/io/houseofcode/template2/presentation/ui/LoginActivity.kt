package io.houseofcode.template2.presentation.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.houseofcode.template2.R
import io.houseofcode.template2.databinding.ActivityLoginBinding
import io.houseofcode.template2.presentation.feature.login.LoginContract
import io.houseofcode.template2.presentation.feature.login.LoginPresenter
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

    // Binding of layout (R.layout.activity_login), should be used to access type safe views.
    private lateinit var layout: ActivityLoginBinding

    // Presenter for login actions.
    private lateinit var presenter: LoginContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layout = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(layout.root)

        title = getString(R.string.activity_label_login)

        presenter = LoginPresenter(this)
        // Parameters can be parsed to presenter when initialized.
        presenter.attach(this, LoginPresenter.Params(success = true))

        layout.loginSubmitButton.setOnClickListener {
            presenter.login(layout.loginEmailEditText.text.toString(), layout.loginPasswordEditText.text.toString())
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