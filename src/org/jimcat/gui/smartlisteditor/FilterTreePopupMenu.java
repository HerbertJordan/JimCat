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

package org.jimcat.gui.smartlisteditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.jdesktop.swingx.JXTree;
import org.jimcat.gui.smartlisteditor.model.ConstantFilterNode;
import org.jimcat.gui.smartlisteditor.model.FilterTreeNode;
import org.jimcat.gui.smartlisteditor.model.GroupFilterTreeNode;

/**
 * the default popup menu for a filter tree node.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class FilterTreePopupMenu extends JPopupMenu implements ActionListener {

	/**
	 * commands to determine action
	 */
	private final static String COMMAND_NEGATE = "negate";

	private final static String COMMAND_EDIT = "edit";

	private final static String COMMAND_INSERT_GROUP = "insert";

	private final static String COMMAND_DELETE = "delete";

	/**
	 * the tree this handler is installed on
	 */
	private JXTree tree;

	/**
	 * the editor this handler is working for
	 */
	private SmartListEditor editor;
	
	/**
	 * the item used for delete function
	 */
	private JMenuItem delete;

	/**
	 * the item used for editing function
	 */
	private JMenuItem edit;

	/**
	 * the item used for adding filters
	 */
	private FilterTreeAddPopup addMenu;

	/**
	 * the currently associated filter node
	 */
	private FilterTreeNode currentNode;

	/**
	 * a constructor requesting a reference to the albumlibrary
	 * 
	 * @param editor
	 * @param tree
	 */
	public FilterTreePopupMenu(SmartListEditor editor, JXTree tree) {
		// init members
		this.tree = tree;
		this.editor = editor;
		
		initComponents();
	}

	/**
	 * 
	 */
	private void initComponents() {
		// add edit item
		edit = new JMenuItem("Edit Filter...");
		edit.setActionCommand(COMMAND_EDIT);
		edit.addActionListener(this);
		add(edit);

		// add negate item
		JMenuItem rename = new JMenuItem("Negate");
		rename.setActionCommand(COMMAND_NEGATE);
		rename.addActionListener(this);
		add(rename);

		// add insert parent group item
		JMenuItem insert = new JMenuItem("Insert Parent Group");
		insert.setActionCommand(COMMAND_INSERT_GROUP);
		insert.addActionListener(this);
		add(insert);

		// add new filter
		addMenu = new FilterTreeAddPopup(this);
		addMenu.setText("Add New Filter");
		add(addMenu);

		// add delete item
		delete = new JMenuItem("Delete");
		delete.setActionCommand(COMMAND_DELETE);
		delete.addActionListener(this);
		add(delete);
	}

	/**
	 * @param currentNode
	 *            the currentNode to set
	 */
	public void setCurrentFilterNode(FilterTreeNode currentNode) {
		this.currentNode = currentNode;
		// root can't be deleted
		// => disable delet button
		delete.setEnabled(currentNode.getParent() != null);

		// you can only add filters to groups
		addMenu.setEnabled(currentNode instanceof GroupFilterTreeNode);

		// disable edit if edit isn't supported
		FilterTreeEditor cellEditor = (FilterTreeEditor) tree.getCellEditor();
		edit.setEnabled(cellEditor.isCellEditable(currentNode));
	}

	/**
	 * handling menu actions
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (COMMAND_NEGATE.equals(command)) {
			performRename();
		} else if (COMMAND_EDIT.equals(command)) {
			performEdit();
		} else if (COMMAND_INSERT_GROUP.equals(command)) {
			performInsertGroup();
		} else if (COMMAND_DELETE.equals(command)) {
			performDelete();
		}
	}

	/**
	 * start the editing command
	 */
	private void performRename() {
		// just switch state
		currentNode.setNegate(!currentNode.isNegate());
	}

	/**
	 * initiate delete process
	 */
	private void performDelete() {
		// get parent of current node
		GroupFilterTreeNode parent = currentNode.getParent();
		if (parent != null) {
			parent.removeChild(currentNode);
		}
		// root can't be deleted
	}

	/**
	 * insert a group as new parent to current node
	 */
	private void performInsertGroup() {
		// get parent of current node
		GroupFilterTreeNode parent = currentNode.getParent();

		// create new Group
		GroupFilterTreeNode newGroup = new GroupFilterTreeNode(null, GroupFilterTreeNode.Type.ALL);

		// move current node
		if (!(currentNode instanceof ConstantFilterNode)) {
			newGroup.addChild(currentNode);
		} else {
			// remove constant node
			if (parent!=null) {
				parent.removeChild(currentNode);
			}
		}

		// if root was moved
		if (parent == null) {
			editor.setNewFilterRoot(newGroup);
		} else {
			// append new Group to parent
			parent.addChild(newGroup);
		}

		// expand new group
		tree.expandPath(currentNode.getPath());
		tree.startEditingAtPath(newGroup.getPath());
	}

	/**
	 * start editing
	 */
	private void performEdit() {
		// just tell tree to edit current node
		tree.startEditingAtPath(currentNode.getPath());
	}

	/**
	 * @return the currentNode
	 */
	public FilterTreeNode getCurrentNode() {
		return currentNode;
	}

	/**
	 * @return the tree
	 */
	public JXTree getTree() {
		return tree;
	}

}
