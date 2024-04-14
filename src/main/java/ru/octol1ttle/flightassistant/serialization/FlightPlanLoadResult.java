package ru.octol1ttle.flightassistant.serialization;

import java.util.List;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.octol1ttle.flightassistant.computers.impl.navigation.Waypoint;

public class FlightPlanLoadResult {
    private final @Nullable List<Waypoint> loaded;
    private final LoadResultType type;

    public FlightPlanLoadResult(@Nullable List<Waypoint> loaded, @NotNull LoadResultType type) {
        this.loaded = loaded;
        this.type = type;
    }

    public List<Waypoint> getWaypoints() {
        return loaded;
    }

    public LoadResultType getType() {
        return type;
    }

    public enum LoadResultType {
        SUCCESS(Text.translatable("commands.flightassistant.flight_plan_loaded")),
        NOT_FOUND(Text.translatable("commands.flightassistant.flight_plan_not_found")),
        NBT_NOT_SUPPORTED(Text.translatable("commands.flightassistant.flight_plan_not_supported")),
        ERROR(Text.translatable("commands.flightassistant.flight_plan_load_error"));

        private final Text text;

        LoadResultType(Text text) {
            this.text = text;
        }

        public Text getText() {
            return text;
        }
    }
}
