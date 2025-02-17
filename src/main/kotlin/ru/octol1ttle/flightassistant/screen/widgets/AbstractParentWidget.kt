package ru.octol1ttle.flightassistant.screen.widgets

import kotlin.jvm.optionals.getOrNull
import net.minecraft.client.gui.*
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder

abstract class AbstractParentWidget : AbstractParentElement(), Drawable, Selectable {
    private var hoveredElement: Element? = null

    override fun render(context: DrawContext?, mouseX: Int, mouseY: Int, delta: Float) {
        for (child: Element in children()) {
            if (child is Drawable) {
                child.render(context, mouseX, mouseY, delta)
            }
        }
        this.hoveredElement = this.hoveredElement(mouseX.toDouble(), mouseY.toDouble()).getOrNull()
    }

    override fun appendNarrations(builder: NarrationMessageBuilder?) {
    }

    override fun getType(): Selectable.SelectionType {
        return if (this.isFocused) {
            Selectable.SelectionType.FOCUSED
        } else {
            if (this.hoveredElement != null) Selectable.SelectionType.HOVERED else Selectable.SelectionType.NONE
        }
    }
}
