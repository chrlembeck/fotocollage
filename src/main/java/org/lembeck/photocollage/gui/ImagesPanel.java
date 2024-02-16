package org.lembeck.photocollage.gui;

import org.lembeck.photocollage.CollageUtil;
import org.lembeck.photocollage.ImageRef;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.awt.GridBagConstraints.*;
import static javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
import static org.lembeck.photocollage.gui.GuiUtil.createButtonPanel;
import static org.lembeck.photocollage.gui.Icons.OPTIONS;

public class ImagesPanel extends JPanel {

    private final JTable imagesTable;

    private final ImagesTableModel imagesTableModel = new ImagesTableModel();

    private final CollageGUI gui;

    private final JLabel lbInfo = new JLabel("");

    ImagesPanel(CollageGUI gui) {
        this.gui = gui;
        setLayout(new BorderLayout());
        JPanel content = new JPanel();
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        content.setLayout(new GridBagLayout());
        add(content, BorderLayout.CENTER);
        JLabel lbImageInfo = new JLabel();


        imagesTable = new JTable(imagesTableModel);
        imagesTable.setAutoCreateRowSorter(true);
        imagesTable.setSelectionMode(MULTIPLE_INTERVAL_SELECTION);
        JScrollPane spImageTable = new JScrollPane(imagesTable);
        spImageTable.getViewport().getView().setBackground(imagesTable.getBackground());
        spImageTable.getViewport().setBackground(imagesTable.getBackground());

        content.add(new JLabel("Wählen Sie hier die Bilder aus, aus denen die Foto-Collage erstellt werden soll."), new GridBagConstraints(0, 0, REMAINDER, 1, 1, 0, WEST, NONE, new Insets(2, 2, 2, 2), 2, 2));
        content.add(lbImageInfo, new GridBagConstraints(0, 2, 3, 1, 0, 0, WEST, HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        content.add(spImageTable, new GridBagConstraints(0, RELATIVE, REMAINDER, 1, 1, 1, CENTER, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
        content.add(lbInfo, new GridBagConstraints(0, RELATIVE, REMAINDER, 1, 1, 0, EAST, NONE, new Insets(2, 2, 2, 2), 0, 0));

        JButton btRemove = new JButton(new RemoveAction());
        JButton btAddImages = new JButton(new SelectImagesAction(this, gui));
        JButton btOpenOutputDialog = new JButton(new OpenOutputDialogAction());
        JPanel buttons = createButtonPanel(btRemove, btAddImages, btOpenOutputDialog);
        add(buttons, BorderLayout.SOUTH);
        imagesTableModel.addTableModelListener(event -> updateInfo());
        updateInfo();
    }

    private void updateInfo() {
        lbInfo.setText("Anzahl Bilder: %d (%,d Bytes)".formatted(imagesTableModel.getRowCount(), imagesTableModel.getFileSize()));
    }

    public void addImages(File[] files) {
        List<ImageRef> newImages = new ArrayList<>();

        for (File file : files) {
            if (file.isDirectory()) {
                int option = JOptionPane.showConfirmDialog(gui, "Wollen sie alle Bilder des Verzeichnisses '%s' hinzufügen?".formatted(file.toString()), "Alle hinzufügen?", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    try {
                        List<ImageRef> bilder = CollageUtil.ladeBilder(file.toPath());
                        newImages.addAll(bilder);
                    } catch (IOException ioe) {
                        ioe.printStackTrace(System.err); // TODO
                    }
                }
            } else {
                newImages.add(new ImageRef(file.toPath()));
            }
        }
        imagesTableModel.addAll(newImages);
        new Thread(new ImageMetadataLoader(newImages)).start();
    }

    public List<ImageRef> getImageList() {
        return imagesTableModel.getImageList();
    }

    class RemoveAction extends AbstractAction {

        RemoveAction() {
            putValue(NAME, "Entfernen");
            putValue(SMALL_ICON, Icons.REMOVE);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int[] selection = imagesTable.getSelectedRows();
            if (selection == null || selection.length == 0) {
                JOptionPane.showMessageDialog(ImagesPanel.this, "Sie haben keine Bilder zum Entfernen ausgewählt.", "Keine Auswahl", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    class OpenOutputDialogAction extends AbstractAction {

        public OpenOutputDialogAction() {
            putValue(NAME, "Collage erstellen");
            putValue(SMALL_ICON, OPTIONS);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            new OutputDialog(gui).setVisible(true);
        }
    }
}