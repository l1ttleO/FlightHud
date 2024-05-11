package ru.octol1ttle.flightassistant.config;

import dev.isxander.yacl3.config.v2.api.SerialEntry;

public class IndicatorConfigStorage {

    @SerialEntry
    public final IndicatorConfig flying = createFull();
    @SerialEntry
    public final IndicatorConfig notFlyingHasElytra = createMinimal();
    @SerialEntry
    public final IndicatorConfig notFlyingNoElytra = createDisabled();

    public static IndicatorConfig createFull() {
        return new IndicatorConfig();
    }

    public static IndicatorConfig createMinimal() {
        return new IndicatorConfig().setMinimal();
    }

    public static IndicatorConfig createDisabled() {
        return new IndicatorConfig().disableAll();
    }
}
