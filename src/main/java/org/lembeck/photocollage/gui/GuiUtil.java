package org.lembeck.photocollage.gui;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.prefs.Preferences;

import static org.lembeck.photocollage.gui.Icons.*;

public final class GuiUtil {

    public static void prepareGraphics(Graphics2D graphics) {
        graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }

    public static void saveLastDirectory(String key, File directory) {
        Preferences.userNodeForPackage(CollageGUI.class).put(key, directory.toString());
    }

    public static File getLastDirectory(String key) {
        String pref = Preferences.userNodeForPackage(CollageGUI.class).get(key, null);
        if (pref == null || pref.isBlank()) {
            return null;
        } else {
            File dir = new File(pref);
            return (dir.isDirectory() || dir.canRead()) ? dir : null;
        }
    }

    public static JPanel createButtonPanel(JButton... buttons) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        for (final var button : buttons) {
            panel.add(button);
        }
        return panel;
    }

    public static void setApplicationIcon(Window window) {
        window.setIconImages(List.of(ICON_16, ICON_24, ICON_32));
    }
}
