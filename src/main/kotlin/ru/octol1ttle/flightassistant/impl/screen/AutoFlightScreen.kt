package ru.octol1ttle.flightassistant.impl.screen

import java.util.function.Consumer
import java.util.function.Predicate
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.client.gui.widget.TextWidget
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import ru.octol1ttle.flightassistant.FlightAssistant.mc
import ru.octol1ttle.flightassistant.api.util.extensions.autoflight
import ru.octol1ttle.flightassistant.impl.computer.ComputerHost
import ru.octol1ttle.flightassistant.impl.computer.autoflight.AutoFlightComputer
import ru.octol1ttle.flightassistant.impl.screen.widgets.ColoredButtonWidget

class AutoFlightScreen : FABaseScreen(Text.translatable("menu.flightassistant.autoflight")) {
    private lateinit var flightDirectors: ColoredButtonWidget
    private lateinit var autoThrust: ColoredButtonWidget
    private lateinit var autopilot: ColoredButtonWidget

    private var targetSpeed: TextFieldWidget? = null
    private var targetPitch: TextFieldWidget? = null
    private var targetHeading: TextFieldWidget? = null

    override fun init() {
        super.init()

        val autoflight: AutoFlightComputer = ComputerHost.autoflight

        flightDirectors = ColoredButtonWidget.builder(Text.translatable("menu.flightassistant.autoflight.flight_directors")) {
            autoflight.setFlightDirectors(ComputerHost, !autoflight.flightDirectors)
        }.position(this.centerX - 100, this.centerY + 50).width(200).build()
        autoThrust = ColoredButtonWidget.builder(Text.translatable("menu.flightassistant.autoflight.auto_thrust")) {
            autoflight.setAutoThrust(ComputerHost, !autoflight.autoThrust, true)
        }.position(this.centerX - 100, this.centerY + 80).width(95).build()
        autopilot = ColoredButtonWidget.builder(Text.translatable("menu.flightassistant.autoflight.autopilot")) {
            autoflight.setAutoPilot(ComputerHost, !autoflight.autopilot, true)
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

        targetPitch = createAutoFlightWidget(this.centerX - 50, this.centerY - 30, 40, 15,
            targetPitch,
            Text.translatable("menu.flightassistant.autoflight.pitch"),
            Predicate {
                if (it.isEmpty() || it == "-") return@Predicate true
                val v: Float = it.toFloatOrNull() ?: return@Predicate false
                return@Predicate v in -90.0f..90.0f
            },
            {
                autoflight.selectedPitch = it.toFloatOrNull()
            },
            if (autoflight.selectedPitch == null) null
            else "%.1f".format(autoflight.selectedPitch)
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
