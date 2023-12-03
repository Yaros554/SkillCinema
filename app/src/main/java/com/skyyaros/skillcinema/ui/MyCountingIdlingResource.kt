package com.skyyaros.skillcinema.ui

import androidx.annotation.VisibleForTesting
import androidx.test.espresso.idling.CountingIdlingResource
object MyCountingIdlingResource {
    private const val RESOURCE = "GLOBAL"
    @JvmField
    @VisibleForTesting
    val countingIdlingResource = CountingIdlingResource(RESOURCE)

    fun increment() {
        countingIdlingResource.increment()
    }

    fun decrement() {
        if (!countingIdlingResource.isIdleNow) {
            countingIdlingResource.decrement()
        }
    }
}