package io.houseofcode.template2.domain.usecase

import android.content.SharedPreferences
import io.houseofcode.template2.domain.interactor.CovariantInteractor
import io.houseofcode.template2.domain.live.SharedPreferenceBooleanLiveData

/**
 * Use case for retrieving value from SharedPreferences wrapped in custom LiveData.
 */
class GetFirstRunFlagUseCase(private val sharedPreferences: SharedPreferences): CovariantInteractor<SharedPreferenceBooleanLiveData, Void>() {

    private companion object {
        private const val PREF_FIRST_LAUNCH = "first_launch"
    }

    override fun build(params: Void?): SharedPreferenceBooleanLiveData {
        return SharedPreferenceBooleanLiveData(sharedPreferences, PREF_FIRST_LAUNCH, true)
    }

    // No post processing is necessary in this use case.
    override fun process(data: SharedPreferenceBooleanLiveData): SharedPreferenceBooleanLiveData = data
}