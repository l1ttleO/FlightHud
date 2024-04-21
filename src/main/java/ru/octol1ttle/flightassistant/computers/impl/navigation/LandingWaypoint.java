package ru.octol1ttle.flightassistant.computers.impl.navigation;

import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2d;

public class LandingWaypoint extends Waypoint {
    public final @Nullable LandingMinimums minimums;

    public LandingWaypoint(Vector2d targetPosition, @Nullable LandingMinimums minimums) {
        super(targetPosition, null, null);
        this.minimums = minimums;
    }

    public Integer minimums(double ground) {
        if (minimums == null) {
            return null;
        }

        return switch (minimums.type()) {
            case ABSOLUTE -> minimums.altitude();
            case ABOVE_GROUND -> MathHelper.ceil(ground + minimums.altitude());
        };
    }

    public Text formatMinimums() {
        if (minimums == null) {
            return Text.translatable("mode.flightassistant.minimums.not_set");
        }

        return switch (minimums.type()) {
            case ABSOLUTE -> Text.translatable("mode.flightassistant.minimums.absolute", minimums.altitude());
            case ABOVE_GROUND -> Text.translatable("mode.flightassistant.minimums.relative", minimums.altitude());
        };
    }
}
