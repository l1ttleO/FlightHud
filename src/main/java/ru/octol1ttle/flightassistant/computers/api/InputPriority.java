package ru.octol1ttle.flightassistant.computers.api;

/**
 * Defines the priority of controller inputs. This doesn't affect the order of execution, this affects which controllers' inputs are considered.
 * For example, if a controller requests an input with the HIGHEST priority, then only HIGHEST priority inputs will be executed.
 * The rest (HIGH, NORMAL, LOW, etc.) will be discarded.
 * Note that priorities are bound to inputs, not controllers.
 */
public enum InputPriority {
    HIGHEST(0),
    HIGH(1),
    NORMAL(2),
    LOW(3),
    LOWEST(4);

    public final int numerical;

    InputPriority(int numerical) {
        this.numerical = numerical;
    }
}
