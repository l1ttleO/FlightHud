package ru.octol1ttle.flightassistant.config.options

import dev.isxander.yacl3.api.NameableEnum
import dev.isxander.yacl3.config.v2.api.SerialEntry
import net.minecraft.text.Text

class SafetyOptions {
    @SerialEntry
    var considerInvulnerability: Boolean = true

    @SerialEntry
    var elytraDurabilityAlertMode: AlertMode = AlertMode.WARNING_AND_CAUTION
    @SerialEntry
    var elytraAutoOpen: Boolean = true
    @SerialEntry
    var elytraCloseUnderwater: Boolean = true

    @SerialEntry
    var stallAlertMode: AlertMode = AlertMode.WARNING_AND_CAUTION
    @SerialEntry
    var stallLimitPitch: Boolean = true
    @SerialEntry
    var stallAutoThrust: Boolean = true

    @SerialEntry
    var voidAlertMode: AlertMode = AlertMode.WARNING_AND_CAUTION
    @SerialEntry
    var voidLimitPitch: Boolean = true
    @SerialEntry
    var voidAutoThrust: Boolean = true
    @SerialEntry
    var voidAutoPitch: Boolean = true

    @SerialEntry
    var sinkRateAlertMode: AlertMode = AlertMode.WARNING_AND_CAUTION
    @SerialEntry
    var sinkRateLimitPitch: Boolean = true
    @SerialEntry
    var sinkRateAutoPitch: Boolean = true

    @SerialEntry
    var obstacleAlertMode: AlertMode = AlertMode.WARNING_AND_CAUTION
    @SerialEntry
    var obstacleLimitPitch: Boolean = false
    @SerialEntry
    var obstacleAutoPitch: Boolean = true

    internal fun setDisabled(): SafetyOptions {
        this.elytraDurabilityAlertMode = AlertMode.DISABLED

        this.stallAlertMode = AlertMode.DISABLED
        this.stallLimitPitch = false
        this.stallAutoThrust = false
        
        this.voidAlertMode = AlertMode.DISABLED
        this.voidLimitPitch = false
        this.voidAutoThrust = false
        this.voidAutoPitch = false

        this.sinkRateAlertMode = AlertMode.DISABLED
        this.sinkRateLimitPitch = false
        this.sinkRateAutoPitch = false

        this.obstacleAlertMode = AlertMode.DISABLED
        this.obstacleLimitPitch = false
        this.obstacleAutoPitch = false
        return this
    }

    enum class AlertMode : NameableEnum {
        WARNING_AND_CAUTION {
            override fun getDisplayName(): Text =
                Text.translatable("config.flightassistant.options.safety.alert_mode.warning_and_caution")
        },
        WARNING {
            override fun getDisplayName(): Text =
                Text.translatable("config.flightassistant.options.safety.alert_mode.warning")
        },
        CAUTION {
            override fun getDisplayName(): Text =
                Text.translatable("config.flightassistant.options.safety.alert_mode.caution")
        },
        DISABLED {
            override fun getDisplayName(): Text =
                Text.translatable("config.flightassistant.options.safety.alert_mode.disabled")
        };

        fun warning(): Boolean {
            return this == WARNING_AND_CAUTION || this == WARNING
        }

        fun caution(): Boolean {
            return this == WARNING_AND_CAUTION || this == CAUTION
        }
    }

    companion object {
        val DISABLED: SafetyOptions = SafetyOptions().setDisabled()
    }
}
