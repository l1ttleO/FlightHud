package ru.octol1ttle.flightassistant

//? if fabric {
import dev.architectury.registry.client.keymappings.KeyMappingRegistry
import net.fabricmc.api.ClientModInitializer
import net.minecraft.client.option.KeyBinding
import nl.enjarai.doabarrelroll.compat.flightassistant.DaBRCompatFA

object FlightAssistantFabric : ClientModInitializer {
    override fun onInitializeClient() {
        FlightAssistant.init()
        DaBRCompatFA.init()
        for (keyBinding: KeyBinding in FAKeyBindings.keyBindings) {
            KeyMappingRegistry.register(keyBinding)
        }
    }
}
//?}
