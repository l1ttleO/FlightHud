package ru.octol1ttle.flightassistant.screen

import java.util.function.Consumer
import java.util.function.Predicate
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.client.gui.widget.TextWidget
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import ru.octol1ttle.flightassistant.FlightAssistant.mc
import ru.octol1ttle.flightassistant.impl.computer.ComputerHost
import ru.octol1ttle.flightassistant.impl.computer.autoflight.AutoFlightComputer
import ru.octol1ttle.flightassistant.screen.widgets.ColoredButtonWidget

class AutoFlightScreen : FABaseScreen(Text.translatable("menu.flightassistant.autoflight")) {
    private lateinit var flightDirectors: ColoredButtonWidget
    private lateinit var autoThrust: ColoredButtonWidget
    private lateinit var autopilot: ColoredButtonWidget

    private var targetSpeed: TextFieldWidget? = null
    private var targetAltitude: TextFieldWidget? = null
    private var targetHeading: TextFieldWidget? = null

    override fun init() {
        super.init()

        val autoflight: AutoFlightComputer = ComputerHost.autoflight

        flightDirectors = ColoredButtonWidget.builder(Text.translatable("menu.flightassistant.autoflight.flight_directors")) {
            autoflight.setFlightDirectors(!autoflight.flightDirectors)
        }.position(this.centerX - 100, this.centerY + 50).width(200).build()
        autoThrust = ColoredButtonWidget.builder(Text.translatable("menu.flightassistant.autoflight.auto_thrust")) {
            autoflight.setAutoThrust(!autoflight.autoThrust, true)
        }.position(this.centerX - 100, this.centerY + 80).width(95).build()
        autopilot = ColoredButtonWidget.builder(Text.translatable("menu.flightassistant.autoflight.autopilot")) {
            autoflight.setAutoPilot(!autoflight.autopilot, true)
        }.position(this.centerX + 5, this.centerY + 80).width(95).build()

        targetSpeed = createAutoFlightWidget(this.centerX - 100, this.centerY - 30, 40, 15,
            targetSpeed,
            Text.translatable("menu.flightassistant.autoflight.speed"),
            Predicate {
                if (it.isEmpty()) return@Predicate true
                val v: Int = it.toIntOrNull() ?: return@Predicate false
                return@Predicate v > 0
            },
            {
                autoflight.selectedSpeed = it.toIntOrNull()
            },
            autoflight.selectedSpeed?.toString()
        )

        targetAltitude = createAutoFlightWidget(this.centerX - 50, this.centerY - 30, 40, 15,
            targetAltitude,
            Text.translatable("menu.flightassistant.autoflight.altitude"),
            Predicate {
                if (it.isEmpty()) return@Predicate true
                return@Predicate it.toIntOrNull() != null
            },
            {
                autoflight.selectedAltitude = it.toIntOrNull()
            },
            if (autoflight.selectedAltitude == null) null
            else autoflight.selectedAltitude.toString()
        )

        targetHeading = createAutoFlightWidget(this.centerX, this.centerY - 30, 40, 15,
            targetHeading,
            Text.translatable("menu.flightassistant.autoflight.heading"),
            Predicate {
                if (it.isEmpty()) return@Predicate true
                val v: Int = it.toIntOrNull() ?: return@Predicate false
                return@Predicate v in 0..360
            },
            {
                autoflight.selectedHeading = it.toIntOrNull()
            },
            autoflight.selectedHeading?.toString()
        )

        this.addDrawableChild(flightDirectors)
        this.addDrawableChild(autoThrust)
        this.addDrawableChild(autopilot)
    }

    private fun createAutoFlightWidget(x: Int, y: Int, width: Int, height: Int, copyFrom: TextFieldWidget?, title: Text, textPredicate: Predicate<String>, changedListener: Consumer<String>, initialValue: String?): TextFieldWidget {
        val widget = TextFieldWidget(mc.textRenderer, x, y, width, height, copyFrom, title)
        widget.setTextPredicate(textPredicate)
        widget.setChangedListener(changedListener)
        if (initialValue != null) {
            widget.text = initialValue
        }
        this.addDrawableChild(TextWidget(x, y - 20, width, height, title, mc.textRenderer))
        this.addDrawableChild(widget)

        return widget
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
