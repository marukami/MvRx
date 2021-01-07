package com.airbnb.mvrx.hellohilt.di

import com.airbnb.mvrx.MavericksViewModel
import javax.inject.Inject
import kotlin.reflect.KClass

// MutableSet must be used cause Set does not work in a @Provides parameter argument.
typealias ViewModelFactorySet = MutableSet<Pair<KClass<out MavericksViewModel<*>>, AssistedViewModelFactory<*, *>>>

abstract class ViewModelFactoryContext(factorySet: ViewModelFactorySet = mutableSetOf()) {
    open val factories: Map<Class<out MavericksViewModel<*>>, AssistedViewModelFactory<*, *>> =
        factorySet.map { it.first.java to it.second }.toMap()
}

class SingletonViewModelFactoryContext @Inject constructor(
    override val factories: Map<Class<out MavericksViewModel<*>>, AssistedViewModelFactory<*, *>>
) : ViewModelFactoryContext()

class ActivityViewModelFactoryContext(factorySet: ViewModelFactorySet) : ViewModelFactoryContext(factorySet)
class FragmentViewModelFactoryContext(factorySet: ViewModelFactorySet) : ViewModelFactoryContext(factorySet)
