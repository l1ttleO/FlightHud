package ru.octol1ttle.flightassistant.impl.computer.safety

import java.time.Duration
import kotlin.math.round
import kotlin.math.roundToInt
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerView
import ru.octol1ttle.flightassistant.api.util.extensions.canUse
import ru.octol1ttle.flightassistant.config.FAConfig
import ru.octol1ttle.flightassistant.config.options.DisplayOptions
import ru.octol1ttle.flightassistant.impl.computer.AirDataComputer

class ElytraStatusComputer(computers: ComputerView) : Computer(computers) {
    private var activeElytra: ItemStack? = null
    private var syncedFlyingState: Boolean? = null

    override fun tick() {
        val data: AirDataComputer = computers.data
        activeElytra = findActiveElytra(data.player)

        if (data.player.isOnGround) {
            syncedFlyingState = null
            return
        }
        if (syncedFlyingState != null) {
            if (syncedFlyingState != data.flying) {
                syncedFlyingState = null
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
        val hasUsableElytra: Boolean = data.player.armorItems.contains(activeElytra) && activeElytra.canUse()
        val notLookingToClutch: Boolean = data.pitch > -70.0f
        if (FAConfig.safety.elytraAutoOpen && !data.fallDistanceSafe && !flying && hasUsableElytra && notLookingToClutch) {
            sendSwitchState(data)
        }
    }

    private fun sendSwitchState(data: AirDataComputer) {
        syncedFlyingState = data.flying
//? if neoforge {
        /*data.player.networkHandler.send(ClientCommandC2SPacket(data.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING))
*///?} else {
        data.player.networkHandler.sendPacket(ClientCommandC2SPacket(data.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING))
//?}
    }

    private fun findActiveElytra(player: PlayerEntity): ItemStack? {
//? if >=1.21.2 {
        /*for (equipmentSlot in net.minecraft.entity.EquipmentSlot.VALUES) {
            val stack: ItemStack = player.getEquippedStack(equipmentSlot)
            if (net.minecraft.entity.LivingEntity.canGlideWith(stack, equipmentSlot)) {
                return stack
            }
        }
*///?} else {
        for (stack: ItemStack in player.armorItems) {
            if (stack.item is net.minecraft.item.ElytraItem) {
                return stack
            }
        }
//?}
        for (stack: ItemStack in player.handItems) {
//? if >=1.21.2 {
            /*if (stack.contains(net.minecraft.component.DataComponentTypes.GLIDER)) {
*///?} else
            if (stack.item is net.minecraft.item.ElytraItem) {
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
//? if >=1.21.2 {
            /*player.world.registryManager.getOrThrow(net.minecraft.registry.RegistryKeys.ENCHANTMENT).getEntry(Enchantments.UNBREAKING.value).get()
*///?} else if >=1.21 {
            /*player.world.registryManager.get(net.minecraft.registry.RegistryKeys.ENCHANTMENT).getEntry(Enchantments.UNBREAKING).get()
*///?} else
            Enchantments.UNBREAKING
            , active
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
                Text.literal("${duration.toMinutesPart()}:${"%02d".format(seconds)}")
            }
        }
    }

    fun getRemainingFlightTime(@Suppress("UNUSED_PARAMETER", "KotlinRedundantDiagnosticSuppress") player: PlayerEntity): Int? {
        val active: ItemStack = activeElytra ?: return null
        if (!active.isDamageable) {
            return Int.MAX_VALUE
        }

        val unbreakingLevel: Int = EnchantmentHelper.getLevel(
//? if >=1.21.2 {
            /*player.world.registryManager.getOrThrow(net.minecraft.registry.RegistryKeys.ENCHANTMENT).getEntry(Enchantments.UNBREAKING.value).get()
*///?} else if >=1.21 {
            /*player.world.registryManager.get(net.minecraft.registry.RegistryKeys.ENCHANTMENT).getEntry(Enchantments.UNBREAKING).get()
*///?} else
            Enchantments.UNBREAKING
            , active
        )
        return (active.maxDamage - active.damage - 1) * (unbreakingLevel + 1)
    }


    override fun reset() {
        activeElytra = null
        syncedFlyingState = null
    }

    companion object {
        val ID: Identifier = FlightAssistant.id("elytra_status")
    }
}
