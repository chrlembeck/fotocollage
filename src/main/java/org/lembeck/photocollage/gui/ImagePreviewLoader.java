package org.lembeck.photocollage.gui;

import org.lembeck.photocollage.ImageRef;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ImagePreviewLoader implements Runnable {

    private final List<ImageRef> images;

    public ImagePreviewLoader(Collection<ImageRef> images) {
        this.images = new ArrayList<>(images);
    }

    @Override
    public void run() {
        images.parallelStream().forEach(ImageRef::loadPreview);
    }
}