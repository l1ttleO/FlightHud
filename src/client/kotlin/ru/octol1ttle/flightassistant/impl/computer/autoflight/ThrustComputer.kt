package ru.octol1ttle.flightassistant.impl.computer.autoflight

import net.minecraft.text.*
import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.*
import ru.octol1ttle.flightassistant.api.computer.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.computer.autoflight.thrust.*
import ru.octol1ttle.flightassistant.api.event.autoflight.thrust.*
import ru.octol1ttle.flightassistant.api.util.*

class ThrustComputer : Computer() {
    private val sources: ArrayList<ThrustSource> = ArrayList()
    private val controllers: ArrayList<ThrustController> = ArrayList()
    private var targetThrust: Float = 0.0f
    private var manualThrust: Float = 0.0f
    var thrustMode: Text? = null
    var noThrustSource: Boolean = false
    var thrustLocked: Boolean = false

    override fun invokeEvents() {
        ThrustSourceRegistrationCallback.EVENT.invoker().register(sources::add)
        ThrustControllerRegistrationCallback.EVENT.invoker().register(controllers::add)
    }

    // TODO: thrust keybindings
    override fun tick(computers: ComputerAccess) {
        if (!computers.data.automationsAllowed()) {
            return
        }

        val thrustSource: ThrustSource? = sources.filter { it.isAvailable() }.minByOrNull { it.priority.value }
        if (thrustSource != null) {
            noThrustSource = false
        }

        var inputs: List<ControlInput> =
            controllers.mapNotNull { it.getThrustInput(computers) }.sortedBy { it.priority.value }
        inputs =
            inputs.filter { it.priority != ControlInput.Priority.SUGGESTION && it.priority.value == inputs[0].priority.value }
        if (inputs.isNotEmpty()) {
            val finalInput: ControlInput = inputs.maxBy { it.target }

            targetThrust = finalInput.target
            thrustMode = finalInput.text
            thrustLocked = false
        } else if (targetThrust != manualThrust) {
            thrustMode =
                Text.translatable("mode.flightassistant.thrust.locked").setStyle(Style.EMPTY.withColor(cautionColor))
            thrustLocked = true
        } else {
            targetThrust = manualThrust
            thrustLocked = false
        }
        if (targetThrust == 0.0f) {
            return
        }

        if (thrustSource == null) {
            noThrustSource = true
            thrustMode = null
            return
        }

        thrustSource.tickThrust(computers, targetThrust)
    }

    companion object {
        val ID: Identifier = FlightAssistant.id("thrust")
    }
}
