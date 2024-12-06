package ru.octol1ttle.flightassistant.impl.computer.autoflight

import java.awt.Color
import kotlin.math.roundToInt
import net.minecraft.text.*
import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.*
import ru.octol1ttle.flightassistant.api.computer.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.computer.autoflight.thrust.*
import ru.octol1ttle.flightassistant.api.event.autoflight.thrust.*
import ru.octol1ttle.flightassistant.api.util.*
import ru.octol1ttle.flightassistant.api.util.FATickCounter.totalTicks

class ThrustComputer : Computer() {
    private val sources: ArrayList<ThrustSource> = ArrayList()
    private val controllers: ArrayList<ThrustController> = ArrayList()

    private var lastInputAutomatic: Boolean = false

    var targetThrust: Float = 0.0f
        internal set(value) {
            field = value
            lastInputAutomatic = false
        }

    var activeThrustInput: ControlInput? = null
        private set
    var noThrustSource: Boolean = false
        private set
    var thrustLocked: Boolean = false
        private set

    override fun invokeEvents() {
        ThrustSourceRegistrationCallback.EVENT.invoker().register(sources::add)
        ThrustControllerRegistrationCallback.EVENT.invoker().register(controllers::add)
    }

    override fun tick(computers: ComputerAccess) {
        if (!computers.data.automationsAllowed()) {
            return
        }

        val thrustSource: ThrustSource? = sources.filter { it.isAvailable() }.minByOrNull { it.priority.value }
        noThrustSource = thrustSource == null

        var inputs: List<ControlInput> = controllers.mapNotNull { it.getThrustInput(computers) }.sortedBy { it.priority.value }
        inputs = inputs.filter { it.priority.value == inputs[0].priority.value }.sortedBy { it.target }
        val finalInput: ControlInput? = inputs.firstOrNull()
        if (finalInput != null && finalInput.active) {
            targetThrust = finalInput.target.coerceIn(-1.0f..1.0f)
            activeThrustInput = finalInput.copy(active = !noThrustSource)
            thrustLocked = false
            lastInputAutomatic = true
            thrustSource?.tickThrust(computers, targetThrust)
            return
        }

        thrustLocked = lastInputAutomatic
        if (targetThrust == 0.0f) {
            noThrustSource = finalInput != null && thrustSource == null
            activeThrustInput = null
            return
        }

        val thrustValueText: MutableText = Text.literal((targetThrust * 100).roundToInt().toString() + "%").setStyle(Style.EMPTY.withColor(advisoryColor))

        activeThrustInput = ControlInput(targetThrust, ControlInput.Priority.NORMAL, if (thrustLocked) {
            if (totalTicks % 20 >= 10)
                if (targetThrust == 1.0f) Text.translatable("mode.flightassistant.thrust.locked_toga").setStyle(Style.EMPTY.withColor(cautionColor))
                else Text.translatable("mode.flightassistant.thrust.locked", thrustValueText).setStyle(Style.EMPTY.withColor(cautionColor))
            else null
        } else {
            if (targetThrust == 1.0f) Text.translatable("mode.flightassistant.thrust.manual_toga").setStyle(Style.EMPTY.withColor(Color.WHITE.rgb))
            else Text.translatable("mode.flightassistant.thrust.manual", thrustValueText).setStyle(Style.EMPTY.withColor(Color.WHITE.rgb))
        }, active = !noThrustSource)

        thrustSource?.tickThrust(computers, targetThrust)
    }

    companion object {
        val ID: Identifier = FlightAssistant.id("thrust")
    }
}
