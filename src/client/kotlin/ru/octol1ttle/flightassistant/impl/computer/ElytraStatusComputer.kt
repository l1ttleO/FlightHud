package ru.octol1ttle.flightassistant.impl.computer

import dev.isxander.yacl3.api.NameableEnum
import java.time.Duration
import kotlin.math.*
import net.minecraft.enchantment.*
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.*
import net.minecraft.registry.RegistryKeys
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.*
import ru.octol1ttle.flightassistant.api.util.data

class ElytraStatusComputer : Computer() {
    private var activeElytra: ItemStack? = null

    override fun tick(computers: ComputerAccess) {
        val player: PlayerEntity = computers.data.player

        for (stack in player.armorItems) {
            if (stack.item is ElytraItem) { // TODO: update to 1.21.3 and replace with isGliding check
                activeElytra = stack
                return
            }
        }
        for (stack in player.handItems) {
            if (stack.item is ElytraItem) {
                activeElytra = stack
                return
            }
        }
    }

    fun formatDurability(units: DurabilityUnits, player: PlayerEntity): Text? {
        val active: ItemStack = activeElytra ?: return null
        if (!active.isDamageable) {
            return Text.translatable("short.flightassistant.infinite")
        }

        val unbreakingLevel: Int = EnchantmentHelper.getLevel(
            player.world.registryManager.get(RegistryKeys.ENCHANTMENT).getEntry(Enchantments.UNBREAKING).get(), active
        )

        return when (units) {
            DurabilityUnits.RAW -> Text.literal((active.maxDamage - active.damage).toString())
            DurabilityUnits.PERCENTAGE -> Text.literal("${round((active.maxDamage - active.damage - 1) * 100 / active.maxDamage.toFloat()).roundToInt()}%")
            DurabilityUnits.TIME -> {
                val duration: Duration = Duration.ofSeconds(getRemainingFlightTime(player)!!.toLong())
                val seconds: String = if (unbreakingLevel > 0) "XX" else String.format("%02d", duration.toSecondsPart())
                Text.literal("${duration.toMinutesPart()}:$seconds")
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
        val ID: Identifier = FlightAssistant.computerId("elytra_status")
    }

    enum class DurabilityUnits : NameableEnum {
        RAW {
            override fun getDisplayName(): Text {
                return Text.translatable("config.flightassistant.options.display.elytra_durability.units.raw")
            }
        },
        PERCENTAGE {
            override fun getDisplayName(): Text {
                return Text.translatable("config.flightassistant.options.display.elytra_durability.units.percentage")
            }
        },
        TIME {
            override fun getDisplayName(): Text {
                return Text.translatable("config.flightassistant.options.display.elytra_durability.units.time")
            }
        };
    }
}
