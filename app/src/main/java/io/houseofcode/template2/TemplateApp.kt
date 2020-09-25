package io.houseofcode.template2

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.room.Room
import io.houseofcode.template2.data.ItemService
import io.houseofcode.template2.presentation.helper.FlipperClientInitializer
import io.houseofcode.template2.presentation.repository.CachedItemRepository
import io.houseofcode.template2.presentation.repository.RemoteItemRepository
import io.houseofcode.template2.presentation.room.CacheDatabase
import io.houseofcode.template2.presentation.ui.LoginActivity
import io.houseofcode.template2.presentation.util.isNetworkAvailable
import io.houseofcode.template2.presentation.viewmodel.SharedPreferencesViewModel
import okhttp3.OkHttpClient
import timber.log.Timber

class TemplateApp: Application(), ViewModelStoreOwner {

    companion object {
        // Getter for instance of application class.
        lateinit var instance: TemplateApp
            private set

        // Example database for caching.
        lateinit var cacheDatabase: CacheDatabase

        // Example remote item repository with default caching.
        private lateinit var cachedOkHttpClient: OkHttpClient
        lateinit var remoteRepository: RemoteItemRepository

        // Example cached remote item repository with custom caching.
        private lateinit var noCacheOkHttpClient: OkHttpClient
        lateinit var cachedRepository: CachedItemRepository
    }

    // View model for SharedPreferences.
    private lateinit var sharedPreferencesViewModel: SharedPreferencesViewModel
    // Store/container for view models, used to store SharedPreference view model.
    private val appViewModelStore: ViewModelStore by lazy {
        ViewModelStore()
    }

    override fun getViewModelStore(): ViewModelStore {
        return appViewModelStore
    }

    override fun onCreate() {
        super.onCreate()

        // Set instance, so we can access it from companion object.
        instance = this

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        // Setup Flipper debugging.
        val flipperInitializer = FlipperClientInitializer()
        flipperInitializer.start(this)

        // Create view model for SharedPreferences, used within the application class.
        sharedPreferencesViewModel = ViewModelProvider(this, SharedPreferencesViewModel.SharedPreferencesViewModelFactory(this))
            .get(SharedPreferencesViewModel::class.java)

        // Create local database for caching.
        cacheDatabase = Room
            .databaseBuilder(applicationContext, CacheDatabase::class.java, "template-cache-database")
            // Wipe and rebuild database instead of migration.
            .fallbackToDestructiveMigration()
            .build()

        // NB! Only one of the following two repositories (remoteRepository or cachedRepository) should be necessary in most projects.
        // This is the only place where we should access the data layer from the presentation layer.

        // Create OkHttp client with default caching.
        cachedOkHttpClient = ItemService.createOkHttpClient(
            this.cacheDir,
            flipperInitializer.getDebugNetworkInterceptor(),
            { sharedPreferencesViewModel.getLoginToken() },
            { this.isNetworkAvailable() },
            getString(R.string.error_no_network)
        ).build()
        // Create remote item repository.
        remoteRepository = RemoteItemRepository(
            this,
            ItemService.createService(cachedOkHttpClient),
            sharedPreferencesViewModel
        )

        // Create OkHttp client with default caching disabled.
        noCacheOkHttpClient = ItemService.createOkHttpClient(
            null,
            flipperInitializer.getDebugNetworkInterceptor(),
            { sharedPreferencesViewModel.getLoginToken() },
            { this.isNetworkAvailable() },
            getString(R.string.error_no_network)
        ).build()
        // Create cached remote item repository.
        cachedRepository = CachedItemRepository(
            this,
            cacheDatabase.cacheEntryDao(),
            cacheDatabase.itemDao(),
            ItemService.createService(noCacheOkHttpClient),
            sharedPreferencesViewModel
        )
    }

    /**
     * Log out user and go to login.
     */
    fun logout(context: Context) {
        // Cancel all queued requests if client has been set.
        cachedOkHttpClient.dispatcher.cancelAll()
        noCacheOkHttpClient.dispatcher.cancelAll()

        // Clear data in persistent storage.
        sharedPreferencesViewModel.logout()

        // Go to login.
        startActivity(
            LoginActivity.newIntent(context)
        )
    }
}