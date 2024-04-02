package ru.octol1ttle.flightassistant.registries;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import ru.octol1ttle.flightassistant.hud.api.IHudDisplay;

public abstract class HudDisplayRegistry {
    private static final HashMap<Identifier, IHudDisplay> DISPLAYS = new HashMap<>();

    public static void register(Identifier id, IHudDisplay display) {
        if (DISPLAYS.containsKey(id)) {
            throw new IllegalStateException("Display already registered with ID: %s".formatted(id));
        }

        DISPLAYS.put(id, display);
    }

    @ApiStatus.Internal
    public static Set<Map.Entry<Identifier, IHudDisplay>> getDisplays() {
        return DISPLAYS.entrySet();
    }
}
