package ru.octol1ttle.flightassistant.impl.computer

import net.minecraft.client.render.RenderTickCounter
import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.FlightAssistant.mc
import ru.octol1ttle.flightassistant.api.computer.*
import ru.octol1ttle.flightassistant.api.event.ComputerRegistrationCallback
import ru.octol1ttle.flightassistant.api.util.FATickCounter
import ru.octol1ttle.flightassistant.config.FAConfig
import ru.octol1ttle.flightassistant.impl.computer.autoflight.*
import ru.octol1ttle.flightassistant.impl.computer.safety.*
import ru.octol1ttle.flightassistant.mixin.ClientShouldTickInvoker

internal object ComputerHost : ComputerAccess {
    private val computers: MutableMap<Identifier, Computer> = HashMap()

    fun isFaulted(identifier: Identifier): Boolean {
        return get(identifier).faulted
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
        register(PitchComputer.ID, PitchComputer())
        register(ThrustComputer.ID, ThrustComputer())
        register(FireworkComputer.ID, FireworkComputer(mc))

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

    internal fun tick(tickCounter: RenderTickCounter) {
        val paused: Boolean = mc.isPaused || !(mc as ClientShouldTickInvoker).invokeShouldTick()
        FATickCounter.tick(mc.player!!, tickCounter, paused)
        if (paused || !FAConfig.global.modEnabled) {
            return
        }

        mc.profiler.push("flightassistant:computers")
        for ((id: Identifier, computer: Computer) in computers) {
            mc.profiler.push(id.toString())
            if (!computer.faulted) {
                try {
                    computer.tick(this)
                } catch (t: Throwable) {
                    computer.faulted = true
                    FlightAssistant.logger.atError().setCause(t)
                        .log("Exception ticking computer with identifier: {}", id)
                }
            }
            mc.profiler.pop()
        }
        mc.profiler.pop()
    }

    override fun get(identifier: Identifier): Computer {
        return computers[identifier] ?: throw IllegalArgumentException("No computer registered with ID: $identifier")
    }
}
