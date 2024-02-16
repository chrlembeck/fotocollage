package org.lembeck.photocollage.event;

@FunctionalInterface
public interface ImageComposeProgressListener {

    void registerProgress(ImageComposeProgressEvent event);
}