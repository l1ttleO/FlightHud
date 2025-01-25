package ru.octol1ttle.flightassistant.api.computer.autoflight

import net.minecraft.text.Text
import net.minecraft.util.Identifier

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
