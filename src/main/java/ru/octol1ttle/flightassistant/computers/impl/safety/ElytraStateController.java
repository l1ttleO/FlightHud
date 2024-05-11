package ru.octol1ttle.flightassistant.computers.impl.safety;

import net.minecraft.MinecraftVersion;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import ru.octol1ttle.flightassistant.MinecraftProtocolVersions;
import ru.octol1ttle.flightassistant.computers.impl.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.api.ITickableComputer;
import ru.octol1ttle.flightassistant.config.FAConfig;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;

public class ElytraStateController implements ITickableComputer {
    private final AirDataComputer data = ComputerRegistry.resolve(AirDataComputer.class);
    private boolean syncedState;
    private boolean changesPending;

    @Override
    public void tick() {
        if (syncedState != data.isFlying() || data.player().isOnGround()) {
            changesPending = false;
        }
        if (!isAvailable() || changesPending || !data.canAutomationsActivate(false)) {
            return;
        }

        if (FAConfig.computer().closeElytraUnderwater && data.isFlying() && data.player().isSubmergedInWater()) {
            // Retract the wings
            sendSwitchState();
        }

        boolean flying = data.isFlying() || data.player().getAbilities().flying;
        boolean hasUsableElytra = data.elytraHealth != null && data.elytraHealth.isUsable();
        boolean notLookingToClutch = data.pitch() > -70.0f;
        if (FAConfig.computer().openElytraAutomatically
                && data.fallDistance() > 3.0f && !flying && hasUsableElytra && notLookingToClutch) {
            // Extend the wings
            sendSwitchState();
        }
    }

    private void sendSwitchState() {
        syncedState = data.isFlying();
        data.player().networkHandler.sendPacket(new ClientCommandC2SPacket(data.player(), ClientCommandC2SPacket.Mode.START_FALL_FLYING));
        changesPending = true;
    }

    public static boolean isAvailable() {
        return MinecraftVersion.CURRENT.getProtocolVersion() >= MinecraftProtocolVersions.R20_2;
    }

    @Override
    public String getId() {
        return "elytra_state";
    }

    @Override
    public void reset() {
        syncedState = false;
        changesPending = false;
    }
}