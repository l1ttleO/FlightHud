package ru.octol1ttle.flightassistant.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import org.joml.Vector2d;
import ru.octol1ttle.flightassistant.commands.plan.AddWaypointCommand;
import ru.octol1ttle.flightassistant.commands.plan.ClearWaypointsCommand;
import ru.octol1ttle.flightassistant.commands.plan.ExecutePlanCommand;
import ru.octol1ttle.flightassistant.commands.plan.InsertWaypointCommand;
import ru.octol1ttle.flightassistant.commands.plan.ListWaypointsCommand;
import ru.octol1ttle.flightassistant.commands.plan.LoadFlightPlanCommand;
import ru.octol1ttle.flightassistant.commands.plan.RemoveWaypointCommand;
import ru.octol1ttle.flightassistant.commands.plan.ReplaceWaypointCommand;
import ru.octol1ttle.flightassistant.commands.plan.SaveFlightPlanCommand;
import ru.octol1ttle.flightassistant.computers.impl.navigation.LandingMinimums;
import ru.octol1ttle.flightassistant.computers.impl.navigation.LandingWaypoint;
import ru.octol1ttle.flightassistant.computers.impl.navigation.Waypoint;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class FlightPlanCommand {
    public static void register(LiteralArgumentBuilder<FabricClientCommandSource> builder) {
        var plan = literal("plan");

        var add = literal("add");
        readWaypoint(add, AddWaypointCommand::execute);

        var remove = literal("remove")
                .then(argument("waypointIndex", IntegerArgumentType.integer(0))
                        .executes(RemoveWaypointCommand::execute)
                );

        RequiredArgumentBuilder<FabricClientCommandSource, Integer> insertAt = argument("insertAt", IntegerArgumentType.integer(0));
        readWaypoint(insertAt, InsertWaypointCommand::execute);
        var insert = literal("insert").then(insertAt);

        RequiredArgumentBuilder<FabricClientCommandSource, Integer> replaceAt = argument("replaceAt", IntegerArgumentType.integer(0));
        readWaypoint(replaceAt, ReplaceWaypointCommand::execute);
        var replace = literal("replace").then(replaceAt);

        var list = literal("list").executes(ListWaypointsCommand::execute);

        var clear = literal("clear")
                .executes(context -> ClearWaypointsCommand.execute(context, 0))
                .then(argument("fromWaypoint", IntegerArgumentType.integer(0))
                        .executes(
                                context -> ClearWaypointsCommand.execute(context, IntegerArgumentType.getInteger(context, "fromWaypoint"))
                        )
                );

        var execute = literal("execute")
                .executes(context -> ExecutePlanCommand.execute(context, 0))
                .then(argument("fromWaypoint", IntegerArgumentType.integer(0))
                        .executes(
                                context -> ExecutePlanCommand.execute(context, IntegerArgumentType.getInteger(context, "fromWaypoint"))
                        )
                );

        var save = literal("save")
                .then(argument("name", StringArgumentType.greedyString())
                        .executes(
                                SaveFlightPlanCommand::execute
                        )
                );

        var load = literal("load")
                .then(argument("name", StringArgumentType.greedyString())
                        .executes(
                                LoadFlightPlanCommand::execute
                        )
                );

        plan.then(add);
        plan.then(remove);
        plan.then(insert);
        plan.then(replace);
        plan.then(list);
        plan.then(clear);
        plan.then(execute);
        plan.then(save);
        plan.then(load);
        builder.then(plan);
    }

    private static <T extends ArgumentBuilder<FabricClientCommandSource, T>> void readWaypoint(ArgumentBuilder<FabricClientCommandSource, T> builder, ContextWaypointConsumer command) {
        builder
                .then(argument("targetX", IntegerArgumentType.integer(-30_000_000, 30_000_000))
                        .then(argument("targetZ", IntegerArgumentType.integer(-30_000_000, 30_000_000))
                                .executes(context -> command.execute(context, new Waypoint(
                                                new Vector2d(
                                                        IntegerArgumentType.getInteger(context, "targetX"),
                                                        IntegerArgumentType.getInteger(context, "targetZ")
                                                ),
                                                null,
                                                null
                                        ))
                                )
                                .then(argument("targetAltitude", IntegerArgumentType.integer(-120, 1200))
                                        .executes(context -> command.execute(context, new Waypoint(
                                                        new Vector2d(
                                                                IntegerArgumentType.getInteger(context, "targetX"),
                                                                IntegerArgumentType.getInteger(context, "targetZ")
                                                        ),
                                                        IntegerArgumentType.getInteger(context, "targetAltitude"),
                                                        null
                                                ))
                                        )
                                        .then(argument("targetSpeed", IntegerArgumentType.integer(0, 30))
                                                .executes(context -> command.execute(context, new Waypoint(
                                                                new Vector2d(
                                                                        IntegerArgumentType.getInteger(context, "targetX"),
                                                                        IntegerArgumentType.getInteger(context, "targetZ")
                                                                ),
                                                                IntegerArgumentType.getInteger(context, "targetAltitude"),
                                                                IntegerArgumentType.getInteger(context, "targetSpeed")
                                                        ))
                                                )
                                        )
                                )
                                .then(literal("land")
                                        .executes(context -> command.execute(context, new LandingWaypoint(
                                                        new Vector2d(
                                                                IntegerArgumentType.getInteger(context, "targetX"),
                                                                IntegerArgumentType.getInteger(context, "targetZ")
                                                        ),
                                                        null
                                                ))
                                        )
                                        .then(argument("minimums", IntegerArgumentType.integer(-60, 400))
                                                .then(literal("absolute")
                                                        .executes(context -> command.execute(context, new LandingWaypoint(
                                                                new Vector2d(
                                                                        IntegerArgumentType.getInteger(context, "targetX"),
                                                                        IntegerArgumentType.getInteger(context, "targetZ")
                                                                ),
                                                                new LandingMinimums(
                                                                        LandingMinimums.AltitudeType.ABSOLUTE,
                                                                        IntegerArgumentType.getInteger(context, "minimums")
                                                                )
                                                        )))
                                                )
                                                .then(literal("aboveGround")
                                                        .executes(context -> command.execute(context, new LandingWaypoint(
                                                                new Vector2d(
                                                                        IntegerArgumentType.getInteger(context, "targetX"),
                                                                        IntegerArgumentType.getInteger(context, "targetZ")
                                                                ),
                                                                new LandingMinimums(
                                                                        LandingMinimums.AltitudeType.ABOVE_GROUND,
                                                                        IntegerArgumentType.getInteger(context, "minimums")
                                                                )
                                                        )))
                                                )
                                        )
                                )
                        )
                );
    }
}
