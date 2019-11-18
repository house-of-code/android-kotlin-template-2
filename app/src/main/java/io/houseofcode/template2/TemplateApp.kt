package io.houseofcode.template2

import android.app.Application
import androidx.room.Room
import io.houseofcode.template2.data.ItemService
import io.houseofcode.template2.presentation.repository.CachedItemRepository
import io.houseofcode.template2.presentation.repository.RemoteItemRepository
import io.houseofcode.template2.presentation.room.CacheDatabase
import io.houseofcode.template2.presentation.util.isNetworkAvailable
import timber.log.Timber

class TemplateApp: Application() {

    companion object {

        // Example database for caching.
        lateinit var cacheDatabase: CacheDatabase

        // Examle cached remote item repository.
        lateinit var cachedRepository: CachedItemRepository

        // Example remote item repository.
        lateinit var remoteRepository: RemoteItemRepository
    }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        // Create local database for caching.
        cacheDatabase = Room
            .databaseBuilder(applicationContext, CacheDatabase::class.java, "template-cache-database")
            // Wipe and rebuild database instead of migration.
            .fallbackToDestructiveMigration()
            .build()

        // Create cached remote item repository.
        cachedRepository = CachedItemRepository(
            cacheDatabase.cacheEntryDao(),
            cacheDatabase.itemDao(),
            // No cache file is provided, as we want to handle caching our self.
            ItemService.create(BuildConfig.DEBUG, null) {
                this.isNetworkAvailable()
            }
        )

        // Create remote item repository.
        remoteRepository = RemoteItemRepository(BuildConfig.DEBUG, this.cacheDir) {
            this.isNetworkAvailable()
        }
    }
}