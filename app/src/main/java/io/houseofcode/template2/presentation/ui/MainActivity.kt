package io.houseofcode.template2.presentation.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import io.houseofcode.template2.R
import io.houseofcode.template2.TemplateApp
import io.houseofcode.template2.domain.model.Item
import io.houseofcode.template2.presentation.feature.main.MainContract
import io.houseofcode.template2.presentation.feature.main.MainPresenter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class MainActivity: AuthActivity(), MainContract.View {

    private lateinit var presenter: MainContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        presenter = MainPresenter(this)
        presenter.attach(this)

        firstRunToggleButton.setOnCheckedChangeListener { _, isChecked ->
            Timber.d("setOnCheckedChangeListener { isChecked: $isChecked }")
            presenter.setFirstRunFlag(isChecked)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onResume() {
        super.onResume()

        // Set single item.
        presenter.getItem("1")

        // Get all items.
        presenter.getItems()

        // Add new item.
        presenter.addItem("5")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuMainLogout -> {
                TemplateApp.logout(this)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onItemReceived(item: Item) {
        // Requested item was received.
        Timber.d("onItemReceived { item: $item }")
    }

    override fun onItemsReceived(items: List<Item>) {
        // Requested item list received.
        Timber.d("onItemsReceived { $items }")
    }

    override fun onNewItemAdded(item: Item) {
        // Newly added item was received.
        Timber.d("onNewItemAdded { item: $item }")
    }

    override fun onFirstRunFlagReceived(isFirstRun: Boolean) {
        // Flag received from persistent storage.
        // Will be true first time app is executed, or when flag is manually switched.
        Timber.d("onFirstRunFlagReceived { isFirstRun: $isFirstRun }")

        firstRunToggleButton.isChecked = isFirstRun

        if (isFirstRun) {
            Timber.i("Welcome! Setting first run flag in 2 seconds ...")
            CoroutineScope(Dispatchers.Main).launch {
                delay(2000)
                // Set flag to false.
                presenter.setFirstRunFlag(false)
            }
        }
    }

    override fun onError(message: String) {
        Timber.w("onError { message: $message }")
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
