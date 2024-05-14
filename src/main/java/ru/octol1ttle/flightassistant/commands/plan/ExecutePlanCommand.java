package ru.octol1ttle.flightassistant.commands.plan;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import ru.octol1ttle.flightassistant.computers.impl.navigation.FlightPlanner;
import ru.octol1ttle.flightassistant.computers.impl.navigation.LandingWaypoint;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;

public class ExecutePlanCommand {
    private static final SimpleCommandExceptionType CANNOT_EXECUTE_FROM_LANDING = new SimpleCommandExceptionType(Text.translatable("commands.flightassistant.cannot_execute_from_landing"));

    public static int execute(CommandContext<FabricClientCommandSource> context, int fromWaypoint) throws CommandSyntaxException {
        FlightPlanner plan = ComputerRegistry.resolve(FlightPlanner.class);
        WaypointUtil.throwIfNotFound(plan, fromWaypoint);
        if (plan.get(fromWaypoint) instanceof LandingWaypoint) {
            throw CANNOT_EXECUTE_FROM_LANDING.create();
        }

        plan.execute(fromWaypoint);
        context.getSource().sendFeedback(Text.translatable("commands.flightassistant.flight_plan.executed", fromWaypoint, plan.size()));
        return 0;
    }
}