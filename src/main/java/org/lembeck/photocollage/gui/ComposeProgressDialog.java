package org.lembeck.photocollage.gui;

import org.lembeck.photocollage.CollageSettings;
import org.lembeck.photocollage.ImageRef;
import org.lembeck.photocollage.event.ComposeFinishedEvent;
import org.lembeck.photocollage.event.ComposeStartedEvent;
import org.lembeck.photocollage.event.NewRatioFoundEvent;
import org.lembeck.photocollage.event.StartAttemptEvent;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static java.awt.Dialog.ModalityType.APPLICATION_MODAL;
import static java.lang.Integer.parseInt;
import static org.lembeck.photocollage.gui.GuiUtil.createButtonPanel;
import static org.lembeck.photocollage.gui.GuiUtil.setApplicationIcon;

public class ComposeProgressDialog extends JDialog {

    private final ComposeWorker worker;

    private JProgressBar pbAttempts;

    private JProgressBar pbPartitions;

    private JProgressBar pbImages;

    public static Optional<BufferedImage> compose(OutputDialog outputDialog, List<ImageRef> images) {
        final var dialog = new ComposeProgressDialog(outputDialog, images);

        dialog.startWorking();
        dialog.setVisible(true);
        try {
            return dialog.worker.isCancelled()
                    ? Optional.empty()
                    : Optional.of(dialog.worker.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace(System.err);
            return Optional.empty();
        }
    }

    private void startWorking() {
        worker.execute();
    }

    private ComposeProgressDialog(OutputDialog outputDialog, List<ImageRef> images) {
        super(outputDialog, "Bildberechnung", APPLICATION_MODAL);
        setApplicationIcon(this);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        CollageSettings settings = new CollageSettings(parseInt(outputDialog.getImageWidth()),
                parseInt(outputDialog.getImageHeight()),
                parseInt(outputDialog.getGap()),
                parseInt(outputDialog.getBorderSize()),
                outputDialog.getSelectedBackgroundColor());
        this.worker = createWorker(settings, images);
        init(images.size());
        pack();
        setLocationRelativeTo(outputDialog);
    }

    private ComposeWorker createWorker(CollageSettings settings, List<ImageRef> images) {
        final ComposeWorker worker = new ComposeWorker(this, settings, images);
        worker.addPropertyChangeListener(event -> {
            if ("state".equals(event.getPropertyName()) && SwingWorker.StateValue.DONE == event.getNewValue()) {
                setVisible(false);
                dispose();
            }
        });
        return worker;
    }

    private void init(int totalImageCount) {
        setLayout(new BorderLayout());
        JButton btCancel = new JButton(new CancelAction());
        JPanel buttonPanel = createButtonPanel(btCancel);
        JPanel contentPanel = new JPanel();
        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        contentPanel.setLayout(new GridBagLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));


        pbPartitions = new JProgressBar(JProgressBar.HORIZONTAL);
        pbAttempts = new JProgressBar(JProgressBar.HORIZONTAL);
        pbImages = new JProgressBar(JProgressBar.HORIZONTAL);
        pbImages.setMaximum(totalImageCount);

        contentPanel.add(pbPartitions, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        contentPanel.add(pbAttempts, new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        contentPanel.add(pbImages, new GridBagConstraints(1, 2, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    }

    public void composeStarted(ComposeStartedEvent cse) {
        pbPartitions.setMaximum(cse.getTotalPartCount());
    }

    public void composeFinished(ComposeFinishedEvent cfe) {
        pbPartitions.setValue(cfe.getPartIdx() + 1);
        pbAttempts.setValue(pbAttempts.getMaximum());
    }


    public void attemptStarted(StartAttemptEvent sae) {
        if (sae.getAttemptIndex() == 0) {
            pbAttempts.setMaximum(sae.getMaxAttempts());
        }
        pbAttempts.setValue(sae.getAttemptIndex());
    }

    public void newRatioFound(NewRatioFoundEvent nrfe) {
        System.out.println(nrfe.getBestRatioDiff());
    }

    public void imagePainted() {
        pbImages.setValue(pbImages.getValue() + 1);
    }

    class CancelAction extends AbstractAction {

        CancelAction() {
            putValue(AbstractAction.NAME, "Abbrechen");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            worker.cancel(true);
            setVisible(false);
            dispose();
        }
    }
}