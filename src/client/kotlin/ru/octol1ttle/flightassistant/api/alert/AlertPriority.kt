package ru.octol1ttle.flightassistant.api.alert

import net.minecraft.sound.SoundEvent
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.config.FAConfig

class AlertPriority(val priorityValue: Int, val soundEvent: SoundEvent, val colorSupplier: () -> Int) {
    companion object {
        val MASTER_WARNING =
            AlertPriority(0, FlightAssistant.soundEvent("master_warning")) { FAConfig.display.warningColor.rgb }
        val MASTER_CAUTION =
            AlertPriority(1, FlightAssistant.soundEvent("master_caution")) { FAConfig.display.cautionColor.rgb }
    }
}
