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

import static org.jimcat.model.filter.metadata.ImageSizeFilter.Type.BIGGER_THAN;
import static org.jimcat.model.filter.metadata.ImageSizeFilter.Type.HEIGHER_THAN;
import static org.jimcat.model.filter.metadata.ImageSizeFilter.Type.LOWER_THAN;
import static org.jimcat.model.filter.metadata.ImageSizeFilter.Type.SMALLER_THAN;
import static org.jimcat.model.filter.metadata.ImageSizeFilter.Type.THINER_THAN;
import static org.jimcat.model.filter.metadata.ImageSizeFilter.Type.WIDER_THAN;

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
import org.jimcat.gui.smartlisteditor.model.ImageSizeFilterNode;

/**
 * An editor for image size filter.
 * 
 * It proviedes a ComboBox to select type of comparison.
 * 
 * it also supports 2 textfield to enter dimensions.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class ImageSizeFilterEditor extends BaseNodeEditor {

	/**
	 * type options
	 */
	private static final String BIGGER = "bigger";

	private static final String SMALLER = "smaller";

	private static final String WIDER = "wider";

	private static final String THINNER = "thinner";

	private static final String HEIGHER = "heigher";

	private static final String LOWER = "lower";

	/**
	 * the editor panel
	 */
	private JPanel editor;

	/**
	 * the combo box to select the type
	 */
	private JComboBox typeSelection;

	/**
	 * label to display static text
	 */
	private JLabel mustBe;

	/**
	 * label to display static text
	 */
	private JLabel than;

	private JLabel slash;

	/**
	 * label to display static text
	 */
	private JLabel pixel;

	/**
	 * the textfield for editing width
	 */
	private JTextField width;

	/**
	 * the textfield for editing height
	 */
	private JTextField height;

	/**
	 * the current node to edit
	 */
	private ImageSizeFilterNode currentNode;

	/**
	 * default constructor
	 */
	public ImageSizeFilterEditor() {
		// build up GUI
		initComponents();
	}

	/**
	 * build up gui components
	 */
	private void initComponents() {
		// general setup
		editor = new JPanel();
		editor.setOpaque(false);
		editor.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

		// must be - label
		mustBe = new JLabel("must be ");
		mustBe.setOpaque(false);
		editor.add(mustBe);

		// type chooser
		String[] options = new String[] { BIGGER, SMALLER, HEIGHER, LOWER, WIDER, THINNER };
		typeSelection = new JComboBox(options);
		typeSelection.addActionListener(new TypeSelectionListener());
		editor.add(typeSelection);

		// then - label
		than = new JLabel(" than ");
		than.setOpaque(false);
		editor.add(than);

		DimensionFieldListener dimListener = new DimensionFieldListener();

		// width field
		width = new JTextField(4);
		width.addActionListener(dimListener);
		width.addFocusListener(dimListener);
		editor.add(width);

		// slash - label
		slash = new JLabel("/");
		slash.setOpaque(false);
		editor.add(slash);

		// height field
		height = new JTextField(4);
		height.addActionListener(dimListener);
		height.addFocusListener(dimListener);
		editor.add(height);

		// pixel label
		pixel = new JLabel();
		pixel = new JLabel(" pixel");
		editor.add(pixel);
	}

	/**
	 * prepair the current editor to edit given node
	 * 
	 * @see org.jimcat.gui.smartlisteditor.editor.BaseNodeEditor#getEditor(javax.swing.JTree,
	 *      org.jimcat.gui.smartlisteditor.model.FilterTreeNode)
	 */
	@Override
	@SuppressWarnings("unused")
	public JComponent getEditor(JTree tree, FilterTreeNode node) {

		currentNode = (ImageSizeFilterNode) node;

		// setup fields
		// type switch
		switch (currentNode.getType()) {
		case BIGGER_THAN:
			typeSelection.setSelectedItem(BIGGER);
			break;
		case SMALLER_THAN:
			typeSelection.setSelectedItem(SMALLER);
			break;
		case HEIGHER_THAN:
			typeSelection.setSelectedItem(HEIGHER);
			break;
		case LOWER_THAN:
			typeSelection.setSelectedItem(LOWER);
			break;
		case WIDER_THAN:
			typeSelection.setSelectedItem(WIDER);
			break;
		case THINER_THAN:
			typeSelection.setSelectedItem(THINNER);
			break;
		}

		// width dimension
		width.setText(Integer.toString(currentNode.getWidth()));

		// width dimension
		height.setText(Integer.toString(currentNode.getHeight()));

		// update format
		updateLayout();

		return editor;
	}

	/**
	 * this methode will reassemble the editor to show just required fields
	 */
	private void updateLayout() {
		String sel = (String) typeSelection.getSelectedItem();

		// clear current
		editor.remove(width);
		editor.remove(slash);
		editor.remove(height);
		editor.remove(pixel);

		if (BIGGER.equals(sel) || SMALLER.equals(sel)) {
			// all must be shown
			editor.add(width);
			editor.add(slash);
			editor.add(height);
		} else if (WIDER.equals(sel) || THINNER.equals(sel)) {
			// just width must be added
			editor.add(width);
		} else if (HEIGHER.equals(sel) || LOWER.equals(sel)) {
			// just height must be added
			editor.add(height);
		}

		// pixel must be added anyway
		editor.add(pixel);
	}

	/**
	 * a small listener to react on type selection events
	 */
	private class TypeSelectionListener implements ActionListener {
		/**
		 * react on a type selection event
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@SuppressWarnings("unused")
		public void actionPerformed(ActionEvent e) {
			String selection = (String) typeSelection.getSelectedItem();
			if (BIGGER.equals(selection)) {
				currentNode.setType(BIGGER_THAN);
			} else if (SMALLER.equals(selection)) {
				currentNode.setType(SMALLER_THAN);
			} else if (HEIGHER.equals(selection)) {
				currentNode.setType(HEIGHER_THAN);
			} else if (LOWER.equals(selection)) {
				currentNode.setType(LOWER_THAN);
			} else if (WIDER.equals(selection)) {
				currentNode.setType(WIDER_THAN);
			} else if (THINNER.equals(selection)) {
				currentNode.setType(THINER_THAN);
			}

			// update editor gui
			updateLayout();
		}
	}

	/**
	 * reacto on dimension field input
	 */
	private class DimensionFieldListener extends FocusAdapter implements ActionListener {
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
			int value = 0;
			// get privous values
			if (field == width) {
				value = currentNode.getWidth();
			} else {
				value = currentNode.getHeight();
			}

			// get new value
			try {
				int newValue = Integer.parseInt(field.getText());
				// value must be positive
				if (newValue < 0) {
					throw new NumberFormatException("dimension is negativ");
				}
				value = newValue;
			} catch (NumberFormatException nfe) {
				SwingClient.getInstance().showMessage("Dimension " + field.getText() + " not valid",
				        "Illegal dimension", JOptionPane.ERROR_MESSAGE);
			}

			// set new value
			field.setText(Integer.toString(value));
			if (field == width) {
				currentNode.setWidth(value);
			} else {
				currentNode.setHeight(value);
			}
		}
	}
}
