package ru.octol1ttle.flightassistant.util.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;

@FunctionalInterface
public interface FireworkBoostCallback {
    Event<FireworkBoostCallback> EVENT = EventFactory.createArrayBacked(
            FireworkBoostCallback.class,
            (listeners) -> (rocket, shooter) -> {
                for (FireworkBoostCallback event : listeners) {
                    event.onFireworkBoost(rocket, shooter);
                }
            }
    );

    void onFireworkBoost(FireworkRocketEntity rocket, LivingEntity shooter);
}
