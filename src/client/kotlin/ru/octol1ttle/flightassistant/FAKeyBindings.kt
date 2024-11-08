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

    fun addKeyBinding(translationKey: String, code: Int): KeyBinding {
        val keyBinding = KeyBinding(translationKey, InputUtil.Type.KEYSYM, code, "keys.flightassistant")
        keyBindings.add(keyBinding)
        return keyBinding
    }

    // tODO: reverse thrust
    fun checkPressed(computers: ComputerAccess) {
        while (setIdle.wasPressed()) {
            computers.thrust.manualThrust = 0.0f
            computers.thrust.targetThrust = 0.0f
        }
        while (setToga.wasPressed()) {
            computers.thrust.manualThrust = 1.0f
            computers.thrust.targetThrust = 1.0f
        }
        while (decreaseThrust.wasPressed()) {
            val oldManual: Float = computers.thrust.manualThrust
            val targetThrust: Float = computers.thrust.targetThrust

            computers.thrust.manualThrust = (computers.thrust.manualThrust - FATickCounter.timePassed / 3).coerceIn(0.0f..1.0f)
            val newManual: Float = computers.thrust.manualThrust
            if (oldManual >= targetThrust && newManual <= targetThrust) {
                computers.thrust.targetThrust = newManual
            }
        }
        while (increaseThrust.wasPressed()) {
            val oldManual: Float = computers.thrust.manualThrust
            val targetThrust: Float = computers.thrust.targetThrust

            computers.thrust.manualThrust = (computers.thrust.manualThrust + FATickCounter.timePassed / 3).coerceIn(0.0f..1.0f)
            val newManual: Float = computers.thrust.manualThrust
            if (oldManual <= targetThrust && newManual >= targetThrust) {
                computers.thrust.targetThrust = newManual
            }
        }
    }
}
