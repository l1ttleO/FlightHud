package ru.octol1ttle.flightassistant.registries;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import ru.octol1ttle.flightassistant.computers.IComputer;

public abstract class ComputerRegistry {
    private static final HashMap<Identifier, Supplier<IComputer>> suppliers = new HashMap<>();
    private static final HashMap<Class<IComputer>, IComputer> instanceMap = new HashMap<>();

    public static void register(Identifier id, Supplier<IComputer> factory) {
        if (suppliers.containsKey(id)) {
            throw new IllegalStateException("Computer supplier already registered with ID: %s".formatted(id));
        }

        suppliers.put(id, factory);
    }

    @SuppressWarnings("unchecked")
    public static <T extends IComputer> T resolve(Class<T> clazz) {
        return (T) instanceMap.computeIfAbsent((Class<IComputer>) clazz, cl -> {
            for (Supplier<IComputer> supplier : suppliers.values()) {
                IComputer computer = supplier.get();
                if (cl.equals(computer.getClass())) {
                    instanceMap.put(cl, computer);
                    return computer;
                }
            }

            throw new IllegalStateException("Unable to resolve computer with requested class: %s".formatted(clazz));
        });
    }

    @ApiStatus.Internal
    public static Set<Map.Entry<Class<IComputer>, IComputer>> getComputers() {
        return instanceMap.entrySet();
    }
}
