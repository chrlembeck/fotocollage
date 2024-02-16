package org.lembeck.photocollage;

import java.awt.*;
import java.util.Map;

class InnerNode implements TreeNode {

    private SplitType splitType;

    private final TreeNode left;

    private final TreeNode right;

    private float targetRatio;

    public InnerNode(TreeNode left, TreeNode right, float targetRatio, SplitType splitType) {
        this.left = left;
        this.right = right;
        this.targetRatio = targetRatio;
        this.splitType = splitType;
    }

    @Override
    public float getAspectRatio() {
        final float leftRatio = left.getAspectRatio();
        final float rightRatio = right.getAspectRatio();
        return switch (splitType) {
            case VERTICAL -> leftRatio + rightRatio;
            case HORIZONTAL -> (leftRatio * rightRatio) / (leftRatio + rightRatio);
        };
    }

    @Override
    public String toString() {
        return "(" + left + " " + (splitType == SplitType.VERTICAL ? "V" : "H") + " " + right + " => " + getAspectRatio() + "/" + targetRatio + ")";
    }

    @Override
    public void adjust(float sigma) {
        float aspectRatio = getAspectRatio();
        if (aspectRatio > targetRatio * sigma) {
            splitType = SplitType.HORIZONTAL;
        }
        if (aspectRatio < targetRatio / sigma) {
            splitType = SplitType.VERTICAL;
        }
        if (splitType == SplitType.VERTICAL) {
            if (left instanceof InnerNode inner) {
                inner.setTargetRatio(targetRatio / 2);
            }
            if (right instanceof InnerNode inner) {
                inner.setTargetRatio(targetRatio / 2);
            }
        } else {
            if (left instanceof InnerNode inner) {
                inner.setTargetRatio(targetRatio * 2);
            }
            if (right instanceof InnerNode inner) {
                inner.setTargetRatio(targetRatio * 2);
            }
        }
        left.adjust(sigma);
        right.adjust(sigma);
    }

    private void setTargetRatio(float targetRatio) {
        this.targetRatio = targetRatio;
    }

    @Override
    public float getTargetRatio() {
        return targetRatio;
    }

    @Override
    public void alignImages(CollageSettings settings, Map<ImageRef, Rectangle> map, Rectangle bounds) {
        switch (splitType) {
            case VERTICAL: {
                int height = bounds.height;
                int leftWidth = (int) (left.getAspectRatio() * height);
                int rightWidth = (int) (right.getAspectRatio() * height);
                int diff = bounds.width - settings.getInnerBorderWidth() - leftWidth - rightWidth;
                int leftDiff = leftWidth == 0 ? 0 : (diff * leftWidth) / (leftWidth + rightWidth);
                int rightDiff = diff - leftDiff;
                leftWidth = Math.max(0, leftWidth + leftDiff);
                rightWidth = Math.max(0, rightWidth + rightDiff);
                left.alignImages(settings, map, new Rectangle(bounds.x, bounds.y, leftWidth, height));
                right.alignImages(settings, map, new Rectangle(bounds.x + leftWidth + settings.getInnerBorderWidth(), bounds.y, rightWidth, height));
                // w = w1 + b + w2
                // w1 / h = a1  --> w1 = a1 * h
                // w2 / h = a2  --> w2 = a2 * h
                break;
            }
            case HORIZONTAL: {
                int width = bounds.width;
                int topHeight = (int) (width / left.getAspectRatio());
                int bottomHeight = (int) (width / right.getAspectRatio());
                int diff = bounds.height - settings.getInnerBorderWidth() - topHeight - bottomHeight;
                int topDiff = topHeight == 0 ? 0 : (diff * topHeight) / (topHeight + bottomHeight);
                int bottomDiff = diff - topDiff;
                topHeight = Math.max(0, topHeight + topDiff);
                bottomHeight = Math.max(0, bottomHeight + bottomDiff);
                left.alignImages(settings, map, new Rectangle(bounds.x, bounds.y, width, topHeight));
                right.alignImages(settings, map, new Rectangle(bounds.x, bounds.y + topHeight + settings.getInnerBorderWidth(), width, bottomHeight));
                break;
            }
            default:
                throw new IllegalStateException("Unexpected value: " + splitType);
        }
    }
}