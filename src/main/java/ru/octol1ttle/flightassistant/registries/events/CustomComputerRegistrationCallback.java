package ru.octol1ttle.flightassistant.registries.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface CustomComputerRegistrationCallback {
    Event<CustomComputerRegistrationCallback> EVENT = EventFactory.createArrayBacked(
            CustomComputerRegistrationCallback.class,
            (listeners) -> () -> {
                for (CustomComputerRegistrationCallback event : listeners) {
                    event.registerCustomComputers();
                }
            }
    );

    /**
     * Called when built-in computers have been registered. Register your custom computers in this event
     */
    void registerCustomComputers();
}
