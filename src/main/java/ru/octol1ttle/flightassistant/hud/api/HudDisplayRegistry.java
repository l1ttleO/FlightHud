package ru.octol1ttle.flightassistant.hud.api;

import java.util.HashMap;
import net.minecraft.util.Identifier;

public abstract class HudDisplayRegistry {
    private static final HashMap<Identifier, IHudDisplay> DISPLAYS = new HashMap<>();

    public static void register(Identifier id, IHudDisplay display) {
        if (DISPLAYS.containsKey(id)) {
            throw new IllegalStateException("Display already registered with ID: %s".formatted(id));
        }

        DISPLAYS.put(id, display);
    }
}
