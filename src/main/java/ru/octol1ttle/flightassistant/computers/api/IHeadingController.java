package ru.octol1ttle.flightassistant.computers.api;

import net.minecraft.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IHeadingController {
    /**
     * Gets the target heading that this controller wants
     * @return a pair of two floats: the first one is the target heading, second is the delta time multiplier. If 'null', the controller doesn't want any yaw at this moment
     */
    @Nullable
    Pair<@NotNull Float, @NotNull Float> getControlledHeading();

    ControllerPriority getPriority();
}
