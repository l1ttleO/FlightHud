package ru.octol1ttle.flightassistant.alerts.impl;

import net.minecraft.sound.SoundEvent;
import org.jetbrains.annotations.Nullable;
import ru.octol1ttle.flightassistant.config.ComputerConfig;

import static ru.octol1ttle.flightassistant.FlightAssistant.id;

public record AlertSoundData(@Nullable SoundEvent sound, int priority) {
    public static final AlertSoundData STALL = new AlertSoundData(
            SoundEvent.of(id("stall")),
            0
    );
    public static final AlertSoundData PULL_UP = new AlertSoundData(
            SoundEvent.of(id("pull_up")),
            1
    );
    public static final AlertSoundData SINK_RATE = new AlertSoundData(
            SoundEvent.of(id("sink_rate")),
            2
    );
    public static final AlertSoundData TERRAIN = new AlertSoundData(
            SoundEvent.of(id("terrain")),
            2
    );
    public static final AlertSoundData TOO_LOW_TERRAIN = new AlertSoundData(
            SoundEvent.of(id("too_low_terrain")),
            2
    );
    public static final AlertSoundData AUTOPILOT_DISCONNECT = new AlertSoundData(
            SoundEvent.of(id("autopilot_disconnect")),
            3
    );
    public static final AlertSoundData MASTER_WARNING = new AlertSoundData(
            SoundEvent.of(id("warning")),
            4
    );
    public static final AlertSoundData MINIMUMS = new AlertSoundData(
            SoundEvent.of(id("minimums")),
            5
    );
    public static final AlertSoundData MASTER_CAUTION = new AlertSoundData(
            SoundEvent.of(id("caution")),
            6
    );
    public static final AlertSoundData EMPTY = new AlertSoundData(
            null,
            Integer.MAX_VALUE
    );

    public static AlertSoundData ifEnabled(ComputerConfig.WarningMode mode, AlertSoundData data) {
        if (mode.audioDisabled()) {
            return AlertSoundData.EMPTY;
        }

        return data;
    }
}
