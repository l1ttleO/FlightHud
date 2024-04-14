package ru.octol1ttle.flightassistant.registries.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface CustomHudDisplayRegistrationCallback {
    Event<CustomHudDisplayRegistrationCallback> EVENT = EventFactory.createArrayBacked(
            CustomHudDisplayRegistrationCallback.class,
            (listeners) -> () -> {
                for (CustomHudDisplayRegistrationCallback event : listeners) {
                    event.registerCustomDisplays();
                }
            }
    );

    /**
     * Called when built-in displays have been registered. Register your custom displays in this event
     */
    void registerCustomDisplays();
}
