package ru.octol1ttle.flightassistant.screen.status

import com.google.common.collect.ImmutableList
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.Selectable
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.ElementListWidget
import net.minecraft.client.gui.widget.TextWidget
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.FlightAssistant.mc
import ru.octol1ttle.flightassistant.api.SystemHost
import ru.octol1ttle.flightassistant.api.util.cautionColor
import ru.octol1ttle.flightassistant.api.util.textRenderer

class SystemStatusListWidget(
    width: Int, height: Int, top: Int, bottom: Int, left: Int,
    systems: Collection<Identifier>, baseKey: String, systemHost: SystemHost
)
    : ElementListWidget<SystemStatusListWidget.SystemStatusWidgetEntry>(mc, width, height, top, bottom, 10) {
    init {
        var y = 20
        for (system: Identifier in systems) {
            this.addEntry(SystemStatusWidgetEntry(
                left, y, width, system, Text.translatable("$baseKey.$system"), systemHost)
            )
            y += 25
        }
    }

    override fun getScrollbarPositionX(): Int {
        return Int.MAX_VALUE
    }

    class SystemStatusWidgetEntry(val x: Int, val y: Int, private val listWidth: Int,
                                  val identifier: Identifier, displayNameText: Text,
                                  private val systemHost: SystemHost
    )
        : Entry<SystemStatusWidgetEntry>() {
        private val displayName: TextWidget = TextWidget(x, y, this.listWidth / 4, 9, displayNameText, textRenderer).alignLeft()
        private val faultText: TextWidget = TextWidget(x, y, this.listWidth / 8, 9, FAULT_TEXT, textRenderer)
        private val offText: TextWidget = TextWidget(x, y, this.listWidth / 8, 9, OFF_TEXT, textRenderer)
        private val toggleButton: ButtonWidget = ButtonWidget.builder(OFF_RESET_TEXT) {
            it.message =
                if (systemHost.toggleEnabled(identifier)) OFF_RESET_TEXT
                else ON_TEXT
        }.position(x, y).width(60).build()

        override fun render(context: DrawContext?, index: Int, y: Int, x: Int, entryWidth: Int, entryHeight: Int, mouseX: Int, mouseY: Int, hovered: Boolean, tickDelta: Float) {
            displayName.x = this.x + 10

            displayName.render(context, mouseX, mouseY, tickDelta)

            toggleButton.message =
                if (systemHost.isEnabled(identifier)) OFF_RESET_TEXT
                else ON_TEXT
            toggleButton.x = this.x + this.listWidth - toggleButton.width - 5
            toggleButton.y = this.y - toggleButton.height / 4 - 1
            toggleButton.render(context, mouseX, mouseY, tickDelta)

            offText.x = toggleButton.x - toggleButton.width / 2 - textRenderer.getWidth(OFF_TEXT) - 2
            offText.setTextColor(
                if (systemHost.isEnabled(identifier)) 0x0F0F0F
                else 0xFFFFFF
            )
            offText.render(context, mouseX, mouseY, tickDelta)

            faultText.x = offText.x - textRenderer.getWidth(FAULT_TEXT) - 2
            faultText.setTextColor(
                if (systemHost.isFaulted(identifier)) cautionColor
                else 0x0F0F0F
            )
            faultText.render(context, mouseX, mouseY, tickDelta)
        }

        override fun children(): MutableList<out Element> {
            return ImmutableList.of(displayName, faultText, offText, toggleButton)
        }

        override fun selectableChildren(): MutableList<out Selectable> {
            return ImmutableList.of(displayName, faultText, offText, toggleButton)
        }

        companion object {
            val FAULT_TEXT: Text = Text.translatable("menu.flightassistant.system.fault")
            val OFF_TEXT: Text = Text.translatable("menu.flightassistant.system.off")

            val OFF_RESET_TEXT: Text = Text.translatable("menu.flightassistant.system.off_reset")
            val ON_TEXT: Text = Text.translatable("menu.flightassistant.system.on")
        }
    }
}
