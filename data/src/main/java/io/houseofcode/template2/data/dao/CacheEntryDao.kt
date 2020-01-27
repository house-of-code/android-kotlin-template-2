package io.houseofcode.template2.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.houseofcode.template2.data.model.CacheEntry

/**
 * Data access object for cache entries.
 * A cache entry stores when a specific cache key was last cached.
 */
@Dao
interface CacheEntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCacheEntry(cacheEntry: CacheEntry)

    @Query("SELECT * FROM cache WHERE cache_key = :key")
    suspend fun getCacheEntry(key: String): CacheEntry?
}