package ru.octol1ttle.flightassistant.screen

import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import ru.octol1ttle.flightassistant.api.computer.ComputerView
import ru.octol1ttle.flightassistant.impl.computer.ComputerHost
import ru.octol1ttle.flightassistant.screen.widgets.ColoredButtonWidget
import ru.octol1ttle.flightassistant.screen.widgets.autoflight.ThrustModeWidget

class AutoFlightScreen : FABaseScreen(Text.translatable("menu.flightassistant.autoflight")) {
    private lateinit var flightDirectors: ColoredButtonWidget
    private lateinit var autoThrust: ColoredButtonWidget
    private lateinit var autopilot: ColoredButtonWidget

    override fun init() {
        super.init()

        val computers: ComputerView = ComputerHost

        flightDirectors = this.addDrawableChild(ColoredButtonWidget.builder(Text.translatable("menu.flightassistant.autoflight.flight_directors")) {
            computers.automations.setFlightDirectors(!computers.automations.flightDirectors)
        }.position(this.centerX - 100, this.centerY + 50).width(200).build())
        autoThrust = this.addDrawableChild(ColoredButtonWidget.builder(Text.translatable("menu.flightassistant.autoflight.auto_thrust")) {
            computers.automations.setAutoThrust(!computers.automations.autoThrust, true)
        }.position(this.centerX - 100, this.centerY + 80).width(95).build())
        autopilot = this.addDrawableChild(ColoredButtonWidget.builder(Text.translatable("menu.flightassistant.autoflight.autopilot")) {
            computers.automations.setAutoPilot(!computers.automations.autopilot, true)
        }.position(this.centerX + 5, this.centerY + 80).width(95).build())

        this.addDrawableChild(ThrustModeWidget(computers))
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        flightDirectors.color =
            if (ComputerHost.automations.flightDirectors) Formatting.GREEN.colorValue!!
            else 0xFFFFFF
        autoThrust.color =
            if (ComputerHost.automations.autoThrust) Formatting.GREEN.colorValue!!
            else 0xFFFFFF
        autopilot.color =
            if (ComputerHost.automations.autopilot) Formatting.GREEN.colorValue!!
            else 0xFFFFFF

        super.render(context, mouseX, mouseY, delta)
    }
}
