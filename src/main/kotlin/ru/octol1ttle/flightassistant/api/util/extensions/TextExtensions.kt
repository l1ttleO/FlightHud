package ru.octol1ttle.flightassistant.api.util.extensions

import net.minecraft.text.MutableText
import net.minecraft.text.Text

fun MutableText.appendWithSeparation(other: Text) {
    if (this.siblings.isNotEmpty()) {
        this.append(" ")
    }
    this.append(other)
}
