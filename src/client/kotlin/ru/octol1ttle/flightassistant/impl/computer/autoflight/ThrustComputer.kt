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
import ru.octol1ttle.flightassistant.api.util.cautionColor

class ThrustComputer : Computer() {
    private val sources: ArrayList<ThrustSource> = ArrayList()
    private val controllers: ArrayList<ThrustController> = ArrayList()

    var targetThrust: Float = 0.0f
        internal set
    var manualThrust: Float = 0.0f
        internal set

    var thrustMode: Text? = null
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

        var inputs: List<ControlInput> =
            controllers.mapNotNull { it.getThrustInput(computers) }.sortedBy { it.priority.value }
        inputs =
            inputs.filter { it.priority != ControlInput.Priority.SUGGESTION && it.priority.value == inputs[0].priority.value }
        if (inputs.isNotEmpty()) {
            val finalInput: ControlInput = inputs.maxBy { it.target }

            targetThrust = finalInput.target.coerceIn(-1.0f..1.0f)
            thrustMode = finalInput.text
            thrustLocked = false
        } else if (targetThrust != manualThrust) {
            thrustMode =
                if (totalTicks % 20 >= 10) Text.translatable("mode.flightassistant.thrust.locked").setStyle(Style.EMPTY.withColor(cautionColor))
                else null
            thrustLocked = true
        } else {
            thrustMode =
                if (manualThrust != 0.0f)
                    Text.translatable(
                        "mode.flightassistant.thrust.manual",
                        Text.literal((manualThrust * 100).roundToInt().toString() + "%").setStyle(Style.EMPTY.withColor(advisoryColor))
                    ).setStyle(Style.EMPTY.withColor(Color.WHITE.rgb))
                else
                    null
            thrustLocked = false
        }
        if (targetThrust == 0.0f) {
            noThrustSource = false
            return
        }

        if (thrustSource == null) {
            noThrustSource = true
            return
        }

        thrustSource.tickThrust(computers, targetThrust)
    }

    companion object {
        val ID: Identifier = FlightAssistant.id("thrust")
    }
}
