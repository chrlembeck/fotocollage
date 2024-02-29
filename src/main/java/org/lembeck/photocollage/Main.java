package org.lembeck.photocollage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Paths;
import java.util.List;

import static org.lembeck.photocollage.CollageUtil.ladeBilder;

public class Main {

    public static void main(String[] args) throws Exception {
        CollageSettings settings = new CollageSettings(8000, 4000, 15, 15, Color.WHITE, PhotoComposer.CollageStyle.GROUPED);
        List<ImageRef> images = ladeBilder(Paths.get("D:", "temp", "leinwand"));
        images.forEach(ImageRef::initSize);
        PhotoComposer photoComposer = new PhotoComposer(images, event -> {
        });
        BufferedImage image = photoComposer.compose(PhotoComposer.CollageStyle.GROUPED, settings);
        System.out.println("Speichere JPG.");
        ImageIO.write(image, "jpg", new File("d:\\temp\\image.jpg"));
        System.out.println("Speichere PNG.");
        ImageIO.write(image, "png", new File("d:\\temp\\image.png"));
    }
}