package ru.octol1ttle.flightassistant.util.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import org.joml.Matrix4f;

public interface RollMatrixCallback {
    Event<RollMatrixCallback> EVENT = EventFactory.createArrayBacked(
            RollMatrixCallback.class,
            (listeners) -> (matrix4f) -> {
                for (RollMatrixCallback event : listeners) {
                    event.onMatrixUpdate(matrix4f);
                }
            }
    );

    /**
     * Called after the roll matrix has been updated
     *
     * @param matrix4f the roll matrix
     */
    void onMatrixUpdate(Matrix4f matrix4f);
}
