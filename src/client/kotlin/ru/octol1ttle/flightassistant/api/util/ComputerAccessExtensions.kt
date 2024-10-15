package ru.octol1ttle.flightassistant.api.util

import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.impl.computer.*

val ComputerAccess.data: AirDataComputer
    get() = get(AirDataComputer.ID) as AirDataComputer

val ComputerAccess.elytra: ElytraStatusComputer
    get() = get(ElytraStatusComputer.ID) as ElytraStatusComputer

val ComputerAccess.alert: AlertComputer
    get() = get(AlertComputer.ID) as AlertComputer