package ru.octol1ttle.flightassistant.impl.screen.widgets

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.tooltip.Tooltip
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.text.Text
import net.minecraft.util.math.MathHelper

class ColoredButtonWidget(x: Int, y: Int, width: Int, height: Int, message: Text?, onPress: PressAction?, narrationSupplier: NarrationSupplier?)
    : ButtonWidget(x, y, width, height, message, onPress, narrationSupplier) {
    var color: Int = 16777215

//? if >=1.21 {
    /*private val textures: net.minecraft.client.gui.screen.ButtonTextures = net.minecraft.client.gui.screen.ButtonTextures(net.minecraft.util.Identifier.ofVanilla("widget/button"), net.minecraft.util.Identifier.ofVanilla("widget/button_disabled"), net.minecraft.util.Identifier.ofVanilla("widget/button_highlighted"))
    override fun renderWidget(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
*///?} else
    override fun renderButton(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        val minecraftClient = MinecraftClient.getInstance()
        context.setShaderColor(1.0f, 1.0f, 1.0f, this.alpha)
        RenderSystem.enableBlend()
        RenderSystem.enableDepthTest()
//? if >=1.21 {
        /*context.drawGuiTexture(textures[active, this.isSelected], this.x, this.y, this.getWidth(), this.getHeight())
*///?} else
        context.drawNineSlicedTexture(WIDGETS_TEXTURE, this.x, this.y, this.getWidth(), this.getHeight(), 20, 4, 200, 20, 0, this.getTextureY())
        context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        val i: Int = if (this.active) color else 10526880
        this.drawMessage(context, minecraftClient.textRenderer, i or (MathHelper.ceil(this.alpha * 255.0f) shl 24))
    }

    private fun getTextureY(): Int {
        var i = 1
        if (!this.active) {
            i = 0
        } else if (this.isSelected) {
            i = 2
        }

        return 46 + i * 20
    }

    class Builder(private val message: Text, private val onPress: PressAction) {
        private var tooltip: Tooltip? = null
        private var x = 0
        private var y = 0
        private var width = 150
        private var height = 20
        private var narrationSupplier: NarrationSupplier = DEFAULT_NARRATION_SUPPLIER

        fun position(x: Int, y: Int): Builder {
            this.x = x
            this.y = y
            return this
        }

        fun width(width: Int): Builder {
            this.width = width
            return this
        }

        fun size(width: Int, height: Int): Builder {
            this.width = width
            this.height = height
            return this
        }

        fun dimensions(x: Int, y: Int, width: Int, height: Int): Builder {
            return position(x, y).size(width, height)
        }

        fun tooltip(tooltip: Tooltip?): Builder {
            this.tooltip = tooltip
            return this
        }

        fun narrationSupplier(narrationSupplier: NarrationSupplier): Builder {
            this.narrationSupplier = narrationSupplier
            return this
        }

        fun build(): ColoredButtonWidget {
            val buttonWidget = ColoredButtonWidget(this.x, this.y, this.width, this.height, this.message, this.onPress, this.narrationSupplier)
            buttonWidget.tooltip = tooltip
            return buttonWidget
        }
    }

    companion object {
        fun builder(message: Text, onPress: PressAction): Builder {
            return Builder(message, onPress)
        }
    }
}
