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
package com.github.weisj.darklaf.ui.cell.hint;

import java.awt.*;

import javax.swing.*;

public interface IndexedCellContainer<T extends JComponent, I> extends CellContainer<T> {

    default Rectangle getCellBoundsAt(final I position) {
        return getCellBoundsAt(position, false);
    }

    Rectangle getCellBoundsAt(final I position, final boolean isEditing);

    I getCellPosition(final Point p);

    Color getBackgroundAt(final I position, final Component renderer);

    default Component getEffectiveCellRendererComponent(final I position, final boolean isEditing) {
        if (isEditing) {
            return getCellEditorComponent(position);
        } else {
            return getCellRendererComponent(position);
        }
    }

    boolean isEditingCell(final I position);

    Component getCellRendererComponent(final I position);

    Component getCellEditorComponent(final I position);
}
