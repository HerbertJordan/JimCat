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
import org.jimcat.gui.smartlisteditor.model.MegaPixelFilterNode;
import org.jimcat.model.filter.metadata.MegaPixelFilter.Type;

/**
 * An editor component for Megapixel Filter Nodes.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class MegaPixelFilterEditor extends BaseNodeEditor {

	/*
	 * constants for type switch
	 */
	private static final String AT_LEAST = "at least";

	private static final String LESS = "less than";

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
	private MegaPixelFilterNode currentNode;

	/**
	 * default constructor
	 */
	public MegaPixelFilterEditor() {
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
		JLabel mustHave = new JLabel("must have ");
		mustHave.setOpaque(false);
		editor.add(mustHave);

		// type switch
		typeSwitch = new JComboBox(new String[] { AT_LEAST, LESS });
		typeSwitch.addActionListener(new TypeSwitchListener());
		editor.add(typeSwitch);

		// add size text field
		size = new JTextField(8);
		SizeFieldListener listener = new SizeFieldListener();
		size.addActionListener(listener);
		size.addFocusListener(listener);
		editor.add(size);

		// add byte label
		JLabel byteLabel = new JLabel(" MegaPixel");
		byteLabel.setOpaque(false);
		editor.add(byteLabel);
	}

	/**
	 * prepaire editor for editing
	 * 
	 * @see org.jimcat.gui.smartlisteditor.editor.BaseNodeEditor#getEditor(javax.swing.JTree,
	 *      org.jimcat.gui.smartlisteditor.model.FilterTreeNode)
	 */
	@Override
	@SuppressWarnings("unused")
	public JComponent getEditor(JTree tree, FilterTreeNode node) {
		currentNode = (MegaPixelFilterNode) node;

		// select right option
		if (currentNode.getType() == Type.BIGGER_OR_EQUAL) {
			typeSwitch.setSelectedItem(AT_LEAST);
		} else {
			typeSwitch.setSelectedItem(LESS);
		}

		// set size
		size.setText(Float.toString(currentNode.getMp()));
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
			if (AT_LEAST.equals(sel)) {
				currentNode.setType(Type.BIGGER_OR_EQUAL);
			} else {
				currentNode.setType(Type.SMALLER_THEN);
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
			float value = currentNode.getMp();

			// get new value
			try {
				float newValue = Float.parseFloat(field.getText());
				// value must be positive
				if (newValue < 0) {
					throw new NumberFormatException("file size is negativ");
				}
				value = newValue;
			} catch (NumberFormatException nfe) {
				SwingClient.getInstance().showMessage("Nummber of Megapixel (" + field.getText() + ") is not valid", "Illegal value",
				        JOptionPane.ERROR_MESSAGE);
			}

			// set new value
			field.setText(Float.toString(value));
			currentNode.setMp(value);
		}
	}
}
