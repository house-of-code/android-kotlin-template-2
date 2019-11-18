package io.houseofcode.template2.domain

import io.houseofcode.template2.domain.model.Item
import io.houseofcode.template2.domain.model.ItemEntity

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