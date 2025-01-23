package ru.octol1ttle.flightassistant.api.event.autoflight.thrust

import dev.architectury.event.Event
import dev.architectury.event.EventFactory

fun interface ThrustChangeCallback {
    /**
     * Called when the current thrust has been changed.
     */
    fun onThrustChange(oldThrust: Float, newThrust: Float, automatic: Boolean)

    companion object {
        @JvmField
        val EVENT: Event<ThrustChangeCallback> = EventFactory.createLoop()
    }
}
