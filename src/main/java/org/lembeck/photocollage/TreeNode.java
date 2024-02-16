package org.lembeck.photocollage;

import java.awt.*;
import java.security.SecureRandom;
import java.util.Map;

public interface TreeNode {

    float getAspectRatio();

    void adjust(float sigma);

    float getTargetRatio();

    void alignImages(CollageSettings settings, Map<ImageRef, Rectangle> map, Rectangle bounds);

    enum SplitType {
        VERTICAL, HORIZONTAL;

        private static final SecureRandom RANDOM = new SecureRandom();

        public static SplitType random() {
            return RANDOM.nextBoolean() ? VERTICAL : HORIZONTAL;
        }
    }
}