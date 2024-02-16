package org.lembeck.photocollage.gui;

import org.lembeck.photocollage.ImageRef;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Optional;

import static org.lembeck.photocollage.gui.Icons.WIZARD;

public class ComposeAction extends AbstractAction {
    private final OutputDialog outputDialog;

    public ComposeAction(OutputDialog outputDialog) {
        this.outputDialog = outputDialog;
        putValue(NAME, "Starte Berechnung");
        putValue(SMALL_ICON, WIZARD);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (inputValidation()) {
            Optional<BufferedImage> image = ComposeProgressDialog.compose(outputDialog, outputDialog.getGui().getImageList());
            image.ifPresent(i -> new PreviewDialog(outputDialog.getGui(), i).setVisible(true));
        }
    }

    private boolean inputValidation() {
        String width = outputDialog.getImageWidth();
        String height = outputDialog.getImageHeight();
        String gap = outputDialog.getGap();
        String borderSize = outputDialog.getBorderSize();
        List<ImageRef> images = outputDialog.getGui().getImageList();
        return checkAndAlert(width.matches("[1-9][0-9]*"), "Die Eingabe der Breite ist ungültig.")
                && checkAndAlert(height.matches("[1-9][0-9]*"), "Die Eingabe der Höhe ist ungültig.")
                && checkAndAlert(!images.isEmpty(), "Es wurden keine Bilder für die Verarbeitung ausgewählt.")
                && checkAndAlert(gap.matches("[1-9][0-9]*"), "Der Abstand zwischen den Bildern muss ein Zahl sein.")
                && checkAndAlert(borderSize.matches("[1-9][0-9]*"), "Die Rahmenbreite ist ungültig.")
                ;
    }

    private boolean checkAndAlert(boolean check, String message) {
        if (!check) {
            JOptionPane.showMessageDialog(outputDialog.getGui(), message, "Achtung", JOptionPane.WARNING_MESSAGE);
        }
        return check;
    }
}