package com.example.mvrx_navigation_example

import com.airbnb.mvrx.*

class NavigationViewModel(
    initialState: State
) : BaseMvRxViewModel<NavigationViewModel.State>(initialState, true) {

    data class State(
        @PersistState val name: String = "MvRx power level: ",
        @PersistState val powerLevel: Int = 9_000
    ) : MvRxState

    companion object : MvRxViewModelFactory<NavigationViewModel, State> {
        override fun initialState(viewModelContext: ViewModelContext): State =
            State()
    }

    fun powerUp() {
        setState {
            copy(powerLevel = powerLevel + 1)
        }
    }

    fun powerDown() {
        setState {
            copy(powerLevel = powerLevel - 1)
        }
    }
}
