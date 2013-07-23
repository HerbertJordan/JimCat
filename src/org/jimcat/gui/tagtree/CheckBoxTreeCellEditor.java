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

package org.jimcat.gui.tagtree;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.CellEditorListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreePath;

/**
 * A editor for a TagTreeNode.
 * 
 * Some features are looked up from DefaultTreeCellEditor.
 * 
 * @see DefaultTreeCellEditor
 * 
 * $Id: CheckBoxTreeCellEditor.java 935 2007-06-15 09:21:09Z 07g1t1u2 $
 * 
 * @author Herbert
 */
public class CheckBoxTreeCellEditor implements TreeCellEditor, TreeSelectionListener, ActionListener {

	/**
	 * a list of listeners
	 */
	private List<CellEditorListener> listeners = new CopyOnWriteArrayList<CellEditorListener>();

	/**
	 * the component representing the actual editor
	 */
	private JComponent renderer = null;

	/**
	 * the checkbox within the renderer
	 */
	private JCheckBox checkBox = null;

	/**
	 * the label within the renderer
	 */
	private JTextField label = null;

	/**
	 * <code>JTree</code> instance listening too.
	 */
	private JTree tree;

	/**
	 * Last path that was selected.
	 */
	private TreePath lastPath;

	/**
	 * Row that was last passed into <code>getTreeCellEditorComponent</code>.
	 */
	private int lastRow;

	/**
	 * Used in editing. Indicates x position to place
	 * <code>editingComponent</code>.
	 */
	private int offset = 10;

	/**
	 * default constructor
	 * @param tree 
	 */
	public CheckBoxTreeCellEditor(JTree tree) {
		setTree(tree);

		renderer = new JPanel();
		renderer.setLayout(new BorderLayout());
		renderer.setOpaque(false);

		checkBox = new JCheckBox();
		checkBox.setOpaque(false);
		checkBox.setText("");
		checkBox.setFocusable(false);
		// this should avoid the statechange of the hook
		checkBox.addActionListener(new ActionListener() {
			@SuppressWarnings("unused")
			public void actionPerformed(ActionEvent e) {
				checkBox.setSelected(!checkBox.isSelected());
			}
		});

		renderer.add(checkBox, BorderLayout.WEST);

		label = new JTextField();
		label.setPreferredSize(new Dimension(120, 10));
		label.setOpaque(false);
		label.setFocusTraversalKeysEnabled(false);
		label.addActionListener(new ActionListener() {
			@SuppressWarnings("unused")
			public void actionPerformed(ActionEvent e) {
				finishEditing();
			}
		});
		renderer.add(label, BorderLayout.CENTER);
	}

	/**
	 * this will stop the editing process
	 */
	private void finishEditing() {
		tree.stopEditing();
	}

	/**
	 * modiefies editor and returns it
	 * 
	 * @see javax.swing.tree.TreeCellEditor#getTreeCellEditorComponent(javax.swing.JTree,
	 *      java.lang.Object, boolean, boolean, boolean, int)
	 */
	@SuppressWarnings("unused")
	public Component getTreeCellEditorComponent(JTree sourceTree, Object value, boolean isSelected, boolean expanded,
	        boolean leaf, int row) {

		// set values
		setTree(sourceTree);
		lastRow = row;

		// prepair component
		TagTreeNode node = (TagTreeNode) value;

		// Prepair checkbox
		switch (node.getState()) {
		case SET: {
			checkBox.setSelected(true);
			checkBox.setEnabled(true);
			break;
		}
		case UNSET: {
			checkBox.setSelected(false);
			checkBox.setEnabled(true);
			break;
		}
		case PARTITAL: {
			checkBox.setSelected(true);
			checkBox.setEnabled(false);
			break;
		}
		}

		// Prepair textfield
		String name = node.getTag().getName();
		label.setText(name);
		label.select(0, name.length());
		return renderer;
	}

	/**
	 * adds a new listener
	 * 
	 * @see javax.swing.CellEditor#addCellEditorListener(javax.swing.event.CellEditorListener)
	 */
	public void addCellEditorListener(CellEditorListener l) {
		listeners.add(l);
	}

	/**
	 * abourt cell editing
	 * 
	 * @see javax.swing.CellEditor#cancelCellEditing()
	 */
	public void cancelCellEditing() {
		// do nothing ...
	}

