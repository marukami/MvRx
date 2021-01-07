package com.airbnb.mvrx.hellohilt.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class SingletonAssistedViewModel

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ActivityAssistedViewModel

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class FragmentAssistedViewModel
