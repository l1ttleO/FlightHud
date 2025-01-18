package ru.octol1ttle.flightassistant.screen

import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.TextWidget
import net.minecraft.text.Text
import ru.octol1ttle.flightassistant.screen.status.SystemStatusScreen

class FlightConfigurationScreen : FABaseScreen(Text.translatable("menu.flightassistant")) {
    override fun init() {
        super.init()

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("menu.flightassistant.flight_plan")) {
            TODO()
        }.position(this.centerX - 200, this.centerY - 10).width(98).build()).active = false
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("menu.flightassistant.system")) {
            this.client!!.setScreen(SystemStatusScreen())
        }.position(this.centerX + 102, this.centerY - 10).width(98).build())

        this.addDrawableChild(TextWidget(0, centerY - 40, this.width, 9, this.title, this.textRenderer))
    }
}
