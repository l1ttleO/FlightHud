package ru.octol1ttle.flightassistant.impl.display

import net.minecraft.client.gui.DrawContext
import net.minecraft.text.*
import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.alert.*
import ru.octol1ttle.flightassistant.api.computer.ComputerAccess
import ru.octol1ttle.flightassistant.api.display.*
import ru.octol1ttle.flightassistant.api.util.*
import ru.octol1ttle.flightassistant.config.FAConfig

class AlertDisplay : Display() {
    private val withUnderline: Style = Style.EMPTY.withUnderline(true)

    override fun enabled(): Boolean {
        return FAConfig.display.showAlerts
    }

    override fun render(drawContext: DrawContext, computers: ComputerAccess) {
        with(drawContext) {
            val x: Int = HudFrame.left + 10
            var y: Int = HudFrame.top + 5

            var renderedCentered = false
            for (category: AlertCategory in computers.alert.categories) {
                val copy: MutableText = category.categoryText.copy()
                drawText(
                    copy.setStyle(withUnderline),
                    x,
                    y,
                    (category.getHighestPriority() ?: continue).colorSupplier.invoke()
                )
                copy.append(" ")

                for (alert: Alert in category.activeAlerts) {
                    if (!renderedCentered && alert is CenteredAlert) {
                        alert.render(this, computers, centerYI + 8)
                        renderedCentered = true
                        y += fontHeight
                    }

                    if (alert is ECAMAlert) {
                        y += fontHeight * alert.render(this, computers, x + getTextWidth(copy), x, y)
                    }
                }
                y += 3
            }
        }
    }

    override fun renderFaulted(drawContext: DrawContext) {
        with(drawContext) {
            val x: Int = HudFrame.left + 10
            val y: Int = HudFrame.top + 5

            drawText(Text.translatable("short.flightassistant.alert"), x, y, warningColor)
        }
    }

    companion object {
        val ID: Identifier = FlightAssistant.id("alert")
    }
}
