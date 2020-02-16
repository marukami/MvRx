package com.airbnb.mvrx.navigation

import androidx.lifecycle.Lifecycle
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.lang.IllegalStateException



@RunWith(JUnit4::class)
class NavigationLifecycleAwareLazyTest {

    private lateinit var owner: NavTestLifecycleOwner
    private lateinit var lazyProp: navigationLifecycleAwareLazy<String>

    @Before
    fun setup() {
        owner = NavTestLifecycleOwner()
        lazyProp = navigationLifecycleAwareLazy(owner) { "Hello World" }
    }

    @Test
    fun testNotInitializedBeforeOnCreate() {
        owner.lifecycle.currentState = Lifecycle.State.INITIALIZED
        assertFalse(lazyProp.isInitialized())
    }

    @Test
    fun testNotInitializedAfterOnCreate() {
        owner.lifecycle.currentState = Lifecycle.State.INITIALIZED
        owner.lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        assertTrue(lazyProp.isInitialized())
    }

    @Test
    fun testInitializedIfAlreadyStarted() {
        owner.lifecycle.currentState = Lifecycle.State.STARTED
        lazyProp = navigationLifecycleAwareLazy(owner) { "Hello World" }
        assertTrue(lazyProp.isInitialized())
    }

    @Test
    fun testNotInitializedIfErrorIsThrown() {
        owner.lifecycle.currentState = Lifecycle.State.STARTED
        lazyProp = navigationLifecycleAwareLazy(owner) { throw IllegalStateException() }
        assertFalse(lazyProp.isInitialized())
    }

    @Test
    fun testInitializedAfterOnCreateThrowsButOnStartSucceeds() {
        owner.lifecycle.currentState = Lifecycle.State.CREATED
        lazyProp = navigationLifecycleAwareLazy(owner) {
            if(owner.lifecycle.currentState == Lifecycle.State.CREATED) {
                throw IllegalStateException()
            } else {
                "Hello World"
            }
        }
        owner.lifecycle.currentState = Lifecycle.State.STARTED
        lazyProp.lifecycleObserver.onStart(owner)
        assertTrue(lazyProp.isInitialized())
    }
}

