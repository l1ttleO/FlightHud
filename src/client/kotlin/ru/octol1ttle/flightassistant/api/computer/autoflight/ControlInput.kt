package ru.octol1ttle.flightassistant.api.computer.autoflight

import net.minecraft.text.Text

data class ControlInput(val target: Float, val priority: Priority, val text: Text?, val deltaTimeMultiplier: Float = 1.0f) {
    enum class Priority(val value: Int) {
        HIGHEST(0),
        HIGH(1),
        NORMAL(2),
        LOW(3),

        /**
         * Indicates that this control input is only a suggestion and shouldn't be used to control the player camera
         */
        SUGGESTION(4);

        fun isHigherOrSame(other: Priority?): Boolean {
            if (other == null) {
                return true
            }

            return this.value <= other.value
        }
    }
}
