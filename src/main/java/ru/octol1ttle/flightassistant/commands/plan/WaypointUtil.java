package ru.octol1ttle.flightassistant.commands.plan;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.text.Text;
import ru.octol1ttle.flightassistant.computers.impl.navigation.FlightPlanner;
import ru.octol1ttle.flightassistant.computers.impl.navigation.LandingWaypoint;
import ru.octol1ttle.flightassistant.computers.impl.navigation.Waypoint;

public class WaypointUtil {
    private static final SimpleCommandExceptionType FLIGHT_PLAN_EMPTY = new SimpleCommandExceptionType(Text.translatable("commands.flightassistant.flight_plan.empty"));
    private static final SimpleCommandExceptionType NO_SUCH_WAYPOINT = new SimpleCommandExceptionType(Text.translatable("commands.flightassistant.no_such_waypoint"));
    private static final SimpleCommandExceptionType FIRST_WAYPOINT_CANNOT_BE_LANDING = new SimpleCommandExceptionType(Text.translatable("commands.flightassistant.first_wp_cannot_be_landing"));

    public static void throwIfNotFound(FlightPlanner plan, int index) throws CommandSyntaxException {
        if (plan.isEmpty()) {
            throw FLIGHT_PLAN_EMPTY.create();
        }
        if (!plan.waypointExistsAt(index)) {
            throw NO_SUCH_WAYPOINT.create();
        }
    }

    public static void throwIfFirstLanding(FlightPlanner plan, Waypoint waypoint) throws CommandSyntaxException {
        if (plan.isEmpty() && waypoint instanceof LandingWaypoint) {
            throw FIRST_WAYPOINT_CANNOT_BE_LANDING.create();
        }
    }
}
