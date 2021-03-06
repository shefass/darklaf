/*
 * MIT License
 *
 * Copyright (c) 2020 Jannis Weis
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */
package com.github.weisj.darklaf.ui.list;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;

import com.github.weisj.darklaf.ui.cell.CellConstants;
import com.github.weisj.darklaf.ui.cell.CellUtil;
import com.github.weisj.darklaf.ui.cell.DarkCellRendererPane;
import com.github.weisj.darklaf.util.DarkUIUtil;
import com.github.weisj.darklaf.util.PropertyUtil;

/**
 * @author Jannis Weis
 */
public class DarkListUI extends DarkListUIBridge implements CellConstants {

    protected static final String KEY_PREFIX = "JList.";
    public static final String KEY_IS_COMBO_LIST = KEY_PREFIX + ".isComboList";
    public static final String KEY_ALTERNATE_ROW_COLOR = KEY_PREFIX + "alternateRowColor";
    public static final String KEY_RENDER_BOOLEAN_AS_CHECKBOX = KEY_PREFIX + "renderBooleanAsCheckBox";
    public static final String KEY_BOOLEAN_RENDER_TYPE = KEY_PREFIX + "booleanRenderType";
    public static final String KEY_SHRINK_WRAP = KEY_PREFIX + "shrinkWrap";
    public static final String KEY_FULL_ROW_SELECTION = KEY_PREFIX + "fullRowSelection";
    public static final String KEY_IS_EDITING = KEY_PREFIX + "isEditing";
    public static final String KEY_IS_LIST_EDITOR = "JComponent.listCellEditor";

    protected DarkListCellRendererDelegate rendererDelegate;

    public static ComponentUI createUI(final JComponent list) {
        return new DarkListUI();
    }

    @Override
    public void installUI(final JComponent c) {
        super.installUI(c);
        c.remove(rendererPane);
        rendererPane = createCellRendererPane();
        c.add(rendererPane);
    }

    @Override
    protected void installDefaults() {
        super.installDefaults();
        rendererDelegate = new DarkListCellRendererDelegate();
        PropertyUtil.installBooleanProperty(list, KEY_ALTERNATE_ROW_COLOR, "List.alternateRowColor");
    }

    protected CellRendererPane createCellRendererPane() {
        return new DarkCellRendererPane();
    }

    @Override
    protected Handler getHandler() {
        if (handler == null) {
            handler = new DarkHandler();
        }
        return handler;
    }

    protected void paintImpl(final Graphics g, final JComponent c) {
        switch (layoutOrientation) {
            case JList.VERTICAL_WRAP :
                if (list.getHeight() != listHeight) {
                    updateLayoutStateNeeded |= heightChanged;
                    redrawList();
                }
                break;
            case JList.HORIZONTAL_WRAP :
                if (list.getWidth() != listWidth) {
                    updateLayoutStateNeeded |= widthChanged;
                    redrawList();
                }
                break;
            default :
                break;
        }
        maybeUpdateLayoutState();

        ListCellRenderer<Object> renderer = getCellRenderer(list);
        ListModel<Object> dataModel = list.getModel();
        ListSelectionModel selModel = list.getSelectionModel();

        if ((renderer == null) || dataModel.getSize() == 0) {
            return;
        }

        // Determine how many columns we need to paint
        Rectangle paintBounds = g.getClipBounds();

        int startColumn, endColumn;
        if (c.getComponentOrientation().isLeftToRight()) {
            startColumn = convertLocationToColumn(paintBounds.x, paintBounds.y);
            endColumn = convertLocationToColumn(paintBounds.x + paintBounds.width, paintBounds.y);
        } else {
            startColumn = convertLocationToColumn(paintBounds.x + paintBounds.width, paintBounds.y);
            endColumn = convertLocationToColumn(paintBounds.x, paintBounds.y);
        }
        int maxY = paintBounds.y + paintBounds.height;
        int maxX = paintBounds.x + paintBounds.width;
        int leadIndex = adjustIndex(list.getLeadSelectionIndex(), list);
        int rowIncrement = (layoutOrientation == JList.HORIZONTAL_WRAP) ? columnCount : 1;

        Rectangle rowBounds;
        for (int colCounter = startColumn; colCounter <= endColumn; colCounter++) {
            int row = convertLocationToRowInColumn(paintBounds.y, colCounter);
            int rowCount = Math.max(rowsPerColumn, getRowCount(colCounter));
            int index = getModelIndex(colCounter, row);
            rowBounds = getCellBounds(list, index);

            if (rowBounds == null) {
                // Not valid, bail!
                return;
            }
            boolean lastColumn = colCounter == endColumn
                                 && getColumnCount() > 1
                                 && colCounter * rowsPerColumn + rowCount >= dataModel.getSize();
            int bgWidth = lastColumn ? maxX - rowBounds.x : 0;
            int maxRow = lastColumn ? Integer.MAX_VALUE : rowCount;
            while (row < maxRow && rowBounds.y < maxY) {
                rowBounds.height = getHeight(colCounter, row);
                if (rowBounds.height <= 0) {
                    rowBounds.height = colCounter >= 1 ? getHeight(colCounter - 1, row) : getRowHeight(row);
                }
                g.setClip(rowBounds.x, rowBounds.y, bgWidth > 0 ? bgWidth : rowBounds.width, rowBounds.height);
                g.clipRect(paintBounds.x, paintBounds.y, paintBounds.width, paintBounds.height);
                paintCell(g, index, rowBounds, renderer, dataModel, selModel, leadIndex, row, bgWidth);
                rowBounds.y += rowBounds.height;
                index += rowIncrement;
                row++;
            }
        }
        // Empty out the renderer pane, allowing renderers to be gc'ed.
        rendererPane.removeAll();
    }

