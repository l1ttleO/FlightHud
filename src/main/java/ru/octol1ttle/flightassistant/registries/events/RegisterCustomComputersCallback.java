package ru.octol1ttle.flightassistant.registries.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

@FunctionalInterface
public interface RegisterCustomComputersCallback {
    Event<RegisterCustomComputersCallback> EVENT = EventFactory.createArrayBacked(
            RegisterCustomComputersCallback.class,
            (listeners) -> () -> {
                for (RegisterCustomComputersCallback event : listeners) {
                    event.registerCustomComputers();
                }
            }
    );

    /**
     * Called when built-in computers have been registered. Register your custom computers in this event
     */
    void registerCustomComputers();
}
