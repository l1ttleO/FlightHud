package ru.octol1ttle.flightassistant.commands.plan;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import ru.octol1ttle.flightassistant.computers.navigation.FlightPlanner;
import ru.octol1ttle.flightassistant.hud.HudDisplayHost;
import ru.octol1ttle.flightassistant.computers.ComputerHost;
import ru.octol1ttle.flightassistant.computers.navigation.Waypoint;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;

public class ReplaceWaypointCommand {
    public static int execute(CommandContext<FabricClientCommandSource> context, Waypoint waypoint) throws CommandSyntaxException {
        FlightPlanner plan = ComputerRegistry.resolve(FlightPlanner.class);
        int waypointIndex = IntegerArgumentType.getInteger(context, "replaceAt");
        WaypointUtil.throwIfNotFound(plan, waypointIndex);

        plan.set(waypointIndex, waypoint);
        context.getSource().sendFeedback(Text.translatable("commands.flightassistant.waypoint_replaced", waypointIndex, plan.size()));
        return 0;
    }
}
