package ru.octol1ttle.flightassistant.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;
import ru.octol1ttle.flightassistant.registries.HudDisplayRegistry;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class ResetCommand {
    public static void register(LiteralArgumentBuilder<FabricClientCommandSource> builder) {
        builder
                .then(literal("reset")
                        .then(literal("computers")
                                .executes(context -> {
                                    ComputerRegistry.resetFaulted();
                                    return 0;
                                })
                        )
                        .then(literal("displays")
                                .executes(context -> {
                                    HudDisplayRegistry.resetFaulted();
                                    return 0;
                                })
                        )
                );
    }
}
