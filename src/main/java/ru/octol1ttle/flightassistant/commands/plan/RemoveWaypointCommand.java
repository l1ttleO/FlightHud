package ru.octol1ttle.flightassistant.commands.plan;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import ru.octol1ttle.flightassistant.computers.impl.navigation.FlightPlanner;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;

public class RemoveWaypointCommand {
    public static int execute(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        FlightPlanner plan = ComputerRegistry.resolve(FlightPlanner.class);
        int waypointIndex = IntegerArgumentType.getInteger(context, "waypointIndex");
        WaypointUtil.throwIfNotFound(plan, waypointIndex);

        plan.remove(waypointIndex);
        context.getSource().sendFeedback(Text.translatable("commands.flightassistant.waypoint.removed", waypointIndex, plan.size()));
        return 0;
    }
}
