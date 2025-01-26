package ru.octol1ttle.flightassistant.impl.screen

import kotlin.properties.Delegates
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text

abstract class FABaseScreen(title: Text?) : Screen(title) {
    protected var centerX by Delegates.notNull<Int>()
    protected var centerY by Delegates.notNull<Int>()

    override fun init() {
        this.centerX = this.width / 2
        this.centerY = this.height / 2
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        this.renderBackground(context
/*? if >=1.21 {*//*, mouseX, mouseY, delta *///?}
        )
        super.render(context, mouseX, mouseY, delta)
    }

    override fun shouldPause(): Boolean = false
}
