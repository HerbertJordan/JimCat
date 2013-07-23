/*
 *  This file is part of JimCat.
 *
 *  JimCat is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation version 2.
 *
 *  JimCat is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with JimCat; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.jimcat.gui.tagtree;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.jimcat.model.tag.Tag;
import org.jimcat.model.tag.TagGroup;


/**
 * A Renderer Class to draw a TagTree Node.
 * 
 * $Id: CheckBoxTreeCellRenderer.java 329 2007-04-18 13:01:15Z 07g1t1u1 $
 * 
 * @author Herbert
 */
public class CheckBoxTreeCellRenderer extends DefaultTreeCellRenderer {

	/**
	 * the component representing the actual renderer
	 */
	private JComponent renderer = null;

	/**
	 * the checkbox within the renderer
	 */
	private JCheckBox checkBox = null;

	/**
	 * the label within the renderer
	 */
	private JLabel label = null;

	/**
	 * font of a normal tag
	 */
	private Font tagFont;

	/**
	 * font of a taggroup
	 */
	private Font tagGroupFont;

	/**
	 * default constructor
	 */
	public CheckBoxTreeCellRenderer() {
		renderer = new JPanel();
		renderer.setLayout(new BorderLayout());
		renderer.setOpaque(false);

		checkBox = new JCheckBox();
		checkBox.setOpaque(false);
		checkBox.setText("");
		renderer.add(checkBox, BorderLayout.WEST);

		label = new JLabel();
		label.setOpaque(false);
		renderer.add(label, BorderLayout.CENTER);

		tagFont = label.getFont();
		tagGroupFont = tagFont.deriveFont(Font.BOLD);
	}

	/**
	 * overriden to provide own cell renderer
	 * 
	 * @see javax.swing.tree.DefaultTreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree,
	 *      java.lang.Object, boolean, boolean, boolean, int, boolean)
	 */
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
	        boolean leaf, int row, boolean focus) {
		if (value instanceof TagTreeNode) {
			TagTreeNode node = (TagTreeNode) value;

			// Select drawing style
			switch (node.getState()) {
			case SET: {
				checkBox.setEnabled(true);
				checkBox.setSelected(true);
				break;
			}
			case UNSET: {
				checkBox.setEnabled(true);
				checkBox.setSelected(false);
				break;
			}
			case PARTITAL: {
				checkBox.setEnabled(false);
				checkBox.setSelected(true);
				break;
			}
			}

			TagGroup tag = node.getTag();
			label.setText(" " + tag.getName());

			if (tag instanceof Tag) {
				label.setFont(tagFont);
			} else {
				label.setFont(tagGroupFont);
			}

			return renderer;
		}
		return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, focus);
	}

}
