package ru.octol1ttle.flightassistant.api.autoflight.roll

import dev.architectury.event.Event
import dev.architectury.event.EventFactory
import java.util.function.Consumer

fun interface RollSourceRegistrationCallback {
    /**
     * Called during [ru.octol1ttle.flightassistant.api.computer.Computer.invokeEvents].
     * Register your custom roll sources in this event using the provided function
     */
    fun register(registerFunction: Consumer<RollSource>)

    companion object {
        @JvmField
        val EVENT: Event<RollSourceRegistrationCallback> = EventFactory.createLoop()
    }
}
