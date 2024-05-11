package ru.octol1ttle.flightassistant.computers.api;

import org.jetbrains.annotations.Nullable;

public interface IRollController extends IComputer {
    /**
     * Gets the target roll that this controller wants
     *
     * @return a {@link ControlInput} with {@link ControlInput#target} being the target roll
     */
    @Nullable
    ControlInput getControlledRoll();

    ControllerPriority getPriority();
}