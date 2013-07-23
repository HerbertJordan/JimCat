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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.TreeCellEditor;

import org.jimcat.gui.smartlisteditor.model.FilterTreeNode;

/**
 * A base class for FilterNodeEditors.
 * 
 * 
 * $Id$
 * 
 * @author Herbert
 */
public abstract class BaseNodeEditor implements TreeCellEditor {

	/**
	 * constants as negate list items
	 */
	protected static final String MUST = "must";

	protected static final String MUST_NOT = "must not";

	/**
	 * the negate hook for used by moste editors
	 */
	private JCheckBox negate;

	/**
	 * a combo box to choose negation state
	 */
	private JComboBox negateList;

	/**
	 * the currently edited node
	 */
	private FilterTreeNode currentNode;

	/**
	 * a common implementation for the getTreeCellEditorComponent methode
	 * 
	 * @see javax.swing.tree.TreeCellEditor#getTreeCellEditorComponent(javax.swing.JTree,
	 *      java.lang.Object, boolean, boolean, boolean, int)
	 */
	@SuppressWarnings("unused")
	public final Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded,
	        boolean leaf, int row) {

		// update negate hook
		currentNode = (FilterTreeNode) value;
		updateNegateItems();

		// get editor and return
		return getEditor(tree, currentNode);
	}

	/**
	 * a hook methode to get the editor component from the subclass
	 * 
	 * @param tree -
	 *            the tree using this editor
	 * @param node -
	 *            the node to edit
	 * @return - the editor component
	 */
	public abstract JComponent getEditor(JTree tree, FilterTreeNode node);

	/**
	 * get a JComponent to edit negation state
	 * 
	 * @return the JCheckbox to edit negation
	 */
	protected JCheckBox getNegateHook() {
		if (negate == null) {
			// build up negate hook
			negate = new JCheckBox("negate");
			negate.setOpaque(false);
			negate.addChangeListener(new NegatHookListener());
		}
		return negate;
	}

	/**
	 * get a JComponent to edit negate state as Combo Box
	 * 
	 * @return the the JComboBox to edit negation
	 */
	protected JComboBox getNegateComboBox() {
		if (negateList == null) {
			// build up negate list
			negateList = new JComboBox(new String[] { MUST, MUST_NOT });
			negateList.setOpaque(false);
			negateList.addActionListener(new NegateListListener());
		}
		return negateList;
	}

	/**
	 * a list of listeners
	 */
	private List<CellEditorListener> listeners = new CopyOnWriteArrayList<CellEditorListener>();

	/**
	 * add a new CellEditorListener
	 * 
	 * @see javax.swing.CellEditor#addCellEditorListener(javax.swing.event.CellEditorListener)
	 */
	public void addCellEditorListener(CellEditorListener l) {
		listeners.add(l);
	}

	/**
	 * react on a cnaceld editing process.
	 * 
	 * default implementation does nothing. override if required
	 * 
	 * @see javax.swing.CellEditor#cancelCellEditing()
	 */
	public void cancelCellEditing() {
		// just do nothing
	}

	/**
	 * return the result of the editing process
	 * 
	 * default returns null. Override if required.
	 * 
	 * @see javax.swing.CellEditor#getCellEditorValue()
	 */
	public Object getCellEditorValue() {
		// just return null - setting values is done by editors
		return null;
	}

	/**
	 * determine if the given cell is editable.
	 * 
	 * by default all cells are editable
	 * 
	 * @see javax.swing.CellEditor#isCellEditable(java.util.EventObject)
	 */
	@SuppressWarnings("unused")
	public boolean isCellEditable(EventObject anEvent) {
		return true;
	}

	/**
	 * remove a cell editor listener
	 * 
	 * @see javax.swing.CellEditor#removeCellEditorListener(javax.swing.event.CellEditorListener)
	 */
	public void removeCellEditorListener(CellEditorListener l) {
		listeners.remove(l);
	}

	/**
	 * by default, a cell should be selected
	 * 
	 * @see javax.swing.CellEditor#shouldSelectCell(java.util.EventObject)
	 */
	@SuppressWarnings("unused")
	public boolean shouldSelectCell(EventObject anEvent) {
		return true;
	}

	/**
	 * default implementation just accepts stop
	 * 
	 * @see javax.swing.CellEditor#stopCellEditing()
	 */
	public boolean stopCellEditing() {
		return true;
	}

	/**
	 * this will update the negate hook and combo box to match current negate
	 * state
	 */
	protected void updateNegateItems() {
		// if there is no current node => nothing happens
		if (currentNode == null) {
			return;
		}
		// update items state
		if (currentNode.isNegate()) {
			if (negate != null)
				negate.setSelected(true);
			if (negateList != null)
				negateList.setSelectedItem(MUST_NOT);
		} else {
			if (negate != null)
				negate.setSelected(false);
			if (negateList != null)
				negateList.setSelectedItem(MUST);
		}
	}

	/**
	 * a small class listening to the change of the negate hook item
	 * 
	 * $Id$
	 * 
	 * @author Herbert
	 */
	private class NegatHookListener implements ChangeListener {
		/**
		 * react on a negate hook change
		 * 
		 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
		 */
		public void stateChanged(ChangeEvent e) {
			if (e.getSource() == negate) {
				// change state
				currentNode.setNegate(negate.isSelected());

				// update items
				updateNegateItems();
			}
		}
	}

	/**
	 * a small class listening to action events from the negate combo box
	 * 
	 * $Id$
	 * 
	 * @author Herbert
	 */
	private class NegateListListener implements ActionListener {
		/**
		 * react on a changed selection
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == negateList) {
				// change state
				currentNode.setNegate(negateList.getSelectedItem().equals(MUST_NOT));

				// update items
				updateNegateItems();
			}
		}
	}
}
