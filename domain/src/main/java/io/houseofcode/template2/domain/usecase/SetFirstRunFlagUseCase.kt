package io.houseofcode.template2.domain.usecase

import android.content.SharedPreferences
import io.houseofcode.template2.domain.live.SharedPreferenceBooleanLiveData

/**
 * Use case for setting value in SharedPreferences.
 * This use case returns the preference on execution, though it might not be used,
 * so we can pass the data through the #build and #process methods, similar to the LiveDataInteractor.
 */
class SetFirstRunFlagUseCase(private val sharedPreferences: SharedPreferences): CovariantInteractor<SharedPreferenceBooleanLiveData, SetFirstRunFlagUseCase.Params>() {

    private companion object {
        private const val PREF_FIRST_LAUNCH = "first_launch"
    }

    override fun build(params: Params?): SharedPreferenceBooleanLiveData {
        val state = checkNotNull(params) { "Params must not be null" }

        // Set value and return preference as LiveData if needed by consumer.
        return SharedPreferenceBooleanLiveData(sharedPreferences, PREF_FIRST_LAUNCH, true).apply {
            setValueInPreferences(PREF_FIRST_LAUNCH, state.isFirstRun)
        }
    }

    // No post processing is necessary in this use case.
    override fun process(data: SharedPreferenceBooleanLiveData): SharedPreferenceBooleanLiveData = data

    data class Params(val isFirstRun: Boolean)
}