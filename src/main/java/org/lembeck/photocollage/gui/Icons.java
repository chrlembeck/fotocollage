package org.lembeck.photocollage.gui;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class Icons {

    public static final Image ICON_16 = loadImage("icon-16.png");
    public static final Image ICON_24 = loadImage("icon-24.png");
    public static final Image ICON_32 = loadImage("icon-32.png");
    public static final ImageIcon ADD = loadIcon("edit-add-32.png");
    public static final ImageIcon WIZARD = loadIcon("tools-wizard-2-32.png");
    public static final ImageIcon REMOVE = loadIcon("list-remove-6-32.png");

    public static final ImageIcon PICTURE = loadIcon("image-x-nikon-nef-32.png");

    public static final ImageIcon ZOOM_ORIGINAL = loadIcon("zoom-original-2-32.png");
    public static final ImageIcon ZOOM_PLUS = loadIcon("zoom-in-3-32.png");
    public static final ImageIcon ZOOM_MINUS = loadIcon("zoom-out-3-32.png");
    public static final ImageIcon ZOOM_FIT = loadIcon("zoom-fit-best-2-32.png");

    public static final ImageIcon SAVE = loadIcon("document-save-5-32.png");

    public static final ImageIcon OPTIONS = loadIcon("appointment-new-32.png");
    public static final ImageIcon CANCEL = loadIcon("edit-delete-6-32.png");

    private static ImageIcon loadIcon(String name) {
        return new ImageIcon(Objects.requireNonNull(Icons.class.getResource("/images/" + name)));
    }

    private static Image loadImage(String name) {
        return Toolkit.getDefaultToolkit().getImage(Icons.class.getResource("/images/" + name));
    }
}
