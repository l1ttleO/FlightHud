package ru.octol1ttle.flightassistant.api.event

import dev.architectury.event.Event
import dev.architectury.event.EventFactory
import java.util.function.BiConsumer
import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.api.display.Display

fun interface HudDisplayRegistrationCallback {
    /**
     * Called when the client has started, after all built-in displays have been initialized.
     * Register your custom displays in this event using the provided function
     */
    fun register(registerFunction: BiConsumer<Identifier, Display>)

    companion object {
        @JvmField
        val EVENT: Event<HudDisplayRegistrationCallback> = EventFactory.createLoop()
    }
}
