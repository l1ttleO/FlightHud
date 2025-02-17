package ru.octol1ttle.flightassistant.api.computer

/**
 * A class responsible for computing data and providing it to [ru.octol1ttle.flightassistant.api.display.Display]s and [ru.octol1ttle.flightassistant.api.alert.Alert]s
 */
abstract class Computer(val computers: ComputerView) {
    /**
     * Whether or not this computer is enabled. Disabled computers do not tick.
     */
    var enabled: Boolean = true

    /**
     * Whether or not this computer has faulted. This value is set to false whenever the computer is reset.
     */
    var faulted: Boolean = false

    /**
     * The amount of times this computer has faulted.
     */
    var faultCount: Int = 0

    fun disabledOrFaulted(): Boolean {
        return !enabled || faulted
    }

    /**
     * Called once per world render
     *
     * If this method throws an exception or error, it is caught and the computer is considered "faulted".
     * It won't be ticked until it is reset and an alert about the issue will be displayed
     */
    abstract fun tick()

    /**
     * Called when this computer should be reset. This computer's state should be reset to the initial ("everything is good") state.
     * The computer will be ticked again after it is reset.
     */
    abstract fun reset()

    /**
     * Called once after all computers have been registered. Subscribe to any events provided by other computers here.
     */
    open fun subscribeToEvents() {}

    /**
     * Called once after [subscribeToEvents]. Invoke events that your computer provides here.
     */
    open fun invokeEvents() {}
}
