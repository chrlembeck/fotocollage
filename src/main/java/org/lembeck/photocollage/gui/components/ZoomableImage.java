package org.lembeck.photocollage.gui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.image.RenderedImage;
import java.util.Objects;

import static java.lang.Math.ceil;
import static java.lang.Math.floor;

public class ZoomableImage extends JComponent implements ZoomableImageChangeListener {

    static {
        UIManager.put(ZoomableImageUI.class.getName(), BasicZoomableImageUI.class.getName());
    }

    private ZoomableImageModel model;

    public ZoomableImage(RenderedImage image) {
        init(new DefaultZoomableImageModel(image));
    }

    private void init(ZoomableImageModel model) {
        setModel(model);

        updateUI();
    }

    public ZoomableImageModel getModel() {
        return model;
    }

    private void setModel(ZoomableImageModel newModel) {
        var old = this.model;
        if (this.model != null) {
            this.model.removeChangeListener(this);
        }
        this.model = Objects.requireNonNull(newModel);
        this.model.addChangeListener(this);
        firePropertyChange("model", old, newModel);
    }

    public void setUI(ZoomableImageUI ui) {
        super.setUI(ui);
    }

    public void updateUI() {
        setUI((ZoomableImageUI) UIManager.getUI(this));
        invalidate();
    }

    public String getUIClassID() {
        return ZoomableImageUI.class.getName();
    }


    @Override
    public void imageChanged() {
        revalidate();
        repaint();
    }

    @Override
    public void zoomfactorChanged(final float oldFactor, final float newFactor) {
        Rectangle viewRectOld = null;
        final Dimension prefSizeNew = getPreferredSize();
        final Dimension prefSizeOld = new Dimension(
                (int) ceil(prefSizeNew.width * oldFactor / newFactor),
                (int) ceil(prefSizeNew.height * oldFactor / newFactor));
        if (getParent() != null && getParent() instanceof JViewport vp) {
            viewRectOld = vp.getViewRect();
        }
        revalidate();
        repaint();
        if (viewRectOld != null && getParent() != null && getParent() instanceof JViewport vp) {
            final Rectangle viewRectNew = vp.getViewRect();
            int oldCenterX;
            int oldCenterY;
            if (prefSizeOld.width <= viewRectOld.width) {
                oldCenterX = prefSizeOld.width / 2;
            } else {
                oldCenterX = viewRectOld.width / 2 + viewRectOld.x;
            }
            if (prefSizeOld.height <= viewRectOld.height) {
                oldCenterY = prefSizeOld.height / 2;
            } else {
                oldCenterY = viewRectOld.height / 2 + viewRectOld.y;
            }
            int newCenterX = (int) floor(oldCenterX * newFactor / oldFactor);
            int newCenterY = (int) floor(oldCenterY * newFactor / oldFactor);
            int newX = 0;
            int newY = 0;
            if (prefSizeNew.width > viewRectNew.width) {
                newX = newCenterX - viewRectNew.width / 2;
            }
            if (prefSizeNew.height > viewRectNew.height) {
                newY = newCenterY - viewRectNew.height / 2;
            }
            vp.setViewPosition(new Point(newX, newY));
        }
    }

    @Override
    public ZoomableImageUI getUI() {
        return (ZoomableImageUI) super.getUI();
    }
}