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

package org.jimcat.gui.smartlisteditor.editor;

import java.awt.FlowLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;

import org.jimcat.gui.smartlisteditor.model.FilterTreeNode;

/**
 * A simple editor to edit has tags filter nodes.
 * 
 * it just allowes to switch between must and must not
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class HasTagsFilterEditor extends BaseNodeEditor {

	/**
	 * the editor component
	 */
	private JPanel editor;

	/**
	 * defautl constructor
	 */
	public HasTagsFilterEditor() {
		initComponents();
	}

	/**
	 * build up editor
	 */
	private void initComponents() {
		// setup editor
		editor = new JPanel();
		editor.setOpaque(false);
		editor.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

		// add negate change list
		editor.add(getNegateComboBox());

		JLabel text = new JLabel(" have any tags");
		text.setOpaque(false);
		editor.add(text);
	}

	/**
	 * prepaire and return editor
	 * 
	 * @see org.jimcat.gui.smartlisteditor.editor.BaseNodeEditor#getEditor(javax.swing.JTree,
	 *      org.jimcat.gui.smartlisteditor.model.FilterTreeNode)
	 */
	@Override
	@SuppressWarnings("unused")
	public JComponent getEditor(JTree tree, FilterTreeNode node) {
		// accept current node
		return editor;
	}

}
