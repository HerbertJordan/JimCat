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
import javax.swing.UIManager;

import org.jdesktop.swingx.JXDatePicker;
import org.jimcat.gui.smartlisteditor.model.FilterTreeNode;
import org.jimcat.gui.smartlisteditor.model.PictureTakenFilterNode;
import org.jimcat.model.filter.metadata.PictureTakenFilter;
import org.joda.time.DateTime;

/**
 * An editor for a pricture taken Filter editor.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class PictureTakenFilterEditor extends BaseNodeEditor {

	/**
	 * constants for type change
	 */
	private static final String BEFORE = "before";

	private static final String AFTER = "after";

	/**
	 * the editor component
	 */
	private JPanel editor;

	/**
	 * the type switch used to select before / after type
	 */
	private JComboBox typeSwitch;

	/**
	 * field used to edit date
	 */
	private JXDatePicker date;

	/**
	 * the edited node
	 */
	private PictureTakenFilterNode currentNode;

	/**
	 * default constructor
	 */
	public PictureTakenFilterEditor() {
		initComponents();
	}

	/**
	 * build up editor
	 */
	private void initComponents() {
		// basic setup
		editor = new JPanel();
		editor.setOpaque(false);
		editor.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

		// text
		JLabel text = new JLabel("must be taken ");
		text.setOpaque(false);
		editor.add(text);

		// type switch
		typeSwitch = new JComboBox(new String[] { BEFORE, AFTER });
		typeSwitch.addActionListener(new TypeSwitchListener());
		editor.add(typeSwitch);

		// workaround for mac os JXDatepicker bug 
		UIManager.put(JXDatePicker.uiClassID,
		"org.jdesktop.swingx.plaf.basic.BasicDatePickerUI");
		// date picker
		date = new JXDatePicker();
		date.addActionListener(new DateListener());
		editor.add(date);
	}

	/**
	 * prepaire and return editor component
	 * 
	 * @see org.jimcat.gui.smartlisteditor.editor.BaseNodeEditor#getEditor(javax.swing.JTree,
	 *      org.jimcat.gui.smartlisteditor.model.FilterTreeNode)
	 */
	@Override
	@SuppressWarnings("unused")
	public JComponent getEditor(JTree tree, FilterTreeNode node) {

		currentNode = (PictureTakenFilterNode) node;

		// choose type
		if (PictureTakenFilter.Type.AFTER.equals(currentNode.getType())) {
			typeSwitch.setSelectedItem(AFTER);
		} else {
			typeSwitch.setSelectedItem(BEFORE);
		}

		// set date
		date.setDate(currentNode.getDate().toDate());

		return editor;
	}

	/**
	 * class reacting on change events of the type switch
	 */
	private class TypeSwitchListener implements ActionListener {
		/**
		 * react on a change event
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@SuppressWarnings("unused")
		public void actionPerformed(ActionEvent e) {
			String sel = (String) typeSwitch.getSelectedItem();
			if (BEFORE.equals(sel)) {
				currentNode.setType(PictureTakenFilter.Type.BEFORE);
			} else {
				currentNode.setType(PictureTakenFilter.Type.AFTER);
			}
		}
	}

	/**
	 * listen to the date picker => react on events
	 */
	private class DateListener implements ActionListener {
		/**
		 * react on a changed date
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@SuppressWarnings("unused")
		public void actionPerformed(ActionEvent e) {
			currentNode.setDate(new DateTime(date.getDate()));
		}
	}
}
