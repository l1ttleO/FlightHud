package ru.octol1ttle.flightassistant.impl.computer.autoflight

import dev.architectury.event.events.common.InteractionEvent
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.FireworkRocketItem
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.autoflight.thrust.ThrustSource
import ru.octol1ttle.flightassistant.api.autoflight.thrust.ThrustSourceRegistrationCallback
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerView
import ru.octol1ttle.flightassistant.api.util.FATickCounter
import ru.octol1ttle.flightassistant.api.util.LimitedFIFOQueue
import ru.octol1ttle.flightassistant.api.util.event.FireworkBoostCallback
import ru.octol1ttle.flightassistant.config.FAConfig

class FireworkComputer(computers: ComputerView, private val mc: MinecraftClient) : Computer(computers), ThrustSource {
    override val priority: ThrustSource.Priority = ThrustSource.Priority.LOW
    override val supportsReverse: Boolean = false
    override val optimumClimbPitch: Float = 55.0f

    private var safeFireworkCount: Int = 0
    private var safeFireworkSlot: Int? = null

    var waitingForResponse: Boolean = false
    var lastActivationTime: Int = 0
    var responseTimes: LimitedFIFOQueue<Int> = LimitedFIFOQueue(5)

    override fun subscribeToEvents() {
        ThrustSourceRegistrationCallback.EVENT.register { it.accept(this) }
        InteractionEvent.RIGHT_CLICK_ITEM.register(InteractionEvent.RightClickItem { player, hand ->
            val stack: ItemStack = player.getStackInHand(hand)
            if (!player.world.isClient()) {
//? if >=1.21.2 {
                /*return@RightClickItem net.minecraft.util.ActionResult.PASS
*///?} else
                return@RightClickItem dev.architectury.event.CompoundEventResult.pass()

            }

            if (FAConfig.safety.fireworkLockExplosive && !isEmptyOrSafe(player, hand)) {
//? if >=1.21.2 {
                /*return@RightClickItem net.minecraft.util.ActionResult.FAIL
*///?} else
                return@RightClickItem dev.architectury.event.CompoundEventResult.interruptFalse(stack)
            }

            if (!waitingForResponse && stack.item is FireworkRocketItem) {
                lastActivationTime = FATickCounter.totalTicks
                waitingForResponse = true
            }

//? if >=1.21.2 {
            /*return@RightClickItem net.minecraft.util.ActionResult.PASS
*///?} else
            return@RightClickItem dev.architectury.event.CompoundEventResult.pass()
        })
        FireworkBoostCallback.EVENT.register(FireworkBoostCallback { _, _ ->
            if (waitingForResponse) {
                waitingForResponse = false
                responseTimes.add(FATickCounter.totalTicks - lastActivationTime)
            }
        })
    }

    override fun tick() {
        if (!computers.data.flying) {
            waitingForResponse = false
        }

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

    fun isEmptyOrSafe(player: PlayerEntity, hand: Hand): Boolean {
        return hasNoExplosions(player.getStackInHand(hand))
    }

    private fun isFireworkAndSafe(stack: ItemStack): Boolean {
        return stack.item is FireworkRocketItem && hasNoExplosions(stack)
    }

    private fun hasNoExplosions(stack: ItemStack): Boolean {
//? if >=1.21 {
        /*return stack.get(net.minecraft.component.DataComponentTypes.FIREWORKS)?.explosions?.isEmpty() != false
*///?} else
        return stack.getSubNbt("Fireworks")?.getList("Explosions", net.minecraft.nbt.NbtElement.COMPOUND_TYPE.toInt())?.isEmpty() != false
    }

    private fun tryActivateFirework(player: PlayerEntity) {
        if (FATickCounter.totalTicks < lastActivationTime + 10) {
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
        waitingForResponse = true
    }

    override fun isAvailable(): Boolean {
        return safeFireworkCount > 0
    }

    override fun tickThrust(currentThrust: Float) {
        if (currentThrust > computers.data.forwardVelocity.length() * 20.0f / 30.0f) {
            tryActivateFirework(computers.data.player)
        }
    }

    override fun calculateThrustForSpeed(targetSpeed: Int): Float {
        return (targetSpeed / 30.0f).coerceIn(0.0f..1.0f)
    }

    override fun reset() {
        safeFireworkCount = 0
        safeFireworkSlot = null
        waitingForResponse = false
        lastActivationTime = 0
        responseTimes.clear()
    }

    companion object {
        val ID: Identifier = FlightAssistant.id("firework")
    }
}
