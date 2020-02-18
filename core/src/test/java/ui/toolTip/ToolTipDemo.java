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
 */
package ui.toolTip;

import com.github.weisj.darklaf.components.alignment.AlignmentStrategy;
import com.github.weisj.darklaf.components.tooltip.ToolTipContext;
import com.github.weisj.darklaf.components.tooltip.ToolTipStyle;
import com.github.weisj.darklaf.components.tooltip.TooltipAwareButton;
import com.github.weisj.darklaf.util.Alignment;
import ui.ComponentDemo;
import ui.DemoPanel;

import javax.swing.*;
import java.awt.*;

public class ToolTipDemo implements ComponentDemo {

    public static void main(final String[] args) {
        ComponentDemo.showDemo(new ToolTipDemo());
    }

    @Override
    public JComponent createComponent() {
        TooltipAwareButton button = new TooltipAwareButton("Demo Button");
        DemoPanel panel = new DemoPanel(button);
        ToolTipContext context = button.getToolTipContext();
        button.setToolTipText("ToolTip demo text!");

        JPanel controlPanel = panel.getControls();
        controlPanel.setLayout(new GridLayout(4, 2));

        controlPanel.add(new JLabel());
        controlPanel.add(new JCheckBox("Align inside") {{
            setSelected(context.isAlignInside());
            addActionListener(e -> context.setAlignInside(isSelected()));
        }});
        controlPanel.add(new JLabel("Tooltip Style:", JLabel.RIGHT));
        controlPanel.add(new JComboBox<ToolTipStyle>(ToolTipStyle.values()) {{
            setSelectedItem(ToolTipStyle.BALLOON);
            addItemListener(e -> context.setToolTipStyle((ToolTipStyle) e.getItem()));
        }});
        controlPanel.add(new JLabel("Alignment:", JLabel.RIGHT));
        controlPanel.add(new JComboBox<Alignment>(Alignment.values()) {{
            setSelectedItem(context.getAlignment());
            addItemListener(e -> context.setAlignment((Alignment) e.getItem()));
        }});
        controlPanel.add(new JLabel("Center Alignment:", JLabel.RIGHT));
        controlPanel.add(new JComboBox<Alignment>(Alignment.values()) {{
            setSelectedItem(context.getCenterAlignment());
            addItemListener(e -> context.setCenterAlignment((Alignment) e.getItem()));
        }});
        controlPanel.add(new JLabel("Alignment Strategy:", JLabel.RIGHT));
        controlPanel.add(new JComboBox<AlignmentStrategy>(AlignmentStrategy.values()) {{
            setSelectedItem(context.getAlignmentStrategy());
            addItemListener(e -> context.setAlignmentStrategy((AlignmentStrategy) e.getItem()));
        }});
        return panel;
    }

    @Override
    public String getTitle() {
        return "ToolTip Demo";
    }
}