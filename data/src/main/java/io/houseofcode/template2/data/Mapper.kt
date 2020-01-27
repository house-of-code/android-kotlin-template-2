package io.houseofcode.template2.data

import io.houseofcode.template2.data.model.ItemEntity
import io.houseofcode.template2.domain.model.Item

/**
 * Map from database entry to item model.
 */
fun ItemEntity.mapToItem(): Item {
    return Item(
        this.id,
        this.title,
        this.createdAt
    )
}

/**
 * Map from item model to database entry.
 */
fun Item.mapToEntity(): ItemEntity {
    return ItemEntity(
        this.id,
        this.title,
        this.createdAt
    )
}