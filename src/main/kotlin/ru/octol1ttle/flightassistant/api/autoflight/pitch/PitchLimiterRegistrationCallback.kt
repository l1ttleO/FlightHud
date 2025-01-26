package ru.octol1ttle.flightassistant.api.autoflight.pitch

import dev.architectury.event.Event
import dev.architectury.event.EventFactory
import java.util.function.Consumer

fun interface PitchLimiterRegistrationCallback {
    /**
     * Called during [ru.octol1ttle.flightassistant.api.computer.Computer.invokeEvents].
     * Register your custom pitch limiters in this event using the provided function
     */
    fun register(registerFunction: Consumer<PitchLimiter>)

    companion object {
        @JvmField
        val EVENT: Event<PitchLimiterRegistrationCallback> = EventFactory.createLoop()
    }
}
