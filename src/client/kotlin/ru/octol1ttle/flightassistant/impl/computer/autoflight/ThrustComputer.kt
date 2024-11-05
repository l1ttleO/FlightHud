package ru.octol1ttle.flightassistant.impl.computer.autoflight

import kotlin.math.roundToInt
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.*
import ru.octol1ttle.flightassistant.api.computer.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.computer.autoflight.thrust.*
import ru.octol1ttle.flightassistant.api.event.autoflight.thrust.*
import ru.octol1ttle.flightassistant.api.util.data

class ThrustComputer : Computer() {
    private val sources: ArrayList<ThrustSource> = ArrayList()
    private val controllers: ArrayList<ThrustController> = ArrayList()
    private var manualThrust: Float = 0.0f
    var currentThrustMode: Text? = null
    var needsManualThrust: Boolean = false

    override fun invokeEvents() {
        ThrustSourceRegistrationCallback.EVENT.invoker().register(sources::add)
        ThrustControllerRegistrationCallback.EVENT.invoker().register(controllers::add)
    }

    override fun tick(computers: ComputerAccess) {
        if (!computers.data.automationsAllowed()) {
            return
        }
        needsManualThrust = false

        val thrustSource: ThrustSource? = sources.filter { it.isAvailable() }.minByOrNull { it.priority.value }

        val inputs: List<ControlInput> = controllers.mapNotNull { it.getThrustInput(computers) }.sortedBy { it.priority.value }
        if (inputs.isEmpty()) {
            thrustSource?.tickThrust(computers, manualThrust)
            currentThrustMode =
                if (manualThrust != 0.0f) Text.translatable(
                    "mode.flightassistant.thrust.manual",
                    manualThrust.roundToInt()
                )
                else null
            return
        }

        if (thrustSource == null) {
            needsManualThrust = true
            return
        }

        val finalInput: ControlInput = inputs.filter { it.priority.value == inputs[0].priority.value }.maxBy { it.target }

        if (finalInput.priority != ControlInput.Priority.SUGGESTION) {
            thrustSource.tickThrust(computers, finalInput.target)
            currentThrustMode = finalInput.text
        }
    }

    companion object {
        val ID: Identifier = FlightAssistant.id("thrust")
    }
}
