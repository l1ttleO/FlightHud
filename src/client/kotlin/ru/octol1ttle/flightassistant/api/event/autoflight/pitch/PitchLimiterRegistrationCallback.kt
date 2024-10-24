package ru.octol1ttle.flightassistant.api.event.autoflight.pitch

import java.util.function.Consumer
import net.fabricmc.fabric.api.event.*
import ru.octol1ttle.flightassistant.api.computer.autoflight.pitch.PitchLimiter

fun interface PitchLimiterRegistrationCallback {
    /**
     * Called during [ru.octol1ttle.flightassistant.api.computer.Computer.invokeEvents].
     * Register your custom pitch limiters in this event using the provided function
     */
    fun register(registerFunction: Consumer<PitchLimiter>)

    companion object {
        val EVENT: Event<PitchLimiterRegistrationCallback> =
            EventFactory.createArrayBacked(PitchLimiterRegistrationCallback::class.java)
            { listeners: Array<PitchLimiterRegistrationCallback> ->
                PitchLimiterRegistrationCallback {
                    for (event: PitchLimiterRegistrationCallback in listeners) {
                        event.register(it)
                    }
                }
            }
    }
}
