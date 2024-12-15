package ru.octol1ttle.flightassistant

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW
import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.util.FATickCounter
import ru.octol1ttle.flightassistant.api.util.thrust

object FAKeyBindings {
    private val keyBindings: ArrayList<KeyBinding> = ArrayList()

    private lateinit var setIdle: KeyBinding
    private lateinit var decreaseThrust: KeyBinding
    private lateinit var increaseThrust: KeyBinding
    private lateinit var setToga: KeyBinding

    fun setup() {
        setIdle = addKeyBinding("keys.flightassistant.set_idle", GLFW.GLFW_KEY_LEFT)
        decreaseThrust = addKeyBinding("keys.flightassistant.decrease_thrust", GLFW.GLFW_KEY_DOWN)
        increaseThrust = addKeyBinding("keys.flightassistant.increase_thrust", GLFW.GLFW_KEY_UP)
        setToga = addKeyBinding("keys.flightassistant.set_toga", GLFW.GLFW_KEY_RIGHT)
        for (keyBinding: KeyBinding in keyBindings) {
            KeyBindingHelper.registerKeyBinding(keyBinding)
        }
    }

    private fun addKeyBinding(translationKey: String, code: Int): KeyBinding {
        val keyBinding = KeyBinding(translationKey, InputUtil.Type.KEYSYM, code, "keys.flightassistant")
        keyBindings.add(keyBinding)
        return keyBinding
    }

    fun checkPressed(computers: ComputerAccess) {
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
