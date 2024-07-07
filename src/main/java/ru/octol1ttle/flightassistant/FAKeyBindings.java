package ru.octol1ttle.flightassistant;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import ru.octol1ttle.flightassistant.computers.impl.autoflight.AutoFlightController;
import ru.octol1ttle.flightassistant.computers.impl.autoflight.ThrustController;
import ru.octol1ttle.flightassistant.computers.impl.safety.AlertController;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;

public class FAKeyBindings {
    private final List<KeyBinding> keyBindings = new ArrayList<>();
    
    private final KeyBinding toggleFlightDirectors;
    private final KeyBinding toggleAutoThrust;
    private final KeyBinding toggleAutoPilot;

    private final KeyBinding hideAlert;
    private final KeyBinding recallAlert;

    private final KeyBinding setIdle;
    private final KeyBinding decreaseThrust;
    private final KeyBinding increaseThrust;
    private final KeyBinding setToga;

    public FAKeyBindings() {
        toggleFlightDirectors = addKeyBinding("key.flightassistant.toggle_flight_directors", GLFW.GLFW_KEY_KP_1);
        toggleAutoThrust = addKeyBinding("key.flightassistant.toggle_auto_thrust", GLFW.GLFW_KEY_KP_2);
        toggleAutoPilot = addKeyBinding("key.flightassistant.toggle_auto_pilot", GLFW.GLFW_KEY_KP_3);

        hideAlert = addKeyBinding("key.flightassistant.hide_alert", GLFW.GLFW_KEY_KP_0);
        recallAlert = addKeyBinding("key.flightassistant.recall_alert", GLFW.GLFW_KEY_KP_DECIMAL);

        setIdle = addKeyBinding("key.flightassistant.set_idle", GLFW.GLFW_KEY_LEFT);
        decreaseThrust = addKeyBinding("key.flightassistant.decrease_thrust", GLFW.GLFW_KEY_DOWN);
        increaseThrust = addKeyBinding("key.flightassistant.increase_thrust", GLFW.GLFW_KEY_UP);
        setToga = addKeyBinding("key.flightassistant.set_toga", GLFW.GLFW_KEY_RIGHT);
    }
    
    private KeyBinding addKeyBinding(String translationKey, int code) {
        KeyBinding keyBinding = new KeyBinding(translationKey, InputUtil.Type.KEYSYM, code, "mod.flightassistant");
        keyBindings.add(keyBinding);
        return keyBinding;
    }

    public void registerAll() {
        for (KeyBinding keyBinding : keyBindings) {
            KeyBindingHelper.registerKeyBinding(keyBinding);
        }
    }

    public void tick() {
        if (!ComputerRegistry.isFaulted(AlertController.class)) {
            AlertController alert = ComputerRegistry.resolve(AlertController.class);
            while (hideAlert.wasPressed()) {
                alert.hide();
            }
            while (recallAlert.wasPressed()) {
                alert.recall();
            }
        }

        boolean disconnectAutoThrust = false;
        if (!ComputerRegistry.isFaulted(ThrustController.class)) {
            ThrustController thrust = ComputerRegistry.resolve(ThrustController.class);

            while (setIdle.wasPressed()) {
                thrust.setThrust(0.0f);
                disconnectAutoThrust = true;
            }
            while (setToga.wasPressed()) {
                thrust.setThrust(1.0f);
                disconnectAutoThrust = true;
            }

            while (decreaseThrust.wasPressed()) {
                thrust.addThrustTick(-1.0f);
                disconnectAutoThrust = true;
            }
            while (increaseThrust.wasPressed()) {
                thrust.addThrustTick(1.0f);
                disconnectAutoThrust = true;
            }
        }

        if (!ComputerRegistry.isFaulted(AutoFlightController.class)) {
            AutoFlightController autoflight = ComputerRegistry.resolve(AutoFlightController.class);

            if (disconnectAutoThrust) {
                autoflight.disconnectAutoThrust(true);
            }

            while (toggleFlightDirectors.wasPressed()) {
                autoflight.flightDirectorsEnabled = !autoflight.flightDirectorsEnabled;
            }

            while (toggleAutoThrust.wasPressed()) {
                if (!autoflight.autoThrustEnabled) {
                    autoflight.autoThrustEnabled = true;
                } else {
                    autoflight.disconnectAutoThrust(false);
                }
            }

            while (toggleAutoPilot.wasPressed()) {
                if (!autoflight.autoPilotEnabled) {
                    autoflight.autoPilotEnabled = true;
                } else {
                    autoflight.disconnectAutopilot(false);
                }
            }
        }
    }
}
