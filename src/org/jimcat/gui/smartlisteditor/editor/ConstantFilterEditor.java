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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;

import org.jimcat.gui.smartlisteditor.model.ConstantFilterNode;
import org.jimcat.gui.smartlisteditor.model.FilterTreeNode;

/**
 * An editor for a constant filter node
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class ConstantFilterEditor extends BaseNodeEditor {

	/**
	 * options for typeChooser
	 */
	private static final String TRUE = "true";

	private static final String FALSE = "false";

	/**
	 * the editor component
	 */
	private JPanel editor;

	/**
	 * the currently edited constant filter node
	 */
	private ConstantFilterNode currentNode;

	/**
	 * used to change constant value
	 */
	private JComboBox typeChooser;

	/**
	 * default constructor
	 */
	public ConstantFilterEditor() {
		initComponents();
	}

	/**
	 * build up editing component
	 */
	private void initComponents() {
		// build up editor
		editor = new JPanel();
		editor.setOpaque(false);
		editor.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

		// add text
		JLabel constant = new JLabel("constant ");
		constant.setOpaque(false);
		editor.add(constant);

		// type chooser
		typeChooser = new JComboBox(new String[] { TRUE, FALSE });
		typeChooser.addActionListener(new TypeListener());
		editor.add(typeChooser);
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
		currentNode = (ConstantFilterNode) node;

		// select option
		if (currentNode.isNegate()) {
			typeChooser.setSelectedItem(FALSE);
		} else {
			typeChooser.setSelectedItem(TRUE);
		}

		return editor;
	}

	/**
	 * small class listening to type Chooser
	 */
	private class TypeListener implements ActionListener {
		/**
		 * react on a change
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@SuppressWarnings("unused")
		public void actionPerformed(ActionEvent e) {
			String sel = (String) typeChooser.getSelectedItem();
			currentNode.setNegate(FALSE.equals(sel));
		}
	}

}
