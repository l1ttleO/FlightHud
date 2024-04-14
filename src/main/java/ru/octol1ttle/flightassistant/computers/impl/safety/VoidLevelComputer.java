package ru.octol1ttle.flightassistant.computers.impl.safety;

import ru.octol1ttle.flightassistant.computers.impl.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.api.ITickableComputer;
import ru.octol1ttle.flightassistant.computers.impl.autoflight.FireworkController;
import ru.octol1ttle.flightassistant.computers.impl.autoflight.PitchController;
import ru.octol1ttle.flightassistant.config.FAConfig;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;

public class VoidLevelComputer implements ITickableComputer {
    private final AirDataComputer data = ComputerRegistry.resolve(AirDataComputer.class);
    private final FireworkController firework = ComputerRegistry.resolve(FireworkController.class);
    private final StallComputer stall = ComputerRegistry.resolve(StallComputer.class);
    public VoidLevelStatus status = VoidLevelStatus.UNKNOWN;
    public float minimumSafePitch = -90.0f;

    @Override
    public void tick() {
        status = computeStatus();
        if (FAConfig.computer().voidUseFireworks && aboveVoid() && data.altitude() - data.voidLevel() < 12) {
            firework.activateFirework(true);
        }
        minimumSafePitch = computeMinimumSafePitch();
    }

    private VoidLevelStatus computeStatus() {
        if (!data.isFlying() || data.player().isTouchingWater()) {
            return VoidLevelStatus.UNKNOWN;
        }
        if (data.player().isInvulnerableTo(data.player().getDamageSources().outOfWorld())) {
            return VoidLevelStatus.PLAYER_INVULNERABLE;
        }

        if (data.groundLevel != data.voidLevel()) {
            return VoidLevelStatus.NOT_ABOVE_VOID;
        }

        if (data.altitude() - data.voidLevel() >= 8) {
            return VoidLevelStatus.ALTITUDE_SAFE;
        }

        if (data.altitude() >= data.voidLevel()) {
            return VoidLevelStatus.APPROACHING_DAMAGE_LEVEL;
        }

        return VoidLevelStatus.REACHED_DAMAGE_LEVEL;
    }

    private float computeMinimumSafePitch() {
        if (status == VoidLevelStatus.UNKNOWN || status == VoidLevelStatus.PLAYER_INVULNERABLE || status == VoidLevelStatus.NOT_ABOVE_VOID) {
            return -90.0f;
        }
        if (data.altitude() - data.voidLevel() < 16) {
            return Math.min(PitchController.ALTITUDE_PRESERVE_PITCH, stall.maximumSafePitch);
        }

        return PitchController.DESCEND_PITCH + 10;
    }

    public boolean aboveVoid() {
        return status == VoidLevelStatus.ALTITUDE_SAFE || approachingOrReachedDamageLevel();
    }

    public boolean approachingOrReachedDamageLevel() {
        return status == VoidLevelStatus.APPROACHING_DAMAGE_LEVEL || status == VoidLevelStatus.REACHED_DAMAGE_LEVEL;
    }

    public boolean shouldBlockPitchChange(float newPitch) {
        return FAConfig.computer().voidProtection.override() && newPitch < minimumSafePitch;
    }

    @Override
    public String getId() {
        return "void_level";
    }

    @Override
    public void reset() {
        status = VoidLevelStatus.UNKNOWN;
        minimumSafePitch = -90.0f;
    }

    public enum VoidLevelStatus {
        REACHED_DAMAGE_LEVEL,
        APPROACHING_DAMAGE_LEVEL,
        ALTITUDE_SAFE,
        NOT_ABOVE_VOID,
        PLAYER_INVULNERABLE,
        UNKNOWN
    }
}
