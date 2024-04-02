package ru.octol1ttle.flightassistant.alerts.nav;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import ru.octol1ttle.flightassistant.DrawHelper;
import ru.octol1ttle.flightassistant.alerts.AlertSoundData;
import ru.octol1ttle.flightassistant.alerts.BaseAlert;
import ru.octol1ttle.flightassistant.alerts.IECAMAlert;
import ru.octol1ttle.flightassistant.computers.navigation.FlightPlanner;
import ru.octol1ttle.flightassistant.config.FAConfig;

public class MinimumsAlert extends BaseAlert implements IECAMAlert {
    private final FlightPlanner plan;
    private boolean triggered = false;

    public MinimumsAlert(FlightPlanner plan) {
        this.plan = plan;
    }

    @Override
    public boolean isTriggered() {
        if (plan.getMinimums(0) == null) {
            triggered = false;
        }
        if (plan.isBelowMinimums()) {
            triggered = true;
        }
        return triggered;
    }

    @Override
    public @NotNull AlertSoundData getSoundData() {
        return AlertSoundData.MINIMUMS;
    }

    @Override
    public int render(TextRenderer textRenderer, DrawContext context, int x, int y, boolean highlight) {
        if (!plan.isBelowMinimums()) {
            return 0;
        }

        return DrawHelper.drawHighlightedText(textRenderer, context, Text.translatable("alerts.flightassistant.reached_minimums"), x, y,
                FAConfig.indicator().cautionColor, false);
    }
}
