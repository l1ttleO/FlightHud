package ru.octol1ttle.flightassistant.impl.computer.autoflight

import kotlin.math.abs
import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.autoflight.FlightController
import ru.octol1ttle.flightassistant.api.autoflight.heading.HeadingControllerRegistrationCallback
import ru.octol1ttle.flightassistant.api.autoflight.pitch.PitchControllerRegistrationCallback
import ru.octol1ttle.flightassistant.api.autoflight.roll.RollControllerRegistrationCallback
import ru.octol1ttle.flightassistant.api.autoflight.thrust.ThrustChangeCallback
import ru.octol1ttle.flightassistant.api.autoflight.thrust.ThrustControllerRegistrationCallback
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerView
import ru.octol1ttle.flightassistant.api.util.FATickCounter
import ru.octol1ttle.flightassistant.api.util.event.ChangeLookDirectionEvents

class AutomationsComputer(computers: ComputerView) : Computer(computers), FlightController {
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

    override fun subscribeToEvents() {
        ThrustControllerRegistrationCallback.EVENT.register { it.accept(this) }
        PitchControllerRegistrationCallback.EVENT.register { it.accept(this) }
        HeadingControllerRegistrationCallback.EVENT.register { it.accept(this) }
        RollControllerRegistrationCallback.EVENT.register { it.accept(this) }
        ThrustChangeCallback.EVENT.register(ThrustChangeCallback { _, _, input ->
            if (input?.identifier != AutopilotLogicComputer.ID) {
                setAutoThrust(false, alert = true)
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
                setAutoPilot(false, alert = true)
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
                setAutoPilot(false, alert = true)
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
                setAutoThrust(false, alert = true)
            }
        }

        if (autopilot) {
            autopilotAlert = false

            val pitchInput: ControlInput? = computers.pitch.activeInput
            if (computers.pitch.disabledOrFaulted() || pitchInput != null && pitchInput.identifier != AutopilotLogicComputer.ID) {
                setAutoPilot(false, alert = true)
            }

            val headingInput: ControlInput? = computers.heading.activeInput
            if (computers.heading.disabledOrFaulted() || headingInput != null && headingInput.identifier != AutopilotLogicComputer.ID) {
                setAutoPilot(false, alert = true)
            }
        }
    }

    fun setFlightDirectors(flightDirectors: Boolean) {
        this.flightDirectors = flightDirectors
    }

    fun setAutoThrust(autoThrust: Boolean, alert: Boolean? = null) {
        if (alert != null) {
            this.autoThrustAlert = this.autoThrust && !autoThrust && alert
        }
        this.autoThrust = autoThrust
    }

    fun setAutoPilot(autopilot: Boolean, alert: Boolean? = null) {
        if (alert != null) {
            this.autopilotAlert = this.autopilot && !autopilot && alert
        }
        this.autopilot = autopilot
    }

    override fun getThrustInput(): ControlInput? {
        if (!autoThrust) {
            return null
        }

        return computers.autopilot.computeThrust()
    }

    override fun getPitchInput(): ControlInput? {
        if (!flightDirectors && !autopilot) {
            return null
        }

        return computers.autopilot.computePitch(autopilot)
    }

    override fun getHeadingInput(): ControlInput? {
        if (!flightDirectors && !autopilot) {
            return null
        }

        return computers.autopilot.computeHeading(autopilot)
    }

    override fun getRollInput(): ControlInput? {
        if (!autopilot) {
            return null
        }

        return ControlInput(0.0f, ControlInput.Priority.NORMAL)
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
        val ID: Identifier = FlightAssistant.id("automations")
    }
}