    protected ListCellRenderer<Object> getCellRenderer(final JList<Object> list) {
        ListCellRenderer<Object> renderer = list.getCellRenderer();
        rendererDelegate.setDelegate(renderer);
        return rendererDelegate;
    }

    protected void paintCell(final Graphics g, final int index, final Rectangle rowBounds,
                             final ListCellRenderer<Object> cellRenderer,
                             final ListModel<Object> dataModel,
                             final ListSelectionModel selModel, final int leadIndex, final int row,
                             final int bgWidth) {
        boolean empty = index >= list.getModel().getSize();
        Object value = empty ? null : dataModel.getElementAt(index);
        boolean cellHasFocus = list.hasFocus() && (index == leadIndex);
        boolean isSelected = selModel.isSelectedIndex(index);

        int cx = rowBounds.x;
        int cy = rowBounds.y;
        int cw = rowBounds.width;
        int ch = rowBounds.height;

        if (empty) {
            g.setColor(CellUtil.getListBackground(list, list, false, row));
            // g.fillRect(cx, cy, bgWidth > 0 ? bgWidth : cw, ch);
        } else {
            Component rendererComponent = cellRenderer.getListCellRendererComponent(list, value, index, isSelected,
                                                                                    cellHasFocus);
            if (PropertyUtil.getBooleanProperty(list, KEY_SHRINK_WRAP)) {
                // Shrink renderer to preferred size. This is mostly used on Windows
                // where selection is only shown around the file name, instead of
                // across the whole list cell.
                int w = Math.min(cw, rendererComponent.getPreferredSize().width + 4);
                if (!list.getComponentOrientation().isLeftToRight()) {
                    cx += (cw - w);
                }
                cw = w;
            }
            rendererPane.paintComponent(g, rendererComponent, list, cx, cy, cw, ch, true);
        }
    }

    @Override
    public int getRowCount(final int column) {
        return super.getRowCount(column);
    }

    public int getColumnCount() {
        return columnCount;
    }

    protected class DarkHandler extends Handler {

        @Override
        public void propertyChange(final PropertyChangeEvent e) {
            super.propertyChange(e);
            String key = e.getPropertyName();
            if (KEY_ALTERNATE_ROW_COLOR.equals(key)) {
                list.repaint();
            }
        }

        @Override
        protected void adjustSelection(final MouseEvent e) {
            int row = list.locationToIndex(e.getPoint());
            if (row < 0) {
                // If shift is down in multi-select, we should do nothing.
                // For single select or non-shift-click, clear the selection
                if (isFileList && !PropertyUtil.getBooleanProperty(list, KEY_FULL_ROW_SELECTION)
                    && e.getID() == MouseEvent.MOUSE_PRESSED &&
                    (!e.isShiftDown() || list.getSelectionMode() == ListSelectionModel.SINGLE_SELECTION)) {
                    list.clearSelection();
                }
            } else {
                int anchorIndex = adjustIndex(list.getAnchorSelectionIndex(), list);
                boolean anchorSelected;
                if (anchorIndex == -1) {
                    anchorIndex = 0;
                    anchorSelected = false;
                } else {
                    anchorSelected = list.isSelectedIndex(anchorIndex);
                }

                if (DarkUIUtil.isMenuShortcutKeyDown(e)) {
                    if (e.isShiftDown()) {
                        if (anchorSelected) {
                            list.addSelectionInterval(anchorIndex, row);
                        } else {
                            list.removeSelectionInterval(anchorIndex, row);
                            if (isFileList) {
                                list.addSelectionInterval(row, row);
                                list.getSelectionModel().setAnchorSelectionIndex(anchorIndex);
                            }
                        }
                    } else if (list.isSelectedIndex(row)) {
                        list.removeSelectionInterval(row, row);
                    } else {
                        list.addSelectionInterval(row, row);
                    }
                } else if (e.isShiftDown()) {
                    list.setSelectionInterval(anchorIndex, row);
                } else {
                    list.setSelectionInterval(row, row);
                }
            }
        }
    }
}
