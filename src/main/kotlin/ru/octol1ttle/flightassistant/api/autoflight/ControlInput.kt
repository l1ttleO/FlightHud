package ru.octol1ttle.flightassistant.api.autoflight

import net.minecraft.text.Text
import net.minecraft.util.Identifier

/**
 * Represents an input that computers may provide to other computers.
 *
 * @param target The target value of this input (e.g. the target thrust)
 * @param priority The priority of this input
 * @param text The text that will be shown on the [ru.octol1ttle.flightassistant.impl.display.AutomationModesDisplay] when this input is being satisfied
 * @param deltaTimeMultiplier The multiplier for the delta time, used to determine how fast an input will be satisfied
 * @param active If the input is active, it will be satisfied. Otherwise, this input serves as a notification
 * @param identifier The identifier of this input
 */
data class ControlInput(val target: Float, val priority: Priority, val text: Text? = null, val deltaTimeMultiplier: Float = 1.0f, val active: Boolean = true, val identifier: Identifier? = null) {
    enum class Priority(val value: Int) {
        HIGHEST(0),
        HIGH(1),
        NORMAL(2),
        LOW(3);

        fun isHigherOrSame(other: Priority?): Boolean {
            if (other == null) {
                return true
            }

            return this.value <= other.value
        }
    }
}
