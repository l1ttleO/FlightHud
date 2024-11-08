package ru.octol1ttle.flightassistant.api.util

import java.awt.Color
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import ru.octol1ttle.flightassistant.FlightAssistant.mc
import ru.octol1ttle.flightassistant.config.FAConfig

internal val textRenderer: TextRenderer = mc.textRenderer

val fontHeight: Int
    get() = textRenderer.fontHeight

val DrawContext.halfWidth: Float
    get() = scaledWindowWidth * 0.5f

val DrawContext.centerX: Float
    get() = halfWidth - 1

val DrawContext.centerXI: Int
    get() = centerX.toInt()

val DrawContext.centerY: Float
    get() = scaledWindowHeight * 0.5f

val DrawContext.centerYI: Int
    get() = centerY.toInt()

val primaryColor: Int
    get() = FAConfig.display.primaryColor.rgb

val advisoryColor: Int
    get() = FAConfig.display.advisoryColor.rgb

val cautionColor: Int
    get() = FAConfig.display.cautionColor.rgb

val warningColor: Int
    get() = FAConfig.display.warningColor.rgb

fun DrawContext.scaleMatrix(scale: Float, trueX: Int, trueY: Int): Pair<Int, Int> {
    matrices.scale(scale, scale, 1.0f)

    val x: Int = (trueX / scale).toInt()
    val y: Int = (trueY / scale).toInt()
    return Pair(x, y)
}

fun DrawContext.scaleMatrix(scale: Float, trueX: Float, trueY: Float): Pair<Int, Int> {
    matrices.scale(scale, scale, 1.0f)

    val x: Int = (trueX / scale).toInt()
    val y: Int = (trueY / scale).toInt()
    return Pair(x, y)
}

fun DrawContext.drawHorizontalLineDashed(
    x1: Int, x2: Int, y: Int,
    dashCount: Int, color: Int
) {
    val width: Int = x2 - x1
    val segmentCount: Int = dashCount * 2 - 1
    val dashSize: Int = width / segmentCount
    for (i in 0 until segmentCount) {
        if (i % 2 != 0) {
            continue
        }
        val dx1: Int = i * dashSize + x1
        val dx2: Int = if (i == segmentCount - 1) x2 else ((i + 1) * dashSize) + x1
        drawHorizontalLine(dx1, dx2, y, color)
    }
}

fun getTextWidth(text: String): Int {
    return textRenderer.getWidth(text)
}

fun DrawContext.drawText(text: String, x: Int, y: Int, color: Int) {
    drawText(textRenderer, text, x, y, color, false)
}

fun DrawContext.drawRightAlignedText(text: String, x: Int, y: Int, color: Int) {
    drawText(textRenderer, text, x - textRenderer.getWidth(text), y, color, false)
}

fun DrawContext.drawMiddleAlignedText(text: String, x: Int, y: Int, color: Int) {
    drawText(textRenderer, text, x - textRenderer.getWidth(text) / 2 + 1, y, color, false)
}

fun getTextWidth(text: Text): Int {
    return textRenderer.getWidth(text)
}

fun DrawContext.drawText(text: Text, x: Int, y: Int, color: Int) {
    drawText(textRenderer, text, x, y, color, false)
}

private fun getContrasting(original: Int): Int {
    val red: Int = original shr 16 and 255
    val green: Int = original shr 8 and 255
    val blue: Int = original shr 0 and 255
    val luma: Double = (0.299 * red + 0.587 * green + 0.114 * blue) / 255.0
    return if (luma > 0.5) Color.BLACK.rgb else Color.WHITE.rgb
}

fun DrawContext.drawRightAlignedText(text: Text, x: Int, y: Int, color: Int) {
    drawText(textRenderer, text, x - getTextWidth(text), y, color, false)
}

fun DrawContext.drawMiddleAlignedText(text: Text, x: Int, y: Int, color: Int) {
    drawText(textRenderer, text, x - getTextWidth(text) / 2 + 1, y, color, false)
}

fun DrawContext.drawHighlightedCenteredText(text: Text, x: Int, y: Int, color: Int, highlight: Boolean) {
    matrices.push()

    if (highlight) {
        val halfWidth: Int = getTextWidth(text) / 2
        fill(x - halfWidth - 1, y - 1, x + halfWidth + 2, y + 8, color)
        matrices.translate(0, 0, 100)
        drawMiddleAlignedText(text, x, y, getContrasting(color))
    } else {
        drawMiddleAlignedText(text, x, y, color)
    }

    matrices.pop()
}

fun MatrixStack.translate(x: Int, y: Int, z: Int) {
    translate(x.toFloat(), y.toFloat(), z.toFloat())
}
