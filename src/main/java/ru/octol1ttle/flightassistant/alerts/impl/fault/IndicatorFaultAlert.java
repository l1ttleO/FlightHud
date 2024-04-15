package ru.octol1ttle.flightassistant.alerts.impl.fault;

import java.util.Map;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import ru.octol1ttle.flightassistant.DrawHelper;
import ru.octol1ttle.flightassistant.alerts.impl.AlertSoundData;
import ru.octol1ttle.flightassistant.alerts.api.BaseAlert;
import ru.octol1ttle.flightassistant.alerts.api.IECAMAlert;
import ru.octol1ttle.flightassistant.config.FAConfig;
import ru.octol1ttle.flightassistant.hud.api.IHudDisplay;
import ru.octol1ttle.flightassistant.registries.HudDisplayRegistry;

public class IndicatorFaultAlert extends BaseAlert implements IECAMAlert {
    @Override
    public boolean isTriggered() {
        return HudDisplayRegistry.anyFaulted();
    }

    @Override
    public @NotNull AlertSoundData getSoundData() {
        return AlertSoundData.MASTER_CAUTION;
    }

    @Override
    public int render(TextRenderer textRenderer, DrawContext context, int x, int y, boolean highlight) {
        int i = 0;
        for (Map.Entry<Identifier, IHudDisplay> entry : HudDisplayRegistry.getDisplays()) {
            Identifier id = entry.getKey();
            if (HudDisplayRegistry.isFaulted(id)) {
                i += DrawHelper.drawText(textRenderer, context,
                        Text.translatable("alerts.flightassistant.fault.hud." + id.getPath()), x, y,
                        FAConfig.indicator().cautionColor);
                y += 10;
            }
        }

        return i;
    }
}
