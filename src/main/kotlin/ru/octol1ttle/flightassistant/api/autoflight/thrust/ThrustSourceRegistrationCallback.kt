package ru.octol1ttle.flightassistant.api.autoflight.thrust

import dev.architectury.event.Event
import dev.architectury.event.EventFactory
import java.util.function.Consumer

fun interface ThrustSourceRegistrationCallback {
    /**
     * Called during [ru.octol1ttle.flightassistant.api.computer.Computer.invokeEvents].
     * Register your custom thrust sources in this event using the provided function
     */
    fun register(registerFunction: Consumer<ThrustSource>)

    companion object {
        @JvmField
        val EVENT: Event<ThrustSourceRegistrationCallback> = EventFactory.createLoop()
    }
}
