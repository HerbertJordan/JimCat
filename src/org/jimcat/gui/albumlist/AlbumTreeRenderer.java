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

package org.jimcat.gui.albumlist;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * used to presentate a node within the AlbumTree component.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class AlbumTreeRenderer extends DefaultTreeCellRenderer {

	/**
	 * the actual rendering component
	 */
	private JComponent renderer;

	/**
	 * the field for the icon
	 */
	private JLabel icon;

	/**
	 * the field for the titel
	 */
	private JLabel titel;

	/**
	 * default constructor
	 */
	public AlbumTreeRenderer() {
		// init component
		initComponent();
	}

	/**
	 * build up renderer element
	 */
	private void initComponent() {
		renderer = new JPanel();
		renderer.setOpaque(false);
		renderer.setLayout(new BorderLayout());

		icon = new JLabel();
		icon.setOpaque(false);
		icon.setText("");
		renderer.add(icon, BorderLayout.WEST);

		titel = new JLabel();
		titel.setOpaque(false);
		titel.setText("");
		titel.setBorder(new EmptyBorder(0, 5, 0, 0));
		renderer.add(titel, BorderLayout.CENTER);
	}

	/**
	 * prepaire component for display
	 * 
	 * @see javax.swing.tree.TreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree,
	 *      java.lang.Object, boolean, boolean, boolean, int, boolean)
	 */
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean isSelected, boolean expanded,
	        boolean leaf, int row, boolean focus) {
		if (value instanceof AlbumTreeNode) {
			// extract node
			AlbumTreeNode node = (AlbumTreeNode) value;
			// set titel
			titel.setText(node.getTitel());
			// set icon
			icon.setIcon(node.getIcon());
			// return renderer
			return renderer;
		}
		return super.getTreeCellRendererComponent(tree, value, isSelected, expanded, leaf, row, focus);
	}

}
