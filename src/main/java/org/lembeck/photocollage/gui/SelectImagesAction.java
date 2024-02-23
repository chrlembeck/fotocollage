package org.lembeck.photocollage.gui;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import static org.lembeck.photocollage.gui.GuiUtil.loadFileReference;
import static org.lembeck.photocollage.gui.GuiUtil.saveFileReference;
import static org.lembeck.photocollage.gui.Icons.ADD;

public class SelectImagesAction extends AbstractAction {

    public static final String IMAGE_DIRECTORY_KEY = "image-directory";

    private final ImagesPanel imagesPanel;

    private final CollageGUI gui;

    SelectImagesAction(ImagesPanel imagesPanel, CollageGUI gui) {
        this.imagesPanel = imagesPanel;
        this.gui = gui;
        putValue(AbstractAction.NAME, "Bilder Hinzufügen");
        putValue(MNEMONIC_KEY, KeyEvent.VK_H);
        putValue(AbstractAction.SMALL_ICON, ADD);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Bilder-Ordner wählen");
        chooser.setMultiSelectionEnabled(true);
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("Alle Bilder (*.png; *.jpg; *.jpeg)", "PNG", "JPG", "JPEG"));
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("PNG (*.png)", "PNG"));
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("JPEG (*.jpg; *.jpeg)", "JPG", "JPEG"));
        chooser.setDragEnabled(false);
        File dir = loadFileReference(IMAGE_DIRECTORY_KEY);
        if (dir != null) {
            chooser.setCurrentDirectory(dir);
        }

        int result = chooser.showOpenDialog(gui);
        if (result == JFileChooser.APPROVE_OPTION) {
            File[] files = chooser.getSelectedFiles();
            saveFileReference(IMAGE_DIRECTORY_KEY, chooser.getCurrentDirectory());
            imagesPanel.addImages(files);
        }
    }
}
