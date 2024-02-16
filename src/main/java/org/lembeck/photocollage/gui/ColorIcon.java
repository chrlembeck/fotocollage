package org.lembeck.photocollage.gui;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class ColorIcon implements Icon {

    private final int width;

    private final int height;

    private final Color borderColor;

    private Color color;

    public ColorIcon(int width, int height, Color borderColor, Color color) {
        this.width = width;
        this.height = height;
        this.color = color;
        this.borderColor = borderColor;
    }


    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.setColor(color);
        g.fillRect(x + 1, y + 1, width - 1, height - 1);
        g.setColor(borderColor);
        g.drawRect(x, y, width, height);
    }

    @Override
    public int getIconWidth() {
        return width;
    }

    @Override
    public int getIconHeight() {
        return height;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color newColor) {
        this.color = Objects.requireNonNull(newColor);
    }
}
