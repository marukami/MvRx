@file:Suppress("ClassName")

package com.airbnb.mvrx.navigation

import androidx.annotation.RestrictTo
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import java.io.Serializable

private object UninitializedValue

/**
 * This was copied from SynchronizedLazyImpl but modified to automatically initialize in ON_CREATE.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
@SuppressWarnings("Detekt.ClassNaming")
class navigationLifecycleAwareLazy<out T>(owner: LifecycleOwner, initializer: () -> T) : Lazy<T>, Serializable {
    private var initializer: (() -> T)? = initializer
    @Volatile
    @SuppressWarnings("Detekt.VariableNaming")
    private var _value: Any? = UninitializedValue
    // final field is required to enable safe publication of constructed instance
    private val lock = this

    @VisibleForTesting
    internal val lifecycleObserver: DefaultLifecycleObserver =
            object : DefaultLifecycleObserver {
                /**
                 * Try eagerly execute the initializer and remove our self as an observer.
                 *
                 * During re-configuration the navigation host will not yet available. So, we ignore the
                 * error for now and wait for either the onStart or the view to manually trigger the initializer.
                 *
                 */
                override fun onCreate(owner: LifecycleOwner) {
                    super.onCreate(owner)
                    try {
                        if (!isInitialized()) value
                    } catch (_: IllegalStateException) {
                        // Ignore the IllegalStateException initializer throws when there is no navController.
                        // Fallback to onStart of view triggering the initializer.
                    }
                    owner.lifecycle.removeObserver(this)
                }

                /**
                 * If the view did not by this point trigger the initializer we should do so now.
                 * Once the initializer is executed here or by the view we remove our self as an observer.
                 */
                override fun onStart(owner: LifecycleOwner) {
                    super.onStart(owner)
                    // If we fail here, then it means the back-stack entry was not
                    // found in the backstack of the navController.
                    if (!isInitialized()) value
                    owner.lifecycle.removeObserver(this)
                }
            }

    init {
        owner.lifecycle.addObserver(lifecycleObserver)
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
