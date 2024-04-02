package ru.octol1ttle.flightassistant;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import ru.octol1ttle.flightassistant.computers.ComputerHost;
import ru.octol1ttle.flightassistant.hud.HudDisplayHost;

public class FAKeyBindings {
    private static KeyBinding toggleFlightDirectors;
    private static KeyBinding toggleAutoFirework;
    private static KeyBinding toggleAutoPilot;

    private static KeyBinding hideAlert;
    private static KeyBinding recallAlert;

    private static KeyBinding lockManualFireworks;

    public static void setup() {
        toggleFlightDirectors = new KeyBinding("key.flightassistant.toggle_flight_directors", InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_1, "mod.flightassistant");
        toggleAutoFirework = new KeyBinding("key.flightassistant.toggle_auto_firework", InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_2, "mod.flightassistant");
        toggleAutoPilot = new KeyBinding("key.flightassistant.toggle_auto_pilot", InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_3, "mod.flightassistant");

        hideAlert = new KeyBinding("key.flightassistant.hide_alert", InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_0, "mod.flightassistant");
        recallAlert = new KeyBinding("key.flightassistant.recall_alert", InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_DECIMAL, "mod.flightassistant");

        lockManualFireworks = new KeyBinding("key.flightassistant.lock_manual_fireworks", InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_ENTER, "mod.flightassistant");

        KeyBindingHelper.registerKeyBinding(toggleFlightDirectors);
        KeyBindingHelper.registerKeyBinding(toggleAutoFirework);
        KeyBindingHelper.registerKeyBinding(toggleAutoPilot);

        KeyBindingHelper.registerKeyBinding(hideAlert);
        KeyBindingHelper.registerKeyBinding(recallAlert);

        KeyBindingHelper.registerKeyBinding(lockManualFireworks);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ComputerHost host = HudDisplayHost.getHost();
            if (!host.faulted.contains(host.autoflight)) {
                while (toggleFlightDirectors.wasPressed()) {
                    host.autoflight.flightDirectorsEnabled = !host.autoflight.flightDirectorsEnabled;
                }

                while (toggleAutoFirework.wasPressed()) {
                    if (!host.autoflight.autoFireworkEnabled) {
                        host.autoflight.autoFireworkEnabled = true;
                    } else {
                        host.autoflight.disconnectAutoFirework(false);
                    }
                }

                while (toggleAutoPilot.wasPressed()) {
                    if (!host.autoflight.autoPilotEnabled) {
                        host.autoflight.autoPilotEnabled = true;
                    } else {
                        host.autoflight.disconnectAutopilot(false);
                    }
                }
            }

            if (!host.faulted.contains(host.alert)) {
                while (hideAlert.wasPressed()) {
                    host.alert.hide();
                }
                while (recallAlert.wasPressed()) {
                    host.alert.recall();
                }
            }

            if (!host.faulted.contains(host.firework)) {
                while (lockManualFireworks.wasPressed()) {
                    host.firework.lockManualFireworks = !host.firework.lockManualFireworks;
                }
            }
        });
    }
}
