package ru.octol1ttle.flightassistant.api.event

import java.util.function.Consumer
import net.fabricmc.fabric.api.event.*
import ru.octol1ttle.flightassistant.api.alert.AlertCategory

fun interface AlertCategoryRegistrationCallback {
    /**
     * Called when the client has started, after all built-in alert categories have been initialized.
     * Register your custom alert categories in this event using the provided function
     */
    fun register(registerFunction: Consumer<AlertCategory>)

    companion object {
        val EVENT: Event<AlertCategoryRegistrationCallback> =
            EventFactory.createArrayBacked(AlertCategoryRegistrationCallback::class.java)
            { listeners: Array<AlertCategoryRegistrationCallback> ->
                AlertCategoryRegistrationCallback {
                    for (event: AlertCategoryRegistrationCallback in listeners) {
                        event.register(it)
                    }
                }
            }
    }
}
