package ru.octol1ttle.flightassistant.impl.computer.autoflight

import java.awt.Color
import net.minecraft.text.MutableText
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.autoflight.thrust.*
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerView
import ru.octol1ttle.flightassistant.api.util.FATickCounter.totalTicks
import ru.octol1ttle.flightassistant.api.util.extensions.advisoryColor
import ru.octol1ttle.flightassistant.api.util.extensions.cautionColor
import ru.octol1ttle.flightassistant.api.util.extensions.filterNonFaulted
import ru.octol1ttle.flightassistant.api.util.extensions.getActiveHighestPriority
import ru.octol1ttle.flightassistant.api.util.furtherFromZero
import ru.octol1ttle.flightassistant.api.util.requireIn

class ThrustComputer(computers: ComputerView) : Computer(computers) {
    private val sources: MutableList<ThrustSource> = ArrayList()
    private val controllers: MutableList<ThrustController> = ArrayList()

    private var lastChangeAutomatic: Boolean = false

    var current: Float = 0.0f
        private set

    var activeInput: ControlInput? = null
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

    override fun tick() {
        val thrustSource: ThrustSource? = sources.filterNonFaulted().filter { it.isAvailable() }.minByOrNull { it.priority.value }

        val inputs: List<ControlInput> = controllers.filterNonFaulted().mapNotNull { it.getThrustInput() }.sortedBy { it.priority.value }
        val finalInput: ControlInput? = inputs.getActiveHighestPriority().maxByOrNull { it.target }

        noThrustSource = false
        reverseUnsupported = false

        if (finalInput?.active == true) {
            setTarget(finalInput.target, finalInput)
            activeInput = finalInput
            thrustLocked = false
        } else if (current == 0.0f) {
            noThrustSource = finalInput != null && thrustSource == null
            val text: Text? = if (!noThrustSource) finalInput?.text else finalInput?.text?.copy()?.styled { it.withColor(cautionColor) }
            activeInput = finalInput?.copy(text = text!!)
            thrustLocked = false
            return
        } else {
            thrustLocked = lastChangeAutomatic
            reverseUnsupported = current < 0.0f && thrustSource?.supportsReverse == false

            val thrustValueText: MutableText = Text.literal(furtherFromZero(current * 100).toInt().toString() + "%").setStyle(Style.EMPTY.withColor(advisoryColor))
            val manualThrustText: Text =
                if (thrustLocked) {
                    if (totalTicks % 20 >= 10)
                        if (current >= TOGA_THRESHOLD) Text.translatable("mode.flightassistant.thrust.locked_toga").setStyle(Style.EMPTY.withColor(cautionColor))
                        else Text.translatable("mode.flightassistant.thrust.locked", thrustValueText).setStyle(Style.EMPTY.withColor(cautionColor))
                    else Text.empty()
                } else {
                    if (current >= TOGA_THRESHOLD) Text.translatable("mode.flightassistant.thrust.manual_toga").setStyle(Style.EMPTY.withColor(Color.WHITE.rgb))
                    else Text.translatable("mode.flightassistant.thrust.manual", thrustValueText).setStyle(Style.EMPTY.withColor(Color.WHITE.rgb))
                }

            activeInput = ControlInput(current, ControlInput.Priority.NORMAL, manualThrustText)
        }

        noThrustSource = thrustSource == null
        current.requireIn(-1.0f..1.0f)

        val active: Boolean = !noThrustSource && !reverseUnsupported
        val text: Text? = if (active) activeInput?.text else activeInput?.text?.copy()?.styled { it.withColor(cautionColor) }
        activeInput = activeInput?.copy(text = text!!, active = active)

        if (computers.data.automationsAllowed()) {
            thrustSource?.tickThrust(current.coerceIn((if (thrustSource.supportsReverse) -1.0f else 0.0f)..1.0f))
        }
    }

    fun setTarget(target: Float, input: ControlInput? = null) {
        val oldThrust: Float = current
        if (oldThrust != target) {
            current = target
            ThrustChangeCallback.EVENT.invoker().onThrustChange(oldThrust, current, input)
        }
        lastChangeAutomatic = input != null
    }

    fun getOptimumClimbPitch(): Float {
        val thrustSource: ThrustSource? = sources.filterNonFaulted().filter { it.isAvailable() }.minByOrNull { it.priority.value }
        if (thrustSource != null) {
            return thrustSource.optimumClimbPitch
        }

        return 55.0f
    }

    fun calculateThrustForSpeed(targetSpeed: Int): Float? {
        val thrustSource: ThrustSource? = sources.filterNonFaulted().filter { it.isAvailable() }.minByOrNull { it.priority.value }
        if (thrustSource != null) {
            return thrustSource.calculateThrustForSpeed(targetSpeed)
        }

        return null
    }

    override fun reset() {
        lastChangeAutomatic = false
        current = 0.0f
        activeInput = null
        noThrustSource = false
        reverseUnsupported = false
        thrustLocked = false
    }

    companion object {
        val ID: Identifier = FlightAssistant.id("thrust")
        const val TOGA_THRESHOLD: Float = 0.99f
    }
}
