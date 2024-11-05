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
                1,
                FlightAssistant.soundEvent("approaching_stall"),
                RepeatType.REPEAT_FADE_VOLUME
            ) { FAConfig.display.cautionColor.rgb }
        val MASTER_WARNING =
            AlertData(
                2,
                FlightAssistant.soundEvent("master_warning"),
                RepeatType.REPEAT_FADE_VOLUME
            ) { FAConfig.display.warningColor.rgb }
        val MASTER_CAUTION =
            AlertData(
                3,
                FlightAssistant.soundEvent("master_caution"),
                RepeatType.NO_REPEAT
            ) { FAConfig.display.cautionColor.rgb }
    }

    enum class RepeatType {
        REPEAT_FADE_VOLUME,
        REPEAT_CONSTANT_VOLUME,
        NO_REPEAT
    }
}
