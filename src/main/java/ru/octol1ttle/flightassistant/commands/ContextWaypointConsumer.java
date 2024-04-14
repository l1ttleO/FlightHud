package ru.octol1ttle.flightassistant.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import ru.octol1ttle.flightassistant.computers.impl.navigation.Waypoint;

@FunctionalInterface
public interface ContextWaypointConsumer {
    int execute(CommandContext<FabricClientCommandSource> context, Waypoint waypoint) throws CommandSyntaxException;
}
