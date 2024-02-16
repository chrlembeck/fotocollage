package org.lembeck.photocollage.gui.components;

import java.awt.image.RenderedImage;
import java.util.ArrayList;
import java.util.List;

public class DefaultZoomableImageModel implements ZoomableImageModel {

    private RenderedImage image;

    private float zoomfactor = 1;

    private float zoomFactorMin = 0.1f;

    private float zoomFactorMax = 8;

    public DefaultZoomableImageModel(RenderedImage image) {
        this.image = image;
    }

    private final List<ZoomableImageChangeListener> listeners = new ArrayList<>();

    @Override
    public RenderedImage getImage() {
        return image;
    }

    @Override
    public void setImage(RenderedImage newImage) {
        this.image = newImage;
        for (int i = listeners.size() - 1; i >= 0; i--) {
            listeners.get(i).imageChanged();
        }
    }

    @Override
    public float getZoomfactor() {
        return zoomfactor;
    }

    @Override
    public void setZoomfactor(float newZoomfactor) {
        newZoomfactor = Math.max(newZoomfactor, zoomFactorMin);
        newZoomfactor = Math.min(newZoomfactor, zoomFactorMax);
        if (newZoomfactor != zoomfactor) {
            final float oldZoomfactor = zoomfactor;
            this.zoomfactor = newZoomfactor;
            for (int i = listeners.size() - 1; i >= 0; i--) {
                listeners.get(i).zoomfactorChanged(oldZoomfactor, newZoomfactor);
            }
        }
    }

    @Override
    public void addChangeListener(ZoomableImageChangeListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeChangeListener(ZoomableImageChangeListener listener) {
        listeners.remove(listener);
    }

    public void setZoomFactorMin(float zoomFactorMin) {
        this.zoomFactorMin = zoomFactorMin;
    }

    public void setZoomFactorMax(float zoomFactorMax) {
        this.zoomFactorMax = zoomFactorMax;
    }

    public float getZoomFactorMin() {
        return zoomFactorMin;
    }

    public float getZoomFactorMax() {
        return zoomFactorMax;
    }
}