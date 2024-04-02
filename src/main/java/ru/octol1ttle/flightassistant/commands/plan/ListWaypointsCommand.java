package ru.octol1ttle.flightassistant.commands.plan;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import ru.octol1ttle.flightassistant.hud.HudDisplayHost;
import ru.octol1ttle.flightassistant.computers.ComputerHost;
import ru.octol1ttle.flightassistant.computers.navigation.FlightPlanner;
import ru.octol1ttle.flightassistant.computers.navigation.LandingWaypoint;
import ru.octol1ttle.flightassistant.computers.navigation.Waypoint;

public class ListWaypointsCommand {
    public static int execute(CommandContext<FabricClientCommandSource> context) {
        ComputerHost host = ComputerHost.instance();
        context.getSource().sendFeedback(Text.translatable("commands.flightassistant.total_waypoints", host.plan.size()));
        FlightPlanner plan = host.plan;
        for (int i = 0; i < plan.size(); i++) {
            Waypoint waypoint = plan.get(i);
            Text feedback;
            if (waypoint instanceof LandingWaypoint landing) {
                feedback = Text.translatable("commands.flightassistant.land_waypoint_info",
                        i,
                        (int) waypoint.targetPosition().x,
                        (int) waypoint.targetPosition().y,
                        landing.formatMinimums());
            } else {
                //noinspection WrongTypeInTranslationArgs
                feedback = Text.translatable("commands.flightassistant.waypoint_info",
                        i,
                        (int) waypoint.targetPosition().x,
                        (int) waypoint.targetPosition().y,
                        waypoint.targetAltitude() != null ? waypoint.targetAltitude() : Text.translatable("commands.flightassistant.waypoint_info_not_set"),
                        waypoint.targetSpeed() != null ? waypoint.targetSpeed() : Text.translatable("commands.flightassistant.waypoint_info_not_set"));
            }

            context.getSource().sendFeedback(feedback);
        }
        return 0;
    }
}
