package org.lembeck.photocollage;

import com.mortennobel.imagescaling.ResampleFilters;
import com.mortennobel.imagescaling.ResampleOp;
import org.lembeck.photocollage.gui.ImageDataListener;
import org.lembeck.photocollage.gui.ImageDataLoadedEvent;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static org.lembeck.photocollage.gui.ImageDataLoadedEvent.DataType.SIZE;
import static org.lembeck.photocollage.gui.ImageDataLoadedEvent.DataType.THUMBNAIL;

public class ImageRef {

    private final long fileSize;

    private final Path path;

    private int width;

    private int height;

    private BufferedImage preview;

    private final List<ImageDataListener> imageDataListeners = new ArrayList<>(1);

    public ImageRef(Path path) {
        this(path, 0, 0);
    }

    public ImageRef(Path path, int width, int height) {
        this.fileSize = path.toFile().length();
        this.path = path;
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public float getAspectRatio() {
        return (float) width / (float) height;
    }

    public BufferedImage getImage() {
        if (path == null) {
            return new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        } else {
            try {
                return ImageIO.read(path.toFile());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void initSize() {
        try {
            Dimension dim = getImageDimension(path);
            this.width = dim.width;
            this.height = dim.height;
            imageDataListeners.forEach(l -> l.imageMetadataLoaded(new ImageDataLoadedEvent(this, SIZE)));
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    public void loadPreview() {
        BufferedImage image = getImage();
        final int newWidth, newHeight;
        final int imageSize = 80 - 2;
        if (image.getWidth() > image.getHeight()) {
            newWidth = imageSize;
            newHeight = image.getHeight() * imageSize / image.getWidth();
        } else {
            newHeight = imageSize;
            newWidth = image.getWidth() * imageSize / image.getHeight();
        }

        ResampleOp resizeOp = new ResampleOp(newWidth, newHeight);
        resizeOp.setFilter(ResampleFilters.getLanczos3Filter());
        this.preview = resizeOp.filter(image, null);
        imageDataListeners.forEach(l -> l.imagePreviewLoaded(new ImageDataLoadedEvent(this, THUMBNAIL)));
    }

    public static Dimension getImageDimension(Path imgFile) throws IOException {
        int pos = imgFile.getFileName().toString().lastIndexOf(".");
        if (pos == -1) {
            throw new IOException("No extension for file: " + imgFile);
        }
        String suffix = imgFile.getFileName().toString().substring(pos + 1);
        Iterator<ImageReader> iter = ImageIO.getImageReadersBySuffix(suffix);
        if (iter.hasNext()) {
            ImageReader reader = iter.next();
            try {
                ImageInputStream stream = new FileImageInputStream(imgFile.toFile());
                reader.setInput(stream);
                int width = reader.getWidth(reader.getMinIndex());
                int height = reader.getHeight(reader.getMinIndex());
                return new Dimension(width, height);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                reader.dispose();
            }
        }
        throw new IOException("Not a known image file: " + imgFile);
    }

    public Path getPath() {
        return path;
    }

    public long getFileSize() {
        return fileSize;
    }

    public BufferedImage getPreview() {
        return preview;
    }

    public void addImageDataListener(ImageDataListener listener) {
        imageDataListeners.add(listener);
    }

    public boolean removeImageDataListener(ImageDataListener listener) {
        return imageDataListeners.remove(listener);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageRef imageRef = (ImageRef) o;
        return path.equals(imageRef.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }
}