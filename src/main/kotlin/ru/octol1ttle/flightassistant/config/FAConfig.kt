package ru.octol1ttle.flightassistant.config

import com.google.gson.GsonBuilder
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder
import dev.isxander.yacl3.platform.YACLPlatform
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.item.Items
import ru.octol1ttle.flightassistant.FlightAssistant.MOD_ID
import ru.octol1ttle.flightassistant.FlightAssistant.id
import ru.octol1ttle.flightassistant.FlightAssistant.mc
import ru.octol1ttle.flightassistant.api.util.extensions.fallFlying
import ru.octol1ttle.flightassistant.config.options.DisplayOptions
import ru.octol1ttle.flightassistant.config.options.DisplayOptionsStorage
import ru.octol1ttle.flightassistant.config.options.GlobalOptions
import ru.octol1ttle.flightassistant.config.options.SafetyOptions

object FAConfig {
    private val GLOBAL_HANDLER: ConfigClassHandler<GlobalOptions> =
        ConfigClassHandler.createBuilder(GlobalOptions::class.java)
            .id(id("global"))
            .serializer {
                GsonConfigSerializerBuilder.create(it)
                    .setPath(YACLPlatform.getConfigDir().resolve("$MOD_ID/global.json5"))
                    .appendGsonBuilder(GsonBuilder::setPrettyPrinting) // not needed, pretty print by default
                    .setJson5(true)
                    .build()
            }
            .build()

    private val DISPLAY_HANDLER: ConfigClassHandler<DisplayOptionsStorage> =
        ConfigClassHandler.createBuilder(DisplayOptionsStorage::class.java)
            .id(id("displays"))
            .serializer {
                GsonConfigSerializerBuilder.create(it)
                    .setPath(YACLPlatform.getConfigDir().resolve("$MOD_ID/displays.json5"))
                    .appendGsonBuilder(GsonBuilder::setPrettyPrinting) // not needed, pretty print by default
                    .setJson5(true)
                    .build()
            }
            .build()

    private val SAFETY_HANDLER: ConfigClassHandler<SafetyOptions> =
        ConfigClassHandler.createBuilder(SafetyOptions::class.java)
            .id(id("safety"))
            .serializer {
                GsonConfigSerializerBuilder.create(it)
                    .setPath(YACLPlatform.getConfigDir().resolve("$MOD_ID/safety.json5"))
                    .appendGsonBuilder(GsonBuilder::setPrettyPrinting) // not needed, pretty print by default
                    .setJson5(true)
                    .build()
            }
            .build()

    internal val global: GlobalOptions
        get() = GLOBAL_HANDLER.instance()
    internal val hudEnabled: Boolean
        get() = global.modEnabled && global.hudEnabled
    internal val safetyEnabled: Boolean
        get() = global.modEnabled && global.safetyEnabled
    internal val displaysStorage: DisplayOptionsStorage
        get() = DISPLAY_HANDLER.instance()

    val display: DisplayOptions
        get() {
            val player: ClientPlayerEntity = checkNotNull(mc.player)

            if (player.fallFlying) {
                return displaysStorage.flying
            }

            if (!player.abilities.allowFlying && player.
//? if >=1.21 {
                /*equippedItems
*///?} else
                itemsEquipped
                    .any { stack -> stack.item == Items.ELYTRA }) {
                return displaysStorage.notFlyingHasElytra
            }

            return displaysStorage.notFlyingNoElytra
        }

    val safety: SafetyOptions
        get() = if (safetyEnabled) SAFETY_HANDLER.instance() else SafetyOptions.DISABLED

    fun load() {
        GLOBAL_HANDLER.load()
        DISPLAY_HANDLER.load()
        SAFETY_HANDLER.load()
    }

    fun save() {
        GLOBAL_HANDLER.save()
        DISPLAY_HANDLER.save()
        SAFETY_HANDLER.save()
    }
}
