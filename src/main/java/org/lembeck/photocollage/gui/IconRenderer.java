package org.lembeck.photocollage.gui;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

import static javax.swing.SwingConstants.CENTER;

public class IconRenderer implements TableCellRenderer {

    private final JLabel label = new JLabel((String) null);

    public IconRenderer() {
        label.setHorizontalAlignment(CENTER);
        label.setHorizontalTextPosition(CENTER);
        label.setVerticalAlignment(CENTER);
        label.setVerticalTextPosition(CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
        Icon icon = (Icon) value;
        label.setIcon(icon);
        return label;
    }

}
