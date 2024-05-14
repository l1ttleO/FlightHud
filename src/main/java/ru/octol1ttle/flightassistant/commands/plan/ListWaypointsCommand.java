package ru.octol1ttle.flightassistant.commands.plan;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import ru.octol1ttle.flightassistant.computers.impl.navigation.FlightPlanner;
import ru.octol1ttle.flightassistant.computers.impl.navigation.LandingWaypoint;
import ru.octol1ttle.flightassistant.computers.impl.navigation.Waypoint;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;

public class ListWaypointsCommand {
    public static int execute(CommandContext<FabricClientCommandSource> context) {
        FlightPlanner plan = ComputerRegistry.resolve(FlightPlanner.class);
        context.getSource().sendFeedback(Text.translatable("commands.flightassistant.total_waypoints", plan.size()));
        for (int i = 0; i < plan.size(); i++) {
            Waypoint waypoint = plan.get(i);
            Text feedback;
            if (waypoint instanceof LandingWaypoint landing) {
                feedback = Text.translatable(
                        "commands.flightassistant.waypoint_info.land",
                        i,
                        (int) waypoint.targetPosition().x,
                        (int) waypoint.targetPosition().y,
                        landing.formatMinimums());
            } else {
                feedback = Text.translatable(
                        "commands.flightassistant.waypoint_info",
                        i,
                        (int) waypoint.targetPosition().x,
                        (int) waypoint.targetPosition().y,
                        waypoint.targetAltitude() != null ? waypoint.targetAltitude() : Text.translatable("commands.flightassistant.waypoint_info.not_set"),
                        waypoint.targetSpeed() != null ? waypoint.targetSpeed() : Text.translatable("commands.flightassistant.waypoint_info.not_set")
                );
            }

            context.getSource().sendFeedback(feedback);
        }
        return 0;
    }
}
