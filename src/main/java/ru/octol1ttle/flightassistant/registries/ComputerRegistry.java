package ru.octol1ttle.flightassistant.registries;

import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;
import org.jetbrains.annotations.ApiStatus;
import ru.octol1ttle.flightassistant.computers.IComputer;

@SuppressWarnings("unchecked")
public abstract class ComputerRegistry {
    private static final HashMap<Class<IComputer>, IComputer> instances = new HashMap<>();

    public static void register(IComputer computer) {
        Class<IComputer> clazz = (Class<IComputer>) computer.getClass();
        if (instances.containsKey(clazz)) {
            throw new IllegalStateException("Computer already registered with class: %s".formatted(clazz));
        }

        instances.put(clazz, computer);
    }

    public static <T extends IComputer> T resolve(Class<T> clazz) {
        return (T) Objects.requireNonNull(instances.get(clazz), "Unable to resolve computer with requested class: %s".formatted(clazz));
    }

    @ApiStatus.Internal
    public static Collection<IComputer> getComputers() {
        return instances.values();
    }
}
