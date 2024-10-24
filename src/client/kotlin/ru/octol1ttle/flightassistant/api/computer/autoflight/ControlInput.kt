package ru.octol1ttle.flightassistant.api.computer.autoflight

import net.minecraft.text.Text

data class ControlInput(val target: Float, val priority: Priority, val text: Text?) {
    enum class Priority(val value: Int) {
        HIGHEST(0),
        HIGH(1),
        NORMAL(2),
        LOW(3),
        LOWEST(4)
    }
}
