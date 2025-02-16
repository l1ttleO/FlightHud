package ru.octol1ttle.flightassistant.api.computer

import ru.octol1ttle.flightassistant.api.SystemView
import ru.octol1ttle.flightassistant.impl.computer.AirDataComputer
import ru.octol1ttle.flightassistant.impl.computer.autoflight.*
import ru.octol1ttle.flightassistant.impl.computer.safety.*

interface ComputerView : SystemView<Computer> {
    val data: AirDataComputer
        get() = get(AirDataComputer.ID) as AirDataComputer

    val autoflight: AutoFlightComputer
        get() = get(AutoFlightComputer.ID) as AutoFlightComputer

    val firework: FireworkComputer
        get() = get(FireworkComputer.ID) as FireworkComputer

    val heading: HeadingComputer
        get() = get(HeadingComputer.ID) as HeadingComputer

    val pitch: PitchComputer
        get() = get(PitchComputer.ID) as PitchComputer

    val roll: RollComputer
        get() = get(RollComputer.ID) as RollComputer

    val thrust: ThrustComputer
        get() = get(ThrustComputer.ID) as ThrustComputer

    val alert: AlertComputer
        get() = get(AlertComputer.ID) as AlertComputer

    val chunk: ChunkStatusComputer
        get() = get(ChunkStatusComputer.ID) as ChunkStatusComputer

    val elytra: ElytraStatusComputer
        get() = get(ElytraStatusComputer.ID) as ElytraStatusComputer

    val protections: FlightProtectionsComputer
        get() = get(FlightProtectionsComputer.ID) as FlightProtectionsComputer

    val gpws: GroundProximityComputer
        get() = get(GroundProximityComputer.ID) as GroundProximityComputer

    val stall: StallComputer
        get() = get(StallComputer.ID) as StallComputer

    val voidProximity: VoidProximityComputer
        get() = get(VoidProximityComputer.ID) as VoidProximityComputer
}
