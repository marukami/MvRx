package com.airbnb.mvrx.hellohilt.di

import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.hellohilt.HelloViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import dagger.multibindings.IntoSet
import kotlin.reflect.KClass

/**
 * The InstallIn SingletonComponent scope must match the context scope used in [HiltMavericksViewModelFactory]
 *
 * If you want an Activity or Fragment scope
 */
@Module
@InstallIn(SingletonComponent::class)
interface AppModule {
    @Binds
    @IntoMap
    @ViewModelKey(HelloViewModel::class)
    fun helloViewModelFactory(factory: HelloViewModel.Factory): AssistedViewModelFactory<*, *>
}

@Module
@InstallIn(ActivityComponent::class)
object ActivityModule {

    @Provides
    @IntoSet
    @ActivityAssistedViewModel
    fun activityHelloViewModelFactory(factory: HelloViewModel.Factory): Pair<KClass<out MavericksViewModel<*>>, AssistedViewModelFactory<*, *>> =
        HelloViewModel::class to factory

    @Provides
    fun singletonAssistedViewModelFactories(
        @ActivityAssistedViewModel factories: ViewModelFactorySet
    ): ActivityViewModelFactoryContext =
        ActivityViewModelFactoryContext(factories)
}

@Module
@InstallIn(FragmentComponent::class)
object FragmentModule {

    @Provides
    @IntoSet
    @FragmentAssistedViewModel
    fun fragmentHelloViewModelFactory(factory: HelloViewModel.Factory): Pair<KClass<out MavericksViewModel<*>>, AssistedViewModelFactory<*, *>> =
        HelloViewModel::class to factory

    @Provides
    fun singletonAssistedViewModelFactories(
        @FragmentAssistedViewModel factories: ViewModelFactorySet
    ): FragmentViewModelFactoryContext =
        FragmentViewModelFactoryContext(factories)
}
