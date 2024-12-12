package ru.octol1ttle.flightassistant.config.options

import dev.isxander.yacl3.config.v2.api.SerialEntry
import java.awt.Color
import ru.octol1ttle.flightassistant.impl.computer.safety.ElytraStatusComputer

class DisplayOptions {
    @SerialEntry
    var frameWidth: Float = 0.5f
    @SerialEntry
    var frameHeight: Float = 0.5f

    @SerialEntry
    var primaryColor: Color = Color.GREEN
    @SerialEntry
    var advisoryColor: Color = Color.CYAN

    @SerialEntry
    var cautionColor: Color = Color.YELLOW
    @SerialEntry
    var warningColor: Color = Color.RED

    @SerialEntry
    var showAttitude: Boolean = true // potential TODO: allow displaying only the horizon?
    @SerialEntry
    var attitudeDegreeStep: Int = 15
    @SerialEntry
    var drawHorizonOutsideFrame: Boolean = true
    @SerialEntry
    var drawPitchOutsideFrame: Boolean = true
    @SerialEntry
    var showHorizonHeading: Boolean = true
    @SerialEntry
    var headingDegreeStep: Int = 10

    @SerialEntry
    var showSpeedReading: Boolean = true
    @SerialEntry
    var showSpeedScale: Boolean = true

    @SerialEntry
    var showAltitudeReading: Boolean = true
    @SerialEntry
    var showAltitudeScale: Boolean = true
    @SerialEntry
    var showRadarAltitude: Boolean = true

    @SerialEntry
    var showFlightPathVector: Boolean = true
    @SerialEntry
    var flightPathVectorSize: Float = 1.0f

    @SerialEntry
    var showElytraDurability: Boolean = true
    @SerialEntry
    var elytraDurabilityUnits: ElytraStatusComputer.DurabilityUnits = ElytraStatusComputer.DurabilityUnits.PERCENTAGE

    @SerialEntry
    var showCoordinates: Boolean = true

    @SerialEntry
    var showGroundSpeed: Boolean = true
    @SerialEntry
    var showVerticalSpeed: Boolean = true

    @SerialEntry
    var showAlerts: Boolean = true

    @SerialEntry
    var showAutomationModes: Boolean = true

    internal fun setMinimal(): DisplayOptions {
        this.showAttitude = false
        this.showSpeedReading = false
        this.showSpeedScale = false
        this.showAltitudeScale = false
        this.showRadarAltitude = false
        this.showFlightPathVector = false
        this.showVerticalSpeed = false
        return this
    }

    internal fun setDisabled(): DisplayOptions {
        this.showAttitude = false
        this.showSpeedReading = false
        this.showSpeedScale = false
        this.showAltitudeReading = false
        this.showAltitudeScale = false
        this.showRadarAltitude = false
        this.showFlightPathVector = false
        this.showElytraDurability = false
        this.showCoordinates = false
        this.showGroundSpeed = false
        this.showVerticalSpeed = false
        this.showAlerts = false
        this.showAutomationModes = false
        return this
    }
}
