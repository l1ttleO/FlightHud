package nl.enjarai.doabarrelroll.compat.flightassistant

import dev.architectury.platform.Platform
import net.minecraft.text.Text
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.alert.AlertCategory
import ru.octol1ttle.flightassistant.api.alert.AlertCategoryRegistrationCallback
import ru.octol1ttle.flightassistant.api.autoflight.thrust.ThrustSourceRegistrationCallback
import ru.octol1ttle.flightassistant.api.computer.ComputerRegistrationCallback
import ru.octol1ttle.flightassistant.impl.alert.fault.computer.ComputerFaultAlert

object DaBRCompatFA {
//? if do-a-barrel-roll {
    private lateinit var thrustComputer: DaBRThrustComputer

    fun init() {
        if (!Platform.isModLoaded("do_a_barrel_roll")) {
            return
        }
        FlightAssistant.logger.info("Initializing support for Do a Barrel Roll")

        ComputerRegistrationCallback.EVENT.register(ComputerRegistrationCallback { computers, registerFunction ->
            thrustComputer = DaBRThrustComputer(computers)
            registerFunction.accept(DaBRThrustComputer.ID, thrustComputer)
        })
        AlertCategoryRegistrationCallback.EVENT.register(AlertCategoryRegistrationCallback { computers, registerFunction ->
            registerFunction.accept(
                AlertCategory(Text.translatable("alerts.do_a_barrel_roll.thrust"))
                    .add(ComputerFaultAlert(computers, DaBRThrustComputer.ID, Text.translatable("alerts.do_a_barrel_roll.thrust.fault")))
            )
        })
        ThrustSourceRegistrationCallback.EVENT.register { it.accept(thrustComputer) }
    }
//?} else {
    /*fun init() {
        if (Platform.isModLoaded("do_a_barrel_roll")) {
            FlightAssistant.logger.warn("Do a Barrel Roll is present, but this version of FlightAssistant does not have support for it. Update FlightAssistant, or report this issue if there are no updates available")
        }
    }
*///?}
}
