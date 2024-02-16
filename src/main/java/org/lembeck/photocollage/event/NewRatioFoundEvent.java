package org.lembeck.photocollage.event;

public final class NewRatioFoundEvent implements ImageComposeProgressEvent {

    private final int attemptIndex;

    private final int totalAttemptCount;

    private final float bestRatioDiff;

    private final long minImageArea;

    private final long maxImageArea;

    public NewRatioFoundEvent(int attemptIndex, int totalAttemptCount, float bestRatioDiff, long minImageArea,
            long maxImageArea) {
        this.attemptIndex = attemptIndex;
        this.bestRatioDiff = bestRatioDiff;
        this.minImageArea = minImageArea;
        this.maxImageArea = maxImageArea;
        this.totalAttemptCount = totalAttemptCount;
    }

    public int getAttemptIndex() {
        return attemptIndex;
    }

    public int getTotalAttemptCount() {
        return totalAttemptCount;
    }

    public float getBestRatioDiff() {
        return bestRatioDiff;
    }

    public long getMinImageArea() {
        return minImageArea;
    }

    public long getMaxImageArea() {
        return maxImageArea;
    }
}
