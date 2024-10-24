package ru.octol1ttle.flightassistant.api.event.autoflight.pitch

import java.util.function.Consumer
import net.fabricmc.fabric.api.event.*
import ru.octol1ttle.flightassistant.api.computer.autoflight.pitch.PitchController

fun interface PitchControllerRegistrationCallback {
    /**
     * Called during [ru.octol1ttle.flightassistant.api.computer.Computer.invokeEvents].
     * Register your custom pitch controllers in this event using the provided function
     */
    fun register(registerFunction: Consumer<PitchController>)

    companion object {
        val EVENT: Event<PitchControllerRegistrationCallback> =
            EventFactory.createArrayBacked(PitchControllerRegistrationCallback::class.java)
            { listeners: Array<PitchControllerRegistrationCallback> ->
                PitchControllerRegistrationCallback {
                    for (event: PitchControllerRegistrationCallback in listeners) {
                        event.register(it)
                    }
                }
            }
    }
}
