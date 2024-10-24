package ru.octol1ttle.flightassistant.api.event.autoflight.thrust

import java.util.function.Consumer
import net.fabricmc.fabric.api.event.*
import ru.octol1ttle.flightassistant.api.computer.autoflight.thrust.ThrustSource

fun interface ThrustSourceRegistrationCallback {
    /**
     * Called during [ru.octol1ttle.flightassistant.api.computer.Computer.invokeEvents].
     * Register your custom thrust sources in this event using the provided function
     */
    fun register(registerFunction: Consumer<ThrustSource>)

    companion object {
        val EVENT: Event<ThrustSourceRegistrationCallback> =
            EventFactory.createArrayBacked(ThrustSourceRegistrationCallback::class.java)
            { listeners: Array<ThrustSourceRegistrationCallback> ->
                ThrustSourceRegistrationCallback {
                    for (event: ThrustSourceRegistrationCallback in listeners) {
                        event.register(it)
                    }
                }
            }
    }
}
