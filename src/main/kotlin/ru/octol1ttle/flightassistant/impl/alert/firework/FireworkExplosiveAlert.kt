package ru.octol1ttle.flightassistant.impl.alert.firework

import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import net.minecraft.util.Hand
import ru.octol1ttle.flightassistant.api.alert.Alert
import ru.octol1ttle.flightassistant.api.alert.AlertData
import ru.octol1ttle.flightassistant.api.alert.ECAMAlert
import ru.octol1ttle.flightassistant.api.computer.ComputerView
import ru.octol1ttle.flightassistant.api.util.extensions.cautionColor
import ru.octol1ttle.flightassistant.api.util.extensions.drawText

class FireworkExplosiveAlert(computers: ComputerView, private val hand: Hand)  : Alert(computers), ECAMAlert {
    override val priorityOffset: Int = 5
    override val data: AlertData = AlertData.MASTER_CAUTION

    override fun shouldActivate(): Boolean {
        return !computers.firework.isEmptyOrSafe(computers.data.player, hand)
    }

    override fun render(drawContext: DrawContext, firstLineX: Int, otherLinesX: Int, firstLineY: Int): Int {
        return drawContext.drawText(Text.translatable("alerts.flightassistant.firework.explosive.${hand.toString().lowercase()}"), firstLineX, firstLineY, cautionColor)
    }
}
