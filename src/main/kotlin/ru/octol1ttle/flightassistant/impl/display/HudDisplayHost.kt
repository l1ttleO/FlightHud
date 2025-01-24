package ru.octol1ttle.flightassistant.impl.display

import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.MutableMap
import kotlin.collections.Set
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.filter
import kotlin.collections.iterator
import kotlin.collections.joinToString
import kotlin.collections.set
import net.minecraft.client.gui.DrawContext
import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.SystemHost
import ru.octol1ttle.flightassistant.api.display.Display
import ru.octol1ttle.flightassistant.api.display.HudFrame
import ru.octol1ttle.flightassistant.api.event.HudDisplayRegistrationCallback
import ru.octol1ttle.flightassistant.api.util.FATickCounter
import ru.octol1ttle.flightassistant.api.util.RenderMatrices
import ru.octol1ttle.flightassistant.api.util.updateViewport
import ru.octol1ttle.flightassistant.config.FAConfig
import ru.octol1ttle.flightassistant.impl.computer.ComputerHost

internal object HudDisplayHost: SystemHost {
    private val displays: MutableMap<Identifier, Display> = HashMap()

    private fun get(identifier: Identifier): Display {
        return displays[identifier] ?: throw IllegalArgumentException("No display was found with identifier: $identifier")
    }

    override fun isEnabled(identifier: Identifier): Boolean {
        return get(identifier).enabled
    }

    override fun isFaulted(identifier: Identifier): Boolean {
        return get(identifier).faulted
    }

    override fun toggleEnabled(identifier: Identifier): Boolean {
        val display: Display = get(identifier)
        display.enabled = !display.enabled
        return display.enabled
    }

    fun countFaults(identifier: Identifier): Int {
        return get(identifier).faultCount
    }

    override fun identifiers(): Set<Identifier> {
        return displays.keys
    }

    private fun register(identifier: Identifier, display: Display) {
        if (displays.containsKey(identifier)) {
            throw IllegalArgumentException("Already registered display with identifier: $identifier")
        }

        displays[identifier] = display
    }

    private fun registerBuiltin() {
        register(AlertDisplay.ID, AlertDisplay())
        register(AltitudeDisplay.ID, AltitudeDisplay())
        register(AttitudeDisplay.ID, AttitudeDisplay())
        register(AutomationModesDisplay.ID, AutomationModesDisplay())
        register(CoordinatesDisplay.ID, CoordinatesDisplay())
        register(ElytraDurabilityDisplay.ID, ElytraDurabilityDisplay())
        register(FlightDirectorsDisplay.ID, FlightDirectorsDisplay())
        register(FlightPathDisplay.ID, FlightPathDisplay())
        register(HeadingDisplay.ID, HeadingDisplay())
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

    fun render(drawContext: DrawContext) {
        if (!FAConfig.hudEnabled) {
            return
        }

        HudFrame.update()
        updateViewport()

        for ((id: Identifier, display: Display) in displays.filter { entry -> entry.value.allowedByConfig() }) {
            if (!display.enabled || !RenderMatrices.ready || FATickCounter.ticksSinceWorldLoad < 60) {
                try {
                    display.renderFaulted(drawContext)
                } catch (t: Throwable) {
                    FlightAssistant.logger.atError().setCause(t)
                        .log("Exception rendering already faulted display with identifier: {}", id)
                }
                continue
            }

            try {
                display.render(drawContext, ComputerHost)
                display.faulted = false
            } catch (t: Throwable) {
                display.faulted = true
                display.faultCount++
                display.enabled = false
                FlightAssistant.logger.atError().setCause(t)
                    .log("Exception rendering display with identifier: {}", id)
            }
        }
    }
}