	/**
	 * returns the entered name
	 * 
	 * @see javax.swing.CellEditor#getCellEditorValue()
	 */
	public Object getCellEditorValue() {
		return label.getText();
	}

	/**
	 * All cells are editable - but the must be delayed by 1200
	 * 
	 * @see javax.swing.CellEditor#isCellEditable(java.util.EventObject)
	 */
	public boolean isCellEditable(EventObject event) {
		return !(event instanceof MouseEvent);
		/*
		 * // the return value boolean retValue = false; // is it edititable
		 * boolean editable = false;
		 * 
		 * if (event != null) { if (event.getSource() instanceof JTree) {
		 * setTree((JTree) event.getSource()); if (event instanceof MouseEvent) {
		 * TreePath path = tree.getPathForLocation(((MouseEvent) event).getX(),
		 * ((MouseEvent) event).getY()); editable = (lastPath != null && path !=
		 * null && lastPath.equals(path)); if (path != null) { lastRow =
		 * tree.getRowForPath(path); } } } } if (canEditImmediately(event))
		 * retValue = true; else if (editable && shouldStartEditingTimer(event)) {
		 * startEditingTimer(); } else if (timer != null && timer.isRunning())
		 * timer.stop(); return retValue;
		 */
	}

	/**
	 * remove a CellEditorListener
	 * 
	 * @see javax.swing.CellEditor#removeCellEditorListener(javax.swing.event.CellEditorListener)
	 */
	public void removeCellEditorListener(CellEditorListener l) {
		listeners.remove(l);
	}

	/**
	 * A user has to select the cell first
	 * 
	 * @see javax.swing.CellEditor#shouldSelectCell(java.util.EventObject)
	 */
	@SuppressWarnings("unused")
	public boolean shouldSelectCell(EventObject anEvent) {
		return true;
	}

	/**
	 * Is called when editing tryes to stop. There is no result restriction, so
	 * continue
	 * 
	 * @see javax.swing.CellEditor#stopCellEditing()
	 */
	public boolean stopCellEditing() {
		// allow stopedit
		return true;
	}

	/**
	 * Resets <code>lastPath</code>.
	 * 
	 * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
	 */
	@SuppressWarnings("unused")
	public void valueChanged(TreeSelectionEvent e) {
		if (tree != null) {
			if (tree.getSelectionCount() == 1)
				lastPath = tree.getSelectionPath();
			else
				lastPath = null;
		}
	}

	/**
	 * Messaged when the timer fires, this will start the editing session.
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@SuppressWarnings("unused")
	public void actionPerformed(ActionEvent e) {
		if (tree != null && lastPath != null) {
			tree.startEditingAtPath(lastPath);
		}
	}

	/**
	 * Sets the tree currently editing for. This is needed to add a selection
	 * listener.
	 * 
	 * @param newTree
	 *            the new tree to be edited
	 */
	private void setTree(JTree newTree) {
		if (tree != newTree) {
			if (tree != null)
				tree.removeTreeSelectionListener(this);
			tree = newTree;
			if (tree != null)
				tree.addTreeSelectionListener(this);
		}
	}

	
	/**
	 * Returns true if the passed in location is a valid mouse location to start
	 * editing from. This is implemented to return false if <code>x</code> is <=
	 * the width of the icon and icon gap displayed by the renderer. In other
	 * words this returns true if the user clicks over the text part displayed
	 * by the renderer, and false otherwise.
	 * 
	 * @param x
	 *            the x-coordinate of the point
	 * @param y
	 *            the y-coordinate of the point
	 * @return true if the passed in location is a valid mouse location
	 */
	@SuppressWarnings("unused")
	private boolean inHitRegion(int x, int y) {
		if (lastRow != -1 && tree != null) {
			Rectangle bounds = tree.getRowBounds(lastRow);
			ComponentOrientation treeOrientation = tree.getComponentOrientation();

			if (treeOrientation.isLeftToRight()) {
				if (bounds != null && x <= (bounds.x + offset) && offset < (bounds.width - 5)) {
					return false;
				}
			} else if (bounds != null && (x >= (bounds.x + bounds.width - offset + 5) || x <= (bounds.x + 5))
			        && offset < (bounds.width - 5)) {
				return false;
			}
		}
		return true;
	}

}
