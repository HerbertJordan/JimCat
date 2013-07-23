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

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;

import org.jimcat.gui.smartlisteditor.model.FilterTreeNode;
import org.jimcat.gui.smartlisteditor.model.TextFilterNode;

/**
 * An editor for a Text filter.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class TextFilterEditor extends BaseNodeEditor {

	/**
	 * the editor component
	 */
	private JPanel editor;

	/**
	 * the text pattern
	 */
	private JTextField pattern;

	/**
	 * the currently edited node
	 */
	private TextFilterNode currentNode;

	/**
	 * default constructor
	 */
	public TextFilterEditor() {
		initComponents();
	}

	/**
	 * build up editor
	 */
	private void initComponents() {
		// editor panel
		editor = new JPanel();
		editor.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		editor.setOpaque(false);

		// add must / mustnot switch
		editor.add(getNegateComboBox());

		// add text
		JLabel text = new JLabel(" match text ");
		text.setOpaque(false);
		editor.add(text);

		// add text field
		pattern = new JTextField(12);
		PatternFieldListener listener = new PatternFieldListener();
		pattern.addActionListener(listener);
		pattern.addFocusListener(listener);
		editor.add(pattern);
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
		currentNode = (TextFilterNode) node;

		// set pattern
		pattern.setText(currentNode.getPattern());

		return editor;
	}

	/**
	 * reacto on pattern changes
	 */
	private class PatternFieldListener extends FocusAdapter implements ActionListener {
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
			currentNode.setPattern(field.getText());
		}
	}
}
