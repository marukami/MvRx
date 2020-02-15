@file:Suppress("ClassName")

package com.airbnb.mvrx.navigation

import androidx.annotation.RestrictTo
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import java.io.Serializable

private object UninitializedValue

/**
 * This was copied from SynchronizedLazyImpl but modified to automatically initialize in ON_CREATE.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
@SuppressWarnings("Detekt.ClassNaming")
class navigationLifecycleAwareLazy<out T>(private val owner: LifecycleOwner, initializer: () -> T) : Lazy<T>, Serializable {
    private var initializer: (() -> T)? = initializer
    @Volatile
    @SuppressWarnings("Detekt.VariableNaming")
    private var _value: Any? = UninitializedValue
    // final field is required to enable safe publication of constructed instance
    private val lock = this

    init {
        owner.lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
            fun onCreate() {
                try {
                    if (!isInitialized()) value
                } catch (_: IllegalStateException) {
                    // Fragment tried to access ViewModel before NavHostFragment was accessible
                    // attached to the activity. ViewModel will now wait for onStart.
                }
                owner.lifecycle.removeObserver(this)
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            fun onStart() {
                // If we fail here, then it means the back-stack entry was not
                // found in the backstack of the navController.
                if (!isInitialized()) value
                owner.lifecycle.removeObserver(this)
            }
        })
    }

    @Suppress("LocalVariableName")
    override val value: T
        get() {
            @SuppressWarnings("Detekt.VariableNaming")
            val _v1 = _value
            if (_v1 !== UninitializedValue) {
                @Suppress("UNCHECKED_CAST")
                return _v1 as T
            }

            return synchronized(lock) {
                @SuppressWarnings("Detekt.VariableNaming")
                val _v2 = _value
                if (_v2 !== UninitializedValue) {
                    @Suppress("UNCHECKED_CAST") (_v2 as T)
                } else {
                    val typedValue = initializer!!()
                    _value = typedValue
                    initializer = null
                    typedValue
                }
            }
        }

    override fun isInitialized(): Boolean = _value !== UninitializedValue

    override fun toString(): String = if (isInitialized()) value.toString() else "Lazy value not initialized yet."
}
