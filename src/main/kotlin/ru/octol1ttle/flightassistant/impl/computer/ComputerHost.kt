package ru.octol1ttle.flightassistant.impl.computer

import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.FlightAssistant.mc
import ru.octol1ttle.flightassistant.api.SystemHost
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.event.ComputerRegistrationCallback
import ru.octol1ttle.flightassistant.api.util.FATickCounter
import ru.octol1ttle.flightassistant.config.FAConfig
import ru.octol1ttle.flightassistant.impl.computer.autoflight.FireworkComputer
import ru.octol1ttle.flightassistant.impl.computer.autoflight.PitchComputer
import ru.octol1ttle.flightassistant.impl.computer.autoflight.ThrustComputer
import ru.octol1ttle.flightassistant.impl.computer.safety.*

internal object ComputerHost : ComputerAccess, SystemHost {
    private val computers: MutableMap<Identifier, Computer> = HashMap()

    override fun isEnabled(identifier: Identifier): Boolean {
        return get(identifier).enabled
    }

    override fun isFaulted(identifier: Identifier): Boolean {
        return get(identifier).faulted
    }

    override fun toggleEnabled(identifier: Identifier): Boolean {
        val computer: Computer = get(identifier)
        computer.enabled = !computer.enabled
        if (!computer.enabled) {
            computer.reset()
            computer.faulted = false
        }
        return computer.enabled
    }

    fun getFaultCount(identifier: Identifier): Int {
        return get(identifier).faultCount
    }

    fun identifiers(): Set<Identifier> {
        return computers.keys
    }

    private fun register(identifier: Identifier, computer: Computer) {
        if (computers.containsKey(identifier)) {
            throw IllegalArgumentException("Already registered computer with identifier: $identifier")
        }

        computers[identifier] = computer
    }

    private fun registerBuiltin() {
        register(AirDataComputer.ID, AirDataComputer(mc))

        register(StallComputer.ID, StallComputer())
        register(VoidProximityComputer.ID, VoidProximityComputer())
        register(GroundProximityComputer.ID, GroundProximityComputer())
        register(ElytraStatusComputer.ID, ElytraStatusComputer())
        register(ChunkStatusComputer.ID, ChunkStatusComputer())
        register(FireworkComputer.ID, FireworkComputer(mc))
        register(PitchComputer.ID, PitchComputer())
        register(ThrustComputer.ID, ThrustComputer())

        register(AlertComputer.ID, AlertComputer(mc.soundManager))
    }

    internal fun sendRegistrationEvent() {
        registerBuiltin()
        ComputerRegistrationCallback.EVENT.invoker().register(this::register)
        for (computer: Computer in computers.values) {
            computer.subscribeToEvents()
        }
        for (computer: Computer in computers.values) {
            computer.invokeEvents()
        }

        logRegisterComplete()
    }

    private fun logRegisterComplete() {
        val namespaces = ArrayList<String>()
        for (id: Identifier in computers.keys) {
            if (!namespaces.contains(id.namespace)) {
                namespaces.add(id.namespace)
            }
        }
        FlightAssistant.logger.info(
            "Registered {} computers from mods: {}",
            computers.size,
            namespaces.joinToString(", ")
        )
    }

    internal fun tick(tickDelta: Float) {
        val paused: Boolean = mc.isPaused /*? if >=1.21 {*//*|| !(mc as ru.octol1ttle.flightassistant.mixin.ClientShouldTickInvoker).invokeShouldTick() *///?}
        FATickCounter.tick(mc.player!!, tickDelta, paused)
        if (paused || FATickCounter.ticksSinceWorldLoad < 60 || !FAConfig.global.modEnabled) {
            return
        }

        for ((id: Identifier, computer: Computer) in computers) {
            if (computer.enabled && !computer.faulted) {
                try {
                    computer.tick(this)
                } catch (t: Throwable) {
                    computer.faulted = true
                    computer.faultCount++
                    FlightAssistant.logger.atError().setCause(t)
                        .log("Exception ticking computer with identifier: {}", id)
                }
            }
        }
    }

    override fun get(identifier: Identifier): Computer {
        return computers[identifier] ?: throw IllegalArgumentException("No computer registered with ID: $identifier")
    }
}
