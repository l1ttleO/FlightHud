package ru.octol1ttle.flightassistant.api.autoflight.heading

import dev.architectury.event.Event
import dev.architectury.event.EventFactory
import java.util.function.Consumer

fun interface HeadingControllerRegistrationCallback {
    /**
     * Called during [ru.octol1ttle.flightassistant.api.computer.Computer.invokeEvents].
     * Register your custom heading controllers in this event using the provided function
     */
    fun register(registerFunction: Consumer<HeadingController>)

    companion object {
        @JvmField
        val EVENT: Event<HeadingControllerRegistrationCallback> = EventFactory.createLoop()
    }
}
