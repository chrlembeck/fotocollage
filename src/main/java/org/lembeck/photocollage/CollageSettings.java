package org.lembeck.photocollage;

import java.awt.*;

public class CollageSettings {

    private final int width;

    private final int height;

    private final int outerBorderWidth;

    private final int innerBorderWidth;

    private final Color backgroundColor;

    public CollageSettings(int width, int height, int innerBorderWidth, int outerBorderWidth, Color backgroundColor) {
        this.width = width;
        this.height = height;
        this.innerBorderWidth = innerBorderWidth;
        this.outerBorderWidth = outerBorderWidth;
        this.backgroundColor = backgroundColor;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getOuterBorderWidth() {
        return outerBorderWidth;
    }

    public int getInnerBorderWidth() {
        return innerBorderWidth;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public float getAspectRatio() {
        return (float) (width - 2 * outerBorderWidth) / (float) (height - 2 * outerBorderWidth);
    }
}