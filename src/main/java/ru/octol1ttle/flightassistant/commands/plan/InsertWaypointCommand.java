package ru.octol1ttle.flightassistant.commands.plan;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import ru.octol1ttle.flightassistant.hud.HudDisplayHost;
import ru.octol1ttle.flightassistant.computers.ComputerHost;
import ru.octol1ttle.flightassistant.computers.navigation.LandingWaypoint;
import ru.octol1ttle.flightassistant.computers.navigation.Waypoint;

public class InsertWaypointCommand {
    public static int execute(CommandContext<FabricClientCommandSource> context, Waypoint waypoint) throws CommandSyntaxException {
        ComputerHost host = ComputerHost.instance();
        if (host.plan.isEmpty() && waypoint instanceof LandingWaypoint) {
            WaypointUtil.throwIfFirstLanding(host.plan, waypoint);
        }

        int waypointIndex = IntegerArgumentType.getInteger(context, "insertAt");
        host.plan.add(waypointIndex, waypoint);
        context.getSource().sendFeedback(Text.translatable("commands.flightassistant.waypoint_inserted", waypointIndex, host.plan.size()));
        return 0;
    }
}
