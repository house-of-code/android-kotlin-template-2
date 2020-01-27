package io.houseofcode.template2.data.model

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "cache",
    indices = [
        Index(value = [ "cache_key" ], unique = true)
    ]
)
data class CacheEntry(
    @PrimaryKey @NonNull @ColumnInfo(name = "cache_key") var cacheKey: String,
    @ColumnInfo(name = "cached_at") var cachedAt: Date = Date()
)