package org.lembeck.photocollage.gui.components;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;

public abstract class ZoomableImageUI extends ComponentUI {

    public Action getZoom50Action(ZoomableImage component) {
        return getZoomToAction(component, "50%", 0.5f);
    }

    public Action getZoom100Action(ZoomableImage component) {
        return getZoomToAction(component, "100%", 1);
    }

    public abstract Action getZoomInAction(ZoomableImage component);

    public abstract Action getZoomOutAction(ZoomableImage component);

    public Action getZoom200Action(ZoomableImage component) {
        return getZoomToAction(component, "200%", 2);
    }

    public abstract Action getZoomToAction(ZoomableImage component, String name, float factor);
}