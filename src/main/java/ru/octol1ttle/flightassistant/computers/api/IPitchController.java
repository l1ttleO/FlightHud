package ru.octol1ttle.flightassistant.computers.api;

import org.jetbrains.annotations.Nullable;

public interface IPitchController extends IComputer {
    /**
     * Gets the target pitch that this controller wants
     * @return a {@link ControlInput} with {@link ControlInput#target()} being the target pitch
     */
    @Nullable
    ControlInput getPitchInput();
}
