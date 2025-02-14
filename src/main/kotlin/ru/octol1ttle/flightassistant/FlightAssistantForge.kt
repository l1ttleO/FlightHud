package ru.octol1ttle.flightassistant

//? if !fabric {

/*//? if neoforge {
/^import net.minecraft.client.MinecraftClient
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModLoadingContext
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent
import net.neoforged.neoforge.client.gui.VanillaGuiLayers
import ru.octol1ttle.flightassistant.api.util.event.FixedHudRenderCallback
import ru.octol1ttle.flightassistant.config.FAConfigScreen
import thedarkcolour.kotlinforforge.neoforge.KotlinModLoadingContext

typealias CSF = net.neoforged.neoforge.client.gui.IConfigScreenFactory
^///?} else if forge {
/^import net.minecraftforge.client.event.RegisterGuiOverlaysEvent
import net.minecraftforge.client.event.RegisterKeyMappingsEvent
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.common.Mod
import nl.enjarai.doabarrelroll.compat.flightassistant.DaBRCompatFA
import ru.octol1ttle.flightassistant.api.util.event.FixedHudRenderCallback
import ru.octol1ttle.flightassistant.config.FAConfigScreen
import thedarkcolour.kotlinforforge.KotlinModLoadingContext

typealias CSF = net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory
^///?}

@Mod(FlightAssistant.MOD_ID)
object FlightAssistantForge {
    init {
        FlightAssistant.init()
        DaBRCompatFA.init()
        ModLoadingContext.get().registerExtensionPoint(
            CSF::class.java,
        ) { CSF { _, parent -> FAConfigScreen.generate(parent) } }

        val modEventBus: IEventBus = KotlinModLoadingContext.get().getKEventBus()
        modEventBus.addListener(this::onRegisterKeyMappings)
        modEventBus.addListener(this::onRegisterGuiOverlay)
    }

    fun onRegisterKeyMappings(event: RegisterKeyMappingsEvent) {
        FAKeyBindings.keyBindings.forEach(event::register)
    }

//? if neoforge {
    /^fun onRegisterGuiOverlay(event: RegisterGuiLayersEvent) {
        event.registerBelow(VanillaGuiLayers.HOTBAR, FlightAssistant.id("neoforge_gui")) { context, tickCounter ->
            if (!MinecraftClient.getInstance().options.hudHidden) {
                FixedHudRenderCallback.EVENT.invoker().onRenderHud(context, tickCounter.getTickDelta(true))
            }
        }
    }
^///?} else {
    fun onRegisterGuiOverlay(event: RegisterGuiOverlaysEvent) {
        event.registerBelow(VanillaGuiOverlay.HOTBAR.id(), "flightassistant") { gui, context, tickDelta, _, _ ->
            if (!gui.minecraft.options.hudHidden) {
                gui.setupOverlayRenderState(true, false)
                FixedHudRenderCallback.EVENT.invoker().onRenderHud(context, tickDelta)
            }
        }
    }
//?}
}
*///?}
