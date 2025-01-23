package ru.octol1ttle.flightassistant.impl.computer.autoflight

import net.minecraft.text.Text
import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.computer.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.computer.autoflight.thrust.ThrustController
import ru.octol1ttle.flightassistant.api.event.autoflight.thrust.ThrustChangeCallback
import ru.octol1ttle.flightassistant.api.event.autoflight.thrust.ThrustControllerRegistrationCallback
import ru.octol1ttle.flightassistant.api.util.data
import ru.octol1ttle.flightassistant.api.util.protections
import ru.octol1ttle.flightassistant.api.util.thrust

class AutoFlightComputer : Computer(), ThrustController {
    var flightDirectors: Boolean = false

    var autoThrust: Boolean = false
    var autoThrustAlert: Boolean = false
    var selectedSpeed: Int? = null

    private var targetThrust: Float? = null

    var autopilot: Boolean = false
    var autopilotAlert: Boolean = false

    override fun subscribeToEvents() {
        ThrustControllerRegistrationCallback.EVENT.register { it.accept(this) }
        ThrustChangeCallback.EVENT.register(ThrustChangeCallback { _, newThrust, automatic ->
            if (newThrust != targetThrust) {
                if (autoThrust) {
                    autoThrustAlert = true
                }
                autoThrust = false
            }
            if (!automatic && autoThrustAlert) {
                autoThrustAlert = false
            }
        })
    }

    override fun tick(computers: ComputerAccess) {
        if (computers.protections.protectionsLost) {
            reset()
            return
        }

        if (autoThrust) {
            autoThrustAlert = false
            if (computers.thrust.faulted) {
                autoThrust = false
                autoThrustAlert = true
            }
        }
    }

    override fun reset() {
        flightDirectors = false
        if (autoThrust) {
            autoThrustAlert = true
        }
        autoThrust = false
        if (autopilot) {
            autopilotAlert = true
        }
        autopilot = false
    }

    override fun getThrustInput(computers: ComputerAccess): ControlInput? {
        if (!autoThrust) {
            return null
        }

        if (selectedSpeed == null) {
            selectedSpeed = (computers.data.forwardVelocity.length() * 20).toInt()
        }

        val target: Int = selectedSpeed!!
        targetThrust = computers.thrust.calculateThrustForSpeed(computers, target) ?: 0.0f

        return ControlInput(
            targetThrust!!,
            ControlInput.Priority.NORMAL,
            Text.translatable("mode.flightassistant.thrust.speed", target)
        )
    }

    companion object {
        val ID: Identifier = FlightAssistant.id("autopilot")
    }
}
