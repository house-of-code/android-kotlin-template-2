package io.houseofcode.template2.domain.usecase

import io.houseofcode.template2.domain.interactor.GenericInteractor
import io.houseofcode.template2.domain.model.Item
import java.util.*

/**
 * Generate item with provided id, to showcase how a generic interactor can be used.
 */
class GenerateItemUseCase: GenericInteractor<Item, GenerateItemUseCase.Params>() {

    override fun build(params: Params?): Item {
        val state = checkNotNull(params) { "Params must not be null" }

        return Item(state.itemId, "Item ${state.itemId}", Date())
    }

    data class Params(val itemId: String)
}