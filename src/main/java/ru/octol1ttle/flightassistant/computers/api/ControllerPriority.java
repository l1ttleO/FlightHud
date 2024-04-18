package ru.octol1ttle.flightassistant.computers.api;

public enum ControllerPriority {
    HIGHEST(0),
    HIGH(1),
    NORMAL(2),
    LOW(3),
    LOWEST(4);

    public final int priority;

    ControllerPriority(int priority) {
        this.priority = priority;
    }
}
