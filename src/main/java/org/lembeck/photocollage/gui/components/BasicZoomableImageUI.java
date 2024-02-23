package org.lembeck.photocollage.gui.components;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;

import static org.lembeck.photocollage.gui.GuiUtil.prepareGraphics;

public class BasicZoomableImageUI extends ZoomableImageUI implements MouseListener, MouseWheelListener {

    public static final float ZOOM_DELTA = (float) Math.pow(2, 0.25);

    @SuppressWarnings("unused")
    public static ComponentUI createUI(JComponent comp) {
        return new BasicZoomableImageUI();
    }

    @Override
    public void installUI(JComponent c) {
        ZoomableImage zi = (ZoomableImage) c;
        zi.addMouseListener(this);
        zi.addMouseWheelListener(this);
    }

    @Override
    public void uninstallUI(JComponent c) {
        ZoomableImage zi = (ZoomableImage) c;
        zi.removeMouseListener(this);
        zi.removeMouseWheelListener(this);
    }

    @Override
    public final void paint(Graphics g, JComponent c) {
        paintInternal((Graphics2D) g, (ZoomableImage) c);
    }

    protected void paintInternal(Graphics2D g, ZoomableImage component) {
        final ZoomableImageModel model = component.getModel();
        final RenderedImage image = model.getImage();
        final float zoomfactor = model.getZoomfactor();
        final int imageWidth = (int) Math.ceil(zoomfactor * image.getWidth());
        final int imageHeight = (int) Math.ceil(zoomfactor * image.getHeight());
        final int x = Math.max(0, (component.getWidth() - imageWidth) / 2);
        final int y = Math.max(0, (component.getHeight() - imageHeight) / 2);

        g.setColor(component.getBackground());
        g.fillRect(0, 0, component.getWidth(), component.getHeight());
        prepareGraphics(g);

        //g.drawImage(image, x, y, imageWidth, imageHeight, null);
        AffineTransform transform = AffineTransform.getTranslateInstance(x, y);
        transform.scale(zoomfactor, zoomfactor);
        g.drawRenderedImage(image, transform);
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        ZoomableImage component = (ZoomableImage) c;
        final ZoomableImageModel model = component.getModel();
        final RenderedImage image = model.getImage();
        final float zoomfactor = model.getZoomfactor();
        return new Dimension((int) Math.ceil(image.getWidth() * zoomfactor), (int) Math.ceil(image.getHeight() * zoomfactor));
    }

    @Override
    public Dimension getMinimumSize(JComponent c) {
        return getPreferredSize(c);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public Action getZoomInAction(ZoomableImage component) {
        return new FactorZoomAction(component, ZOOM_DELTA);
    }

    @Override
    public Action getZoomOutAction(ZoomableImage component) {
        return new FactorZoomAction(component, 1 / ZOOM_DELTA);
    }

    @Override
    public Action getZoomToAction(ZoomableImage component, String name, float factor) {
        return new ZoomAction(component, name, factor);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL && e.getModifiersEx() == 0 && e.getWheelRotation() != 0) {
            ZoomableImage component = (ZoomableImage) e.getSource();
            if (e.getWheelRotation() < 0) {
                getZoomInAction(component).actionPerformed(new ActionEvent(e.getSource(), e.getID(), "zoom-in"));
            } else if (e.getWheelRotation() > 0) {
                getZoomOutAction(component).actionPerformed(new ActionEvent(e.getSource(), e.getID(), "zoom-out"));
            }
        }
    }

    static class ZoomAction extends AbstractAction {

        final float factor;

        private final ZoomableImage component;

        ZoomAction(ZoomableImage component, String name, float factor) {
            this.factor = factor;
            this.component = component;
            putValue(NAME, name);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            component.getModel().setZoomfactor(factor);
        }
    }

    static class FactorZoomAction extends AbstractAction {

        private final ZoomableImage component;

        private final float factor;

        FactorZoomAction(ZoomableImage component, float factor) {
            this.component = component;
            this.factor = factor;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            float newFactor = component.getModel().getZoomfactor() * factor;
            component.getModel().setZoomfactor(newFactor);
        }
    }
}