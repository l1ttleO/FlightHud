package ru.octol1ttle.flightassistant

import com.mojang.blaze3d.systems.RenderSystem
import dev.architectury.event.events.client.ClientLifecycleEvent
import net.minecraft.client.MinecraftClient
import net.minecraft.sound.SoundEvent
import net.minecraft.util.Identifier
import net.minecraft.util.math.RotationAxis
import org.joml.Matrix4f
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.octol1ttle.flightassistant.api.event.FixedHudRenderCallback
import ru.octol1ttle.flightassistant.api.event.WorldRenderCallback
import ru.octol1ttle.flightassistant.api.util.RenderMatrices
import ru.octol1ttle.flightassistant.config.FAConfig
import ru.octol1ttle.flightassistant.impl.computer.ComputerHost
import ru.octol1ttle.flightassistant.impl.display.HudDisplayHost

object FlightAssistant  {
    const val MOD_ID: String = "flightassistant"
    internal val mc: MinecraftClient = MinecraftClient.getInstance()
    internal val logger: Logger = LoggerFactory.getLogger("FlightAssistant")

    internal fun init() {
        logger.info("Initializing (stage 1)")
        FAConfig.load()
        FAKeyBindings.setup()
        ClientLifecycleEvent.CLIENT_STARTED.register {
            logger.info("Initializing (stage 2)")
            HudDisplayHost.sendRegistrationEvent()
            ComputerHost.sendRegistrationEvent()
        }
        WorldRenderCallback.EVENT.register { tickDelta, camera, projectionMatrix, positionMatrix ->
            FAKeyBindings.checkPressed(ComputerHost)

            ComputerHost.tick(tickDelta)

            RenderMatrices.projectionMatrix.set(projectionMatrix)
            RenderMatrices.worldSpaceMatrix.set(positionMatrix)
            RenderMatrices.modelViewMatrix.set(RenderSystem.getModelViewMatrix())

            RenderMatrices.worldSpaceNoRollMatrix.set(Matrix4f().apply {
                rotate(RotationAxis.POSITIVE_X.rotationDegrees(camera.pitch))
                rotate(RotationAxis.POSITIVE_Y.rotationDegrees(camera.yaw + 180.0f))
            })

            RenderMatrices.ready = true
        }
        FixedHudRenderCallback.EVENT.register { context, _ ->
            HudDisplayHost.render(context)
        }
    }

    internal fun id(path: String): Identifier {
//? if >=1.21 {
        /*return Identifier.of(MOD_ID, path)
*///?} else
        return Identifier(MOD_ID, path)
    }

    internal fun soundEvent(name: String): SoundEvent {
        return SoundEvent.of(id(name))
    }
}
