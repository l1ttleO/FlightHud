package ru.octol1ttle.flightassistant.computers.impl.autoflight;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Hand;
import ru.octol1ttle.flightassistant.computers.api.IThrustHandler;
import ru.octol1ttle.flightassistant.computers.api.ITickableComputer;
import ru.octol1ttle.flightassistant.computers.impl.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.impl.TimeComputer;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;

public class FireworkController implements ITickableComputer, IThrustHandler {
    public static final float FIREWORK_SPEED = 33.62f;

    private final MinecraftClient mc;
    private final AirDataComputer data = ComputerRegistry.resolve(AirDataComputer.class);
    private final TimeComputer time = ComputerRegistry.resolve(TimeComputer.class);
    private final ThrustController thrust = ComputerRegistry.resolve(ThrustController.class);

    public int safeFireworkCount = Integer.MAX_VALUE;
    public boolean fireworkResponded = true;
    public float lastUseTime = -1.0f;
    public float lastDiff = Float.MIN_VALUE;
    public Float lastProtTrigger;
    public boolean noFireworks = false;
    public boolean activationInProgress = false;
    public boolean lockManualFireworks = false;

    public FireworkController(MinecraftClient mc) {
        this.mc = mc;
    }

    @Override
    public void tick() {
        if (!data.isFlying()) {
            fireworkResponded = true;
        }
        safeFireworkCount = countSafeFireworks();
        if (time.millis != null && lastUseTime > 0) {
            lastDiff = time.millis - lastUseTime;
        }

        noFireworks = true;
        PlayerInventory inventory = data.player().getInventory();
        int i = 0;
        while (PlayerInventory.isValidHotbarIndex(i)) {
            if (isFireworkSafe(inventory.getStack(i))) {
                noFireworks = false;
                break;
            }

            i++;
        }

        if (data.speed() / FIREWORK_SPEED < thrust.currentThrust) {
            activateFirework(false);
        }
    }

    private int countSafeFireworks() {
        int i = 0;

        PlayerInventory inventory = data.player().getInventory();
        for (int j = 0; j < inventory.size(); ++j) {
            ItemStack itemStack = inventory.getStack(j);
            if (isFireworkSafe(itemStack)) {
                i += itemStack.getCount();
            }
        }

        return i;
    }

    private void activateFirework(boolean force) {
        if (!data.canAutomationsActivate() || lastUseTime > 0 && time.millis != null && time.millis - lastUseTime < 1000) {
            return;
        }
        if (force && time.millis != null) {
            this.lastProtTrigger = time.millis;
        }

        if (isFireworkSafe(data.player().getOffHandStack())) {
            tryActivateFirework(Hand.OFF_HAND, force);
            return;
        }
        if (isFireworkSafe(data.player().getMainHandStack())) {
            tryActivateFirework(Hand.MAIN_HAND, force);
            return;
        }

        int i = 0;
        boolean match = false;
        PlayerInventory inventory = data.player().getInventory();
        while (PlayerInventory.isValidHotbarIndex(i)) {
            if (isFireworkSafe(inventory.getStack(i))) {
                inventory.selectedSlot = i;
                match = true;
                break;
            }

            i++;
        }

        if (!match) {
            noFireworks = true;
            return;
        }
        tryActivateFirework(Hand.MAIN_HAND, force);
    }

    private void tryActivateFirework(Hand hand, boolean force) {
        if (!force && !fireworkResponded) {
            return;
        }
        if (mc.interactionManager == null) {
            throw new AssertionError();
        }

        activationInProgress = true;
        mc.interactionManager.interactItem(data.player(), hand);
        activationInProgress = false;
    }

    public boolean isFireworkSafe(ItemStack stack) {
        if (!(stack.getItem() instanceof FireworkRocketItem)) {
            return false;
        }
        if (data.isInvulnerable()) {
            return true;
        }
        NbtCompound nbtCompound = stack.getSubNbt("Fireworks");
        return nbtCompound == null || nbtCompound.getList("Explosions", NbtElement.COMPOUND_TYPE).isEmpty();
    }

    @Override
    public String getId() {
        return "frwk_ctl";
    }

    @Override
    public void reset() {
        safeFireworkCount = Integer.MAX_VALUE;
        fireworkResponded = true;
        lastUseTime = -1.0f;
        lastDiff = Float.MIN_VALUE;
        noFireworks = false;
        activationInProgress = false;
        lockManualFireworks = false;
    }
}
