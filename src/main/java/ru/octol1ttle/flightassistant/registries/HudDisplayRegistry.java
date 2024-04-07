package ru.octol1ttle.flightassistant.registries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import ru.octol1ttle.flightassistant.FlightAssistant;
import ru.octol1ttle.flightassistant.hud.api.IHudDisplay;

public abstract class HudDisplayRegistry {
    private static final HashMap<Identifier, IHudDisplay> DISPLAYS = new HashMap<>();
    private static final List<Identifier> faulted = new ArrayList<>();

    public static void register(Identifier id, IHudDisplay display) {
        if (DISPLAYS.containsKey(id)) {
            throw new IllegalStateException("Display already registered with ID: %s".formatted(id));
        }

        DISPLAYS.put(id, display);
    }

    public static void markFaulted(Identifier id, Throwable cause, @Nullable String message) {
        if (!DISPLAYS.containsKey(id)) {
            throw new IllegalStateException("Display not registered");
        }
        if (faulted.contains(id)) {
            throw new IllegalStateException("Display already marked as faulted");
        }

        faulted.add(id);
        FlightAssistant.LOGGER.error(Objects.requireNonNullElse(message, "Display encountered a fault"), cause);
    }

    public static boolean isFaulted(Identifier id) {
        return faulted.contains(id);
    }

    public static boolean anyFaulted() {
        return !faulted.isEmpty();
    }

    public static void resetFaulted() {
        faulted.clear();
    }

    @ApiStatus.Internal
    public static Set<Map.Entry<Identifier, IHudDisplay>> getDisplays() {
        return DISPLAYS.entrySet();
    }
}
