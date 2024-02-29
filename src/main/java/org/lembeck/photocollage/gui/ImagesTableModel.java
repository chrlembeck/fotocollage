package org.lembeck.photocollage.gui;

import org.lembeck.photocollage.ImageRef;
import org.lembeck.photocollage.gui.components.PreviewIcon;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.*;
import java.util.function.Predicate;

public class ImagesTableModel implements TableModel, ImageDataListener {

    private static final Icon WAIT_ICON;

    static {
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        image.getGraphics().setColor(Color.WHITE);
        image.getGraphics().fillRect(0, 0, 100, 100);
        WAIT_ICON = new PreviewIcon(80, image);
    }

    private final List<ImageRef> images = new ArrayList<>();

    private final List<TableModelListener> listeners = new ArrayList<>();

    @Override
    public int getRowCount() {
        return images.size();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return switch (columnIndex) {
            case 0 -> "Name";
            case 1 -> "Breite";
            case 2 -> "Höhe";
            case 3 -> "Vorschau";
            default -> throw new IndexOutOfBoundsException();
        };
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return switch (columnIndex) {
            case 0 -> String.class;
            case 1, 2 -> Integer.class;
            case 3 -> Icon.class;
            default -> throw new IndexOutOfBoundsException();
        };
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        final ImageRef image = images.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> image.getPath().toString();
            case 1 -> image.getWidth() == 0 ? null : image.getWidth();
            case 2 -> image.getHeight() == 0 ? null : image.getHeight();
            case 3 -> Optional.ofNullable(image.getPreview()).map(i -> (Icon) new PreviewIcon(80, i)).orElse(WAIT_ICON);
            default -> null;
        };
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    }

    @Override
    public void addTableModelListener(TableModelListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
        listeners.remove(l);
    }

    public void addAll(Collection<ImageRef> bilder) {
        int firstRowIdx = images.size();
        bilder.stream()
                .filter(Predicate.not(images::contains))
                .peek(b -> b.addImageDataListener(this))
                .forEach(images::add);
        if (images.size() > firstRowIdx) {
            for (int idx = listeners.size() - 1; idx >= 0; idx--) {
                listeners.get(idx).tableChanged(new TableModelEvent(this, firstRowIdx, images.size() - 1, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
            }
        }
    }

    @Override
    public void imageMetadataLoaded(ImageDataLoadedEvent event) {
        int idx = this.images.indexOf(event.getImage());
        if (idx >= 0) {
            listeners.forEach(l -> l.tableChanged(new TableModelEvent(this, idx, idx, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE)));
        }
    }

    @Override
    public void imagePreviewLoaded(ImageDataLoadedEvent event) {
        int idx = this.images.indexOf(event.getImage());
        if (idx >= 0) {
            listeners.forEach(l -> l.tableChanged(new TableModelEvent(this, idx, idx, 3, TableModelEvent.UPDATE)));
        }
    }

    public List<ImageRef> getImageList() {
        return Collections.unmodifiableList(images);
    }

    public long getFileSize() {
        return images.stream().mapToLong(ImageRef::getFileSize).sum();
    }

    public void removeAll(int[] selection) {
        Arrays.sort(selection);
        for (int idx = selection.length - 1; idx >= 0; idx--) {
            remove(selection[idx]);
        }
    }

    private void remove(int idx) {
        images.remove(idx);
        listeners.forEach(l -> l.tableChanged(new TableModelEvent(this, idx, idx, TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE)));
    }
}