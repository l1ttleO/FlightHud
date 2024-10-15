package ru.octol1ttle.flightassistant.api.event

import java.util.function.BiConsumer
import net.fabricmc.fabric.api.event.*
import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.api.computer.Computer

fun interface ComputerRegistrationCallback {
    /**
     * Called when the client has started, after all built-in computers have been initialized.
     * Register your custom computers in this event using the provided function
     */
    fun register(registerFunction: BiConsumer<Identifier, Computer>)

    companion object {
        val EVENT: Event<ComputerRegistrationCallback> =
            EventFactory.createArrayBacked(ComputerRegistrationCallback::class.java)
            { listeners: Array<ComputerRegistrationCallback> ->
                ComputerRegistrationCallback {
                    for (event: ComputerRegistrationCallback in listeners) {
                        event.register(it)
                    }
                }
            }
    }
}
