package ru.octol1ttle.flightassistant.api.display

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.util.Window
import ru.octol1ttle.flightassistant.FlightAssistant.mc
import ru.octol1ttle.flightassistant.config.FAConfig

object HudFrame {
    private val window: Window = mc.window
    var width: Float = 0.0f
        private set
    private var height: Float = 0.0f
    var top: Int = 0
        private set
    var bottom: Int = 0
        private set
    var left: Int = 0
        private set
    var right: Int = 0
        private set

    fun update() {
        width = window.scaledWidth * FAConfig.display.frameWidth
        height = window.scaledHeight * FAConfig.display.frameHeight
        top = ((window.scaledHeight - height) * 0.5f).toInt()
        bottom = window.scaledHeight - top
        left = ((window.scaledWidth - width) * 0.5f).toInt() + 1
        right = window.scaledWidth - left
    }

    fun scissor(context: DrawContext) {
        context.enableScissor(left, top, right, bottom + 1)
    }
}
