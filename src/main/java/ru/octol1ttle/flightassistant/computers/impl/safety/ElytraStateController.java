package ru.octol1ttle.flightassistant.computers.impl.safety;

import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.MinecraftVersion;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import ru.octol1ttle.flightassistant.MinecraftProtocolVersions;
import ru.octol1ttle.flightassistant.computers.api.ITickableComputer;
import ru.octol1ttle.flightassistant.computers.impl.AirDataComputer;
import ru.octol1ttle.flightassistant.config.FAConfig;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;

public class ElytraStateController implements ITickableComputer {
    private final AirDataComputer data = ComputerRegistry.resolve(AirDataComputer.class);
    private TriState syncedState;

    @Override
    public void tick() {
        if (data.player().isOnGround()) {
            syncedState = TriState.DEFAULT;
            return;
        }
        if (syncedState != TriState.DEFAULT) {
            if (syncedState.get() != data.isFlying()) {
                syncedState = TriState.DEFAULT;
            }
            return;
        }
        if (!isAvailable() || !data.canAutomationsActivate(false)) {
            return;
        }

        if (FAConfig.computer().closeElytraUnderwater && data.isFlying() && data.player().isSubmergedInWater()) {
            // Retract the wings
            sendSwitchState();
        }

        boolean flying = data.isFlying() || data.player().getAbilities().allowFlying;
        boolean hasUsableElytra = data.elytraData != null && data.elytraData.isUsable();
        boolean notLookingToClutch = data.pitch() > -70.0f;
        boolean unsafeFallDistance = data.fallDistance() > 3.0f;
        if (FAConfig.computer().openElytraAutomatically && unsafeFallDistance && !flying && hasUsableElytra && notLookingToClutch) {
            // Extend the wings
            sendSwitchState();
        }
    }

    private void sendSwitchState() {
        syncedState = TriState.of(data.isFlying());
        data.player().networkHandler.sendPacket(new ClientCommandC2SPacket(data.player(), ClientCommandC2SPacket.Mode.START_FALL_FLYING));
    }

    public static boolean isAvailable() {
        return MinecraftVersion.CURRENT.getProtocolVersion() >= MinecraftProtocolVersions.R20_2;
    }

    @Override
    public String getFaultTextBaseKey() {
        return "alerts.flightassistant.fault.computers.elytra_state";
    }

    @Override
    public void reset() {
        syncedState = TriState.DEFAULT;
    }
}
