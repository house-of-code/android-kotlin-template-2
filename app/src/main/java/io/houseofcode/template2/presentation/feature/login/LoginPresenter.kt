package io.houseofcode.template2.presentation.feature.login

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import io.houseofcode.template2.TemplateApp
import io.houseofcode.template2.domain.model.Resource
import io.houseofcode.template2.presentation.viewmodel.ItemViewModel

class LoginPresenter(private val view: LoginContract.View): LoginContract.Presenter {

    private lateinit var lifecycleOwner: LifecycleOwner
    private lateinit var itemViewModel: ItemViewModel

    override fun attach(activity: FragmentActivity) {
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
                        view.onLoginError("Could not receive login token")
                    }
                }
                Resource.Status.ERROR -> {
                    view.onLoginError(resource.errorMessage ?: "Could not perform login")
                }
            }
        })
    }
}