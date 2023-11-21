package ru.octol1ttle.flightassistant.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import ru.octol1ttle.flightassistant.HudRenderer;

public class ResetFaultedIndicatorsCommand implements Command<FabricClientCommandSource> {
    @Override
    public int run(CommandContext<FabricClientCommandSource> context) {
        if (HudRenderer.INSTANCE != null) {
            HudRenderer.INSTANCE.resetFaulted();
        }
        return 0;
    }
}
