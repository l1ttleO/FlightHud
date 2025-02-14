package nl.enjarai.doabarrelroll.compat.flightassistant

import kotlin.math.sign
import net.minecraft.util.Identifier
import nl.enjarai.doabarrelroll.DoABarrelRoll
import nl.enjarai.doabarrelroll.api.event.ThrustEvents
import nl.enjarai.doabarrelroll.config.ModConfig
import ru.octol1ttle.flightassistant.api.autoflight.thrust.ThrustSource
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerView
import ru.octol1ttle.flightassistant.api.util.FATickCounter

class DaBRThrustComputer(computers: ComputerView) : Computer(computers), ThrustSource {
    override val priority: ThrustSource.Priority = ThrustSource.Priority.HIGH
    override val supportsReverse: Boolean = true
    override val optimumClimbPitch: Float
        get() = 30.0f

    override fun subscribeToEvents() {
        ThrustEvents.MODIFY_THRUST_INPUT.register({
            computers.thrust.setTarget((computers.thrust.current + FATickCounter.timePassed / 3 * sign(it)).toFloat().coerceIn(-1.0f..1.0f))
            if (!computers.thrust.disabledOrFaulted()) {
                return@register computers.thrust.current.toDouble()
            }
            return@register it
        }, 10)
    }

    override fun isAvailable(): Boolean {
        return ModConfig.INSTANCE.enableThrust
    }

    override fun calculateThrustForSpeed(targetSpeed: Int): Float {
        return targetSpeed / ModConfig.INSTANCE.maxThrust.toFloat()
    }

    override fun tick() {
    }

    override fun reset() {
    }

    override fun tickThrust(currentThrust: Float) {
    }

    companion object {
        val ID: Identifier = DoABarrelRoll.id("thrust")
    }
}
