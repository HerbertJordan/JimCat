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
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;

import org.jimcat.gui.SwingClient;
import org.jimcat.gui.smartlisteditor.model.FilterTreeNode;
import org.jimcat.gui.smartlisteditor.model.ImportFilterNode;
import org.jimcat.model.filter.ImportFilter.Type;

/**
 * A small editor for import id filter nodes.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class ImportFilterEditor extends BaseNodeEditor {

	/**
	 * constants for type change
	 */
	private static final String AT_LEAST = "bigger or equal than";

	private static final String EXACTLY = "exactly";
	
	private static final String UP_TO = "smaller than";
	
	/**
	 * the editor component
	 */
	private JPanel editor;

	/**
	 * field to edit import id
	 */
	private JTextField importId;

	/**
	 * the currently edited node
	 */
	private ImportFilterNode currentNode;

	/**
	 * the type switch used to select filter type
	 */
	private JComboBox typeSwitch;
	
	/**
	 * default constructor
	 */
	public ImportFilterEditor() {
		initComponents();
	}

	/**
	 * build up gui components
	 */
	private void initComponents() {
		// the editor panel
		editor = new JPanel();
		editor.setOpaque(false);
		editor.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

		// the must / must not switch
		editor.add(getNegateComboBox());

		// label
		JLabel text = new JLabel(" have import id ");
		text.setOpaque(false);
		editor.add(text);
		
		// type switch
		typeSwitch = new JComboBox(new String[] { UP_TO, EXACTLY, AT_LEAST });
		typeSwitch.addActionListener(new TypeSwitchListener());
		editor.add(typeSwitch);

		// import id field
		importId = new JTextField(8);
		ImportFieldListener listener = new ImportFieldListener();
		importId.addActionListener(listener);
		importId.addFocusListener(listener);
		editor.add(importId);
	}

	/**
	 * prepaire and return editing component
	 * 
	 * @see org.jimcat.gui.smartlisteditor.editor.BaseNodeEditor#getEditor(javax.swing.JTree,
	 *      org.jimcat.gui.smartlisteditor.model.FilterTreeNode)
	 */
	@Override
	@SuppressWarnings("unused")
	public JComponent getEditor(JTree tree, FilterTreeNode node) {

		currentNode = (ImportFilterNode) node;
		importId.setText(Long.toString(currentNode.getImportId()));

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
			if (AT_LEAST.equals(sel)) {
				currentNode.setType(Type.AT_LEAST);
			} else if (EXACTLY.equals(sel)) {
				currentNode.setType(Type.EXACT);
			} else {
				currentNode.setType(Type.UP_TO);
			}
		}
	}
	
	/**
	 * reacto on import id field input
	 */
	private class ImportFieldListener extends FocusAdapter implements ActionListener {
		/**
		 * handle new input
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			handleChange((JTextField) e.getSource());
		}

		/**
		 * react on a lost focus
		 * 
		 * @see java.awt.event.FocusAdapter#focusLost(java.awt.event.FocusEvent)
		 */
		@Override
		public void focusLost(FocusEvent e) {
			handleChange((JTextField) e.getSource());
		}

		/**
		 * handle a value change
		 * 
		 * @param field
		 */
		private void handleChange(JTextField field) {
			// get privous values
			if (field != importId) {
				return;
			}

			// get old value
			long value = currentNode.getImportId();

			// get new value
			try {
				long newValue = Long.parseLong(field.getText());
				// value must be positive
				if (newValue < 0) {
					throw new NumberFormatException("dimension is negativ");
				}
				value = newValue;
			} catch (NumberFormatException nfe) {
				SwingClient.getInstance().showMessage("Import ID " + field.getText() + " not valid", "Illegal value",
				        JOptionPane.ERROR_MESSAGE);
			}

			// set new value
			field.setText(Long.toString(value));
			currentNode.setImportId(value);
		}
	}

}
