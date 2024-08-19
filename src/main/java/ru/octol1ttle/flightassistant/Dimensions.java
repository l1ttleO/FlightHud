package ru.octol1ttle.flightassistant;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;
import ru.octol1ttle.flightassistant.config.FAConfig;

public class Dimensions {

    public int hScreen;
    public int wScreen;
    @Deprecated(since = "2.3.2")
    public float degreesPerPixel;
    public int xMid;
    public int yMid;

    public int wFrame;
    public int hFrame;
    public int lFrame;
    public int rFrame;
    public int tFrame;
    public int bFrame;

    public void update(DrawContext context, double fov) {
        hScreen = MathHelper.floor(context.getScaledWindowHeight() / FAConfig.hud().hudScale);
        wScreen = MathHelper.floor(context.getScaledWindowWidth() / FAConfig.hud().hudScale);

        degreesPerPixel = (float) (hScreen / fov);
        xMid = wScreen / 2;
        yMid = hScreen / 2;

        wFrame = (int) (wScreen * FAConfig.hud().frameWidth);
        hFrame = (int) (hScreen * FAConfig.hud().frameHeight);

        lFrame = (wScreen - wFrame) / 2;
        rFrame = lFrame + wFrame;

        tFrame = (hScreen - hFrame) / 2;
        bFrame = tFrame + hFrame;
    }
}
