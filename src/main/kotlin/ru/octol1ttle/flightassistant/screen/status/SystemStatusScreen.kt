package ru.octol1ttle.flightassistant.screen.status

import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import ru.octol1ttle.flightassistant.impl.computer.ComputerHost
import ru.octol1ttle.flightassistant.impl.display.HudDisplayHost
import ru.octol1ttle.flightassistant.screen.FABaseScreen

class SystemStatusScreen : FABaseScreen(Text.translatable("menu.flightassistant.status")) {
    override fun init() {
        super.init()

        val hudListWidget = SystemStatusListWidget(centerX, this.height - 10, 10, this.height, 0, HudDisplayHost.identifiers(), "menu.flightassistant.system.name.hud", HudDisplayHost)
        this.addDrawableChild(hudListWidget)

        val computerListWidget = SystemStatusListWidget(centerX, this.height - 10, 10, this.height, this.centerX, ComputerHost.identifiers(), "menu.flightassistant.system.name.computer",  ComputerHost)
        computerListWidget.setLeftPos(this.centerX)
        this.addDrawableChild(computerListWidget)
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(context, mouseX, mouseY, delta)
        context.drawVerticalLine(this.centerX, 10, this.height - 10, 0xF0F0F0)
    }
}
