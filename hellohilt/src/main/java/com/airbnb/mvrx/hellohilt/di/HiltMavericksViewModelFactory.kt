package com.airbnb.mvrx.hellohilt.di

import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.ViewModelContext
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.components.SingletonComponent
import java.util.AbstractMap
import javax.inject.Inject
import javax.inject.Qualifier
import kotlin.reflect.KClass

/**
 * A [MavericksViewModelFactory] which makes it easy to create instances of a ViewModel
 * using its AssistedInject Factory. This class should be implemented by the companion object
 * of every ViewModel which uses AssistedInject.
 *
 * @param viewModelClass The [Class] of the ViewModel being requested for creation
 *
 * This class accesses the map of [AssistedViewModelFactory]s from [SingletonComponent] via an [EntryPoint]
 * and uses it to retrieve the requested ViewModel's factory class. It then creates an instance of this ViewModel
 * using the retrieved factory and returns it.
 *
 * You can always change the [SingletonComponent] to [ActivityComponent] if you want, and you can create multiple [EntryPoint]s if you need to
 * scope the ViewModel factories.
 *
 * Example:
 *
 * class MyViewModel @AssistedInject constructor(...): BaseViewModel<MyState>(...) {
 *
 *   @AssistedInject.Factory
 *   interface Factory : AssistedViewModelFactory<MyViewModel, MyState> {
 *     ...
 *   }
 *
 *   companion object : HiltMavericksViewModelFactory<MyViewModel, MyState>(MyViewModel::class.java)
 *
 * }
 *
 * If you need multiple EntryPoints for different scopes then you can override the `viewModelFactoryMap`
 * and EntryPoints.get(scopeContext, scopeEntryPoint::class).viewModelFactoryContext
 *
 */
abstract class HiltMavericksViewModelFactory<VM : MavericksViewModel<S>, S : MvRxState>(
    private val viewModelClass: Class<out MavericksViewModel<S>>
) : MavericksViewModelFactory<VM, S> {

    protected open fun viewModelFactoryMap(viewModelContext: ViewModelContext): Map<Class<out MavericksViewModel<*>>, AssistedViewModelFactory<*, *>> =
        EntryPoints
            .get(viewModelContext.activity.applicationContext, HiltMavericksEntryPoint::class.java)
            .viewModelFactoryContext
            .factories

    override fun create(viewModelContext: ViewModelContext, state: S): VM? {
        val viewModelFactoryMap = viewModelFactoryMap(viewModelContext)
        val viewModelFactory = viewModelFactoryMap[viewModelClass]

        @Suppress("UNCHECKED_CAST")
        val castedViewModelFactory = viewModelFactory as? AssistedViewModelFactory<VM, S>
        val viewModel = castedViewModelFactory?.create(state)
        return viewModel as VM
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface HiltMavericksEntryPoint {
    val viewModelFactoryContext: SingletonViewModelFactoryContext
}

@EntryPoint
@InstallIn(ActivityComponent::class)
interface HiltActivityMavericksEntryPoint {
    val viewModelFactoryContext: ActivityViewModelFactoryContext
}

@EntryPoint
@InstallIn(FragmentComponent::class)
interface HiltFragmentMavericksEntryPoint {
    val viewModelFactoryContext: FragmentViewModelFactoryContext
}
