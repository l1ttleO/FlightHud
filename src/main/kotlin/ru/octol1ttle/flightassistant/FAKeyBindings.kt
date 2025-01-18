package ru.octol1ttle.flightassistant

import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW
import ru.octol1ttle.flightassistant.FlightAssistant.mc
import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.util.FATickCounter
import ru.octol1ttle.flightassistant.api.util.alert
import ru.octol1ttle.flightassistant.api.util.thrust
import ru.octol1ttle.flightassistant.screen.FlightConfigurationScreen

object FAKeyBindings {
    internal val keyBindings: ArrayList<KeyBinding> = ArrayList()

    private lateinit var openFlightConfiguration: KeyBinding

    private lateinit var hideCurrentAlert: KeyBinding
    private lateinit var showHiddenAlert: KeyBinding

    private lateinit var setIdle: KeyBinding
    private lateinit var decreaseThrust: KeyBinding
    private lateinit var increaseThrust: KeyBinding
    private lateinit var setToga: KeyBinding

    fun setup() {
        openFlightConfiguration = addKeyBinding("keys.flightassistant.open_flight_configuration", GLFW.GLFW_KEY_KP_ENTER)

        hideCurrentAlert = addKeyBinding("keys.flightassistant.hide_current_alert", GLFW.GLFW_KEY_KP_0)
        showHiddenAlert = addKeyBinding("keys.flightassistant.show_hidden_alert", GLFW.GLFW_KEY_KP_DECIMAL)

        setIdle = addKeyBinding("keys.flightassistant.set_idle", GLFW.GLFW_KEY_LEFT)
        decreaseThrust = addKeyBinding("keys.flightassistant.decrease_thrust", GLFW.GLFW_KEY_DOWN)
        increaseThrust = addKeyBinding("keys.flightassistant.increase_thrust", GLFW.GLFW_KEY_UP)
        setToga = addKeyBinding("keys.flightassistant.set_toga", GLFW.GLFW_KEY_RIGHT)
    }

    private fun addKeyBinding(translationKey: String, code: Int): KeyBinding {
        val keyBinding = KeyBinding(translationKey, InputUtil.Type.KEYSYM, code, "keys.flightassistant")
        keyBindings.add(keyBinding)
        return keyBinding
    }

    fun checkPressed(computers: ComputerAccess) {
        while (openFlightConfiguration.wasPressed()) {
            mc.execute {
                mc.setScreen(FlightConfigurationScreen())
            }
        }

        while (hideCurrentAlert.wasPressed()) {
            computers.alert.hideCurrentAlert()
        }
        while (showHiddenAlert.wasPressed()) {
            computers.alert.showHiddenAlert()
        }

        while (setIdle.wasPressed()) {
            computers.thrust.setTarget(0.0f, false)
        }
        while (setToga.wasPressed()) {
            computers.thrust.setTarget(1.0f, false)
        }
        while (decreaseThrust.wasPressed()) {
            computers.thrust.setTarget((computers.thrust.targetThrust - FATickCounter.timePassed / 3).coerceIn(-1.0f..1.0f), false)
        }
        while (increaseThrust.wasPressed()) {
            computers.thrust.setTarget((computers.thrust.targetThrust + FATickCounter.timePassed / 3).coerceIn(-1.0f..1.0f), false)
        }
    }
}
