package ru.octol1ttle.flightassistant.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import ru.octol1ttle.flightassistant.computers.impl.autoflight.AutoFlightController;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class SelectCommand {
    public static void register(LiteralArgumentBuilder<FabricClientCommandSource> builder) {
        var select = literal("select");
        registerSpeed(select);
        registerAltitude(select);
        registerHeading(select);
        builder.then(select);
    }

    private static void registerSpeed(LiteralArgumentBuilder<FabricClientCommandSource> select) {
        select.then(literal("speed")
                .then(literal("managed")
                        .executes(context -> {
                            ComputerRegistry.resolve(AutoFlightController.class).selectedSpeed = null;
                            return 0;
                        })
                )
                .then(argument("target", IntegerArgumentType.integer(0, 30))
                        .executes(context -> {
                            ComputerRegistry.resolve(AutoFlightController.class).selectedSpeed = IntegerArgumentType.getInteger(context, "target");
                            return 0;
                        })
                )
        );
    }

    private static void registerAltitude(LiteralArgumentBuilder<FabricClientCommandSource> select) {
        select.then(literal("altitude")
                .then(literal("managed")
                        .executes(context -> {
                            ComputerRegistry.resolve(AutoFlightController.class).selectedAltitude = null;
                            return 0;
                        })
                )
                .then(argument("target", IntegerArgumentType.integer(-120, 1200))
                        .executes(context -> {
                            ComputerRegistry.resolve(AutoFlightController.class).selectedAltitude = IntegerArgumentType.getInteger(context, "target");
                            return 0;
                        })
                )
        );
    }

    private static void registerHeading(LiteralArgumentBuilder<FabricClientCommandSource> select) {
        select.then(literal("heading")
                .then(literal("managed")
                        .executes(context -> {
                            ComputerRegistry.resolve(AutoFlightController.class).selectedHeading = null;
                            return 0;
                        })
                )
                .then(argument("target", IntegerArgumentType.integer(0, 360))
                        .executes(context -> {
                            ComputerRegistry.resolve(AutoFlightController.class).selectedHeading = IntegerArgumentType.getInteger(context, "target");
                            return 0;
                        })
                )
        );
    }
}
