package io.houseofcode.template2

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.facebook.stetho.Stetho
import io.houseofcode.template2.data.ItemService
import io.houseofcode.template2.presentation.repository.CachedItemRepository
import io.houseofcode.template2.presentation.repository.RemoteItemRepository
import io.houseofcode.template2.presentation.room.CacheDatabase
import io.houseofcode.template2.presentation.ui.LoginActivity
import io.houseofcode.template2.presentation.util.SharedPreferencesUtil
import io.houseofcode.template2.presentation.util.isNetworkAvailable
import timber.log.Timber

class TemplateApp: Application() {

    companion object {
        // Example of simple persistent storage.
        lateinit var pref: SharedPreferencesUtil

        // Example database for caching.
        lateinit var cacheDatabase: CacheDatabase

        // Example remote item repository.
        lateinit var remoteRepository: RemoteItemRepository

        // Example cached remote item repository.
        lateinit var cachedRepository: CachedItemRepository

        fun logout(context: Context) {
            // Cancel all queued requests if client has been set.
            ItemService.client?.dispatcher?.cancelAll()

            // Clear data in persistent storage.
            pref.logout()

            // Go to login.
            context.startActivity(
                LoginActivity.newIntent(context)
            )
        }
    }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Stetho.initializeWithDefaults(this)
        }

        // Create simple persistent storage.
        pref = SharedPreferencesUtil(this)

        // Create local database for caching.
        cacheDatabase = Room
            .databaseBuilder(applicationContext, CacheDatabase::class.java, "template-cache-database")
            // Wipe and rebuild database instead of migration.
            .fallbackToDestructiveMigration()
            .build()

        // NB! Only one of the following two repositories should be necessary in most projects.
        // Create remote item repository.
        remoteRepository = RemoteItemRepository(
            this,
            this.cacheDir,
            { pref.loginToken },
            { this.isNetworkAvailable() }
        )

        // Create cached remote item repository.
        cachedRepository = CachedItemRepository(
            this,
            cacheDatabase.cacheEntryDao(),
            cacheDatabase.itemDao(),
            { pref.loginToken },
            { this.isNetworkAvailable() }
        )
    }
}