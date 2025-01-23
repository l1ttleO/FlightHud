package ru.octol1ttle.flightassistant.api.computer.autoflight.thrust

import ru.octol1ttle.flightassistant.api.computer.ComputerAccess

interface ThrustSource {
    /**
     * Defines the priority of this thrust source.
     *
     * @see [ThrustSource.Priority]
     */
    val priority: Priority

    /**
     * Defines whether this thrust source supports reverse thrust.
     */
    val supportsReverse: Boolean

    /**
     * Defines the optimum climb pitch which should be used when this thrust source is active.
     */
    val optimumClimbPitch: Float

    /**
     * Defines whether this thrust source currently can be used. For example, there are fireworks in the player's hand that can be used.
     */
    fun isAvailable(): Boolean

    /**
     * Called every computer tick for a single thrust source with the highest priority.
     *
     * @param currentThrust The current requested thrust. Ranges are `[0.0, 1.0]` or `[-1.0, 1.0]` depending on whether this source [supportsReverse]
     */
    fun tickThrust(computers: ComputerAccess, currentThrust: Float)

    /**
     * Calculates the thrust required to achieve the target speed.
     */
    fun calculateThrustForSpeed(computers: ComputerAccess, targetSpeed: Int): Float

    /**
     * Defines the priority, which determines which thrust source to choose in the case there are multiple sources available ([isAvailable]).
     * In the case there are multiple thrust sources with the same priority, a single thrust source will be ticked. Which one exactly is determined by mod loading order
     */
    enum class Priority(val value: Int) {
        /** Use for thrust sources which can work almost unconditionally (e.g. don't require any fuel and/or are locked behind a config option) */
        HIGH(0),
        /** Use for thrust sources whose availability depends on in-game actions (e.g. some type of fuel is needed) */
        NORMAL(1),
        /** Reserved for built-in thrust sources, like fireworks */
        LOW(2)
    }
}
