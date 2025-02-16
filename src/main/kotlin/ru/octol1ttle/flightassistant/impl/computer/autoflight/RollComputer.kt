package ru.octol1ttle.flightassistant.impl.computer.autoflight

import kotlin.math.abs
import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.autoflight.FlightController
import ru.octol1ttle.flightassistant.api.autoflight.roll.RollControllerRegistrationCallback
import ru.octol1ttle.flightassistant.api.autoflight.roll.RollSource
import ru.octol1ttle.flightassistant.api.autoflight.roll.RollSourceRegistrationCallback
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerView
import ru.octol1ttle.flightassistant.api.util.FATickCounter
import ru.octol1ttle.flightassistant.api.util.extensions.filterNonFaulted
import ru.octol1ttle.flightassistant.api.util.extensions.getActiveHighestPriority
import ru.octol1ttle.flightassistant.api.util.findShortestPath
import ru.octol1ttle.flightassistant.api.util.requireIn

class RollComputer(computers: ComputerView) : Computer(computers) {
    private val sources: MutableList<RollSource> = ArrayList()
    private val controllers: MutableList<FlightController> = ArrayList()

    override fun invokeEvents() {
        RollSourceRegistrationCallback.EVENT.invoker().register(sources::add)
        RollControllerRegistrationCallback.EVENT.invoker().register(controllers::add)
    }

    override fun tick() {
        val rollSource: RollSource = sources.filterNonFaulted().singleOrNull { it.isActive() } ?: return

        val inputs: List<ControlInput> = controllers.filterNonFaulted().mapNotNull { it.getRollInput() }.sortedBy { it.priority.value }
        if (inputs.isEmpty()) {
            return
        }
        val finalInput: ControlInput = inputs.getActiveHighestPriority().firstOrNull() ?: return

        if (computers.data.automationsAllowed() && finalInput.active) {
            smoothSetRoll(rollSource, finalInput.target.requireIn(-180.0f..180.0f), finalInput.deltaTimeMultiplier.requireIn(0.001f..Float.MAX_VALUE))
        }
    }

    private fun smoothSetRoll(rollSource: RollSource, target: Float, deltaTimeMultiplier: Float) {
        val diff: Float = findShortestPath(rollSource.getRoll(), target, 360.0f)

        val closeDistanceMultiplier: Float =
            if (diff == 0.0f) 1.0f
            else (1.0f / abs(diff)).coerceAtLeast(1.0f)

        val delta: Float = diff * (FATickCounter.timePassed * deltaTimeMultiplier * closeDistanceMultiplier).coerceIn(0.0f..1.0f)
        rollSource.addRoll(delta)
    }

    override fun reset() {
    }

    companion object {
        val ID: Identifier = FlightAssistant.id("roll")
    }
}
