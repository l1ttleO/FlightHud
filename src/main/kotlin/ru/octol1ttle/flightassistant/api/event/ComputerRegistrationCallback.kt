package ru.octol1ttle.flightassistant.api.event

import dev.architectury.event.Event
import dev.architectury.event.EventFactory
import java.util.function.BiConsumer
import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.api.computer.Computer

fun interface ComputerRegistrationCallback {
    /**
     * Called when the client has started, after all built-in computers have been initialized.
     * Register your custom computers in this event using the provided function
     */
    fun register(registerFunction: BiConsumer<Identifier, Computer>)

    companion object {
        @JvmField
        val EVENT: Event<ComputerRegistrationCallback> = EventFactory.createLoop()
    }
}
