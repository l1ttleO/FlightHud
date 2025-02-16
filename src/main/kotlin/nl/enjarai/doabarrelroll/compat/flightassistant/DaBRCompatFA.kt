package nl.enjarai.doabarrelroll.compat.flightassistant

import dev.architectury.platform.Platform
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.autoflight.roll.RollSourceRegistrationCallback
import ru.octol1ttle.flightassistant.api.autoflight.thrust.ThrustSourceRegistrationCallback
import ru.octol1ttle.flightassistant.api.computer.ComputerRegistrationCallback

object DaBRCompatFA {
//? if do-a-barrel-roll {
    private lateinit var rollComputer: DaBRRollComputer
    private lateinit var thrustComputer: DaBRThrustComputer

    fun init() {
        if (!Platform.isModLoaded("do_a_barrel_roll")) {
            return
        }
        FlightAssistant.logger.info("Initializing support for Do a Barrel Roll")

        ComputerRegistrationCallback.EVENT.register(ComputerRegistrationCallback { computers, registerFunction ->
            rollComputer = DaBRRollComputer(computers)
            registerFunction.accept(DaBRRollComputer.ID, rollComputer)
            thrustComputer = DaBRThrustComputer(computers)
            registerFunction.accept(DaBRThrustComputer.ID, thrustComputer)
        })
        RollSourceRegistrationCallback.EVENT.register { it.accept(rollComputer) }
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
