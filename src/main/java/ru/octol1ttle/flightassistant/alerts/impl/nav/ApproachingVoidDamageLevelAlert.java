package ru.octol1ttle.flightassistant.alerts.impl.nav;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import ru.octol1ttle.flightassistant.DrawHelper;
import ru.octol1ttle.flightassistant.alerts.api.BaseAlert;
import ru.octol1ttle.flightassistant.alerts.api.IECAMAlert;
import ru.octol1ttle.flightassistant.alerts.impl.AlertSoundData;
import ru.octol1ttle.flightassistant.computers.impl.safety.VoidLevelComputer;
import ru.octol1ttle.flightassistant.config.FAConfig;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;

public class ApproachingVoidDamageLevelAlert extends BaseAlert implements IECAMAlert {
    private final VoidLevelComputer voidLevel = ComputerRegistry.resolve(VoidLevelComputer.class);

    @Override
    public boolean isTriggered() {
        return voidLevel.approachingOrReachedDamageLevel();
    }

    @Override
    public @NotNull AlertSoundData getSoundData() {
        return AlertSoundData.MASTER_WARNING;
    }

    @Override
    public int render(TextRenderer textRenderer, DrawContext context, int x, int y, boolean highlight) {
        Text text = Text.translatable("alerts.flightassistant.void_damage_level"
                + (voidLevel.status == VoidLevelComputer.VoidLevelStatus.REACHED_DAMAGE_LEVEL
                ? ".reached"
                : ".approaching"));

        return DrawHelper.drawHighlightedText(textRenderer, context, text, x, y,
                FAConfig.indicator().warningColor, highlight);
    }
}
