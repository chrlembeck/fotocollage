package org.lembeck.photocollage;

import java.awt.*;
import java.util.Map;

class Leaf implements TreeNode {

    private final ImageRef image;

    public Leaf(ImageRef image) {
        this.image = image;
    }

    @Override
    public float getAspectRatio() {
        return image.getAspectRatio();
    }

    @Override
    public String toString() {
        return "(" + getAspectRatio() + ")";
    }

    @Override
    public void adjust(float sigma) {
    }

    @Override
    public float getTargetRatio() {
        return Float.NaN;
    }

    @Override
    public void alignImages(CollageSettings settings, Map<ImageRef, Rectangle> map, Rectangle bounds) {
        map.put(image, bounds);
    }
}