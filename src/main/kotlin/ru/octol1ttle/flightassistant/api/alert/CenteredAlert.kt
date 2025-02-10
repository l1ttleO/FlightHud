package ru.octol1ttle.flightassistant.api.alert

import net.minecraft.client.gui.DrawContext

interface CenteredAlert {
    fun render(drawContext: DrawContext, y: Int): Boolean
}
