package ru.octol1ttle.flightassistant.config;

import dev.isxander.yacl3.api.NameableEnum;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import java.awt.Color;
import java.util.Locale;
import net.minecraft.text.Text;

public class IndicatorConfig {
    @SerialEntry
    public Color frameColor = Color.GREEN;
    @SerialEntry
    public Color statusColor = Color.WHITE;
    @SerialEntry
    public Color advisoryColor = Color.CYAN;
    @SerialEntry
    public Color cautionColor = Color.YELLOW;
    @SerialEntry
    public Color warningColor = Color.RED;

    @SerialEntry
    public boolean showSpeedScale = true;
    @SerialEntry
    public boolean showSpeedReadout = true;
    @SerialEntry
    public boolean showGroundSpeedReadout = true;
    @SerialEntry
    public boolean showVerticalSpeedReadout = true;

    @SerialEntry
    public boolean showAltitudeScale = true;
    @SerialEntry
    public boolean showAltitudeReadout = true;
    @SerialEntry
    public boolean showGroundAltitude = true;

    @SerialEntry
    public boolean showHeadingScale = true;
    @SerialEntry
    public boolean showHeadingReadout = true;

    @SerialEntry
    public boolean showFireworkMode = true;
    @SerialEntry
    public boolean showVerticalMode = true;
    @SerialEntry
    public boolean showLateralMode = true;
    @SerialEntry
    public boolean showAutomationStatus = true;

    @SerialEntry
    public boolean showAlerts = true;
    @SerialEntry
    public boolean showFireworkCount = true;
    @SerialEntry
    public boolean showThrustSetting = true;
    @SerialEntry
    public boolean showDistanceToWaypoint = true;
    @SerialEntry
    public boolean showFlightPhase = true;

    @SerialEntry
    public boolean showAttitudeIndicator = true;
    @SerialEntry
    public boolean showFlightPath = true;
    @SerialEntry
    public boolean showCoordinates = true;
    @SerialEntry
    public boolean showElytraHealth = true;
    @SerialEntry
    public ElytraHealthDisplayUnits elytraHealthUnits = ElytraHealthDisplayUnits.PERCENTAGE;

    IndicatorConfig setMinimal() {
        this.showFlightPath = false;
        this.showAttitudeIndicator = false;
        this.showSpeedScale = false;
        this.showSpeedReadout = false;
        this.showVerticalSpeedReadout = false;
        this.showAltitudeScale = false;
        this.showGroundAltitude = false;
        this.showHeadingScale = false;
        this.showFireworkMode = false;
        this.showFireworkCount = false;
        this.showDistanceToWaypoint = false;

        return this;
    }

    IndicatorConfig disableAll() {
        this.showElytraHealth = false;
        this.showCoordinates = false;
        this.showFlightPath = false;
        this.showAttitudeIndicator = false;
        this.showSpeedScale = false;
        this.showSpeedReadout = false;
        this.showGroundSpeedReadout = false;
        this.showVerticalSpeedReadout = false;
        this.showAltitudeScale = false;
        this.showAltitudeReadout = false;
        this.showGroundAltitude = false;
        this.showHeadingScale = false;
        this.showHeadingReadout = false;
        this.showAlerts = false;
        this.showFireworkMode = false;
        this.showVerticalMode = false;
        this.showLateralMode = false;
        this.showAutomationStatus = false;
        this.showFireworkCount = false;
        this.showDistanceToWaypoint = false;
        this.showFlightPhase = false;
        this.showThrustSetting = false;

        return this;
    }

    public enum ElytraHealthDisplayUnits implements NameableEnum {
        REMAINING_DURABILITY,
        PERCENTAGE;

        @Override
        public Text getDisplayName() {
            return Text.translatable("config.flightassistant.indicators.misc.elytra_health_units." + name().toLowerCase(Locale.ROOT));
        }
    }
}
