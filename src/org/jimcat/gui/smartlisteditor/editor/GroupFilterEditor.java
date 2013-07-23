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

import org.jimcat.gui.smartlisteditor.model.FilterTreeNode;
import org.jimcat.gui.smartlisteditor.model.GroupFilterTreeNode;
import org.jimcat.gui.smartlisteditor.model.GroupFilterTreeNode.Type;

/**
 * This type of editor allowes to edit a GroupFilterNode.
 * 
 * It displayes a list to choose between an ANY or ALL link.
 * 
 * It will also provied a hook to negate filter.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class GroupFilterEditor extends BaseNodeEditor {

	/**
	 * string constants for type switch
	 */
	private static final String ALL = "ALL";

	private static final String ANY = "ANY";

	/**
	 * the editor components
	 */
	private JPanel editor;

	/**
	 * the any/all switch
	 */
	private JComboBox typeSwitch;

	/**
	 * the currently edited node
	 */
	private GroupFilterTreeNode currentNode;

	/**
	 * default constructor
	 */
	public GroupFilterEditor() {
		// build up editor
		initComponents();
	}

	/**
	 * build up editor components
	 */
	private void initComponents() {
		// general setup
		editor = new JPanel();
		editor.setOpaque(false);
		editor.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

		// add negate item - managed by BaseNodeEditor
		JComboBox negate = getNegateComboBox();
		negate.addActionListener(new NegationListener());
		editor.add(negate);

		// add label
		JLabel match = new JLabel(" match ");
		match.setOpaque(false);
		editor.add(match);

		// add ANY/ALL chooser
		typeSwitch = new JComboBox(new String[] { ALL, ANY });
		typeSwitch.addActionListener(new TypeSwitchActionListener());
		editor.add(typeSwitch);

		// add label
		JLabel of = new JLabel(" of ... ");
		of.setOpaque(false);
		editor.add(of);
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
		// save current editing node
		currentNode = (GroupFilterTreeNode) node;

		// select ANY/ALL state
		updateSelection();

		return editor;
	}

	/**
	 * update current selection to match current setup
	 */
	private void updateSelection() {
		// update negation
		updateNegateItems();

		// update typeSwitch
		String selection = ANY;
		if (currentNode.isNegate()) {
			if (currentNode.getType() == Type.ALL) {
				selection = ANY;
			} else {
				selection = ALL;
			}
		} else {
			if (currentNode.getType() == Type.ALL) {
				selection = ALL;
			} else {
				selection = ANY;
			}
		}

		typeSwitch.setSelectedItem(selection);
	}

	/**
	 * small class listening to type switch changes
	 * 
	 * $Id$
	 * 
	 * @author Herbert
	 */
	private class TypeSwitchActionListener implements ActionListener {
		/**
		 * react on a type switch
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@SuppressWarnings("unused")
		public void actionPerformed(ActionEvent e) {
			Type type = Type.ALL;
			if (typeSwitch.getSelectedItem().equals(ANY)) {
				type = Type.ANY;
			}

			// keep negation in mind (revert)
			if (currentNode.isNegate()) {
				if (type == Type.ALL) {
					type = Type.ANY;
				} else {
					type = Type.ALL;
				}
			}

			// update node
			currentNode.setType(type);
		}
	}

	/**
	 * this class is responsible to exchange current selection within type
	 * selection if negation is exchanged.
	 * 
	 * $Id$
	 * 
	 * @author Herbert
	 */
	private class NegationListener implements ActionListener {
		/**
		 * update selection
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@SuppressWarnings("unused")
		public void actionPerformed(ActionEvent e) {
			// update neagte state
			if (currentNode != null) {
				currentNode.setNegate(getNegateComboBox().getSelectedItem().equals(MUST_NOT));

				// update current selection
				updateSelection();
			}

		}
	}
}
