package org.lembeck.photocollage.gui;

import org.lembeck.photocollage.CollageSettings;
import org.lembeck.photocollage.ImageRef;
import org.lembeck.photocollage.PhotoComposer;
import org.lembeck.photocollage.event.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class ComposeWorker extends SwingWorker<BufferedImage, ImageComposeProgressEvent> implements ImageComposeProgressListener {

    private final ComposeProgressDialog dialog;
    private final CollageSettings settings;

    private final List<ImageRef> images;

    public ComposeWorker(ComposeProgressDialog dialog, CollageSettings settings, List<ImageRef> images) {
        this.dialog = dialog;
        this.images = images;
        this.settings = settings;
    }

    @Override
    protected BufferedImage doInBackground() {
        PhotoComposer composer = new PhotoComposer(images, this);
        return composer.compose(PhotoComposer.CollageStyle.GROUPED, settings);
    }

    @Override
    public void registerProgress(ImageComposeProgressEvent event) {
        this.publish(event);
    }

    @Override
    protected void process(List<ImageComposeProgressEvent> chunks) {
        chunks.forEach(ch -> {
            switch (ch) {
                case ComposeStartedEvent cse -> dialog.composeStarted(cse);
                case ComposeFinishedEvent cfe -> dialog.composeFinished(cfe);
                case StartAttemptEvent sae -> dialog.attemptStarted(sae);
                case NewRatioFoundEvent nrfe -> dialog.newRatioFound(nrfe);
                case ImagePaintedEvent ignored -> dialog.imagePainted();
            }
        });
    }
}