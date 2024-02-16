package org.lembeck.photocollage.gui.components;

import java.awt.image.RenderedImage;

public interface ZoomableImageModel {

    RenderedImage getImage();

    void setImage(RenderedImage image);

    float getZoomfactor();

    void setZoomfactor(float zoomfactor);

    void addChangeListener(ZoomableImageChangeListener listener);

    void removeChangeListener(ZoomableImageChangeListener listener);
}