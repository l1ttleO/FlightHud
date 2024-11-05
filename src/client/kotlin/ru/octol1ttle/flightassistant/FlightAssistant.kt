package ru.octol1ttle.flightassistant

import com.mojang.blaze3d.systems.RenderSystem
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.rendering.v1.*
import net.minecraft.client.MinecraftClient
import net.minecraft.sound.SoundEvent
import net.minecraft.util.Identifier
import net.minecraft.util.math.RotationAxis
import org.joml.Matrix4f
import org.slf4j.*
import ru.octol1ttle.flightassistant.api.util.RenderMatrices
import ru.octol1ttle.flightassistant.config.FAConfig
import ru.octol1ttle.flightassistant.impl.computer.ComputerHost
import ru.octol1ttle.flightassistant.impl.display.HudDisplayHost

object FlightAssistant : ClientModInitializer {
    const val MOD_ID: String = "flightassistant"
    internal val mc: MinecraftClient = MinecraftClient.getInstance()
    internal val logger: Logger = LoggerFactory.getLogger("FlightAssistant")

    override fun onInitializeClient() {
        logger.info("Initializing (stage 1)")
        FAConfig.load()
        registerCallbacks()
    }

    private fun registerCallbacks() {
        ClientLifecycleEvents.CLIENT_STARTED.register {
            logger.info("Initializing (stage 2)")
            ComputerHost.sendRegistrationEvent()
            HudDisplayHost.sendRegistrationEvent()
        }
        WorldRenderEvents.START.register {
            ComputerHost.tick(it.tickCounter())

            RenderMatrices.projectionMatrix.set(it.projectionMatrix())
            RenderMatrices.worldSpaceMatrix.set(it.positionMatrix())
            RenderMatrices.modelViewMatrix.set(RenderSystem.getModelViewMatrix())

            RenderMatrices.worldSpaceNoRollMatrix.set(Matrix4f().apply {
                rotate(RotationAxis.POSITIVE_X.rotationDegrees(it.camera().pitch))
                rotate(RotationAxis.POSITIVE_Y.rotationDegrees(it.camera().yaw + 180.0f))
            })

            RenderMatrices.ready = true
        }
        HudRenderCallback.EVENT.register { drawContext, _ -> HudDisplayHost.render(drawContext) }
    }

    internal fun id(path: String): Identifier {
        return Identifier.of(MOD_ID, path)
    }

    internal fun soundEvent(name: String): SoundEvent {
        return SoundEvent.of(id(name))
    }
}
