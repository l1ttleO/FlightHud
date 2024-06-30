package ru.octol1ttle.flightassistant.computers.impl.navigation;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2d;
import ru.octol1ttle.flightassistant.FAMathHelper;
import ru.octol1ttle.flightassistant.computers.api.ITickableComputer;
import ru.octol1ttle.flightassistant.computers.impl.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.impl.TimeComputer;
import ru.octol1ttle.flightassistant.computers.impl.autoflight.PitchController;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;

// TODO: this handles too much stuff
// TODO: like, WAY too much stuff
public class FlightPlanner extends ArrayList<Waypoint> implements ITickableComputer {
    private final AirDataComputer data = ComputerRegistry.resolve(AirDataComputer.class);
    private final TimeComputer time = ComputerRegistry.resolve(TimeComputer.class);

    private final List<Double> groundSpeeds = new ArrayList<>();
    private @Nullable Double averageGroundSpeed = null;
    private float lastAverageGroundSpeedUpdateTime = -1.0f;

    private @Nullable Waypoint targetWaypoint;
    public boolean autolandAllowed = false;
    public @Nullable Integer fallbackApproachAltitude = null;
    public @Nullable Integer landAltitude = null;

    @Override
    public void tick() {
        autolandAllowed = false;
        landAltitude = null;
        if (targetWaypoint != null && !this.contains(targetWaypoint)) {
            nextWaypoint();
        }

        if (targetWaypoint == null) {
            averageGroundSpeed = null;
            return;
        }

        groundSpeeds.add(data.velocity.horizontalLength());
        if (time.millis - lastAverageGroundSpeedUpdateTime > 5000.0f) {
            averageGroundSpeed = groundSpeeds.stream().mapToDouble(d -> d).average().orElseThrow(AssertionError::new);
            groundSpeeds.clear();
            lastAverageGroundSpeedUpdateTime = time.millis;
        }

        Vector2d target = new Vector2d(targetWaypoint.targetPosition());

        if (targetWaypoint instanceof LandingWaypoint) {
            autolandAllowed = tickLanding(target);
            return;
        }

        float altitude = targetWaypoint.targetAltitude() != null ? targetWaypoint.targetAltitude() : data.altitude();
        if (target.sub(data.position().x, data.position().z).length() <= 20.0f && Math.abs(altitude - data.altitude()) <= 10.0f) {
            nextWaypoint();
        }
    }

    private boolean tickLanding(Vector2d target) {
        double distance = Vector2d.distance(target.x, target.y, data.position().x, data.position().z);
        if (distance <= 10.0 && data.heightAboveGround() <= 3.0f) {
            nextWaypoint();
            return false;
        }

        Vec3d landPos = data.findGround(new Vec3d(target.x, data.world().getTopY(), target.y));
        if (landPos == null) {
            return false;
        }
        landAltitude = MathHelper.ceil(landPos.getY());

        float minimumHeight = Math.min(data.heightAboveGround(), Math.abs(data.altitude() - landAltitude));
        if (distance / minimumHeight >= AirDataComputer.OPTIMUM_GLIDE_RATIO) {
            return false;
        }

        float landAngle = FAMathHelper.toDegrees(MathHelper.atan2(landAltitude - data.altitude(), distance));
        if (landAngle < PitchController.DESCEND_PITCH + 10 || landAngle > PitchController.GLIDE_PITCH) {
            return false;
        }
        BlockHitResult result = data.world().raycast(new RaycastContext(data.position(), new Vec3d(target.x, landAltitude, target.y), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.ANY, data.player()));

        return result.getType() == HitResult.Type.MISS || Math.abs(result.getPos().y - landAltitude) <= 5.0;
    }

    private void nextWaypoint() {
        int nextIndex = this.indexOf(targetWaypoint) + 1;
        if (waypointExistsAt(nextIndex)) {
            targetWaypoint = this.get(nextIndex);
        } else {
            targetWaypoint = null;
        }
    }

