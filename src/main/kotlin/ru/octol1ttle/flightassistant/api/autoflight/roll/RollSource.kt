package ru.octol1ttle.flightassistant.api.autoflight.roll

/**
 * Defines a source of roll to be used by the [ru.octol1ttle.flightassistant.impl.computer.autoflight.RollComputer]
 */
interface RollSource {
    /**
     * @return whether this roll source is currently active. In the event there are multiple active roll sources, none of them are used.
     */
    fun isActive(): Boolean

    fun getRoll(): Float
    fun addRoll(diff: Float)
}
