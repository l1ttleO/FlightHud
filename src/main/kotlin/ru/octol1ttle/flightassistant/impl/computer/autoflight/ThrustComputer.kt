package ru.octol1ttle.flightassistant.impl.computer.autoflight

import java.awt.Color
import net.minecraft.text.MutableText
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.computer.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.computer.autoflight.thrust.ThrustController
import ru.octol1ttle.flightassistant.api.computer.autoflight.thrust.ThrustSource
import ru.octol1ttle.flightassistant.api.event.autoflight.thrust.ThrustControllerRegistrationCallback
import ru.octol1ttle.flightassistant.api.event.autoflight.thrust.ThrustSourceRegistrationCallback
import ru.octol1ttle.flightassistant.api.util.*
import ru.octol1ttle.flightassistant.api.util.FATickCounter.totalTicks

class ThrustComputer : Computer() {
    private val sources: ArrayList<ThrustSource> = ArrayList()
    private val controllers: ArrayList<ThrustController> = ArrayList()

    private var lastInputAutomatic: Boolean = false

    var targetThrust: Float = 0.0f
        private set

    var activeThrustInput: ControlInput? = null
        private set
    var noThrustSource: Boolean = false
        private set
    var reverseUnsupported: Boolean = false
        private set
    var thrustLocked: Boolean = false
        private set

    override fun invokeEvents() {
        ThrustSourceRegistrationCallback.EVENT.invoker().register(sources::add)
        ThrustControllerRegistrationCallback.EVENT.invoker().register(controllers::add)
    }

    override fun tick(computers: ComputerAccess) {
        val thrustSource: ThrustSource? = sources.filter { it.isAvailable() }.minByOrNull { it.priority.value }

        val inputs: List<ControlInput> = controllers.mapNotNull { it.getThrustInput(computers) }.sortedBy { it.priority.value }
        val finalInput: ControlInput? = inputs.getActiveHighestPriority()

        noThrustSource = false
        reverseUnsupported = false

        if (finalInput?.active == true) {
            setTarget(finalInput.target, true)
            activeThrustInput = finalInput
        } else if (targetThrust == 0.0f) {
            noThrustSource = finalInput != null && thrustSource == null
            val text: Text? = if (!noThrustSource) finalInput?.text else finalInput?.text?.copy()?.styled { it.withColor(cautionColor) }
            activeThrustInput = finalInput?.copy(text = text)
            thrustLocked = false
            return
        } else {
            thrustLocked = lastInputAutomatic
            reverseUnsupported = targetThrust < 0.0f && thrustSource?.supportsReverse == false

            val thrustValueText: MutableText = Text.literal(furtherFromZero(targetThrust * 100).toInt().toString() + "%").setStyle(Style.EMPTY.withColor(advisoryColor))
            val manualThrustText: Text? =
                if (thrustLocked) {
                    if (totalTicks % 20 >= 10)
                        if (targetThrust == 1.0f) Text.translatable("mode.flightassistant.thrust.locked_toga").setStyle(Style.EMPTY.withColor(cautionColor))
                        else Text.translatable("mode.flightassistant.thrust.locked", thrustValueText).setStyle(Style.EMPTY.withColor(cautionColor))
                    else null
                } else {
                    if (targetThrust == 1.0f) Text.translatable("mode.flightassistant.thrust.manual_toga").setStyle(Style.EMPTY.withColor(Color.WHITE.rgb))
                    else Text.translatable("mode.flightassistant.thrust.manual", thrustValueText).setStyle(Style.EMPTY.withColor(Color.WHITE.rgb))
                }

            activeThrustInput = ControlInput(targetThrust, ControlInput.Priority.NORMAL, manualThrustText)
        }

        noThrustSource = thrustSource == null
        targetThrust.requireIn(-1.0f..1.0f)

        val active: Boolean = !noThrustSource && !reverseUnsupported
        val text: Text? = if (active) activeThrustInput?.text else activeThrustInput?.text?.copy()?.styled { it.withColor(cautionColor) }
        activeThrustInput = activeThrustInput?.copy(text = text, active = active)

        if (computers.data.automationsAllowed()) {
            thrustSource?.tickThrust(computers, targetThrust.coerceIn((if (thrustSource.supportsReverse) -1.0f else 0.0f)..1.0f))
        }
    }

    fun setTarget(target: Float, automatic: Boolean) {
        targetThrust = target
        lastInputAutomatic = automatic
    }

    fun getOptimumClimbPitch(): Float {
        val thrustSource: ThrustSource? = sources.filter { it.isAvailable() }.minByOrNull { it.priority.value }
        if (thrustSource != null) {
            return thrustSource.optimumClimbPitch
        }

        return 55.0f
    }

    companion object {
        val ID: Identifier = FlightAssistant.id("thrust")
    }
}
