package ru.octol1ttle.flightassistant.api.computer

/**
 * A class responsible for computing data and providing it to [ru.octol1ttle.flightassistant.api.display.Display]s and [ru.octol1ttle.flightassistant.api.alert.Alert]s
 */
abstract class Computer {
    internal var faulted: Boolean = false

    /**
     * Called once per world render
     *
     * If this method throws an exception or error, it is caught and the computer is considered "faulted".
     * It won't be ticked and an alert about the issue will be displayed
     *
     * @param computers Access to other computers
     */
    abstract fun tick(computers: ComputerAccess)

    /**
     * Called once after all computers have been registered. Subscribe to any events provided by other computers here.
     */
    open fun subscribeToEvents() {}

    /**
     * Called once after [subscribeToEvents]. Invoke events that your computer provides here.
     */
    open fun invokeEvents() {}
}
