package ru.octol1ttle.flightassistant.computers.api;

import org.jetbrains.annotations.Nullable;

public interface IHeadingController extends IComputer {
    /**
     * Gets the target heading that this controller wants
     * @return a {@link ControlInput} with {@link ControlInput#target} being the target heading
     */
    @Nullable
    ControlInput getControlledHeading();

    ControllerPriority getPriority();
}
