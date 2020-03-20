package io.houseofcode.template2.presentation.feature.login

import io.houseofcode.template2.presentation.feature.BaseActivityPresenter

class LoginContract {

    interface Presenter : BaseActivityPresenter {

        /**
         * Login with email and password
         */
        fun login(email: String, password: String)
    }

    interface View {

        /**
         * Callback on successful login request.
         */
        fun onLoginSuccess(loginToken: String)

        /**
         * Callback on failed login request.
         */
        fun onLoginError(errorMessage: String)
    }
}