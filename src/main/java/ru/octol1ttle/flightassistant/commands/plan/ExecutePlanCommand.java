package ru.octol1ttle.flightassistant.commands.plan;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import ru.octol1ttle.flightassistant.hud.HudDisplayHost;
import ru.octol1ttle.flightassistant.computers.ComputerHost;
import ru.octol1ttle.flightassistant.computers.navigation.LandingWaypoint;

public class ExecutePlanCommand {
    private static final SimpleCommandExceptionType CANNOT_EXECUTE_FROM_LANDING = new SimpleCommandExceptionType(Text.translatable("commands.flightassistant.cannot_execute_from_landing"));

    public static int execute(CommandContext<FabricClientCommandSource> context, int fromWaypoint) throws CommandSyntaxException {
        ComputerHost host = HudDisplayHost.getHost();
        WaypointUtil.throwIfNotFound(host.plan, fromWaypoint);
        if (host.plan.get(fromWaypoint) instanceof LandingWaypoint) {
            throw CANNOT_EXECUTE_FROM_LANDING.create();
        }

        host.plan.execute(fromWaypoint);
        context.getSource().sendFeedback(Text.translatable("commands.flightassistant.flight_plan_executed", fromWaypoint, host.plan.size()));
        return 0;
    }
}