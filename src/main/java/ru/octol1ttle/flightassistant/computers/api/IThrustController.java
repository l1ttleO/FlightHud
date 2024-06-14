package ru.octol1ttle.flightassistant.computers.api;

public interface IThrustController extends IComputer {
    /**
     * Gets the target thrust that this controller wants
     *
     * @return a {@link ThrustControlInput} with {@link ThrustControlInput#target()} being the target thrust
     */
    ThrustControlInput getThrustInput();
}
