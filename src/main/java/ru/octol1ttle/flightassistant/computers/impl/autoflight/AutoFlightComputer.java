package ru.octol1ttle.flightassistant.computers.impl.autoflight;

import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2d;
import ru.octol1ttle.flightassistant.FAMathHelper;
import ru.octol1ttle.flightassistant.computers.impl.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.api.ITickableComputer;
import ru.octol1ttle.flightassistant.computers.impl.navigation.FlightPlanner;
import ru.octol1ttle.flightassistant.computers.impl.safety.GPWSComputer;
import ru.octol1ttle.flightassistant.config.FAConfig;
import ru.octol1ttle.flightassistant.registries.ComputerRegistry;


public class AutoFlightComputer implements ITickableComputer {
    private final AirDataComputer data = ComputerRegistry.resolve(AirDataComputer.class);
    private final GPWSComputer gpws = ComputerRegistry.resolve(GPWSComputer.class);
    private final FlightPlanner plan = ComputerRegistry.resolve(FlightPlanner.class);
    private final FireworkController firework = ComputerRegistry.resolve(FireworkController.class);
    private final PitchController pitch = ComputerRegistry.resolve(PitchController.class);
    private final YawController yaw = ComputerRegistry.resolve(YawController.class);

    public boolean flightDirectorsEnabled = false;
    public boolean autoFireworkEnabled = false;
    public boolean autoPilotEnabled = false;

    public boolean afrwkDisconnectionForced = false;
    public boolean apDisconnectionForced = false;

    public Integer selectedSpeed;
    public Integer selectedAltitude;
    public Integer selectedHeading;

    @Override
    public void tick() {
        if (autoFireworkEnabled && data.isCurrentChunkLoaded && gpws.fireworkUseSafe && gpws.getGPWSLampColor() == FAConfig.indicator().frameColor) {
            Integer targetSpeed = getTargetSpeed();
            Integer targetAltitude = getTargetAltitude();
            if (targetSpeed != null) {
                if (data.speed() < targetSpeed) {
                    firework.activateFirework(false);
                }
            } else if (targetAltitude != null && targetAltitude > data.altitude()
                    && data.speed() < 30
                    && data.velocity.y < 0.0f
                    && data.pitch() > 0) {
                firework.activateFirework(false);
            }
        }

        pitch.targetPitch = autoPilotEnabled ? getTargetPitch() : null;
        yaw.targetHeading = autoPilotEnabled ? getTargetHeading() : null;
    }

    public @Nullable Integer getTargetSpeed() {
        return selectedSpeed != null ? selectedSpeed : plan.getManagedSpeed();
    }

    public @Nullable Integer getTargetAltitude() {
        return selectedAltitude != null ? selectedAltitude : plan.getManagedAltitude();
    }

    public @Nullable Float getTargetPitch() {
        if (getTargetAltitude() == null) {
            return null;
        }

        double altitudeDelta = getTargetAltitude() - data.altitude();

        double distance;
        Vector2d planPos = plan.getTargetPosition();

        boolean isTargetingApproachAltitude = plan.isOnApproach() && !plan.autolandAllowed;
        if (planPos != null && selectedAltitude == null && !isTargetingApproachAltitude) {
            distance = Vector2d.distance(planPos.x, planPos.y, data.position().x, data.position().z);
        } else {
            distance = Math.max(15.0f, data.speed() * 2.0f);
        }

        return FAMathHelper.toDegrees(MathHelper.atan2(altitudeDelta, distance));
    }

    public @Nullable Float getTargetHeading() {
        return selectedHeading != null ? Float.valueOf(selectedHeading) : plan.getManagedHeading();
    }

    public void disconnectAutopilot(boolean force) {
        if (autoPilotEnabled) {
            autoPilotEnabled = false;
            apDisconnectionForced = force;
        }
    }

    public void disconnectAutoFirework(boolean force) {
        if (autoFireworkEnabled) {
            autoFireworkEnabled = false;
            afrwkDisconnectionForced = force;
        }
    }

    @Override
    public String getId() {
        return "auto_flt";
    }

    @Override
    public void reset() {
        flightDirectorsEnabled = false;
        disconnectAutoFirework(true);
        disconnectAutopilot(true);

        pitch.targetPitch = null;
        yaw.targetHeading = null;
    }
}
