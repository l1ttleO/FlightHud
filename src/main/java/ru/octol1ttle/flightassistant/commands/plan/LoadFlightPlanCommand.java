package ru.octol1ttle.flightassistant.commands.plan;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import ru.octol1ttle.flightassistant.hud.HudDisplayHost;
import ru.octol1ttle.flightassistant.computers.ComputerHost;
import ru.octol1ttle.flightassistant.serialization.FlightPlanLoadResult;
import ru.octol1ttle.flightassistant.serialization.FlightPlanSerializer;

public class LoadFlightPlanCommand {

    public static int execute(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        ComputerHost host = HudDisplayHost.getHost();
        String name = StringArgumentType.getString(context, "name");
        FlightPlanLoadResult result = FlightPlanSerializer.load(name);
        if (result.getType() != FlightPlanLoadResult.LoadResultType.SUCCESS) {
            context.getSource().sendError(result.getType().getText());

            return -1;
        }

        host.plan.clear();
        host.plan.addAll(result.getWaypoints());
        context.getSource().sendFeedback(Text.translatable("commands.flightassistant.flight_plan_loaded", host.plan.size(), name));
        return 0;
    }
}
