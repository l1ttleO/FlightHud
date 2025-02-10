package ru.octol1ttle.flightassistant.api.autoflight.pitch

import ru.octol1ttle.flightassistant.api.autoflight.ControlInput

interface PitchController {
    fun getPitchInput(): ControlInput?
}
