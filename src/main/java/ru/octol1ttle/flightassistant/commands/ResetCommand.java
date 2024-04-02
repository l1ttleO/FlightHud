package ru.octol1ttle.flightassistant.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import ru.octol1ttle.flightassistant.hud.HudDisplayHost;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class ResetCommand {
    public static void register(LiteralArgumentBuilder<FabricClientCommandSource> builder) {
        builder
                .then(literal("reset")
                        .then(literal("computers")
                                .then(literal("all")
                                        .executes(context -> {
                                            HudDisplayHost.getHost().resetComputers(true);
                                            return 0;
                                        }))
                                .then(literal("faulted")
                                        .executes(context -> {
                                            HudDisplayHost.getHost().resetComputers(false);
                                            return 0;
                                        })
                                ))
                        .then(literal("hud")
                                .executes(context -> {
                                    HudDisplayHost.INSTANCE.resetFaulted();
                                    return 0;
                                })
                        )
                );
    }
}
