package ru.octol1ttle.flightassistant.computers.api;

import ru.octol1ttle.flightassistant.computers.impl.autoflight.ThrustController;

/**
 * Implementing classes should inject ThrustController and use {@link ThrustController#currentThrust} and {@link ThrustController#targetThrust} in {@link IThrustHandler#tickThrust()}.
 * Implementing this interface is required to resolve any conflicts between multiple thrust handlers.
 * In case of multiple thrust handlers being present, only the first one is registered
 */
public interface IThrustHandler extends IComputer {
    default void tickThrust() {
    }

    boolean available();

    boolean isFireworkLike();
}
