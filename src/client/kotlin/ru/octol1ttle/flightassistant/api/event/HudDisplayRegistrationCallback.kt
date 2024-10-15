package ru.octol1ttle.flightassistant.api.event

import java.util.function.BiConsumer
import net.fabricmc.fabric.api.event.*
import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.api.display.Display

fun interface HudDisplayRegistrationCallback {
    /**
     * Called when the client has started, after all built-in displays have been initialized.
     * Register your custom displays in this event using the provided function
     */
    fun register(registerFunction: BiConsumer<Identifier, Display>)

    companion object {
        val EVENT: Event<HudDisplayRegistrationCallback> =
            EventFactory.createArrayBacked(HudDisplayRegistrationCallback::class.java)
            { listeners: Array<HudDisplayRegistrationCallback> ->
                HudDisplayRegistrationCallback {
                    for (event: HudDisplayRegistrationCallback in listeners) {
                        event.register(it)
                    }
                }
            }
    }
}
