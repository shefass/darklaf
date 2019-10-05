package com.weis.darklaf.ui.cell;

import com.weis.darklaf.components.SelectableTreeNode;
import com.weis.darklaf.decorators.CellRenderer;
import com.weis.darklaf.ui.tree.DarkTreeCellRenderer;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

/**
 * @author vincencopalazzo
 * @author atarw
 */
public class DarkCellRendererToggleButton<T extends JToggleButton & CellEditorToggleButton>
        implements TableCellRenderer, TreeCellRenderer, SwingConstants {

    private final T toggleButton;


    public DarkCellRendererToggleButton(@NotNull final T toggleButton) {
        this.toggleButton = toggleButton;
        toggleButton.setOpaque(false);
    }

    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object value,
                                                   final boolean isSelected, final boolean focus,
                                                   final int row, final int column) {
        if (value instanceof Boolean) {
            toggleButton.setSelected((Boolean) value);
        }
        toggleButton.setHorizontalAlignment(table.getComponentOrientation().isLeftToRight() ? LEFT : RIGHT);
        toggleButton.setHasFocus(focus);

        boolean alternativeRow = UIManager.getBoolean("Table.alternateRowColor");
        Color alternativeRowColor = UIManager.getColor("Table.alternateRowBackground");
        Color normalColor = UIManager.getColor("Table.background");
        if (alternativeRow) {
            if (!isSelected) {
                if (row % 2 == 1) {
                    toggleButton.setBackground(alternativeRowColor);
                } else {
                    toggleButton.setBackground(normalColor);
                }
                toggleButton.setForeground(table.getForeground());
            } else {
                toggleButton.setForeground(table.getSelectionForeground());
                toggleButton.setBackground(table.getSelectionBackground());
            }
        }
        return toggleButton;
    }

    @Override
    public Component getTreeCellRendererComponent(final JTree tree, final Object value, final boolean selected,
                                                  final boolean expanded, final boolean leaf, final int row,
                                                  final boolean focus) {
        if (value instanceof Boolean) {
            toggleButton.setSelected((Boolean) value);
        } else {
            boolean sel = Boolean.TRUE.equals(DarkTreeCellRenderer.unwrapBooleanIfPossible(value));
            toggleButton.setSelected(sel);
            if (value instanceof SelectableTreeNode) {
                toggleButton.setText(((SelectableTreeNode) value).getLabel());
            }
        }
        toggleButton.setHorizontalAlignment(tree.getComponentOrientation().isLeftToRight() ? LEFT : RIGHT);
        toggleButton.setHasFocus(false);

        return toggleButton;
    }

    public JToggleButton getButton() {
        return toggleButton;
    }

    public static class CellEditorCheckBox extends JCheckBox implements CellRenderer, CellEditorToggleButton {

        private boolean hasFocus;

        public void setHasFocus(final boolean hasFocus) {
            this.hasFocus = hasFocus;
        }

        @Override
        public boolean hasFocus() {
            return hasFocus || super.hasFocus();
        }

        @Override
        public boolean isFocusOwner() {
            return super.hasFocus();
        }
    }

    public static class CellEditorRadioButton extends JRadioButton implements CellRenderer, CellEditorToggleButton {

        private boolean hasFocus;

        public void setHasFocus(final boolean hasFocus) {
            this.hasFocus = hasFocus;
        }

        @Override
        public boolean hasFocus() {
            return hasFocus || super.hasFocus();
        }

        @Override
        public boolean isFocusOwner() {
            return super.hasFocus();
        }
    }

}