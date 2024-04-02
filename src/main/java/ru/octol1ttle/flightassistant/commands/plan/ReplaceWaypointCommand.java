package ru.octol1ttle.flightassistant.commands.plan;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import ru.octol1ttle.flightassistant.hud.HudDisplayHost;
import ru.octol1ttle.flightassistant.computers.ComputerHost;
import ru.octol1ttle.flightassistant.computers.navigation.Waypoint;

public class ReplaceWaypointCommand {
    public static int execute(CommandContext<FabricClientCommandSource> context, Waypoint waypoint) throws CommandSyntaxException {
        ComputerHost host = HudDisplayHost.getHost();
        int waypointIndex = IntegerArgumentType.getInteger(context, "replaceAt");
        WaypointUtil.throwIfNotFound(host.plan, waypointIndex);

        host.plan.set(waypointIndex, waypoint);
        context.getSource().sendFeedback(Text.translatable("commands.flightassistant.waypoint_replaced", waypointIndex, host.plan.size()));
        return 0;
    }
}
