package ru.octol1ttle.flightassistant.commands.plan;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import ru.octol1ttle.flightassistant.computers.ComputerHost;
import ru.octol1ttle.flightassistant.computers.navigation.Waypoint;

public class AddWaypointCommand {
    public static int execute(CommandContext<FabricClientCommandSource> context, Waypoint waypoint) throws CommandSyntaxException {
        ComputerHost host = ComputerHost.instance();
        WaypointUtil.throwIfFirstLanding(host.plan, waypoint);

        host.plan.add(waypoint);
        context.getSource().sendFeedback(Text.translatable("commands.flightassistant.waypoint_created", host.plan.size() - 1, host.plan.size()));
        return 0;
    }
}
