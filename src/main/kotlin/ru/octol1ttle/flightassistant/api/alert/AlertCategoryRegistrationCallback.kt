package ru.octol1ttle.flightassistant.api.alert

import dev.architectury.event.Event
import dev.architectury.event.EventFactory
import java.util.function.Consumer
import ru.octol1ttle.flightassistant.api.computer.ComputerView

fun interface AlertCategoryRegistrationCallback {
    /**
     * Called when the client has started, after all built-in alert categories have been initialized.
     * Register your custom alert categories in this event using the provided function
     */
    fun register(computers: ComputerView, registerFunction: Consumer<AlertCategory>)

    companion object {
        @JvmField
        val EVENT: Event<AlertCategoryRegistrationCallback> = EventFactory.createLoop()
    }
}
