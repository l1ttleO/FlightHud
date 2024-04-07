package ru.octol1ttle.flightassistant.commands.plan;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import ru.octol1ttle.flightassistant.computers.ComputerHost;
import ru.octol1ttle.flightassistant.computers.navigation.FlightPlanner;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;

public class ClearWaypointsCommand {
    public static final SimpleCommandExceptionType NOTHING_TO_CLEAR = new SimpleCommandExceptionType(Text.translatable("commands.flightassistant.nothing_to_clear"));

    public static int execute(CommandContext<FabricClientCommandSource> context, int fromWaypoint) throws CommandSyntaxException {
        FlightPlanner plan = ComputerRegistry.resolve(FlightPlanner.class);
        if (!plan.waypointExistsAt(fromWaypoint)) {
            throw NOTHING_TO_CLEAR.create();
        }

        for (int i = plan.size() - 1; i >= 0; i--) {
            if (i >= fromWaypoint) {
                plan.remove(i);
            }
        }

        context.getSource().sendFeedback(Text.translatable("commands.flightassistant.flight_plan_cleared", fromWaypoint, plan.size()));
        return 0;
    }
}
