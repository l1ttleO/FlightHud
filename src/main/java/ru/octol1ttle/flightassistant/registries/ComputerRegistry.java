package ru.octol1ttle.flightassistant.registries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import ru.octol1ttle.flightassistant.FlightAssistant;
import ru.octol1ttle.flightassistant.computers.api.IComputer;
import ru.octol1ttle.flightassistant.registries.events.ComputerRegisteredCallback;

@SuppressWarnings("unchecked")
public abstract class ComputerRegistry {
    private static final HashMap<Class<IComputer>, IComputer> instances = new HashMap<>();
    private static final List<Class<IComputer>> faulted = new ArrayList<>();

    public static void register(IComputer computer) {
        Class<IComputer> clazz = (Class<IComputer>) computer.getClass();
        if (instances.containsKey(clazz)) {
            throw new IllegalStateException("Computer already registered with class: %s".formatted(clazz));
        }

        instances.put(clazz, computer);
        ComputerRegisteredCallback.EVENT.invoker().onComputerRegistered(computer);
    }

    public static <T extends IComputer> T resolve(Class<T> clazz) {
        return (T) Objects.requireNonNull(instances.get(clazz), "Unable to resolve computer with requested class: %s".formatted(clazz));
    }

    public static void markFaulted(IComputer computer, Throwable cause, @Nullable String message) {
        Class<IComputer> clazz = (Class<IComputer>) computer.getClass();
        if (faulted.contains(clazz)) {
            throw new IllegalStateException("Computer already marked as faulted");
        }

        faulted.add(clazz);
        FlightAssistant.LOGGER.error(Objects.requireNonNullElse(message, "Computer encountered a fault"), cause);
    }

    public static <T extends IComputer> boolean isFaulted(Class<T> computer) {
        return faulted.contains(computer);
    }

    public static boolean anyFaulted() {
        return !faulted.isEmpty();
    }

    public static void resetFaulted() {
        faulted.clear();
    }

    @ApiStatus.Internal
    public static Collection<IComputer> getComputers() {
        return instances.values();
    }
}
