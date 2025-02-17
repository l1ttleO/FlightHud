package ru.octol1ttle.flightassistant.api.autoflight.thrust

import dev.architectury.event.Event
import dev.architectury.event.EventFactory
import java.util.function.Consumer
import ru.octol1ttle.flightassistant.api.autoflight.FlightController

fun interface ThrustControllerRegistrationCallback {
    /**
     * Called during [ru.octol1ttle.flightassistant.api.computer.Computer.invokeEvents].
     * Register your custom thrust controllers in this event using the provided function
     */
    fun register(registerFunction: Consumer<FlightController>)

    companion object {
        @JvmField
        val EVENT: Event<ThrustControllerRegistrationCallback> = EventFactory.createLoop()
    }
}
