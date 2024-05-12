package ru.octol1ttle.flightassistant.computers.api;

public interface ITickableComputer extends IComputer {
    void tick();

    String getId();
}
