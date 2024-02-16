package org.lembeck.photocollage;

import org.lembeck.photocollage.TreeNode.SplitType;
import org.lembeck.photocollage.event.ImageComposeProgressListener;
import org.lembeck.photocollage.event.ImagePaintedEvent;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lembeck.photocollage.gui.GuiUtil.prepareGraphics;

public class AlignmentTree {

    public static TreeNode createTree(List<ImageRef> images, CollageSettings settings) {
        return createTree(new ArrayList<>(images), images.size(), settings.getAspectRatio());
    }

    private static TreeNode createTree(List<ImageRef> images, int imagesCount, float targetRatio) {
        if (imagesCount == 1) {
            ImageRef image = findAndRemoveBestImage(images, targetRatio);
            return new Leaf(image);
        }
        SplitType splitType = SplitType.random();
        if (imagesCount == 2) {
            ImageRef[] pair = findAndRemoveBestPair(images, targetRatio, splitType);
            return new InnerNode(new Leaf(pair[0]), new Leaf(pair[1]), targetRatio, splitType);
        }
        float subtreeRatio = splitType == SplitType.VERTICAL ? targetRatio / 2 : targetRatio * 2;
        TreeNode t1 = createTree(images, imagesCount / 2, subtreeRatio);
        TreeNode t2 = createTree(images, imagesCount - (imagesCount / 2), subtreeRatio);

        return new InnerNode(t1, t2, targetRatio, splitType);
    }

    private static ImageRef[] findAndRemoveBestPair(List<ImageRef> images, float targetRatio, SplitType splitType) {
        int bestLeft = 0;
        int bestRight = 1;
        float bestDiff = calcRatio(images.get(bestLeft).getAspectRatio(), images.get(bestRight).getAspectRatio(), splitType);
        for (int l = 0; l < images.size() - 1; l++) {
            for (int r = l + 1; r < images.size(); r++) {
                final float diff = Math.abs(calcRatio(images.get(l).getAspectRatio(), images.get(r).getAspectRatio(), splitType) - targetRatio);
                if (diff < bestDiff) {
                    bestLeft = l;
                    bestRight = r;
                    bestDiff = diff;
                }
            }
        }
        return new ImageRef[]{images.remove(bestRight), images.remove(bestLeft)};
    }

    private static float calcRatio(float leftRatio, float rightRatio, SplitType splitType) {
        return switch (splitType) {
            case VERTICAL -> leftRatio + rightRatio;
            case HORIZONTAL -> (leftRatio * rightRatio) / (leftRatio + rightRatio);
        };
    }

    private static ImageRef findAndRemoveBestImage(List<ImageRef> images, float targetRatio) {
        int bestIdx = 0;
        float bestDiff = Math.abs(images.getFirst().getAspectRatio() - targetRatio);
        for (int i = 1; i < images.size(); i++) {
            float diff = Math.abs(images.get(i).getAspectRatio() - targetRatio);
            if (diff < bestDiff) {
                bestDiff = diff;
                bestIdx = i;
            }
        }
        return images.remove(bestIdx);
    }

    public static Map<ImageRef, Rectangle> alignImages(TreeNode treeNode, CollageSettings settings) {
        Map<ImageRef, Rectangle> map = new HashMap<>();
        treeNode.alignImages(settings, map,
                new Rectangle(settings.getOuterBorderWidth(), settings.getOuterBorderWidth(),
                        settings.getWidth() - 2 * settings.getOuterBorderWidth(),
                        settings.getHeight() - 2 * settings.getOuterBorderWidth()));
        return map;
    }

    public static BufferedImage paintImage(TreeNode treeNode, CollageSettings settings,
            ImageComposeProgressListener progressListener) {
        Map<ImageRef, Rectangle> alignment = alignImages(treeNode, settings);
        BufferedImage result = new BufferedImage(settings.getWidth(), settings.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = (Graphics2D) result.getGraphics();
        prepareGraphics(graphics);
        graphics.setPaint(settings.getBackgroundColor());
        graphics.fillRect(0, 0, result.getWidth(), result.getHeight());
        alignment.forEach((imageRef, bounds) -> {
            BufferedImage image = imageRef.getImage();
            graphics.drawImage(image, bounds.x, bounds.y, bounds.x + bounds.width, bounds.y + bounds.height, 0, 0, image.getWidth(), image.getHeight(),
                    null);
            progressListener.registerProgress(new ImagePaintedEvent());
        });
        return result;
    }
}