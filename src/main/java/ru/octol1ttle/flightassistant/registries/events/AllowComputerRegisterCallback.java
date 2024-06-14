package ru.octol1ttle.flightassistant.registries.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import ru.octol1ttle.flightassistant.computers.api.IComputer;

@FunctionalInterface
public interface AllowComputerRegisterCallback {
    Event<AllowComputerRegisterCallback> EVENT = EventFactory.createArrayBacked(
            AllowComputerRegisterCallback.class,
            (listeners) -> (computer) -> {
                for (AllowComputerRegisterCallback event : listeners) {
                    if (!event.allowRegister(computer)) {
                        return false;
                    }
                }
                return true;
            }
    );

    /**
     * Called when a computer is about to be registered.
     *
     * @param computer the {@link IComputer} instance
     * @return whether the computer should be registered
     */
    boolean allowRegister(IComputer computer);
}
