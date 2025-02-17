package ru.octol1ttle.flightassistant

import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW
import ru.octol1ttle.flightassistant.FlightAssistant.mc
import ru.octol1ttle.flightassistant.api.computer.ComputerView
import ru.octol1ttle.flightassistant.api.util.FATickCounter
import ru.octol1ttle.flightassistant.screen.FlightSetupScreen

object FAKeyBindings {
    internal val keyBindings: MutableList<KeyBinding> = ArrayList()

    private lateinit var openFlightSetup: KeyBinding

    private lateinit var autopilotDisconnect: KeyBinding
    private lateinit var manualPitchOverride: KeyBinding

    private lateinit var hideCurrentAlert: KeyBinding
    private lateinit var showHiddenAlert: KeyBinding

    private lateinit var setIdle: KeyBinding
    private lateinit var decreaseThrust: KeyBinding
    private lateinit var increaseThrust: KeyBinding
    private lateinit var setToga: KeyBinding

    fun setup() {
        openFlightSetup = addKeyBinding("keys.flightassistant.open_flight_setup", GLFW.GLFW_KEY_KP_ENTER)

        autopilotDisconnect = addKeyBinding("keys.flightassistant.autopilot_disconnect", GLFW.GLFW_KEY_CAPS_LOCK)
        manualPitchOverride = addKeyBinding("keys.flightassistant.manual_pitch_override", GLFW.GLFW_KEY_LEFT_ALT)

        hideCurrentAlert = addKeyBinding("keys.flightassistant.hide_current_alert", GLFW.GLFW_KEY_KP_0)
        showHiddenAlert = addKeyBinding("keys.flightassistant.show_hidden_alert", GLFW.GLFW_KEY_KP_DECIMAL)

        setIdle = addKeyBinding("keys.flightassistant.set_idle", GLFW.GLFW_KEY_LEFT)
        decreaseThrust = addKeyBinding("keys.flightassistant.decrease_thrust", GLFW.GLFW_KEY_DOWN)
        increaseThrust = addKeyBinding("keys.flightassistant.increase_thrust", GLFW.GLFW_KEY_UP)
        setToga = addKeyBinding("keys.flightassistant.set_toga", GLFW.GLFW_KEY_RIGHT)
    }

    private fun addKeyBinding(translationKey: String, code: Int, type: InputUtil.Type = InputUtil.Type.KEYSYM): KeyBinding {
        val keyBinding = KeyBinding(translationKey, type, code, "keys.flightassistant")
        keyBindings.add(keyBinding)
        return keyBinding
    }

    fun checkPressed(computers: ComputerView) {
        while (openFlightSetup.wasPressed()) {
            mc.execute {
                mc.setScreen(FlightSetupScreen())
            }
        }

        while (autopilotDisconnect.wasPressed()) {
            computers.automations.setAutoPilot(false, alert = false)
        }
        computers.pitch.manualOverride = manualPitchOverride.isPressed

        while (hideCurrentAlert.wasPressed()) {
            computers.alert.hideCurrentAlert()
        }
        while (showHiddenAlert.wasPressed()) {
            computers.alert.showHiddenAlert()
        }

        while (setIdle.wasPressed()) {
            computers.thrust.setTarget(0.0f)
        }
        while (setToga.wasPressed()) {
            computers.thrust.setTarget(1.0f)
        }
        while (decreaseThrust.wasPressed()) {
            computers.thrust.setTarget((computers.thrust.current - FATickCounter.timePassed / 3).coerceIn(-1.0f..1.0f))
        }
        while (increaseThrust.wasPressed()) {
            computers.thrust.setTarget((computers.thrust.current + FATickCounter.timePassed / 3).coerceIn(-1.0f..1.0f))
        }
    }
}
