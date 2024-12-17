package ru.octol1ttle.flightassistant.impl.computer.autoflight

import net.minecraft.client.MinecraftClient
import net.minecraft.component.DataComponentTypes
import net.minecraft.entity.player.*
import net.minecraft.item.*
import net.minecraft.util.*
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.*
import ru.octol1ttle.flightassistant.api.computer.autoflight.thrust.ThrustSource
import ru.octol1ttle.flightassistant.api.event.autoflight.thrust.ThrustSourceRegistrationCallback
import ru.octol1ttle.flightassistant.api.util.*

class FireworkComputer(private val mc: MinecraftClient) : Computer(), ThrustSource {
    override val priority: ThrustSource.Priority
        get() = ThrustSource.Priority.LOW
    override val supportsReverse: Boolean
        get() = false
    override val optimumClimbPitch: Float
        get() = 55.0f

    private var safeFireworkCount: Int = 0
    private var safeFireworkSlot: Int? = 0

    private var lastActivationTime: Int = 0

    override fun subscribeToEvents() {
        ThrustSourceRegistrationCallback.EVENT.register { it.accept(this) }
    }

    override fun tick(computers: ComputerAccess) {
        safeFireworkCount = 0
        safeFireworkSlot = null
        var lastSlotCount = 0
        for (slot: Int in 0..<PlayerInventory.getHotbarSize()) {
            val stack: ItemStack = computers.data.player.inventory.getStack(slot)
            if (isFireworkAndSafe(stack)) {
                safeFireworkCount += stack.count
                if (safeFireworkSlot == null || stack.count < lastSlotCount) {
                    safeFireworkSlot = slot
                    lastSlotCount = stack.count
                }
            }
        }
    }

    private fun isFireworkAndSafe(stack: ItemStack): Boolean {
        return stack.item is FireworkRocketItem && hasNoExplosions(stack)
    }

    private fun hasNoExplosions(stack: ItemStack): Boolean {
        return stack.get(DataComponentTypes.FIREWORKS)?.explosions?.isEmpty() != false
    }

    private fun tryActivateFirework(player: PlayerEntity) {
        if (FATickCounter.totalTicks < lastActivationTime + 5) {
            return
        }

        if (isFireworkAndSafe(player.offHandStack)) {
            useFirework(player, Hand.OFF_HAND)
        } else if (safeFireworkSlot != null) {
            player.inventory.selectedSlot = safeFireworkSlot!!
            useFirework(player, Hand.MAIN_HAND)
        }
    }

    private fun useFirework(player: PlayerEntity, hand: Hand) {
        mc.interactionManager!!.interactItem(player, hand)
        lastActivationTime = FATickCounter.totalTicks
    }

    override fun isAvailable(): Boolean {
        return safeFireworkCount > 0
    }

    override fun tickThrust(computers: ComputerAccess, currentThrust: Float) {
        if (currentThrust > computers.data.forwardVelocity.length() * 20.0f / 30.0f) {
            tryActivateFirework(computers.data.player)
        }
    }

    companion object {
        val ID: Identifier = FlightAssistant.id("firework")
    }
}
