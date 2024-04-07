package ru.octol1ttle.flightassistant.commands.plan;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import ru.octol1ttle.flightassistant.computers.navigation.FlightPlanner;
import ru.octol1ttle.flightassistant.hud.HudDisplayHost;
import ru.octol1ttle.flightassistant.computers.ComputerHost;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;
import ru.octol1ttle.flightassistant.serialization.FlightPlanSerializer;

public class SaveFlightPlanCommand {
    public static int execute(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        FlightPlanner plan = ComputerRegistry.resolve(FlightPlanner.class);
        String name = StringArgumentType.getString(context, "name");
        FlightPlanSerializer.save(plan, name);
        context.getSource().sendFeedback(Text.translatable("commands.flightassistant.flight_plan_saved", plan.size(), name));
        return 0;
    }
}
