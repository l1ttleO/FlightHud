package ru.octol1ttle.flightassistant.impl.computer.safety

import java.time.Duration
import kotlin.math.*
import net.fabricmc.fabric.api.util.TriState
import net.minecraft.enchantment.*
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.*
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket
import net.minecraft.registry.RegistryKeys
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.*
import ru.octol1ttle.flightassistant.api.util.data
import ru.octol1ttle.flightassistant.config.FAConfig
import ru.octol1ttle.flightassistant.config.options.DisplayOptions
import ru.octol1ttle.flightassistant.impl.computer.AirDataComputer

class ElytraStatusComputer : Computer() {
    private var activeElytra: ItemStack? = null
    private var syncedFlyingState: TriState = TriState.DEFAULT

    override fun tick(computers: ComputerAccess) {
        val data: AirDataComputer = computers.data
        activeElytra = findActiveElytra(data.player)

        if (data.player.isOnGround) {
            syncedFlyingState = TriState.DEFAULT
            return
        }
        if (syncedFlyingState != TriState.DEFAULT) {
            if (syncedFlyingState.get() != data.flying) {
                syncedFlyingState = TriState.DEFAULT
            }
            return
        }
        if (!data.automationsAllowed(false)) {
            return
        }

        if (FAConfig.safety.elytraCloseUnderwater && data.flying && data.player.isSubmergedInWater) {
            sendSwitchState(data)
        }

        val flying: Boolean = data.flying || data.player.abilities.allowFlying
        val hasUsableElytra: Boolean = activeElytra != null && ElytraItem.isUsable(activeElytra)
        val notLookingToClutch: Boolean = data.pitch > -70.0f
        if (FAConfig.safety.elytraAutoOpen && !data.fallDistanceSafe && !flying && hasUsableElytra && notLookingToClutch) {
            sendSwitchState(data)
        }
    }

    private fun sendSwitchState(data: AirDataComputer) {
        syncedFlyingState = TriState.of(data.flying)
        data.player.networkHandler.sendPacket(ClientCommandC2SPacket(data.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING))
    }

    private fun findActiveElytra(player: PlayerEntity): ItemStack? {
        for (stack: ItemStack in player.armorItems) {
            if (stack.item is ElytraItem) {
                return stack
            }
        }
        for (stack: ItemStack in player.handItems) {
            if (stack.item is ElytraItem) {
                return stack
            }
        }

        return null
    }

    fun formatDurability(units: DisplayOptions.DurabilityUnits, player: PlayerEntity): Text? {
        val active: ItemStack = activeElytra ?: return null
        if (!active.isDamageable) {
            return Text.translatable("short.flightassistant.infinite")
        }

        val unbreakingLevel: Int = EnchantmentHelper.getLevel(
            player.world.registryManager.get(RegistryKeys.ENCHANTMENT).getEntry(Enchantments.UNBREAKING).get(), active
        )

        return when (units) {
            DisplayOptions.DurabilityUnits.RAW -> Text.literal((active.maxDamage - active.damage).toString())
            DisplayOptions.DurabilityUnits.PERCENTAGE -> Text.literal("${round((active.maxDamage - active.damage - 1) * 100 / active.maxDamage.toFloat()).roundToInt()}%")
            DisplayOptions.DurabilityUnits.TIME -> {
                val duration: Duration = Duration.ofSeconds(getRemainingFlightTime(player)!!.toLong())
                val seconds: Int = when (unbreakingLevel) {
                    0 -> duration.toSecondsPart()
                    1 -> MathHelper.roundDownToMultiple(duration.toSecondsPart().toDouble(), 5)
                    2 -> MathHelper.roundDownToMultiple(duration.toSecondsPart().toDouble(), 15)
                    else -> MathHelper.roundDownToMultiple(duration.toSecondsPart().toDouble(), 30)
                }
                Text.literal("${duration.toMinutesPart()}:${String.format("%02d", seconds)}")
            }
        }
    }

    fun getRemainingFlightTime(player: PlayerEntity): Int? {
        val active: ItemStack = activeElytra ?: return null
        if (!active.isDamageable) {
            return Int.MAX_VALUE
        }

        val unbreakingLevel: Int = EnchantmentHelper.getLevel(
            player.world.registryManager.get(RegistryKeys.ENCHANTMENT).getEntry(Enchantments.UNBREAKING).get(), active
        )
        return (active.maxDamage - active.damage - 1) * (unbreakingLevel + 1)
    }

    companion object {
        val ID: Identifier = FlightAssistant.id("elytra_status")
    }
}
