package com.airbnb.mvrx.navigation

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

class NavTestLifecycleOwner : LifecycleOwner {

    private val _lifecycle = LifecycleRegistry(this)

    override fun getLifecycle(): LifecycleRegistry = _lifecycle
}
