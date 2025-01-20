package ru.octol1ttle.flightassistant.api.util

import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.impl.computer.*
import ru.octol1ttle.flightassistant.impl.computer.autoflight.*
import ru.octol1ttle.flightassistant.impl.computer.safety.*

val ComputerAccess.data: AirDataComputer
    get() = get(AirDataComputer.ID) as AirDataComputer

val ComputerAccess.firework: FireworkComputer
    get() = get(FireworkComputer.ID) as FireworkComputer

val ComputerAccess.pitch: PitchComputer
    get() = get(PitchComputer.ID) as PitchComputer

val ComputerAccess.thrust: ThrustComputer
    get() = get(ThrustComputer.ID) as ThrustComputer

val ComputerAccess.alert: AlertComputer
    get() = get(AlertComputer.ID) as AlertComputer

val ComputerAccess.chunk: ChunkStatusComputer
    get() = get(ChunkStatusComputer.ID) as ChunkStatusComputer

val ComputerAccess.elytra: ElytraStatusComputer
    get() = get(ElytraStatusComputer.ID) as ElytraStatusComputer

val ComputerAccess.protections: FlightProtectionsComputer
    get() = get(FlightProtectionsComputer.ID) as FlightProtectionsComputer

val ComputerAccess.gpws: GroundProximityComputer
    get() = get(GroundProximityComputer.ID) as GroundProximityComputer

val ComputerAccess.stall: StallComputer
    get() = get(StallComputer.ID) as StallComputer

val ComputerAccess.voidProximity: VoidProximityComputer
    get() = get(VoidProximityComputer.ID) as VoidProximityComputer
