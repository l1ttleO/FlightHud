package ru.octol1ttle.flightassistant.alerts.impl.autoflight;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import ru.octol1ttle.flightassistant.DrawHelper;
import ru.octol1ttle.flightassistant.alerts.api.BaseAlert;
import ru.octol1ttle.flightassistant.alerts.api.IECAMAlert;
import ru.octol1ttle.flightassistant.alerts.impl.AlertSoundData;
import ru.octol1ttle.flightassistant.computers.impl.autoflight.AutoFlightController;
import ru.octol1ttle.flightassistant.config.FAConfig;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;

public class AutoThrustOffAlert extends BaseAlert implements IECAMAlert {
    private final AutoFlightController autoflight = ComputerRegistry.resolve(AutoFlightController.class);

    public AutoThrustOffAlert() {
        this.hidden = true;
    }

    @Override
    public boolean isTriggered() {
        return !autoflight.autoThrustEnabled;
    }

    @Override
    public @NotNull AlertSoundData getSoundData() {
        return autoflight.athrDisconnectionForced ? AlertSoundData.MASTER_CAUTION : AlertSoundData.EMPTY;
    }

    @Override
    public int render(TextRenderer textRenderer, DrawContext context, int x, int y, boolean highlight) {
        return DrawHelper.drawHighlightedText(textRenderer, context, Text.translatable("alerts.flightassistant.autoflight.auto_thrust_off"), x, y,
                FAConfig.indicator().cautionColor,
                highlight && autoflight.athrDisconnectionForced
        );
    }
}
