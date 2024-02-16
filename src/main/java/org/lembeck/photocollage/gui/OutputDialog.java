package org.lembeck.photocollage.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import static org.lembeck.photocollage.gui.GuiUtil.*;

public class OutputDialog extends JDialog {

    public static final String IMAGE_WIDTH_KEY = "image-width";
    public static final String IMAGE_HEIGHT_KEY = "image-height";
    public static final String BORDER_SIZE_KEY = "border-size";
    public static final String GAP_KEY = "gap";
    public static final String BACKGROUND_COLOR_KEY = "background-color";
    private JTextField tfBreite;

    private JTextField tfHoehe;

    private JTextField tfGap;

    private JTextField tfBorderSize;

    private JButton btBackgroundColor;

    private ColorIcon backgroundColorIcon;

    private final CollageGUI gui;

    public OutputDialog(CollageGUI gui) {
        super(gui, "Ausgabeoptionen", ModalityType.MODELESS);
        this.gui = gui;
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setApplicationIcon(this);

        init();
        pack();
        setLocationRelativeTo(gui);
    }

    private void init() {
        setLayout(new BorderLayout());
        JPanel content = new JPanel();
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        content.setLayout(new GridBagLayout());
        JLabel lbBreite = new JLabel("Bildbreite");
        JLabel lbHoehe = new JLabel("Bildhöhe");
        JLabel lbGap = new JLabel("Abstand");
        JLabel lbBorderSize = new JLabel("Rahmenbreite");
        JLabel lbBackgroundColor = new JLabel("Hintergrundfarbe");
        tfBreite = new JTextField("1920", 8);
        tfHoehe = new JTextField("1080", 8);
        tfGap = new JTextField("5", 5);
        tfBorderSize = new JTextField("10", 5);
        btBackgroundColor = createBackgroundColorButton();
        JButton btCancel = new JButton(new CancelAction());
        JButton btCompose = new JButton(new ComposeAction(this));
        lbBreite.setLabelFor(tfBreite);
        lbHoehe.setLabelFor(tfHoehe);

        content.add(new JLabel("Geben Sie hier die Parameter zur Erstellung der Collage ein."), new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
        content.add(lbBreite, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 2, 2), 0, 0));
        content.add(tfBreite, new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 2, 2, 10), 0, 0));
        content.add(lbHoehe, new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 10, 2, 2), 0, 0));
        content.add(tfHoehe, new GridBagConstraints(1, 2, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 10), 0, 0));
        content.add(lbGap, new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 10, 2, 2), 0, 0));
        content.add(tfGap, new GridBagConstraints(1, 3, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 10), 0, 0));
        content.add(lbBorderSize, new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 10, 2, 2), 0, 0));
        content.add(tfBorderSize, new GridBagConstraints(1, 4, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 10), 0, 0));
        content.add(lbBackgroundColor, new GridBagConstraints(0, 5, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 10, 2, 2), 0, 0));
        content.add(btBackgroundColor, new GridBagConstraints(1, 5, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 10), 0, 0));
        JPanel buttonPanel = createButtonPanel(btCancel, btCompose);

        add(content, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        initValues();
    }

    private void initValues() {
        tfBreite.setText(Integer.toString(loadInt(IMAGE_WIDTH_KEY, 1920)));
        tfHoehe.setText(Integer.toString(loadInt(IMAGE_HEIGHT_KEY, 1080)));
        tfBorderSize.setText(Integer.toString(loadInt(BORDER_SIZE_KEY, 10)));
        tfGap.setText(Integer.toString(loadInt(GAP_KEY, 5)));
        backgroundColorIcon.setColor(new Color(loadInt(BACKGROUND_COLOR_KEY, 0xffffff)));
        btBackgroundColor.repaint();
    }

    private JButton createBackgroundColorButton() {
        backgroundColorIcon = new ColorIcon(16, 16, Color.BLACK, Color.WHITE);
        JButton button = new JButton(new SelectColorAction(backgroundColorIcon));
        button.setMargin(new Insets(2, 2, 2, 2));
        return button;
    }

    public CollageGUI getGui() {
        return gui;
    }

    public String getImageWidth() {
        return tfBreite.getText().trim();
    }

    public String getImageHeight() {
        return tfHoehe.getText().trim();
    }

    public String getGap() {
        return tfGap.getText().trim();
    }

    public String getBorderSize() {
        return tfBorderSize.getText().trim();
    }

    public Color getSelectedBackgroundColor() {
        return backgroundColorIcon.getColor();
    }

    class SelectColorAction extends AbstractAction {

        SelectColorAction(Icon icon) {
            putValue(SMALL_ICON, icon);
            putValue(AbstractAction.SHORT_DESCRIPTION, "Legt die Rahmen- und Zwischenraumfarbe der Collage fest.");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Color newColor = JColorChooser.showDialog(btBackgroundColor, "Hintergrundfarbe wählen", backgroundColorIcon.getColor(), false);
            if (newColor != null) {
                backgroundColorIcon.setColor(newColor);
                btBackgroundColor.repaint();
            }
        }
    }

    private class CancelAction extends AbstractAction {

        CancelAction() {
            putValue(NAME, "Abbrechen");
            putValue(SMALL_ICON, Icons.CANCEL);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            dispose();
        }
    }
}
