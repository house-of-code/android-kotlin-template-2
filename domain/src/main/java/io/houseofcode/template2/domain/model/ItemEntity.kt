package io.houseofcode.template2.domain.model

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * Item as database entity.
 * This entity inherits from the CacheEntity super class to add a cache timestamp on the entity,
 * which is not absolutely necessary for the caching to work. You can remove CacheEntity from this
 * class if you don't want to store it in your cache.
 */
@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey @NonNull var id: String,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "created_at") var createdAt: Date
): CacheEntity()