    public @Nullable Integer getManagedSpeed() {
        if (targetWaypoint == null) {
            return null;
        }
        return targetWaypoint.targetSpeed();
    }

    public @Nullable Integer getManagedAltitude() {
        if (targetWaypoint == null) {
            return null;
        }
        if (isOnApproach()) {
            if (landAltitude != null && autolandAllowed) {
                return landAltitude;
            }

            Waypoint previous = getPreviousWaypoint();
            if (previous == null || previous instanceof LandingWaypoint) {
                if (fallbackApproachAltitude == null) {
                    fallbackApproachAltitude = MathHelper.floor(data.altitude());
                }

                return fallbackApproachAltitude;
            }

            return previous.targetAltitude();
        }

        fallbackApproachAltitude = null;
        return targetWaypoint.targetAltitude();
    }

    public @Nullable Float getManagedHeading() {
        if (targetWaypoint == null) {
            return null;
        }

        Vec3d current = data.position();
        Vector2d target = targetWaypoint.targetPosition();

        return AirDataComputer.toHeading(FAMathHelper.toDegrees(MathHelper.atan2(-(target.x - current.x), target.y - current.z)));
    }

    public @Nullable Vector2d getTargetPosition() {
        if (targetWaypoint == null) {
            return null;
        }

        return targetWaypoint.targetPosition();
    }

    public @Nullable Double getDistanceToWaypoint() {
        Vector2d planPos = getTargetPosition();
        if (planPos == null) {
            return null;
        }

        return Vector2d.distance(planPos.x, planPos.y, data.position().x, data.position().z);
    }

    public @Nullable Duration getTimeToWaypoint() {
        Double distance = getDistanceToWaypoint();
        if (distance == null) {
            return null;
        }
        if (averageGroundSpeed == null) {
            return null;
        }

        return Duration.ofSeconds(Math.round(distance / averageGroundSpeed));
    }

    public @Nullable Waypoint getPreviousWaypoint() {
        if (targetWaypoint == null) {
            throw new AssertionError();
        }
        int index = this.indexOf(targetWaypoint) - 1;
        if (!waypointExistsAt(index)) {
            return null;
        }
        return this.get(index);
    }

    public @Nullable Integer getMinimums(double ground) {
        if (targetWaypoint instanceof LandingWaypoint land) {
            return land.minimums(ground);
        }

        return null;
    }

    public boolean isBelowMinimums() {
        Integer minimums = getMinimums(data.groundLevel);
        return data.isFlying() && minimums != null && data.altitude() <= minimums;
    }

    public Integer getCruiseAltitude() {
        Integer cruiseAltitude = null;
        for (Waypoint waypoint : this) {
            if (waypoint.targetAltitude() != null) {
                if (cruiseAltitude == null) {
                    cruiseAltitude = waypoint.targetAltitude();
                }
                cruiseAltitude = Math.max(cruiseAltitude, waypoint.targetAltitude());
            }
        }

        return cruiseAltitude;
    }

    public void execute(int waypointIndex) {
        targetWaypoint = this.get(waypointIndex);
    }

    public boolean isOnApproach() {
        return targetWaypoint instanceof LandingWaypoint;
    }

    public boolean waypointExistsAt(int index) {
        return index >= 0 && index < this.size();
    }

    @Override
    public Waypoint set(int index, Waypoint element) {
        if (index == this.indexOf(targetWaypoint)) {
            targetWaypoint = element;
        }
        return super.set(index, element);
    }

    @Override
    public void reset() {
        targetWaypoint = null;
        autolandAllowed = false;
        groundSpeeds.clear();
        lastAverageGroundSpeedUpdateTime = -1.0f;
        averageGroundSpeed = null;
        fallbackApproachAltitude = null;
        landAltitude = null;
    }

    @Override
    public String getId() {
        return "flt_plan";
    }
}
