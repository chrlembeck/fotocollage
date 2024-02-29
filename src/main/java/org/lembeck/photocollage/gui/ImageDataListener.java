package org.lembeck.photocollage.gui;

public interface ImageDataListener {

    void imageMetadataLoaded(ImageDataLoadedEvent event);

    void imagePreviewLoaded(ImageDataLoadedEvent event);
}