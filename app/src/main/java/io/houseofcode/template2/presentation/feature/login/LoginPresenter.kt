package io.houseofcode.template2.presentation.feature.login

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import io.houseofcode.template2.R
import io.houseofcode.template2.domain.model.Resource
import io.houseofcode.template2.presentation.viewmodel.ItemViewModel
import timber.log.Timber

class LoginPresenter(private val view: LoginContract.View): LoginContract.Presenter {

    private lateinit var context: Context
    private lateinit var lifecycleOwner: LifecycleOwner
    private lateinit var itemViewModel: ItemViewModel

    /**
     * Parameters for initialization of presenter, which can be delivered optionally by view.
     * This parameter class serves as example of how parameters can be parsed to presenter when
     * initialed.
     */
    class Params(val success: Boolean)

    override fun attach(activity: FragmentActivity, params: Params?) {
        context = activity
        lifecycleOwner = activity

        val state = checkNotNull(params) { "Params must be provided to presenter" }
        Timber.d("attach { success: ${state.success} }")

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