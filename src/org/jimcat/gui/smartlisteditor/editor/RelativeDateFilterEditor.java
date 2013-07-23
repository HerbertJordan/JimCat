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
import org.jimcat.gui.smartlisteditor.model.RelativeDateFilterNode;
import org.jimcat.model.filter.metadata.RelativeDateFilter.ReferenceDate;
import org.jimcat.model.filter.metadata.RelativeDateFilter.TimeUnit;

/**
 * an editor for relative date filter nodes.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class RelativeDateFilterEditor extends BaseNodeEditor {

	/**
	 * constants for reference types
	 */
	private static final String DATE_ADDED = "date added";

	private static final String DATE_MODIFIED = "date modified";

	private static final String DATE_TAKEN = "date taken";

	/**
	 * constants for time units
	 */
	private static final String DAYS = "days";

	private static final String WEEKS = "weeks";

	private static final String MONTHS = "months";

	/**
	 * the editor component
	 */
	private JPanel editor;

	/**
	 * the ComboBox to choose reference date
	 */
	private JComboBox referenceDate;

	/**
	 * field to enter value
	 */
	private JTextField value;

	/**
	 * the ComboBox to choose time unit
	 */
	private JComboBox timeUnit;

	/**
	 * the edited node
	 */
	private RelativeDateFilterNode currentNode;

	/**
	 * default constructor
	 */
	public RelativeDateFilterEditor() {
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

		// reference date switch
		referenceDate = new JComboBox(new String[] { DATE_ADDED, DATE_TAKEN, DATE_MODIFIED });
		referenceDate.addActionListener(new ReferenceDateListener());
		editor.add(referenceDate);

		// negation
		editor.add(getNegateComboBox());

		// text
		JLabel mustHave = new JLabel(" be within last ");
		mustHave.setOpaque(false);
		editor.add(mustHave);

		// value
		value = new JTextField(5);
		ImportFieldListener listener = new ImportFieldListener();
		value.addActionListener(listener);
		value.addFocusListener(listener);
		editor.add(value);

		// timeUnit
		timeUnit = new JComboBox(new String[] { DAYS, WEEKS, MONTHS });
		timeUnit.addActionListener(new TimeUnitListener());
		editor.add(timeUnit);
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
		currentNode = (RelativeDateFilterNode) node;

		// select right reference option
		switch(currentNode.getReferenceDate()) {
		case DateAdded:
			referenceDate.setSelectedItem(DATE_ADDED);
			break;
		case DateModified:
			referenceDate.setSelectedItem(DATE_MODIFIED);
			break;
		case DateTaken:
			referenceDate.setSelectedItem(DATE_TAKEN);
			break;
		}
		
		// select right time unit
		switch(currentNode.getTimeUnit()) {
		case DAYS:
			timeUnit.setSelectedItem(DAYS);
			break;
		case WEEKS:
			timeUnit.setSelectedItem(WEEKS);
			break;
		case MONTHS:
			timeUnit.setSelectedItem(MONTHS);
			break;
		}
		
		// set values
		value.setText(Integer.toString(currentNode.getValue()));
		return editor;
	}
	
	/**
	 * class reacting on change events of the reference date
	 */
	private class ReferenceDateListener implements ActionListener {
		/**
		 * react on a change event
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@SuppressWarnings("unused")
		public void actionPerformed(ActionEvent e) {
			String sel = (String) referenceDate.getSelectedItem();
			if (DATE_ADDED.equals(sel)) {
				currentNode.setReferenceDate(ReferenceDate.DateAdded);
			} else if (DATE_MODIFIED.equals(sel)) {
				currentNode.setReferenceDate(ReferenceDate.DateModified);
			} else if (DATE_TAKEN.equals(sel)) {
				currentNode.setReferenceDate(ReferenceDate.DateTaken);
			}
		}
	}
	
	/**
	 * class reacting on change events of the type switch
	 */
	private class TimeUnitListener implements ActionListener {
		/**
		 * react on a change event
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@SuppressWarnings("unused")
		public void actionPerformed(ActionEvent e) {
			String sel = (String) timeUnit.getSelectedItem();
			if (DAYS.equals(sel)) {
				currentNode.setTimeUnit(TimeUnit.DAYS);
			} else if (WEEKS.equals(sel)) {
				currentNode.setTimeUnit(TimeUnit.WEEKS);
			} else {
				currentNode.setTimeUnit(TimeUnit.MONTHS);
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
			if (field != value) {
				return;
			}

			// get old value
			int time = currentNode.getValue();

			// get new value
			try {
				int newValue = Integer.parseInt(field.getText());
				// value must be positive
				if (newValue < 0) {
					throw new NumberFormatException("dimension is negativ");
				}
				time = newValue;
			} catch (NumberFormatException nfe) {
				SwingClient.getInstance().showMessage("Value " + field.getText() + " not valid", "Illegal value",
				        JOptionPane.ERROR_MESSAGE);
			}

			// set new value
			field.setText(Integer.toString(time));
			currentNode.setValue(time);
		}
	}
}
