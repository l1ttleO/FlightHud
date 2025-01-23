package ru.octol1ttle.flightassistant.screen.status

import net.minecraft.text.Text
import ru.octol1ttle.flightassistant.impl.computer.ComputerHost
import ru.octol1ttle.flightassistant.impl.display.HudDisplayHost
import ru.octol1ttle.flightassistant.screen.FABaseScreen

class SystemStatusScreen : FABaseScreen(Text.translatable("menu.flightassistant.status")) {
    override fun init() {
        super.init()

        val hudListWidget = SystemStatusListWidget(centerX, this.height - 10, 10, this.height, 0, HudDisplayHost, "menu.flightassistant.system.name.hud")
        this.addDrawableChild(hudListWidget)

        val computerListWidget = SystemStatusListWidget(centerX, this.height - 10, 10, this.height, this.centerX, ComputerHost, "menu.flightassistant.system.name.computer")
//? if >=1.21 {
        /*computerListWidget.x = this.centerX
*///?} else
        computerListWidget.setLeftPos(this.centerX)
        this.addDrawableChild(computerListWidget)
    }
}
