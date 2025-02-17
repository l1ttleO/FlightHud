package ru.octol1ttle.flightassistant.impl.computer.autoflight

import net.minecraft.text.Text
import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerView

class AutopilotLogicComputer(computers: ComputerView) : Computer(computers) {
    var thrustMode: ThrustMode = ThrustMode(ThrustMode.Type.SelectedSpeed, 15.0f)
    var verticalMode: VerticalMode = VerticalMode(VerticalMode.Type.SelectedPitch, 0.0f)
    var lateralMode: LateralMode = LateralMode(LateralMode.Type.SelectedHeading, 360.0f)

    fun computeThrust(): ControlInput {
        return when (thrustMode.type) {
            ThrustMode.Type.SelectedSpeed ->
                ControlInput(
                    computers.thrust.calculateThrustForSpeed(thrustMode.speed) ?: 0.0f,
                    ControlInput.Priority.NORMAL,
                    Text.translatable("mode.flightassistant.thrust.selected_speed"),
                    identifier = ID
                )
            ThrustMode.Type.VerticalTarget -> TODO()
            ThrustMode.Type.WaypointThrust -> TODO()
        }
    }

    fun computePitch(active: Boolean): ControlInput {
        TODO()
    }

    fun computeHeading(active: Boolean): ControlInput {
        TODO()
    }

    override fun tick() {
    }

    override fun reset() {
    }

    class ThrustMode(var type: Type, var speed: Float) {
        enum class Type {
            SelectedSpeed,
            VerticalTarget,
            WaypointThrust
        }
    }

    class VerticalMode(var type: Type, var pitchOrAltitude: Float) {
        enum class Type {
            SelectedPitch,
            SelectedAltitude,
            WaypointAltitude
        }
    }

    class LateralMode(var type: Type, var heading: Float? = null, var x: Double? = null, var z: Double? = null) {
        enum class Type {
            SelectedHeading,
            SelectedCoordinates,
            WaypointCoordinates
        }
    }

    companion object {
        val ID: Identifier = FlightAssistant.id("autopilot_logic")
    }
}
