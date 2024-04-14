package ru.octol1ttle.flightassistant.serialization;

import org.joml.Vector2d;
import ru.octol1ttle.flightassistant.computers.impl.navigation.LandingMinimums;
import ru.octol1ttle.flightassistant.computers.impl.navigation.LandingWaypoint;
import ru.octol1ttle.flightassistant.computers.impl.navigation.Waypoint;
import ru.octol1ttle.flightassistant.serialization.api.ISerializableObject;

public class WaypointSerializer {
    public static <T extends ISerializableObject> ISerializableObject save(Waypoint waypoint, T object) {
        object.putBoolean("IsLanding", waypoint instanceof LandingWaypoint);
        if (waypoint instanceof LandingWaypoint landing) {
            return saveLanding(landing, object);
        }

        object.putDouble("TargetX", waypoint.targetPosition().x);
        object.putDouble("TargetZ", waypoint.targetPosition().y);
        if (waypoint.targetAltitude() != null) {
            object.putInt("TargetAltitude", waypoint.targetAltitude());
        }
        if (waypoint.targetSpeed() != null) {
            object.putInt("TargetSpeed", waypoint.targetSpeed());
        }

        return object;
    }

    private static ISerializableObject saveLanding(LandingWaypoint waypoint, ISerializableObject object) {
        object.putDouble("TargetX", waypoint.targetPosition().x);
        object.putDouble("TargetZ", waypoint.targetPosition().y);
        if (waypoint.minimums != null) {
            ISerializableObject minimums = FlightPlanSerializer.WRITE_FACTORY.createObject();
            minimums.putString("AltitudeType", waypoint.minimums.type().serializedName);
            minimums.putInt("Altitude", waypoint.minimums.altitude());

            object.put("Minimums", minimums);
        }

        return object;
    }

    public static Waypoint load(ISerializableObject object) {
        if (object.getBoolean("IsLanding")) {
            return readLanding(object);
        }

        Vector2d targetPosition = new Vector2d(object.getDouble("TargetX"), object.getDouble("TargetZ"));
        Integer targetAltitude = null;
        Integer targetSpeed = null;
        if (object.contains("TargetAltitude")) {
            targetAltitude = object.getInt("TargetAltitude");
        }
        if (object.contains("TargetSpeed")) {
            targetSpeed = object.getInt("TargetSpeed");
        }

        return new Waypoint(targetPosition, targetAltitude, targetSpeed);
    }

    private static LandingWaypoint readLanding(ISerializableObject object) {
        Vector2d targetPosition = new Vector2d(object.getDouble("TargetX"), object.getDouble("TargetZ"));
        LandingMinimums minimums = null;
        if (object.contains("Minimums")) {
            minimums = readMinimums(object.get("Minimums"));
        }

        return new LandingWaypoint(targetPosition, minimums);
    }

    private static LandingMinimums readMinimums(ISerializableObject object) {
        return new LandingMinimums(
                LandingMinimums.AltitudeType.fromSerializedName(object.getString("AltitudeType")),
                object.getInt("Altitude")
        );
    }
}
