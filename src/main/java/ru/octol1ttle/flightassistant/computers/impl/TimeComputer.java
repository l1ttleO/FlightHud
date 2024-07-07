package ru.octol1ttle.flightassistant.computers.impl;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import ru.octol1ttle.flightassistant.computers.api.ITickableComputer;

// TODO: using this SUCKS
// TODO: this should probably have timer support
public class TimeComputer implements ITickableComputer {
    private static final int HIGHLIGHT_SWITCH_THRESHOLD = 500;
    private final MinecraftClient mc;
    /**
     * The time between computer ticks *in seconds*
     */
    public float deltaTime;
    public boolean highlight;
    public Float millis;
    private Float prevMillis;
    private float highlightMillis;

    public TimeComputer(MinecraftClient mc) {
        this.mc = mc;
    }

    @Override
    public void tick() {
        float currentMillis = Util.getMeasuringTimeNano() / 1000000.0f;
        if (prevMillis == null) {
            prevMillis = currentMillis;
            return;
        }

        float deltaMS = currentMillis - prevMillis;
        prevMillis = currentMillis;

        deltaTime = MathHelper.clamp(deltaMS * 0.001f, 0.001f, 1.0f);

        if (mc.isInSingleplayer() && mc.isPaused()) {
            return;
        }
        if (millis == null) {
            millis = 0.0f;
        }
        millis += deltaMS;

        if (millis - highlightMillis > HIGHLIGHT_SWITCH_THRESHOLD) {
            highlight = !highlight;
            highlightMillis = millis;
        }
    }

    @Override
    public String getFaultTextBaseKey() {
        return "alerts.flightassistant.fault.computers.time_prvd";
    }

    @Override
    public void reset() {
        deltaTime = 0.0f;
        highlight = false;
        millis = null;
        prevMillis = null;
        highlightMillis = 0.0f;
    }
}
