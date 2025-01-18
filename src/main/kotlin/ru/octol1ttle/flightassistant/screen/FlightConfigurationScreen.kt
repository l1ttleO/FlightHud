package ru.octol1ttle.flightassistant.screen

import dev.isxander.yacl3.gui.ElementListWidgetExt
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.TextWidget
import net.minecraft.client.gui.widget.Widget
import net.minecraft.text.Text

class FlightConfigurationScreen : FABaseScreen(Text.of("Flight Configuration")) {
    override fun init() {
        super.init()

        this.addDrawableChild(ButtonWidget.builder(Text.of("Flight Plan")) {
            TODO()
        }.position(this.centerX - 200, this.centerY - 10).width(98).build()).active = false
        this.addDrawableChild(ButtonWidget.builder(Text.of("System Status")) {
            this.client!!.setScreen(SystemStatusScreen())
        }.position(this.centerX + 102, this.centerY - 10).width(98).build())

        this.addDrawableChild(TextWidget(0, centerY - 40, this.width, 9, this.title, this.textRenderer))
    }
}
