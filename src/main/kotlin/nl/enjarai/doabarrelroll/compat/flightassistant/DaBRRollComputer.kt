package nl.enjarai.doabarrelroll.compat.flightassistant

//? if do-a-barrel-roll {
import net.minecraft.util.Identifier
import nl.enjarai.doabarrelroll.DoABarrelRoll
import nl.enjarai.doabarrelroll.api.RollEntity
import ru.octol1ttle.flightassistant.api.autoflight.roll.RollSource
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerView
import ru.octol1ttle.flightassistant.api.util.FATickCounter

class DaBRRollComputer(computers: ComputerView) : Computer(computers), RollSource {
    override fun isActive(): Boolean {
        return (computers.data.player as RollEntity).`doABarrelRoll$isRolling`()
    }

    override fun getRoll(): Float {
        return (computers.data.player as RollEntity).`doABarrelRoll$getRoll`(FATickCounter.tickProgress)
    }

    override fun addRoll(diff: Float) {
        return (computers.data.player as RollEntity).`doABarrelRoll$setRoll`(getRoll() + diff)
    }

    override fun tick() {
    }

    override fun reset() {
    }

    companion object {
        val ID: Identifier = DoABarrelRoll.id("roll")
    }
}

//?}
