package ru.octol1ttle.flightassistant.hud.impl;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import ru.octol1ttle.flightassistant.Dimensions;
import ru.octol1ttle.flightassistant.DrawHelper;
import ru.octol1ttle.flightassistant.alerts.api.BaseAlert;
import ru.octol1ttle.flightassistant.alerts.api.ICenteredAlert;
import ru.octol1ttle.flightassistant.alerts.api.IECAMAlert;
import ru.octol1ttle.flightassistant.computers.impl.TimeComputer;
import ru.octol1ttle.flightassistant.computers.impl.safety.AlertController;
import ru.octol1ttle.flightassistant.config.FAConfig;
import ru.octol1ttle.flightassistant.hud.api.IHudDisplay;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;

public class AlertDisplay implements IHudDisplay {
    private final Dimensions dim;
    private final AlertController alert = ComputerRegistry.resolve(AlertController.class);
    private final TimeComputer time = ComputerRegistry.resolve(TimeComputer.class);

    public AlertDisplay(Dimensions dim) {
        this.dim = dim;
    }

    @Override
    public void render(DrawContext context, TextRenderer textRenderer) {
        if (!FAConfig.indicator().showAlerts) {
            return;
        }
        if (ComputerRegistry.isFaulted(AlertController.class)) {
            renderFaulted(context, textRenderer, Text.translatable("alerts.flightassistant.fault.computers.alert_mgr"));
            return;
        }
        boolean renderedCentered = false;
        int x = dim.lFrame + 5;
        int y = dim.tFrame + 15;

        for (BaseAlert alert : alert.activeAlerts) {
            if (!renderedCentered && alert instanceof ICenteredAlert centered) {
                renderedCentered = centered.render(textRenderer, context, dim.xMid,
                        dim.yMid + 10, time.highlight);
            }

            if (!alert.hidden && alert instanceof IECAMAlert ecam) {
                y += 10 * ecam.render(textRenderer, context, x, y, time.highlight);
            }
        }
    }

    @Override
    public void renderFaulted(DrawContext context, TextRenderer textRenderer) {
        renderFaulted(context, textRenderer, Text.translatable("alerts.flightassistant.fault.indicators.alert"));
    }

    private void renderFaulted(DrawContext context, TextRenderer textRenderer, Text text) {
        DrawHelper.drawHighlightedText(textRenderer, context, text, dim.lFrame + 5, dim.tFrame + 15,
                FAConfig.indicator().warningColor, time.highlight);
    }

}
