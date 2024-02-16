package org.lembeck.photocollage.gui;

import org.lembeck.photocollage.ImageRef;

public class ImageDataLoadedEvent {

    private final ImageRef image;

    public enum DataType {
        SIZE,

        THUMBNAIL
    }

    private final DataType dataType;

    public ImageDataLoadedEvent(ImageRef image, DataType dataType) {
        this.dataType = dataType;
        this.image = image;
    }

    public DataType getDataType() {
        return dataType;
    }

    public ImageRef getImage() {
        return image;
    }
}