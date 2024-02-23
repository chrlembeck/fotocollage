package org.lembeck.photocollage;

import org.lembeck.photocollage.event.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.*;

import static org.lembeck.photocollage.PhotoComposer.CollageStyle.SIMPLE;
import static org.lembeck.photocollage.gui.GuiUtil.prepareGraphics;

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
        Map<ImageRef, Rectangle> imagePositions = new HashMap<>();
        if (style == SIMPLE) {
            AlignmentInfos alignmentInfos = calculateImageAlignment(images, settings, progressListener, 0, 1);
            imagePositions.putAll(alignmentInfos.calcImagePositions(0, 0));
        } else {
            int totalImageWidth = settings.getWidth() - 2 * settings.getOuterBorderWidth();
            int totalImageHeight = settings.getHeight() - 2 * settings.getOuterBorderWidth();
            double goldenRatio = 1 - 1 / Math.sqrt(5);
            int leftRightWidth = (int) (totalImageWidth * goldenRatio / 2);
            int topBottomHeight = (int) (totalImageHeight * goldenRatio / 2);
            int topBottomWidth = totalImageWidth - leftRightWidth;
            int leftRightHeight = totalImageHeight - topBottomHeight;
            leftRightWidth -= settings.getInnerBorderWidth() / 2;
            topBottomHeight -= settings.getInnerBorderWidth() / 2;
            topBottomWidth -= settings.getInnerBorderWidth() - settings.getInnerBorderWidth() / 2;
            leftRightHeight -= settings.getInnerBorderWidth() - settings.getInnerBorderWidth() / 2;

            CollageSettings topBottomSettings = new CollageSettings(topBottomWidth, topBottomHeight, settings.getInnerBorderWidth(), 0, settings.getBackgroundColor());
            CollageSettings leftRightSettings = new CollageSettings(leftRightWidth, leftRightHeight, settings.getInnerBorderWidth(), 0, settings.getBackgroundColor());
            CollageSettings centerSettings = new CollageSettings(topBottomWidth - leftRightWidth - settings.getInnerBorderWidth(), leftRightHeight - topBottomHeight - settings.getInnerBorderWidth(), settings.getInnerBorderWidth(), 0, settings.getBackgroundColor());

            int imageCount = images.size() / 5;

            AlignmentInfos topTree = calculateImageAlignment(images.subList(0, imageCount), topBottomSettings, progressListener, 0, 5);
            AlignmentInfos bottomTree = calculateImageAlignment(images.subList(imageCount, 2 * imageCount), topBottomSettings, progressListener, 1, 5);
            AlignmentInfos leftTree = calculateImageAlignment(images.subList(2 * imageCount, 3 * imageCount), leftRightSettings, progressListener, 2, 5);
            AlignmentInfos rightTree = calculateImageAlignment(images.subList(3 * imageCount, 4 * imageCount), leftRightSettings, progressListener, 3, 5);
            AlignmentInfos centerTree = calculateImageAlignment(images.subList(4 * imageCount, images.size()), centerSettings, progressListener, 4, 5);

            imagePositions.putAll(topTree.calcImagePositions(settings.getOuterBorderWidth() + leftRightWidth + settings.getInnerBorderWidth(), settings.getOuterBorderWidth()));
            imagePositions.putAll(bottomTree.calcImagePositions(settings.getOuterBorderWidth(), settings.getOuterBorderWidth() + leftRightHeight + settings.getInnerBorderWidth()));
            imagePositions.putAll(leftTree.calcImagePositions(settings.getOuterBorderWidth(), settings.getOuterBorderWidth()));
            imagePositions.putAll(rightTree.calcImagePositions(settings.getWidth() - settings.getOuterBorderWidth() - leftRightWidth, settings.getOuterBorderWidth() + topBottomHeight + settings.getInnerBorderWidth()));
            imagePositions.putAll(centerTree.calcImagePositions(settings.getOuterBorderWidth() + leftRightWidth + settings.getInnerBorderWidth(), settings.getOuterBorderWidth() + topBottomHeight + settings.getInnerBorderWidth()));
        }

        return createImage(settings, imagePositions, progressListener);
    }

    public BufferedImage createImage(CollageSettings settings, Map<ImageRef, Rectangle> imagePositions,
            ImageComposeProgressListener progressListener) {
        BufferedImage result = new BufferedImage(settings.getWidth(), settings.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = (Graphics2D) result.getGraphics();
        prepareGraphics(graphics);
        graphics.setPaint(settings.getBackgroundColor());
        graphics.fillRect(0, 0, result.getWidth(), result.getHeight());
        imagePositions.entrySet().stream().parallel().forEach(e -> {
            BufferedImage image = e.getKey().getImage();
            Rectangle bounds = e.getValue();
            synchronized (result) {
                graphics.drawImage(image, bounds.x, bounds.y, bounds.x + bounds.width, bounds.y + bounds.height, 0, 0, image.getWidth(), image.getHeight(),
                        null);
            }
            progressListener.registerProgress(new ImagePaintedEvent());
        });

        return result;
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

            Map<ImageRef, Rectangle> alignment = AlignmentTree.alignImages(0, 0, t, settings);
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