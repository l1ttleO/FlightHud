package ru.octol1ttle.flightassistant.api.autoflight.pitch

import dev.architectury.event.Event
import dev.architectury.event.EventFactory
import java.util.function.Consumer
import ru.octol1ttle.flightassistant.api.autoflight.FlightController

fun interface PitchControllerRegistrationCallback {
    /**
     * Called during [ru.octol1ttle.flightassistant.api.computer.Computer.invokeEvents].
     * Register your custom pitch controllers in this event using the provided function
     */
    fun register(registerFunction: Consumer<FlightController>)

    companion object {
        @JvmField
        val EVENT: Event<PitchControllerRegistrationCallback> = EventFactory.createLoop()
    }
}
