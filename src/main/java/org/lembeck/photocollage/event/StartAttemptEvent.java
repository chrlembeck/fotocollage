package org.lembeck.photocollage.event;

public final class StartAttemptEvent implements ImageComposeProgressEvent {

    private final int attemptIndex;

    private final int maxAttempts;

    public StartAttemptEvent(int attemptIndex, int maxAttempts) {
        this.attemptIndex = attemptIndex;
        this.maxAttempts = maxAttempts;
    }

    public int getAttemptIndex() {
        return attemptIndex;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }
}