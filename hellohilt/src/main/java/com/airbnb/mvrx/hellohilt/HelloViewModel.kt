package com.airbnb.mvrx.hellohilt

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.BaseMvRxViewModel
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.ViewModelContext
import com.airbnb.mvrx.hellohilt.di.AssistedViewModelFactory
import com.airbnb.mvrx.hellohilt.di.HiltActivityMavericksEntryPoint
import com.airbnb.mvrx.hellohilt.di.HiltMavericksViewModelFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints

data class HelloState(val message: Async<String> = Uninitialized) : MvRxState

class HelloViewModel @AssistedInject constructor(
    @Assisted state: HelloState,
    private val repo: HelloRepository
) : BaseMvRxViewModel<HelloState>(state) {

    init {
        sayHello()
    }

    fun sayHello() {
        repo.sayHello().execute { copy(message = it) }
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<HelloViewModel, HelloState> {
        override fun create(state: HelloState): HelloViewModel
    }

    companion object : HiltMavericksViewModelFactory<HelloViewModel, HelloState>(HelloViewModel::class.java) {
        // Or if you're using multiple EntryPoint scopes you can override what EntryPoint is used by overriding the viewModelFactoryMap
        // override fun viewModelFactoryMap(viewModelContext: ViewModelContext): Map<Class<out MavericksViewModel<*>>, AssistedViewModelFactory<*, *>> =
        //     EntryPoints
        //         .get(viewModelContext.activity, HiltActivityMavericksEntryPoint::class.java)
        //         .viewModelFactoryContext
        //         .factories
    }
}
