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
import org.jimcat.gui.smartlisteditor.model.RatingFilterNode;
import org.jimcat.gui.smartlisteditor.model.SmartlistEditorRatingRepresentation;
import org.jimcat.model.ImageRating;
import org.jimcat.model.filter.RatingFilter.Type;

/**
 * An editor for a pricture taken Filter editor.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class RatingFilterEditor extends BaseNodeEditor {

	/**
	 * constants for type change
	 */
	private static final String AT_LEAST = "at least";

	private static final String EXACTLY = "exactly";

	private static final String UP_TO = "less than";

	private static SmartlistEditorRatingRepresentation[] ratings;

	static {
		ImageRating[] values = ImageRating.values();
		int ratingCount = values.length;
		
		ratings = new SmartlistEditorRatingRepresentation[ratingCount];
		
		for (int i = 0; i < ratingCount; i++) {
			ratings[i] = new SmartlistEditorRatingRepresentation(values[i]);
		}
	}

	/**
	 * the editor component
	 */
	private JPanel editor;

	/**
	 * the type switch used to select filter type
	 */
	private JComboBox typeSwitch;

	/**
	 * the editor used to choose stars
	 */
	private JComboBox ratingSwitch;

	/**
	 * the edited node
	 */
	private RatingFilterNode currentNode;

	/**
	 * default constructor
	 */
	public RatingFilterEditor() {
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

		// neagation tool
		editor.add(getNegateComboBox());

		// text
		JLabel text = new JLabel(" be rated with ");
		text.setOpaque(false);
		editor.add(text);

		// type switch
		typeSwitch = new JComboBox(new String[] { UP_TO, EXACTLY, AT_LEAST });
		typeSwitch.addActionListener(new TypeSwitchListener());
		editor.add(typeSwitch);

		// date rating editor
		ratingSwitch = new JComboBox(ratings);
		ratingSwitch.addActionListener(new RatingSwitchListener());
		editor.add(ratingSwitch);

		// text
		text = new JLabel(" stars ");
		text.setOpaque(false);
		editor.add(text);
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

		currentNode = (RatingFilterNode) node;

		// choose type
		switch (currentNode.getType()) {
		case UP_TO:
			typeSwitch.setSelectedItem(UP_TO);
			break;
		case EXACT:
			typeSwitch.setSelectedItem(EXACTLY);
			break;
		case AT_LEAST:
			typeSwitch.setSelectedItem(AT_LEAST);
			break;
		}

		// set rating
		ratingSwitch.setSelectedItem(ratings[currentNode.getRating().ordinal()]);

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
	 * listen to the date picker => react on events
	 */
	private class RatingSwitchListener implements ActionListener {
		/**
		 * react on a changed date
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@SuppressWarnings("unused")
		public void actionPerformed(ActionEvent e) {
			currentNode.setRating(((SmartlistEditorRatingRepresentation) ratingSwitch.getSelectedItem()).getRating());
		}
	}
}
