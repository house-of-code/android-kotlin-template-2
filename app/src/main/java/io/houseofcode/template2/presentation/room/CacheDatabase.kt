package io.houseofcode.template2.presentation.room

import androidx.room.*
import io.houseofcode.template2.data.dao.CacheEntryDao
import io.houseofcode.template2.data.dao.ItemDao
import io.houseofcode.template2.data.model.CacheEntry
import io.houseofcode.template2.data.model.ItemEntity

@Database(version = 1, entities = [ ItemEntity::class, CacheEntry::class ])
@TypeConverters(Converters::class)
abstract class CacheDatabase: RoomDatabase() {
    abstract fun cacheEntryDao(): CacheEntryDao
    abstract fun itemDao(): ItemDao
}