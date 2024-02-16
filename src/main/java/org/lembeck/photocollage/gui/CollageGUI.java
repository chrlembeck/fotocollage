package org.lembeck.photocollage.gui;

import org.lembeck.photocollage.ImageRef;
import javax.swing.*;
import java.awt.*;
import java.util.List;

import static org.lembeck.photocollage.gui.Icons.*;

public class CollageGUI extends JFrame implements Runnable {

    private ImagesPanel imagesPanel;

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        SwingUtilities.invokeLater(new CollageGUI());
    }

    public CollageGUI() {
        super("Foto-Collage");
    }

    @Override
    public void run() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(imagesPanel = new ImagesPanel(this), BorderLayout.CENTER);

        setIconImages(List.of(ICON_16, ICON_24, ICON_32));

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public List<ImageRef> getImageList() {
        return imagesPanel.getImageList();
    }
}