package ru.octol1ttle.flightassistant.config;

import dev.isxander.yacl3.api.NameableEnum;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import java.util.Locale;
import net.minecraft.text.Text;

public class ComputerConfig {
    @SerialEntry
    public GlobalAutomationsMode globalMode = GlobalAutomationsMode.NO_OVERLAYS;

    @SerialEntry
    public boolean lockUnsafeFireworks = true;
    @SerialEntry
    public boolean lockFireworksFacingTerrain = true;

    @SerialEntry
    public WarningMode stallWarning = WarningMode.SCREEN_AND_AUDIO;
    @SerialEntry
    public ProtectionMode stallProtection = ProtectionMode.HARD;
    @SerialEntry("stallUseFireworks")
    public boolean stallUseThrust = true;

    @SerialEntry
    public WarningMode sinkrateWarning = WarningMode.SCREEN_AND_AUDIO;
    @SerialEntry
    public ProtectionMode sinkrateProtection = ProtectionMode.HARD;
    @SerialEntry
    public WarningMode terrainWarning = WarningMode.SCREEN_AND_AUDIO;
    @SerialEntry
    public ProtectionMode terrainProtection = ProtectionMode.HARD;
    @SerialEntry
    public WarningMode landingClearanceWarning = WarningMode.SCREEN_AND_AUDIO;

    @SerialEntry
    public ProtectionMode voidProtection = ProtectionMode.HARD;
    @SerialEntry("voidUseFireworks")
    public boolean voidUseThrust = true;

    @SerialEntry
    public boolean closeElytraUnderwater = true;
    @SerialEntry
    public boolean openElytraAutomatically = true;

    @SerialEntry
    public boolean preserveAltitudeInUnloadedChunk = true;

    @SerialEntry
    public boolean considerInvulnerability = true;

    public enum GlobalAutomationsMode implements NameableEnum {
        FULL,
        NO_OVERLAYS,
        DISABLED;

        @Override
        public Text getDisplayName() {
            return Text.translatable("config.flightassistant.computers.global." + name().toLowerCase(Locale.ROOT));
        }
    }

    public enum WarningMode implements NameableEnum {
        SCREEN_AND_AUDIO,
        AUDIO_ONLY,
        SCREEN_ONLY,
        DISABLED;

        public boolean screenDisabled() {
            return this == AUDIO_ONLY || this == DISABLED;
        }

        public boolean audioDisabled() {
            return this == SCREEN_ONLY || this == DISABLED;
        }

        @Override
        public Text getDisplayName() {
            return Text.translatable("config.flightassistant.computers.warning." + name().toLowerCase(Locale.ROOT));
        }
    }

    public enum ProtectionMode implements NameableEnum {
        HARD,
        SOFT,
        @SuppressWarnings("unused") DISABLED;

        public boolean override() {
            return this == HARD;
        }

        public boolean recover() {
            return this == HARD || this == SOFT;
        }

        @Override
        public Text getDisplayName() {
            return Text.translatable("config.flightassistant.computers.protection." + name().toLowerCase(Locale.ROOT));
        }
    }
}
