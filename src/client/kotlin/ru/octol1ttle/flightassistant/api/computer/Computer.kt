package ru.octol1ttle.flightassistant.api.computer

/**
 * A class responsible for computing data and providing it to [ru.octol1ttle.flightassistant.display.api.Display]s and [ru.octol1ttle.flightassistant.alert.api.ECAMAlert]
 */
abstract class Computer {
    internal var faulted: Boolean = false

    /**
     * Called once per world render
     *
     * If this method throws an exception or error, it is caught and the computer is considered "faulted".
     * It won't be ticked and an alert about the issue will be displayed
     *
     * @param tickDelta The current tick delta, ignoring tick freeze.
     * @param computers Access to other computers
     */
    abstract fun tick(computers: ComputerAccess)

    open fun subscribeToEvents() {}
    open fun invokeEvents() {}
}
