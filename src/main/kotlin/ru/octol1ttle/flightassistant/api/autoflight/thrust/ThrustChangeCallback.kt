package ru.octol1ttle.flightassistant.api.autoflight.thrust

import dev.architectury.event.Event
import dev.architectury.event.EventFactory
import ru.octol1ttle.flightassistant.api.autoflight.ControlInput

fun interface ThrustChangeCallback {
    /**
     * Called when the current thrust has been changed.
     */
    fun onThrustChange(oldThrust: Float, newThrust: Float, input: ControlInput?)

    companion object {
        @JvmField
        val EVENT: Event<ThrustChangeCallback> = EventFactory.createLoop()
    }
}
