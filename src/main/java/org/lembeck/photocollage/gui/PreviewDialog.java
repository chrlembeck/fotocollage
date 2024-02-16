package org.lembeck.photocollage.gui;

import org.lembeck.photocollage.gui.components.ZoomableImage;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static javax.swing.SwingConstants.HORIZONTAL;
import static org.lembeck.photocollage.gui.GuiUtil.*;
import static org.lembeck.photocollage.gui.Icons.*;

public class PreviewDialog extends JFrame {

    public static final String RESULT_DIRECTORY_KEY = "result-directory";
    private ZoomableImage zoomableImage;

    public PreviewDialog(CollageGUI gui, BufferedImage image) {
        super("Vorschau");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setApplicationIcon(this);


        init(image);
        pack();
        setLocationRelativeTo(gui);
    }

    private void init(BufferedImage image) {
        setLayout(new BorderLayout());

        zoomableImage = new ZoomableImage(image);
        JScrollPane spContent = new JScrollPane(zoomableImage);
        spContent.setWheelScrollingEnabled(false);

        add(spContent, BorderLayout.CENTER);

        JToolBar toolbar = new JToolBar(HORIZONTAL);
        toolbar.setFloatable(false);
        toolbar.setLayout(new FlowLayout(FlowLayout.CENTER));
        add(toolbar, BorderLayout.PAGE_START);

        Action zoom100Action = zoomableImage.getUI().getZoom100Action(zoomableImage);
        zoom100Action.putValue(AbstractAction.SMALL_ICON, ZOOM_ORIGINAL);
        Action zoomInAction = zoomableImage.getUI().getZoomInAction(zoomableImage);
        zoomInAction.putValue(AbstractAction.SMALL_ICON, ZOOM_PLUS);
        Action zoomOutAction = zoomableImage.getUI().getZoomOutAction(zoomableImage);
        zoomOutAction.putValue(AbstractAction.SMALL_ICON, ZOOM_MINUS);

        toolbar.add(new SaveAction());
        toolbar.add(zoomOutAction);
        toolbar.add(zoom100Action);
        toolbar.add(zoomInAction);
    }

    class SaveAction extends AbstractAction {

        SaveAction() {
            putValue(NAME, "Speichern");
            putValue(SMALL_ICON, SAVE);
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser();
            File dir = getLastDirectory(RESULT_DIRECTORY_KEY);
            if (dir != null) {
                chooser.setCurrentDirectory(dir);
            }
            chooser.setMultiSelectionEnabled(false);
            chooser.setDragEnabled(false);
            chooser.setMultiSelectionEnabled(false);
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);
            final FileNameExtensionFilter pngFilter = new FileNameExtensionFilter("PNG (*.png)", "PNG");
            final FileNameExtensionFilter jpegFilter = new FileNameExtensionFilter("JPEG (*.jpg; *.jpeg)", "JPG", "JPEG");
            chooser.addChoosableFileFilter(pngFilter);
            chooser.addChoosableFileFilter(jpegFilter);


            int option = chooser.showSaveDialog(PreviewDialog.this);
            if (option == JFileChooser.APPROVE_OPTION) {
                FileFilter chooserFileFilter = chooser.getFileFilter();
                File file = chooser.getSelectedFile();
                if (!file.exists() || JOptionPane.showConfirmDialog(PreviewDialog.this, "Wollen Sie die existierende Datei '%s' überschreiben?".formatted(file.toString()), "Überschreiben", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    String name = file.getName();
                    if (chooserFileFilter == pngFilter) {
                        if (!name.toLowerCase().endsWith(".png")) {
                            name += ".png";
                        }
                    } else if (chooserFileFilter == jpegFilter) {
                        if (!(name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".jpeg"))) {
                            name += ".jpg";
                        }
                    } else {
                        throw new IllegalStateException();
                    }
                    file = new File(file.getParent(), name);
                    save(file, chooserFileFilter == pngFilter);
                }
            }
        }
    }

    void save(File file, boolean png) {
        try {
            ImageIO.write(zoomableImage.getModel().getImage(), png ? "png" : "jpg", file);
            saveLastDirectory(RESULT_DIRECTORY_KEY, file.getParentFile());
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(this, ioe.getLocalizedMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
            ioe.printStackTrace(System.err);
        }
    }
}