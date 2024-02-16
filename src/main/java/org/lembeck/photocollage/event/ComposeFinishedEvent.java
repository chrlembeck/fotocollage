package org.lembeck.photocollage.event;

public final class ComposeFinishedEvent implements ImageComposeProgressEvent {

    private final int partIdx;

    public ComposeFinishedEvent(int partIdx) {
        this.partIdx = partIdx;
    }

    public int getPartIdx() {
        return partIdx;
    }
}