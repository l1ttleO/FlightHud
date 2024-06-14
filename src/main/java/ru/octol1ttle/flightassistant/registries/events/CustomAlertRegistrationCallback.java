package ru.octol1ttle.flightassistant.registries.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

@FunctionalInterface
public interface CustomAlertRegistrationCallback {
    Event<CustomAlertRegistrationCallback> EVENT = EventFactory.createArrayBacked(
            CustomAlertRegistrationCallback.class,
            (listeners) -> () -> {
                for (CustomAlertRegistrationCallback event : listeners) {
                    event.registerCustomAlerts();
                }
            }
    );

    /**
     * Called when built-in alerts have been registered. Register your custom alerts in this event
     */
    void registerCustomAlerts();
}
