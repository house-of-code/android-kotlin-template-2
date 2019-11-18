package io.houseofcode.template2.data.dao

import androidx.room.*
import io.houseofcode.template2.domain.model.ItemEntity

@Dao
interface ItemDao {
    @Query("SELECT * FROM items WHERE id = :itemId")
    suspend fun getItem(itemId: String): ItemEntity?

    @Query("SELECT * FROM items")
    suspend fun getItems(): List<ItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addItem(item: ItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addItems(items: List<ItemEntity>)

    @Delete
    suspend fun removeItem(item: ItemEntity)
}