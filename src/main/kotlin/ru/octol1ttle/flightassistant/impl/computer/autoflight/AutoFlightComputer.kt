package ru.octol1ttle.flightassistant.impl.computer.autoflight

import net.minecraft.text.Text
import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.computer.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.computer.autoflight.pitch.PitchController
import ru.octol1ttle.flightassistant.api.computer.autoflight.thrust.ThrustController
import ru.octol1ttle.flightassistant.api.event.autoflight.pitch.PitchControllerRegistrationCallback
import ru.octol1ttle.flightassistant.api.event.autoflight.thrust.ThrustChangeCallback
import ru.octol1ttle.flightassistant.api.event.autoflight.thrust.ThrustControllerRegistrationCallback
import ru.octol1ttle.flightassistant.api.util.data
import ru.octol1ttle.flightassistant.api.util.pitch
import ru.octol1ttle.flightassistant.api.util.protections
import ru.octol1ttle.flightassistant.api.util.thrust

class AutoFlightComputer : Computer(), ThrustController, PitchController {
    var flightDirectors: Boolean = false

    var autoThrust: Boolean = false
    var autoThrustAlert: Boolean = false

    var autopilot: Boolean = false
    var autopilotAlert: Boolean = false

    var selectedSpeed: Int? = null
    var selectedPitch: Float? = null

    override fun subscribeToEvents() {
        ThrustControllerRegistrationCallback.EVENT.register { it.accept(this) }
        PitchControllerRegistrationCallback.EVENT.register { it.accept(this) }
        ThrustChangeCallback.EVENT.register(ThrustChangeCallback { _, _, input ->
            if (input?.identifier != ID) {
                if (autoThrust) {
                    autoThrustAlert = true
                }
                autoThrust = false
            }
            if (input == null && autoThrustAlert) {
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

        if (autopilot) {
            autopilotAlert = false
            flightDirectors = true

            val activeInput: ControlInput? = computers.pitch.activeInput
            if (activeInput != null && activeInput.identifier != ID) {
                autopilot = false
                autopilotAlert = true
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

        return ControlInput(
            computers.thrust.calculateThrustForSpeed(computers, target) ?: 0.0f,
            ControlInput.Priority.NORMAL,
            Text.translatable("mode.flightassistant.thrust.speed", target),
            identifier = ID
        )
    }

    override fun getPitchInput(computers: ComputerAccess): ControlInput? {
        if (!flightDirectors && !autopilot) {
            return null
        }

        if (selectedPitch == null) {
            selectedPitch = computers.data.pitch
        }

        return ControlInput(
            selectedPitch!!,
            ControlInput.Priority.NORMAL,
            Text.translatable("mode.flightassistant.pitch.selected", "%.1f".format(selectedPitch)),
            active = autopilot,
            identifier = ID
        )
    }

    companion object {
        val ID: Identifier = FlightAssistant.id("autopilot")
    }
}
