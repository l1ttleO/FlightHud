package ru.octol1ttle.flightassistant.commands.plan;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import ru.octol1ttle.flightassistant.computers.impl.navigation.FlightPlanner;
import ru.octol1ttle.flightassistant.computers.impl.navigation.Waypoint;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;

public class AddWaypointCommand {
    public static int execute(CommandContext<FabricClientCommandSource> context, Waypoint waypoint) throws CommandSyntaxException {
        FlightPlanner plan = ComputerRegistry.resolve(FlightPlanner.class);
        WaypointUtil.throwIfFirstLanding(plan, waypoint);

        plan.add(waypoint);
        context.getSource().sendFeedback(Text.translatable("commands.flightassistant.waypoint_created", plan.size() - 1, plan.size()));
        return 0;
    }
}
