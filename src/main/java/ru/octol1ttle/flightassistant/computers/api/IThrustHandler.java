package ru.octol1ttle.flightassistant.computers.api;

/**
 * Implementing classes should use {@link IThrustHandler#tickThrust(float)}.
 * Implementing this interface is required to resolve any conflicts between multiple thrust handlers.
 * In case of multiple thrust handlers being present, only the first one is registered
 */
public interface IThrustHandler extends IComputer {
    default void tickThrust(float current) {
    }

    boolean enabled();

    boolean canBeUsed();
    boolean isFireworkLike();
    boolean supportsReverseThrust();
}
