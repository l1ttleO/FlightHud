package ru.octol1ttle.flightassistant.api.autoflight

/**
 * Represents a flight controller that can provide thrust, pitch, heading or roll inputs. Implementing all methods is optional.
 * @see [ControlInput]
 */
interface FlightController {
    fun getThrustInput(): ControlInput? {
        return null
    }
    fun getPitchInput(): ControlInput? {
        return null
    }
    fun getHeadingInput(): ControlInput? {
        return null
    }
    fun getRollInput(): ControlInput? {
        return null
    }
}
