package ru.octol1ttle.flightassistant.util.events;

import java.util.Optional;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;

public final class ChangeLookDirectionEvents {
    private ChangeLookDirectionEvents() {
    }

    public static final Event<Pitch> PITCH = EventFactory.createArrayBacked(
            Pitch.class,
            (listeners) -> (entity, pitchDelta) -> {
                for (Pitch event : listeners) {
                    Optional<Float> result = event.onPitchChange(entity, pitchDelta);
                    if (result.isPresent()) {
                        return result;
                    }
                }
                return Optional.empty();
            }
    );

    @FunctionalInterface
    public interface Pitch {
        Optional<Float> onPitchChange(Entity entity, float pitchDelta);
    }
}
