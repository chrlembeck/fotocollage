package org.lembeck.photocollage.gui.components;

import com.mortennobel.imagescaling.ResampleFilters;
import com.mortennobel.imagescaling.ResampleOp;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class PreviewIcon implements Icon {

    private final int size;

    private final BufferedImage image;

    public PreviewIcon(int size, BufferedImage image) {
        this.size = size;
        final int newWidth, newHeight;
        final int imageSize = size - 2;
        if (image.getWidth() > image.getHeight()) {
            newWidth = imageSize;
            newHeight = image.getHeight() * imageSize / image.getWidth();
        } else {
            newHeight = imageSize;
            newWidth = image.getWidth() * imageSize / image.getHeight();
        }

        ResampleOp resizeOp = new ResampleOp(newWidth, newHeight);
        resizeOp.setFilter(ResampleFilters.getLanczos3Filter());
        this.image = resizeOp.filter(image, null);
    }

    @Override
    public void paintIcon(Component comp, Graphics g, int x, int y) {
        g.setColor(Color.WHITE);
        g.fillRect(x, y, size, size);
        g.setColor(Color.DARK_GRAY);
        g.drawRect(x, y, size - 1, size - 1);
        g.drawImage(image, x + (size - image.getWidth()) / 2, y + (size - image.getHeight()) / 2, null);
    }

    @Override
    public int getIconWidth() {
        return size;
    }

    @Override
    public int getIconHeight() {
        return size;
    }
}