package ru.octol1ttle.flightassistant.computers.impl.safety;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import ru.octol1ttle.flightassistant.computers.api.ControlInput;
import ru.octol1ttle.flightassistant.computers.api.IPitchLimiter;
import ru.octol1ttle.flightassistant.computers.api.IThrustController;
import ru.octol1ttle.flightassistant.computers.api.ITickableComputer;
import ru.octol1ttle.flightassistant.computers.api.InputPriority;
import ru.octol1ttle.flightassistant.computers.impl.AirDataComputer;
import ru.octol1ttle.flightassistant.config.ComputerConfig;
import ru.octol1ttle.flightassistant.config.FAConfig;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;

public class StallComputer implements ITickableComputer, IPitchLimiter, IThrustController {
    private final AirDataComputer data = ComputerRegistry.resolve(AirDataComputer.class);
    public StallStatus status = StallStatus.UNKNOWN;
    public float maximumSafePitch = 90.0f;

    @Override
    public void tick() {
        status = computeStalling();
        maximumSafePitch = computeMaximumSafePitch();
    }

    private StallStatus computeStalling() {
        if (!data.isFlying() || data.player().isTouchingWater()) {
            return StallStatus.UNKNOWN;
        }
        if (data.isInvulnerableTo(data.player().getDamageSources().fall())) {
            return StallStatus.PLAYER_INVULNERABLE;
        }
        if (data.fallDistance() <= 3.0f) {
            return StallStatus.FALL_DISTANCE_TOO_LOW;
        }
        if (data.pitch() - data.flightPitch <= 45.0f) {
            return StallStatus.AOA_SAFE;
        }
        if (data.pitch() - data.flightPitch <= 90.0f || data.velocity.y > -10.0f) {
            return StallStatus.APPROACHING_STALL;
        }
        return StallStatus.FULL_STALL;
    }

    private float computeMaximumSafePitch() {
        if (!data.isFlying() || status == StallStatus.UNKNOWN || status == StallStatus.PLAYER_INVULNERABLE) {
            return 90.0f;
        }
        return MathHelper.clamp(data.flightPitch + 90.0f, 0.0f, 90.0f);
    }

    @Override
    public float getMaximumPitch() {
        return maximumSafePitch;
    }

    @Override
    public boolean blockPitchChange(Direction direction) {
        return direction == Direction.UP && status == StallStatus.FULL_STALL;
    }

    @Override
    public ComputerConfig.ProtectionMode getProtectionMode() {
        return FAConfig.computer().stallProtection;
    }

    @Override
    public ControlInput getThrustInput() {
        if (!FAConfig.computer().stallUseThrust || status != StallStatus.FULL_STALL) {
            return null;
        }
        return new ControlInput(1.0f, 1.0f, InputPriority.HIGHEST);
    }

    @Override
    public String getFaultTextBaseKey() {
        return "alerts.flightassistant.fault.computers.stall_det";
    }

    @Override
    public void reset() {
        status = StallStatus.UNKNOWN;
        maximumSafePitch = 90.0f;
    }

    public enum StallStatus {
        FULL_STALL,
        APPROACHING_STALL,
        AOA_SAFE,
        FALL_DISTANCE_TOO_LOW,
        PLAYER_INVULNERABLE,
        UNKNOWN
    }
}
