package io.houseofcode.template2.presentation.feature.login

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import io.houseofcode.template2.R
import io.houseofcode.template2.domain.model.Resource
import io.houseofcode.template2.presentation.viewmodel.ItemViewModel

class LoginPresenter(private val view: LoginContract.View): LoginContract.Presenter {

    private lateinit var context: Context
    private lateinit var lifecycleOwner: LifecycleOwner
    private lateinit var itemViewModel: ItemViewModel

    override fun attach(activity: FragmentActivity) {
        context = activity
        lifecycleOwner = activity

        itemViewModel = ViewModelProvider(activity).get(ItemViewModel::class.java)
    }

    override fun login(email: String, password: String) {
        itemViewModel.login(email, password).observe(lifecycleOwner, Observer { resource ->
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    val loginToken = resource.data
                    if (loginToken != null) {
                        // Return token to view.
                        view.onLoginSuccess(loginToken)
                    } else {
                        view.onLoginError(context.getString(R.string.error_request_login))
                    }
                }
                Resource.Status.ERROR -> {
                    view.onLoginError(resource.errorMessage ?: context.getString(R.string.error_request_login))
                }
            }
        })
    }
}