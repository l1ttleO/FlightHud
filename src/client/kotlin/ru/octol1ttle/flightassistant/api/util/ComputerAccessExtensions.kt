package ru.octol1ttle.flightassistant.api.util

import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.computer.autoflight.thrust.ThrustController
import ru.octol1ttle.flightassistant.impl.computer.*
import ru.octol1ttle.flightassistant.impl.computer.autoflight.*
import ru.octol1ttle.flightassistant.impl.computer.safety.*

val ComputerAccess.data: AirDataComputer
    get() = get(AirDataComputer.ID) as AirDataComputer

val ComputerAccess.elytra: ElytraStatusComputer
    get() = get(ElytraStatusComputer.ID) as ElytraStatusComputer

val ComputerAccess.alert: AlertComputer
    get() = get(AlertComputer.ID) as AlertComputer

val ComputerAccess.voidProximity: VoidProximityComputer
    get() = get(VoidProximityComputer.ID) as VoidProximityComputer

val ComputerAccess.thrust: ThrustController
    get() = get(ThrustComputer.ID) as ThrustController

val ComputerAccess.firework: FireworkComputer
    get() = get(FireworkComputer.ID) as FireworkComputer
