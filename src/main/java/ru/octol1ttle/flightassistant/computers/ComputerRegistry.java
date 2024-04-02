package ru.octol1ttle.flightassistant.computers;

import java.util.HashMap;
import net.minecraft.util.Identifier;

public abstract class ComputerRegistry {
    private static final HashMap<Identifier, IComputer> COMPUTERS = new HashMap<>();

    public static void register(Identifier id, IComputer computer) {
        if (COMPUTERS.containsKey(id)) {
            throw new IllegalStateException("Computer already registered with ID: %s".formatted(id));
        }

        COMPUTERS.put(id, computer);
    }
}
