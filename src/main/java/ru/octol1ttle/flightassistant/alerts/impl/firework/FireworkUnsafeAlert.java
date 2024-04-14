package ru.octol1ttle.flightassistant.alerts.impl.firework;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.NotNull;
import ru.octol1ttle.flightassistant.DrawHelper;
import ru.octol1ttle.flightassistant.alerts.impl.AlertSoundData;
import ru.octol1ttle.flightassistant.alerts.api.BaseAlert;
import ru.octol1ttle.flightassistant.alerts.api.IECAMAlert;
import ru.octol1ttle.flightassistant.computers.impl.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.impl.autoflight.FireworkController;
import ru.octol1ttle.flightassistant.config.FAConfig;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;


public class FireworkUnsafeAlert extends BaseAlert implements IECAMAlert {
    private final AirDataComputer data = ComputerRegistry.resolve(AirDataComputer.class);
    private final FireworkController firework = ComputerRegistry.resolve(FireworkController.class);

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
