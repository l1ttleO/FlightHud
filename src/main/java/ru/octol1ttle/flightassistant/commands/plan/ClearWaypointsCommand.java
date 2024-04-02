package ru.octol1ttle.flightassistant.commands.plan;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import ru.octol1ttle.flightassistant.hud.HudDisplayHost;
import ru.octol1ttle.flightassistant.computers.ComputerHost;

public class ClearWaypointsCommand {
    public static final SimpleCommandExceptionType NOTHING_TO_CLEAR = new SimpleCommandExceptionType(Text.translatable("commands.flightassistant.nothing_to_clear"));

    public static int execute(CommandContext<FabricClientCommandSource> context, int fromWaypoint) throws CommandSyntaxException {
        ComputerHost host = HudDisplayHost.getHost();
        if (!host.plan.waypointExistsAt(fromWaypoint)) {
            throw NOTHING_TO_CLEAR.create();
        }

        for (int i = host.plan.size() - 1; i >= 0; i--) {
            if (i >= fromWaypoint) {
                host.plan.remove(i);
            }
        }

        context.getSource().sendFeedback(Text.translatable("commands.flightassistant.flight_plan_cleared", fromWaypoint, host.plan.size()));
        return 0;
    }
}
