package io.houseofcode.template2.presentation.feature.main

import io.houseofcode.template2.domain.model.Item
import io.houseofcode.template2.presentation.feature.BaseActivityPresenter

interface MainContract {

    interface Presenter: BaseActivityPresenter {
        /**
         * Get item by id.
         */
        fun getItem(id: String)

        /**
         * Get all items.
         */
        fun getItems()

        /**
         * Add new item.
         */
        fun addItem(id: String)

        /**
         * Set flag for first run.
         */
        fun setFirstRunFlag(isFirstRun: Boolean)
    }

    interface View {
        /**
         * Callback for received item.
         */
        fun onItemReceived(item: Item)

        /**
         * Callback for received item list.
         */
        fun onItemsReceived(items: List<Item>)

        /**
         * Callback for newly created item.
         */
        fun onNewItemAdded(item: Item)

        /**
         * Callback on first run flag from persistent storage.
         */
        fun onFirstRunFlagReceived(isFirstRun: Boolean)

        /**
         * An abstract error received.
         */
        fun onError(message: String)
    }
}