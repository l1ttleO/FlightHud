package ru.octol1ttle.flightassistant;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import ru.octol1ttle.flightassistant.computers.impl.TimeComputer;
import ru.octol1ttle.flightassistant.computers.impl.autoflight.AutoFlightController;
import ru.octol1ttle.flightassistant.computers.impl.autoflight.ThrustController;
import ru.octol1ttle.flightassistant.computers.impl.safety.AlertController;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;

public class FAKeyBindings {
    private static KeyBinding toggleFlightDirectors;
    private static KeyBinding toggleAutoThrust;
    private static KeyBinding toggleAutoPilot;

    private static KeyBinding hideAlert;
    private static KeyBinding recallAlert;

    private static KeyBinding setIdle;
    private static KeyBinding decreaseThrust;
    private static KeyBinding increaseThrust;
    private static KeyBinding setToga;

    public static void setup() {
        toggleFlightDirectors = new KeyBinding("key.flightassistant.toggle_flight_directors", InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_1, "mod.flightassistant");
        toggleAutoThrust = new KeyBinding("key.flightassistant.toggle_auto_thrust", InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_2, "mod.flightassistant");
        toggleAutoPilot = new KeyBinding("key.flightassistant.toggle_auto_pilot", InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_3, "mod.flightassistant");

        hideAlert = new KeyBinding("key.flightassistant.hide_alert", InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_0, "mod.flightassistant");
        recallAlert = new KeyBinding("key.flightassistant.recall_alert", InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_DECIMAL, "mod.flightassistant");

        setIdle = new KeyBinding("key.flightassistant.set_idle", InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_LEFT, "mod.flightassistant"
        );
        decreaseThrust = new KeyBinding("key.flightassistant.decrease_thrust", InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_DOWN, "mod.flightassistant"
        );
        increaseThrust = new KeyBinding("key.flightassistant.increase_thrust", InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UP, "mod.flightassistant"
        );
        setToga = new KeyBinding("key.flightassistant.set_toga", InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT, "mod.flightassistant"
        );

        KeyBindingHelper.registerKeyBinding(toggleFlightDirectors);
        KeyBindingHelper.registerKeyBinding(toggleAutoThrust);
        KeyBindingHelper.registerKeyBinding(toggleAutoPilot);

        KeyBindingHelper.registerKeyBinding(hideAlert);
        KeyBindingHelper.registerKeyBinding(recallAlert);

        KeyBindingHelper.registerKeyBinding(setIdle);
        KeyBindingHelper.registerKeyBinding(decreaseThrust);
        KeyBindingHelper.registerKeyBinding(increaseThrust);
        KeyBindingHelper.registerKeyBinding(setToga);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
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

                if (!ComputerRegistry.isFaulted(TimeComputer.class)) {
                    TimeComputer time = ComputerRegistry.resolve(TimeComputer.class);
                    while (decreaseThrust.wasPressed()) {
                        thrust.setThrust(thrust.getTargetThrust() - time.deltaTime * 0.5f);
                        disconnectAutoThrust = true;
                    }
                    while (increaseThrust.wasPressed()) {
                        thrust.setThrust(thrust.getTargetThrust() + time.deltaTime * 0.5f);
                        disconnectAutoThrust = true;
                    }
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
        });
    }
}
