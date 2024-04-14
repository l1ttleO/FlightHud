package ru.octol1ttle.flightassistant.registries.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import ru.octol1ttle.flightassistant.computers.api.IComputer;

public interface ComputerRegisteredCallback {
    Event<ComputerRegisteredCallback> EVENT = EventFactory.createArrayBacked(
            ComputerRegisteredCallback.class,
            (listeners) -> (computer) -> {
                for (ComputerRegisteredCallback event : listeners) {
                    event.onComputerRegistered(computer);
                }
            }
    );

    /**
     * Called after a computer has been registered.
     *
     * @param computer the {@link IComputer} instance
     */
    void onComputerRegistered(IComputer computer);
}
