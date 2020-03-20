package io.houseofcode.template2.presentation.feature.main

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import io.houseofcode.template2.R
import io.houseofcode.template2.domain.model.Item
import io.houseofcode.template2.domain.model.Resource
import io.houseofcode.template2.domain.usecase.GenerateItemUseCase
import io.houseofcode.template2.presentation.viewmodel.ItemViewModel
import io.houseofcode.template2.presentation.viewmodel.SharedPreferencesViewModel

class MainPresenter(private val view: MainContract.View): MainContract.Presenter {

    private lateinit var context: Context
    private lateinit var lifecycleOwner: LifecycleOwner
    private lateinit var itemViewModel: ItemViewModel
    private lateinit var sharedPreferencesViewModel: SharedPreferencesViewModel

    override fun attach(activity: FragmentActivity) {
        context = activity
        lifecycleOwner = activity

        itemViewModel = ViewModelProvider(activity).get(ItemViewModel::class.java)
        sharedPreferencesViewModel = ViewModelProvider(activity).get(SharedPreferencesViewModel::class.java)

        sharedPreferencesViewModel.getFirstRunFlag().observe(activity, Observer { isFirstRun ->
            view.onFirstRunFlagReceived(isFirstRun)
        })
    }

    override fun getItem(id: String) {
        // Get item by id and observe returned data.
        itemViewModel.getItem(id).observe(lifecycleOwner, Observer { resource ->
            // Check if returned resource is successful or has errors.
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    val item: Item? = resource.data
                    if (item != null) {
                        view.onItemReceived(item)
                    } else {
                        view.onError(context.getString(R.string.error_request_get_item))
                    }
                }
                Resource.Status.ERROR -> {
                    view.onError(resource.errorMessage ?: context.getString(R.string.error_request_get_item))
                }
            }
        })
    }

    override fun getItems() {
        itemViewModel.getItems().observe(lifecycleOwner, Observer { resource ->
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    val items: List<Item>? = resource.data
                    if (!items.isNullOrEmpty()) {
                        view.onItemsReceived(items)
                    } else {
                        view.onError(context.getString(R.string.error_request_get_items))
                    }
                }
                Resource.Status.ERROR -> {
                    view.onError(resource.errorMessage ?: context.getString(R.string.error_request_get_items))
                }
            }
        })
    }

    override fun addItem(id: String) {
        // Generate item from id.
        val item = GenerateItemUseCase().execute(
            GenerateItemUseCase.Params(id)
        )

        // Add item and observe newly created data.
        itemViewModel.addItem(item).observe(lifecycleOwner, Observer { resource ->
            // Check if returned resource is successful or has errors.
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    val newItem: Item? = resource.data
                    if (newItem != null) {
                        view.onNewItemAdded(newItem)
                    } else {
                        view.onError(context.getString(R.string.error_request_add_item))
                    }
                }
                Resource.Status.ERROR -> {
                    view.onError(resource.errorMessage ?: context.getString(R.string.error_request_add_item))
                }
            }
        })
    }

    override fun setFirstRunFlag(isFirstRun: Boolean) {
        sharedPreferencesViewModel.setFirstRunFlag(isFirstRun)
    }
}