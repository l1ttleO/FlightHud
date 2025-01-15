package ru.octol1ttle.flightassistant.api.alert

import net.minecraft.sound.SoundEvent
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.config.FAConfig

class AlertData(val priority: Int, val soundEvent: SoundEvent, val repeat: RepeatType, val colorSupplier: () -> Int) {
    companion object {
        val FULL_STALL =
            AlertData(
                0,
                FlightAssistant.soundEvent("full_stall"),
                RepeatType.REPEAT_CONSTANT_VOLUME
            ) { FAConfig.display.warningColor.rgb }
        val APPROACHING_STALL =
            AlertData(
                100,
                FlightAssistant.soundEvent("approaching_stall"),
                RepeatType.REPEAT_FADE_IN_OUT,
            ) { FAConfig.display.cautionColor.rgb }
        val PULL_UP =
            AlertData(
                200,
                FlightAssistant.soundEvent("pull_up"),
                RepeatType.REPEAT_CONSTANT_VOLUME
            ) { FAConfig.display.warningColor.rgb }
        val SINK_RATE =
            AlertData(
                300,
                FlightAssistant.soundEvent("sink_rate"),
                RepeatType.REPEAT_CONSTANT_VOLUME
            ) { FAConfig.display.cautionColor.rgb }
        val TERRAIN_AHEAD =
            AlertData(
                400,
                FlightAssistant.soundEvent("terrain_ahead"),
                RepeatType.REPEAT_CONSTANT_VOLUME
            ) { FAConfig.display.cautionColor.rgb }
        val MASTER_WARNING =
            AlertData(
                500,
                FlightAssistant.soundEvent("master_warning"),
                RepeatType.REPEAT_FADE_OUT
            ) { FAConfig.display.warningColor.rgb }
        val THRUST_LOCKED =
            AlertData(
                600,
                FlightAssistant.soundEvent("thrust_locked"),
                RepeatType.REPEAT_CONSTANT_VOLUME
            ) { FAConfig.display.cautionColor.rgb }
        val MASTER_CAUTION =
            AlertData(
                700,
                FlightAssistant.soundEvent("master_caution"),
                RepeatType.NO_REPEAT
            ) { FAConfig.display.cautionColor.rgb }
    }

    enum class RepeatType {
        REPEAT_FADE_IN_OUT,
        REPEAT_FADE_OUT,
        REPEAT_CONSTANT_VOLUME,
        NO_REPEAT
    }
}
