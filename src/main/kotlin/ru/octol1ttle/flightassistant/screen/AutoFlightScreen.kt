package ru.octol1ttle.flightassistant.screen

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.client.gui.widget.TextWidget
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import ru.octol1ttle.flightassistant.FlightAssistant.mc
import ru.octol1ttle.flightassistant.api.util.autoflight
import ru.octol1ttle.flightassistant.impl.computer.ComputerHost
import ru.octol1ttle.flightassistant.screen.widgets.ColoredButtonWidget

class AutoFlightScreen : FABaseScreen(Text.translatable("menu.flightassistant.autoflight")) {
    private lateinit var flightDirectors: ColoredButtonWidget
    private lateinit var autoThrust: ColoredButtonWidget
    private lateinit var autopilot: ColoredButtonWidget

    private var targetSpeed: TextFieldWidget? = null

    override fun init() {
        super.init()

        flightDirectors = ColoredButtonWidget.builder(Text.translatable("menu.flightassistant.autoflight.flight_directors")) {
            ComputerHost.autoflight.flightDirectors = !ComputerHost.autoflight.flightDirectors
        }.position(this.centerX - 100, this.centerY + 50).width(200).build()
        autoThrust = ColoredButtonWidget.builder(Text.translatable("menu.flightassistant.autoflight.auto_thrust")) {
            ComputerHost.autoflight.autoThrust = !ComputerHost.autoflight.autoThrust
        }.position(this.centerX - 100, this.centerY + 80).width(95).build()
        autopilot = ColoredButtonWidget.builder(Text.translatable("menu.flightassistant.autoflight.autopilot")) {
            ComputerHost.autoflight.autopilot = !ComputerHost.autoflight.autopilot
        }.position(this.centerX + 5, this.centerY + 80).width(95).build()

        targetSpeed = TextFieldWidget(mc.textRenderer, this.centerX - 250, this.centerY - 30, 50, 15, targetSpeed, Text.translatable("menu.flightassistant.autoflight.speed"))
        targetSpeed!!.setTextPredicate { it.isEmpty() || it.toIntOrNull() != null }
        targetSpeed!!.setChangedListener {
            ComputerHost.autoflight.selectedSpeed = it.toIntOrNull()
        }
        targetSpeed!!.text = ComputerHost.autoflight.selectedSpeed.toString()

        this.addDrawableChild(TextWidget(this.centerX - 250, this.centerY - 50, 50, 15, Text.translatable("menu.flightassistant.autoflight.speed"), mc.textRenderer))
        this.addDrawableChild(targetSpeed!!)

        this.addDrawableChild(flightDirectors).active = false
        this.addDrawableChild(autoThrust)
        this.addDrawableChild(autopilot).active = false
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        flightDirectors.color =
            if (ComputerHost.autoflight.flightDirectors) Formatting.GREEN.colorValue!!
            else 0xFFFFFF
        autoThrust.color =
            if (ComputerHost.autoflight.autoThrust) Formatting.GREEN.colorValue!!
            else 0xFFFFFF
        autopilot.color =
            if (ComputerHost.autoflight.autopilot) Formatting.GREEN.colorValue!!
            else 0xFFFFFF

        super.render(context, mouseX, mouseY, delta)
    }
}
