package ru.octol1ttle.flightassistant.api.computer

import dev.architectury.event.Event
import dev.architectury.event.EventFactory
import ru.octol1ttle.flightassistant.api.SystemController

fun interface ComputerRegistrationCallback {
    /**
     * Called when the client has started, after all built-in computers have been initialized.
     * Register your custom computers in this event using the provided function
     */
    fun register(controller: SystemController<Computer>)

    companion object {
        @JvmField
        val EVENT: Event<ComputerRegistrationCallback> = EventFactory.createLoop()
    }
}
