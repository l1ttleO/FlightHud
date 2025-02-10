package ru.octol1ttle.flightassistant.impl.computer.autoflight

import kotlin.math.abs
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.autoflight.heading.HeadingController
import ru.octol1ttle.flightassistant.api.autoflight.heading.HeadingControllerRegistrationCallback
import ru.octol1ttle.flightassistant.api.autoflight.pitch.PitchController
import ru.octol1ttle.flightassistant.api.autoflight.pitch.PitchControllerRegistrationCallback
import ru.octol1ttle.flightassistant.api.autoflight.thrust.ThrustChangeCallback
import ru.octol1ttle.flightassistant.api.autoflight.thrust.ThrustController
import ru.octol1ttle.flightassistant.api.autoflight.thrust.ThrustControllerRegistrationCallback
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerView
import ru.octol1ttle.flightassistant.api.util.FATickCounter
import ru.octol1ttle.flightassistant.api.util.event.ChangeLookDirectionEvents

class AutoFlightComputer(computers: ComputerView) : Computer(computers), ThrustController, PitchController, HeadingController {
    var flightDirectors: Boolean = false
        private set

    var autoThrust: Boolean = false
        private set
    var autoThrustAlert: Boolean = false
        private set

    var autopilot: Boolean = false
        private set
    var autopilotAlert: Boolean = false
        private set
    private var pitchResistance: Float = 0.0f
    private var headingResistance: Float = 0.0f

    var selectedSpeed: Int? = null
    var selectedPitch: Float? = null
    var selectedHeading: Int? = null

    override fun subscribeToEvents() {
        ThrustControllerRegistrationCallback.EVENT.register { it.accept(this) }
        PitchControllerRegistrationCallback.EVENT.register { it.accept(this) }
        HeadingControllerRegistrationCallback.EVENT.register { it.accept(this) }
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
        ChangeLookDirectionEvents.PITCH.register(ChangeLookDirectionEvents.ChangeLookDirection { pitchDelta, output ->
            if (autopilot) {
                pitchResistance += abs(pitchDelta)
                if (pitchResistance < 20.0f) {
                    output.add(ControlInput(0.0f, ControlInput.Priority.NORMAL))
                    return@ChangeLookDirection
                }
                autopilot = false
                autopilotAlert = true
            }

            pitchResistance = 0.0f
        })
        ChangeLookDirectionEvents.HEADING.register(ChangeLookDirectionEvents.ChangeLookDirection { headingDelta, output ->
            if (autopilot) {
                headingResistance += abs(headingDelta)
                if (headingResistance < 40.0f) {
                    output.add(ControlInput(0.0f, ControlInput.Priority.NORMAL))
                    return@ChangeLookDirection
                }
                autopilot = false
                autopilotAlert = true
            }

            headingResistance = 0.0f
        })
    }

    override fun tick() {
        if (computers.protections.protectionsLost || !computers.data.isCurrentChunkLoaded) {
            reset()
            return
        }

        if (computers.pitch.manualOverride) {
            setAutoPilot(false, alert = false)
        }

        pitchResistance = (pitchResistance - FATickCounter.timePassed * 10.0f).coerceAtLeast(0.0f)
        headingResistance = (headingResistance - FATickCounter.timePassed * 20.0f).coerceAtLeast(0.0f)

        if (autoThrust) {
            autoThrustAlert = false
            if (computers.thrust.faulted) {
                autoThrust = false
                autoThrustAlert = true
            }
        }

        if (autopilot) {
            autopilotAlert = false

            val pitchInput: ControlInput? = computers.pitch.activeInput
            if (computers.pitch.disabledOrFaulted() || pitchInput != null && pitchInput.identifier != ID) {
                autopilot = false
                autopilotAlert = true
            }

            val headingInput: ControlInput? = computers.heading.activeInput
            if (computers.heading.disabledOrFaulted() || headingInput != null && headingInput.identifier != ID) {
                autopilot = false
                autopilotAlert = true
            }
        }
    }

    fun setFlightDirectors(flightDirectors: Boolean) {
        if (flightDirectors) {
            setDefaultSelections()
        }
        this.flightDirectors = flightDirectors
    }

    fun setAutoThrust(autoThrust: Boolean, alert: Boolean? = null) {
        if (autoThrust && !this.autoThrust && this.selectedSpeed == null) {
            this.selectedSpeed = (computers.data.forwardVelocity.length() * 20).toInt().coerceAtLeast(1)
        }
        this.autoThrust = autoThrust
        if (alert != null) {
            this.autoThrustAlert = !autoThrust && alert
        }
    }

    fun setAutoPilot(autopilot: Boolean, alert: Boolean? = null) {
        if (autopilot) {
            setDefaultSelections()
        }
        this.autopilot = autopilot
        if (alert != null) {
            this.autopilotAlert = !autopilot && alert
        }
    }

    private fun setDefaultSelections() {
        if (!this.flightDirectors && !this.autopilot && this.selectedPitch == null && this.selectedHeading == null) {
            this.selectedPitch = computers.data.pitch
            this.selectedHeading = computers.data.heading.toInt()
        }
    }

    override fun getThrustInput(): ControlInput? {
        if (!autoThrust) {
            return null
        }

        val target: Int = selectedSpeed ?: return null

        return ControlInput(
            computers.thrust.calculateThrustForSpeed(target) ?: 0.0f,
            ControlInput.Priority.NORMAL,
            Text.translatable("mode.flightassistant.thrust.speed", target),
            identifier = ID
        )
    }

    override fun getPitchInput(): ControlInput? {
        if (!flightDirectors && !autopilot) {
            return null
        }

        val pitch: Float = selectedPitch ?: return null

        return ControlInput(
            pitch,
            ControlInput.Priority.NORMAL,
            Text.translatable("mode.flightassistant.pitch.selected", "%.1f".format(pitch)),
            active = autopilot,
            identifier = ID
        )
    }

    override fun getHeadingInput(): ControlInput? {
        if (!flightDirectors && !autopilot) {
            return null
        }

        val heading: Int = selectedHeading ?: return null

        return ControlInput(
            heading.toFloat(),
            ControlInput.Priority.NORMAL,
            Text.translatable("mode.flightassistant.heading.selected", heading),
            active = autopilot,
            identifier = ID
        )
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
        pitchResistance = 0.0f
        headingResistance = 0.0f
    }

    companion object {
        val ID: Identifier = FlightAssistant.id("autopilot")
    }
}
