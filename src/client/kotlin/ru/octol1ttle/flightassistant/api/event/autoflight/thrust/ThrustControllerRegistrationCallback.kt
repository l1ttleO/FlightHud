package ru.octol1ttle.flightassistant.api.event.autoflight.thrust

import java.util.function.Consumer
import net.fabricmc.fabric.api.event.*
import ru.octol1ttle.flightassistant.api.computer.autoflight.thrust.ThrustController

fun interface ThrustControllerRegistrationCallback {
    /**
     * Called during [ru.octol1ttle.flightassistant.api.computer.Computer.invokeEvents].
     * Register your custom thrust controllers in this event using the provided function
     */
    fun register(registerFunction: Consumer<ThrustController>)

    companion object {
        val EVENT: Event<ThrustControllerRegistrationCallback> =
            EventFactory.createArrayBacked(ThrustControllerRegistrationCallback::class.java)
            { listeners: Array<ThrustControllerRegistrationCallback> ->
                ThrustControllerRegistrationCallback {
                    for (event: ThrustControllerRegistrationCallback in listeners) {
                        event.register(it)
                    }
                }
            }
    }
}
