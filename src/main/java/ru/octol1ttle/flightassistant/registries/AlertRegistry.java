package ru.octol1ttle.flightassistant.registries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.jetbrains.annotations.ApiStatus;
import ru.octol1ttle.flightassistant.alerts.api.BaseAlert;

public abstract class AlertRegistry {
    private static final List<BaseAlert> instances = new ArrayList<>();

    public static void register(BaseAlert alert) {
        if (instances.contains(alert)) {
            throw new IllegalStateException("Alert already registered");
        }

        instances.add(alert);
    }

    @ApiStatus.Internal
    public static Collection<BaseAlert> getAlerts() {
        return instances;
    }
}
