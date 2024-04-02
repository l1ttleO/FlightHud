package ru.octol1ttle.flightassistant.alerts.nav;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import ru.octol1ttle.flightassistant.DrawHelper;
import ru.octol1ttle.flightassistant.alerts.AlertSoundData;
import ru.octol1ttle.flightassistant.alerts.BaseAlert;
import ru.octol1ttle.flightassistant.alerts.IECAMAlert;
import ru.octol1ttle.flightassistant.computers.safety.VoidLevelComputer;
import ru.octol1ttle.flightassistant.config.FAConfig;

public class ApproachingVoidDamageLevelAlert extends BaseAlert implements IECAMAlert {
    private final VoidLevelComputer voidLevel;

    public ApproachingVoidDamageLevelAlert(VoidLevelComputer voidLevel) {
        this.voidLevel = voidLevel;
    }

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
        Text text = voidLevel.status == VoidLevelComputer.VoidLevelStatus.REACHED_DAMAGE_LEVEL
                ? Text.translatable("alerts.flightassistant.reached_void_damage_level")
                : Text.translatable("alerts.flightassistant.approaching_void_damage_level");

        return DrawHelper.drawHighlightedText(textRenderer, context, text, x, y,
                FAConfig.indicator().warningColor, highlight);
    }
}
