package ru.octol1ttle.flightassistant.alerts.firework;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.NotNull;
import ru.octol1ttle.flightassistant.DrawHelper;
import ru.octol1ttle.flightassistant.alerts.AlertSoundData;
import ru.octol1ttle.flightassistant.alerts.BaseAlert;
import ru.octol1ttle.flightassistant.alerts.IECAMAlert;
import ru.octol1ttle.flightassistant.computers.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.autoflight.FireworkController;
import ru.octol1ttle.flightassistant.config.FAConfig;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;


public class FireworkUnsafeAlert extends BaseAlert implements IECAMAlert {
    private final AirDataComputer data;
    private final FireworkController firework;

    public FireworkUnsafeAlert() {
        this.data = ComputerRegistry.resolve(AirDataComputer.class);
        this.firework = ComputerRegistry.resolve(FireworkController.class);
    }

    @Override
    public boolean isTriggered() {
        for (Hand hand : Hand.values()) {
            ItemStack stack = data.player().getStackInHand(hand);
            if (stack.getItem() instanceof FireworkRocketItem && !firework.isFireworkSafe(stack)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public @NotNull AlertSoundData getSoundData() {
        return AlertSoundData.MASTER_WARNING;
    }

    @Override
    public int render(TextRenderer textRenderer, DrawContext context, int x, int y, boolean highlight) {
        return DrawHelper.drawHighlightedText(textRenderer, context, Text.translatable("alerts.flightassistant.firework.unsafe"), x, y,
                FAConfig.indicator().warningColor, highlight && data.isFlying());
    }
}
