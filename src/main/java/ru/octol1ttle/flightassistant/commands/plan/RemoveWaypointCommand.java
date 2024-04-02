package ru.octol1ttle.flightassistant.commands.plan;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import ru.octol1ttle.flightassistant.hud.HudDisplayHost;
import ru.octol1ttle.flightassistant.computers.ComputerHost;

public class RemoveWaypointCommand {
    public static int execute(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        ComputerHost host = HudDisplayHost.getHost();
        int waypointIndex = IntegerArgumentType.getInteger(context, "waypointIndex");
        WaypointUtil.throwIfNotFound(host.plan, waypointIndex);

        host.plan.remove(waypointIndex);
        context.getSource().sendFeedback(Text.translatable("commands.flightassistant.waypoint_removed", waypointIndex, host.plan.size()));
        return 0;
    }
}
