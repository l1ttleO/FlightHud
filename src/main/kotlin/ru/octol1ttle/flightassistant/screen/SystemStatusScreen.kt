package ru.octol1ttle.flightassistant.screen

import dev.isxander.yacl3.gui.ElementListWidgetExt
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.Widget
import net.minecraft.text.Text

class SystemStatusScreen : FABaseScreen(Text.of("System Status")) {
    override fun init() {
        super.init()
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(context, mouseX, mouseY, delta)
        context.drawVerticalLine(this.centerX, 10, this.height - 10, 0xF0F0F0)
    }
}
