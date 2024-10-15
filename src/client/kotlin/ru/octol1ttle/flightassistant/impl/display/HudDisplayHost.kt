package ru.octol1ttle.flightassistant.impl.display

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.FlightAssistant.mc
import ru.octol1ttle.flightassistant.api.display.*
import ru.octol1ttle.flightassistant.api.event.HudDisplayRegistrationCallback
import ru.octol1ttle.flightassistant.api.util.*
import ru.octol1ttle.flightassistant.config.FAConfig
import ru.octol1ttle.flightassistant.impl.computer.ComputerHost

internal object HudDisplayHost {
    private val displays: MutableMap<Identifier, Display> = HashMap()

    fun isFaulted(identifier: Identifier): Boolean {
        return displays[identifier]?.faulted
            ?: throw IllegalArgumentException("No display was found with identifier: $identifier")
    }

    fun identifiers(): Set<Identifier> {
        return displays.keys
    }

    private fun register(identifier: Identifier, display: Display) {
        if (displays.containsKey(identifier)) {
            throw IllegalArgumentException("Already registered display with identifier: $identifier")
        }

        displays[identifier] = display
    }

    internal fun registerBuiltin() {
        register(AlertDisplay.ID, AlertDisplay())
        register(AltitudeDisplay.ID, AltitudeDisplay())
        register(AttitudeDisplay.ID, AttitudeDisplay())
        register(CoordinatesDisplay.ID, CoordinatesDisplay())
        register(ElytraDurabilityDisplay.ID, ElytraDurabilityDisplay())
        register(FlightPathDisplay.ID, FlightPathDisplay())
        register(RadarAltitudeDisplay.ID, RadarAltitudeDisplay())
        register(SpeedDisplay.ID, SpeedDisplay())
        register(VelocityComponentsDisplay.ID, VelocityComponentsDisplay())
    }

    internal fun sendRegistrationEvent() {
        registerBuiltin()
        HudDisplayRegistrationCallback.EVENT.invoker().register(this::register)
        logRegisterComplete()
    }

    private fun logRegisterComplete() {
        val namespaces = ArrayList<String>()
        for (id: Identifier in displays.keys) {
            if (!namespaces.contains(id.namespace)) {
                namespaces.add(id.namespace)
            }
        }
        FlightAssistant.logger.info(
            "Registered {} displays from mods: {}",
            displays.size,
            namespaces.joinToString(", ")
        )
    }

    internal fun render(drawContext: DrawContext, tickCounter: RenderTickCounter) {
        if (!FAConfig.hudEnabled) {
            return
        }

        HudFrame.update()
        updateViewport()

        val tickDelta: Float = tickCounter.getTickDelta(true)

        mc.profiler.push("flightassistant:displays")
        for ((id: Identifier, display: Display) in displays.filter { entry -> entry.value.enabled() }) {
            mc.profiler.push(id.toString())
            if (display.faulted || !RenderMatrices.ready) {
                try {
                    display.renderFaulted(drawContext)
                } catch (t: Throwable) {
                    FlightAssistant.logger.atError().setCause(t)
                        .log("Exception rendering already faulted display with identifier: {}", id)
                }
            } else {
                try {
                    display.render(drawContext, ComputerHost)
                } catch (t: Throwable) {
                    display.faulted = true
                    FlightAssistant.logger.atError().setCause(t)
                        .log("Exception rendering display with identifier: {}", id)
                }
            }
            mc.profiler.pop()
        }
        mc.profiler.pop()
    }
}
