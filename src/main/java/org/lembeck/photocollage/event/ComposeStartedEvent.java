package org.lembeck.photocollage.event;

public final class ComposeStartedEvent implements ImageComposeProgressEvent {

    private final int totalPartCount;

    public ComposeStartedEvent(int totalPartCount) {
        this.totalPartCount = totalPartCount;
    }

    public int getTotalPartCount() {
        return totalPartCount;
    }
}