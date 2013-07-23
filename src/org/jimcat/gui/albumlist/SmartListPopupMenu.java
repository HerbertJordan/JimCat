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

package org.jimcat.gui.albumlist;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;

import org.jimcat.gui.SmartListControl;
import org.jimcat.gui.SwingClient;
import org.jimcat.gui.ViewControl;
import org.jimcat.gui.icons.Icons;

/**
 * the popup menu for a smartlist.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class SmartListPopupMenu extends JPopupMenu implements ActionListener {

	/**
	 * constant to identify rename action
	 */
	private final static String COMMAND_RENAME = "rename";

	/**
	 * constant to identify edit action
	 */
	private final static String COMMAND_EDIT = "edit";

	/**
	 * constant to identify delete action
	 */
	private final static String COMMAND_DELETE = "delete";

	/**
	 * the currently associated smartlist node
	 */
	private AlbumTreeSmartListNode currentSmartListNode;

	/**
	 * a constructor requesting a reference to the albumlibrary
	 * 
	 */
	public SmartListPopupMenu() {
		initComponents();
	}

	/**
	 * 
	 */
	private void initComponents() {
		// add rename item
		JMenuItem rename = new JMenuItem("Rename...");
		rename.setIcon(Icons.SMARTLIST_EDIT);
		rename.setActionCommand(COMMAND_RENAME);
		rename.addActionListener(this);
		add(rename);

		// add edit item
		JMenuItem edit = new JMenuItem("Edit...");
		edit.setIcon(Icons.SMARTLIST_EDIT);
		edit.setActionCommand(COMMAND_EDIT);
		edit.addActionListener(this);
		add(edit);

		// add delete item
		JMenuItem delete = new JMenuItem("Delete...");
		delete.setIcon(Icons.SMARTLIST_REMOVE);
		delete.setActionCommand(COMMAND_DELETE);
		delete.addActionListener(this);
		add(delete);
	}

	/**
	 * @param currentSmartListNode
	 *            the currentSmartListNode to set
	 */
	public void setCurrentSmartListNode(AlbumTreeSmartListNode currentSmartListNode) {
		this.currentSmartListNode = currentSmartListNode;
	}

	/**
	 * handling menu actions
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (COMMAND_RENAME.equals(command)) {
			performRename();
		} else if (COMMAND_DELETE.equals(command)) {
			performDelete();
		} else if (COMMAND_EDIT.equals(command)) {
			performEdit();
		}
	}

	/**
	 * start the renaming process
	 */
	private void performRename() {
		// just start editing process
		TreePath path = currentSmartListNode.getPath();
		currentSmartListNode.getTree().startEditingAtPath(path);
	}

	/**
	 * initiate delete process
	 */
	private void performDelete() {
		SmartListControl control = SwingClient.getInstance().getSmartListControl();
		control.deleteSmartList(currentSmartListNode.getSmartList());

		ViewControl viewControl = SwingClient.getInstance().getViewControl();
		viewControl.clearFilter();
	}

	/**
	 * initiate editing process
	 */
	private void performEdit() {
		SwingClient.getInstance().displaySmartListEditor(currentSmartListNode.getSmartList());
	}

}
