package ru.octol1ttle.flightassistant.impl.display

import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.SystemController
import ru.octol1ttle.flightassistant.api.computer.ComputerView
import ru.octol1ttle.flightassistant.api.display.Display
import ru.octol1ttle.flightassistant.api.display.HudDisplayRegistrationCallback
import ru.octol1ttle.flightassistant.api.display.HudFrame
import ru.octol1ttle.flightassistant.api.util.FATickCounter
import ru.octol1ttle.flightassistant.api.util.RenderMatrices
import ru.octol1ttle.flightassistant.api.util.ScreenSpace
import ru.octol1ttle.flightassistant.api.util.extensions.centerXI
import ru.octol1ttle.flightassistant.api.util.extensions.centerYI
import ru.octol1ttle.flightassistant.api.util.extensions.drawMiddleAlignedText
import ru.octol1ttle.flightassistant.api.util.extensions.primaryColor
import ru.octol1ttle.flightassistant.config.FAConfig

internal object HudDisplayHost: SystemController<Display> {
    private val displays: MutableMap<Identifier, Display> = HashMap()

    override fun get(identifier: Identifier): Display {
        return displays[identifier] ?: throw IllegalArgumentException("No display was found with identifier: $identifier")
    }

    override fun isEnabled(identifier: Identifier): Boolean {
        return get(identifier).enabled
    }

    override fun isFaulted(identifier: Identifier): Boolean {
        return get(identifier).faulted
    }

    override fun setEnabled(identifier: Identifier, enabled: Boolean): Boolean {
        val display: Display = get(identifier)

        val oldEnabled: Boolean = display.enabled
        display.enabled = enabled
        return oldEnabled
    }

    fun countFaults(identifier: Identifier): Int {
        return get(identifier).faultCount
    }

    override fun identifiers(): Set<Identifier> {
        return displays.keys
    }

    override fun register(identifier: Identifier, system: Display) {
        if (FlightAssistant.initComplete) {
            throw IllegalStateException("Initialization is already complete, but trying to register a display with identifier: $identifier")
        }
        if (displays.containsKey(identifier)) {
            throw IllegalArgumentException("Already registered display with identifier: $identifier")
        }

        displays[identifier] = system
    }

    private fun registerBuiltin(computers: ComputerView) {
        register(AlertDisplay.ID, AlertDisplay(computers))
        register(AltitudeDisplay.ID, AltitudeDisplay(computers))
        register(AttitudeDisplay.ID, AttitudeDisplay(computers))
        register(AutomationModesDisplay.ID, AutomationModesDisplay(computers))
        register(CoordinatesDisplay.ID, CoordinatesDisplay(computers))
        register(ElytraDurabilityDisplay.ID, ElytraDurabilityDisplay(computers))
        register(FlightDirectorsDisplay.ID, FlightDirectorsDisplay(computers))
        register(FlightPathDisplay.ID, FlightPathDisplay(computers))
        register(HeadingDisplay.ID, HeadingDisplay(computers))
        register(RadarAltitudeDisplay.ID, RadarAltitudeDisplay(computers))
        register(SpeedDisplay.ID, SpeedDisplay(computers))
        register(VelocityComponentsDisplay.ID, VelocityComponentsDisplay(computers))
    }

    internal fun sendRegistrationEvent(computers: ComputerView) {
        registerBuiltin(computers)
        HudDisplayRegistrationCallback.EVENT.invoker().register(computers, this::register)
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

        HudFrame.updateDimensions()
        ScreenSpace.updateViewport()

        for ((id: Identifier, display: Display) in displays.filter { entry -> entry.value.allowedByConfig() }) {
            if (FATickCounter.ticksSinceWorldLoad < FATickCounter.worldLoadWaitTime) {
                with(drawContext) {
                    drawMiddleAlignedText(Text.translatable("misc.flightassistant.waiting_for_world_load"), centerXI, centerYI - 16, primaryColor)
                    drawMiddleAlignedText(Text.translatable("misc.flightassistant.waiting_for_world_load.maximum_time"), centerXI, centerYI + 8, primaryColor)
                }
                return
            }

            if (!display.enabled || !RenderMatrices.ready) {
                try {
                    display.renderFaulted(drawContext)
                } catch (t: Throwable) {
                    FlightAssistant.logger.atError().setCause(t)
                        .log("Exception rendering disabled display with identifier: {}", id)
                }
                continue
            }

            try {
                display.render(drawContext)
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
