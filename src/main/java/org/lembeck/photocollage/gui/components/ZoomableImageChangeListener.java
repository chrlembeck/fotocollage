package org.lembeck.photocollage.gui.components;

public interface ZoomableImageChangeListener {

    void imageChanged();

    void zoomfactorChanged(float oldFactor, float newFactor);
}