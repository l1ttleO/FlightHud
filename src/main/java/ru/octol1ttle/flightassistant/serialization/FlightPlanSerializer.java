package ru.octol1ttle.flightassistant.serialization;

import dev.isxander.yacl3.platform.YACLPlatform;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.MinecraftVersion;
import org.jetbrains.annotations.NotNull;
import ru.octol1ttle.flightassistant.FlightAssistant;
import ru.octol1ttle.flightassistant.MinecraftProtocolVersions;
import ru.octol1ttle.flightassistant.computers.impl.navigation.Waypoint;
import ru.octol1ttle.flightassistant.serialization.api.ISerializableFactory;
import ru.octol1ttle.flightassistant.serialization.api.ISerializableList;
import ru.octol1ttle.flightassistant.serialization.api.ISerializableObject;
import ru.octol1ttle.flightassistant.serialization.json.JsonFactory;
import ru.octol1ttle.flightassistant.serialization.nbt.NbtFactory;

public class FlightPlanSerializer {
    private static final Path PLAN_PATH = YACLPlatform.getConfigDir().resolve("%s/plans/".formatted(FlightAssistant.MODID));

    static final ISerializableFactory NBT_FACTORY = new NbtFactory();
    static final ISerializableFactory JSON_FACTORY = new JsonFactory();
    static final ISerializableFactory WRITE_FACTORY = JSON_FACTORY;

    public static void save(List<Waypoint> plan, String name) {
        ISerializableObject planObject = WRITE_FACTORY.createObject();

        ISerializableList<ISerializableObject> list = WRITE_FACTORY.createList(plan.size());

        for (Waypoint waypoint : plan) {
            list.add(WaypointSerializer.save(waypoint, WRITE_FACTORY.createObject()));
        }

        planObject.put("Waypoints", list);

        try {
            Files.createDirectories(PLAN_PATH);
            WRITE_FACTORY.createSerializer().write(PLAN_PATH, name, planObject);
        } catch (IOException e) {
            FlightAssistant.LOGGER.error("IO error detected during flight plan serialization with name: %s".formatted(name), e);
        }
    }

    public static @NotNull FlightPlanLoadResult load(String name) {
        List<Waypoint> loaded = new ArrayList<>();

        try {
            ISerializableObject object = JSON_FACTORY.createSerializer().read(PLAN_PATH, name);

            if (object == null) {
                if (MinecraftVersion.CURRENT.getProtocolVersion() >= MinecraftProtocolVersions.R20_3) {
                    object = NBT_FACTORY.createSerializer().read(PLAN_PATH, name);
                } else if (Files.exists(PLAN_PATH.resolve("%s.dat".formatted(name)))) {
                    return new FlightPlanLoadResult(null, FlightPlanLoadResult.LoadResultType.NBT_NOT_SUPPORTED);
                }
            }

            if (object == null) {
                return new FlightPlanLoadResult(null, FlightPlanLoadResult.LoadResultType.NOT_FOUND);
            }

            ISerializableList<ISerializableObject> list = object.getList("Waypoints");
            for (ISerializableObject waypointObject : list) {
                loaded.add(WaypointSerializer.load(waypointObject));
            }

            return new FlightPlanLoadResult(loaded, FlightPlanLoadResult.LoadResultType.SUCCESS);
        } catch (Exception e) {
            FlightAssistant.LOGGER.error("Error deserializing flight plan with name: %s".formatted(name), e);
            return new FlightPlanLoadResult(null, FlightPlanLoadResult.LoadResultType.ERROR);
        }
    }
}
