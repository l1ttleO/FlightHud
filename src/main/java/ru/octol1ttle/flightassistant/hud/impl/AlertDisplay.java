package ru.octol1ttle.flightassistant.hud.impl;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import ru.octol1ttle.flightassistant.Dimensions;
import ru.octol1ttle.flightassistant.DrawHelper;
import ru.octol1ttle.flightassistant.alerts.BaseAlert;
import ru.octol1ttle.flightassistant.alerts.ICenteredAlert;
import ru.octol1ttle.flightassistant.alerts.IECAMAlert;
import ru.octol1ttle.flightassistant.computers.ComputerHost;
import ru.octol1ttle.flightassistant.computers.TimeComputer;
import ru.octol1ttle.flightassistant.computers.safety.AlertController;
import ru.octol1ttle.flightassistant.config.FAConfig;
import ru.octol1ttle.flightassistant.hud.api.IHudDisplay;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;

public class AlertDisplay implements IHudDisplay {
    private final Dimensions dim;
    private final ComputerHost host;
    private final AlertController alert;
    private final TimeComputer time;

    public AlertDisplay(Dimensions dim, ComputerHost host) {
        this.dim = dim;
        this.host = host;
        this.alert = ComputerRegistry.resolve(AlertController.class);
        this.time = ComputerRegistry.resolve(TimeComputer.class);
    }

    @Override
    public void render(DrawContext context, TextRenderer textRenderer) {
        if (!FAConfig.indicator().showAlerts) {
            return;
        }
        /*if (host.faulted.contains(alert)) {
            renderFaulted(context, textRenderer, Text.translatable("alerts.flightassistant.fault.computers.alert_mgr"));
            return;
        }*/
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

    @Override
    public String getId() {
        return "alert";
    }
}
