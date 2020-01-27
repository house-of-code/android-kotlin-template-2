package io.houseofcode.template2.data.model

import androidx.room.ColumnInfo
import java.util.*

/**
 * Super class for cached entities that adds cache time to a data class.
 */
abstract class CacheEntity {
    @ColumnInfo(name = "cached_at") var cachedAt: Date = Date()
}