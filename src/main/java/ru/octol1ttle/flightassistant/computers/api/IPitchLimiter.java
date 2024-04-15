package ru.octol1ttle.flightassistant.computers.api;

import net.minecraft.util.math.Direction;
import ru.octol1ttle.flightassistant.config.ComputerConfig;

public interface IPitchLimiter {
    default ComputerConfig.ProtectionMode getProtectionMode() {
        return ComputerConfig.ProtectionMode.HARD;
    }
    default float getMinimumPitch() {
        return -90.0f;
    }
    default float getMaximumPitch() {
        return 90.0f;
    }
    default boolean blockPitchChange(Direction direction) {
        return false;
    }
}
