package ru.octol1ttle.flightassistant.impl.alert.flight_controls

import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import ru.octol1ttle.flightassistant.api.alert.Alert
import ru.octol1ttle.flightassistant.api.alert.AlertData
import ru.octol1ttle.flightassistant.api.alert.ECAMAlert
import ru.octol1ttle.flightassistant.api.computer.ComputerView
import ru.octol1ttle.flightassistant.api.util.extensions.advisoryColor
import ru.octol1ttle.flightassistant.api.util.extensions.cautionColor
import ru.octol1ttle.flightassistant.api.util.extensions.drawText

class ProtectionsLostAlert(computers: ComputerView) : Alert(computers), ECAMAlert {
    override val priorityOffset: Int = 10
    override val data: AlertData = AlertData.MASTER_CAUTION

    override fun shouldActivate(): Boolean {
        return computers.protections.protectionsLost
    }

    override fun render(drawContext: DrawContext, firstLineX: Int, otherLinesX: Int, firstLineY: Int): Int {
        var i = 0
        i += drawContext.drawText(Text.translatable("alerts.flightassistant.flight_controls.protections_lost"), firstLineX, firstLineY, cautionColor)

        var y: Int = firstLineY + 1
        if (!computers.data.enabled && computers.data.faultCount <= 1) {
            y += 10
            i += drawContext.drawText(Text.translatable("alerts.flightassistant.flight_controls.protections_lost.enable_air_data"), otherLinesX, y, advisoryColor)
        }
        if (!computers.pitch.enabled && computers.pitch.faultCount <= 1) {
            y += 10
            i += drawContext.drawText(Text.translatable("alerts.flightassistant.flight_controls.protections_lost.enable_pitch"), otherLinesX, y, advisoryColor)
        }
        y += 10
        i += drawContext.drawText(Text.translatable("alerts.flightassistant.flight_controls.protections_lost.max_pitch"), otherLinesX, y, advisoryColor)
        y += 10
        i += drawContext.drawText(Text.translatable("alerts.flightassistant.flight_controls.protections_lost.max_descent_rate"), otherLinesX, y, advisoryColor)

        return i
    }
}
