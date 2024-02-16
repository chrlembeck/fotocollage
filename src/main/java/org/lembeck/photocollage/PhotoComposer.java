package org.lembeck.photocollage;

import org.lembeck.photocollage.event.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.*;

import static org.lembeck.photocollage.PhotoComposer.CollageStyle.SIMPLE;

public class PhotoComposer {

    public enum CollageStyle {

        SIMPLE,

        GROUPED
    }

    private final ImageComposeProgressListener progressListener;

    private final List<ImageRef> images = new ArrayList<>();

    public PhotoComposer(List<ImageRef> images, ImageComposeProgressListener progressListener) {
        this.images.addAll(images);
        this.progressListener = Objects.requireNonNull(progressListener);
    }

    public BufferedImage compose(CollageStyle style, CollageSettings settings) {
        if (style == SIMPLE) {
            AlignmentInfos alignmentInfos = calculateImageAlignment(images, settings, progressListener, 0, 1);
            return AlignmentTree.paintImage(alignmentInfos.getTreeNode(), settings, progressListener);
        } else {
            int w = settings.getWidth() - 2 * settings.getOuterBorderWidth();
            int h = settings.getHeight() - 2 * settings.getOuterBorderWidth();
            double f = 1 - 1 / Math.sqrt(5);
            int b = (int) (w * f / 2);
            int d = (int) (h * f / 2);
            int a = w - b;
            int c = h - d;
            b -= settings.getInnerBorderWidth() / 2;
            d -= settings.getInnerBorderWidth() / 2;
            a -= settings.getInnerBorderWidth() - settings.getInnerBorderWidth() / 2;
            c -= settings.getInnerBorderWidth() - settings.getInnerBorderWidth() / 2;

            CollageSettings topBottomSettings = new CollageSettings(a, d, settings.getInnerBorderWidth(), 0, settings.getBackgroundColor());
            CollageSettings leftRightSettings = new CollageSettings(b, c, settings.getInnerBorderWidth(), 0, settings.getBackgroundColor());
            CollageSettings centerSettings = new CollageSettings(a - b - settings.getInnerBorderWidth(), c - d - settings.getInnerBorderWidth(), settings.getInnerBorderWidth(), 0, settings.getBackgroundColor());

            int imageCount = images.size() / 5;

            AlignmentInfos topTree = calculateImageAlignment(images.subList(0, imageCount), topBottomSettings, progressListener, 0, 5);
            AlignmentInfos bottomTree = calculateImageAlignment(images.subList(imageCount, 2 * imageCount), topBottomSettings, progressListener, 1, 5);
            AlignmentInfos leftTree = calculateImageAlignment(images.subList(2 * imageCount, 3 * imageCount), leftRightSettings, progressListener, 2, 5);
            AlignmentInfos rightTree = calculateImageAlignment(images.subList(3 * imageCount, 4 * imageCount), leftRightSettings, progressListener, 3, 5);
            AlignmentInfos centerTree = calculateImageAlignment(images.subList(4 * imageCount, images.size()), centerSettings, progressListener, 4, 5);

            BufferedImage topImage = topTree.createImage(progressListener);
            BufferedImage bottomImage = bottomTree.createImage(progressListener);
            BufferedImage leftImage = leftTree.createImage(progressListener);
            BufferedImage rightImage = rightTree.createImage(progressListener);
            BufferedImage centerImage = centerTree.createImage(progressListener);

            BufferedImage image = new BufferedImage(settings.getWidth(), settings.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = (Graphics2D) image.getGraphics();
            graphics.setPaint(settings.getBackgroundColor());
            graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
            graphics.drawImage(leftImage, settings.getOuterBorderWidth(), settings.getOuterBorderWidth(), null);
            graphics.drawImage(topImage, settings.getOuterBorderWidth() + b + settings.getInnerBorderWidth(), settings.getOuterBorderWidth(), null);
            graphics.drawImage(bottomImage, settings.getOuterBorderWidth(), settings.getOuterBorderWidth() + c + settings.getInnerBorderWidth(), null);
            graphics.drawImage(rightImage, settings.getWidth() - settings.getOuterBorderWidth() - b, settings.getOuterBorderWidth() + d + settings.getInnerBorderWidth(), null);
            graphics.drawImage(centerImage, settings.getOuterBorderWidth() + b + settings.getInnerBorderWidth(), settings.getOuterBorderWidth() + d + settings.getInnerBorderWidth(), null);

            return image;
        }
    }

    static AlignmentInfos calculateImageAlignment(List<ImageRef> images, CollageSettings settings,
            ImageComposeProgressListener progressListener, int partIndex, int totalPartsCount) {
        if (images.isEmpty()) {
            throw new IllegalArgumentException("Keine Bilder zur Verfügung.");
        }
        images = new ArrayList<>(images);
        Collections.shuffle(images);

        final int maxVersuche = 50000;

        progressListener.registerProgress(new ComposeStartedEvent(totalPartsCount));
        float bestRatioDiff = 100;
        TreeNode bestTree = null;
        float neededRatioDiff = Math.min(1f / settings.getWidth(), 1f / settings.getHeight());
        for (int i = 0; i <= maxVersuche && bestRatioDiff > neededRatioDiff; i++) {

            progressListener.registerProgress(new StartAttemptEvent(i, maxVersuche));

            TreeNode t = AlignmentTree.createTree(images, settings);
            t.adjust(1.05f);

            Map<ImageRef, Rectangle> alignment = AlignmentTree.alignImages(t, settings);
            LongSummaryStatistics statistics = alignment.values().stream().mapToLong(a -> ((long) a.width) * ((long) a.height)).summaryStatistics();
            // Verhältnis größtes Bild / kleinstes Bild
            long dimensionRatio = statistics.getMin() == 0 ? 0 : statistics.getMax() / statistics.getMin();

            float ratioDiff = Math.abs(t.getAspectRatio() - t.getTargetRatio());
            if (bestTree != null && (dimensionRatio == 0 || dimensionRatio > 16 || ratioDiff > 0.05)) {
                continue;
            }

            if (bestTree == null || ratioDiff < bestRatioDiff) {
                bestRatioDiff = ratioDiff;
                bestTree = t;

                progressListener.registerProgress(new NewRatioFoundEvent(i, maxVersuche, bestRatioDiff, statistics.getMin(), statistics.getMax()));
            }
        }

        progressListener.registerProgress(new ComposeFinishedEvent(partIndex));
        return new AlignmentInfos(bestTree, settings);
    }
}