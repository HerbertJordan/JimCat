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

import static org.jimcat.model.filter.metadata.FileSizeFilter.Type.BIGGER_THEN;
import static org.jimcat.model.filter.metadata.FileSizeFilter.Type.SMALLER_THEN;

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
import org.jimcat.gui.smartlisteditor.model.FileSizeFilterNode;
import org.jimcat.gui.smartlisteditor.model.FilterTreeNode;

/**
 * An editor for ByteSize filter.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class FileSizeFilterEditor extends BaseNodeEditor {

	/*
	 * constants for type switch
	 */
	private static final String BIGGER = "bigger";

	private static final String SMALLER = "smaller";

	/**
	 * the editor component
	 */
	private JPanel editor;

	/**
	 * used to switch between bigger / smaller
	 */
	private JComboBox typeSwitch;

	/**
	 * the JTextField to edit size
	 */
	private JTextField size;

	/**
	 * the currently edited node
	 */
	private FileSizeFilterNode currentNode;

	/**
	 * default constructor
	 */
	public FileSizeFilterEditor() {
		initComponents();
	}

	/**
	 * build editor component
	 */
	private void initComponents() {
		// panel setup
		editor = new JPanel();
		editor.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		editor.setOpaque(false);

		// add text
		JLabel mustBe = new JLabel("must be ");
		mustBe.setOpaque(false);
		editor.add(mustBe);

		// type switch
		typeSwitch = new JComboBox(new String[] { BIGGER, SMALLER });
		typeSwitch.addActionListener(new TypeSwitchListener());
		editor.add(typeSwitch);

		// add then label
		JLabel then = new JLabel(" than ");
		then.setOpaque(false);
		editor.add(then);

		// add size text field
		size = new JTextField(8);
		SizeFieldListener listener = new SizeFieldListener();
		size.addActionListener(listener);
		size.addFocusListener(listener);
		editor.add(size);

		// add byte label
		JLabel byteLabel = new JLabel(" byte");
		byteLabel.setOpaque(false);
		editor.add(byteLabel);
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

		currentNode = (FileSizeFilterNode) node;

		// select right option
		if (currentNode.getType() == BIGGER_THEN) {
			typeSwitch.setSelectedItem(BIGGER);
		} else {
			typeSwitch.setSelectedItem(SMALLER);
		}

		// set size
		size.setText(Long.toString(currentNode.getSize()));
		return editor;
	}

	/**
	 * the type switch listener to react on a change
	 */
	private class TypeSwitchListener implements ActionListener {
		/**
		 * reacto on a changed state
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@SuppressWarnings("unused")
		public void actionPerformed(ActionEvent e) {
			String sel = (String) typeSwitch.getSelectedItem();
			if (BIGGER.equals(sel)) {
				currentNode.setType(BIGGER_THEN);
			} else {
				currentNode.setType(SMALLER_THEN);
			}
		}
	}

	/**
	 * reacto on size field input
	 */
	private class SizeFieldListener extends FocusAdapter implements ActionListener {
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
			if (field != size) {
				return;
			}

			// get old value
			long value = currentNode.getSize();

			// get new value
			try {
				long newValue = Long.parseLong(field.getText());
				// value must be positive
				if (newValue < 0) {
					throw new NumberFormatException("file size is negativ");
				}
				value = newValue;
			} catch (NumberFormatException nfe) {
				SwingClient.getInstance().showMessage("File size " + field.getText() + " not valid", "Illegal value",
				        JOptionPane.ERROR_MESSAGE);
			}

			// set new value
			field.setText(Long.toString(value));
			currentNode.setSize(value);
		}
	}

}
