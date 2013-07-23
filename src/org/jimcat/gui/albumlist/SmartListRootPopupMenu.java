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

import org.jimcat.gui.SmartListControl;
import org.jimcat.gui.SwingClient;
import org.jimcat.gui.icons.Icons;
import org.jimcat.model.SmartList;

/**
 * the popup menu for the album root
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class SmartListRootPopupMenu extends JPopupMenu implements ActionListener {

	/**
	 * constant to identify add action
	 */
	private final static String COMMAND_ADD = "add list";

	/**
	 * constant to identify add from current action
	 */
	private final static String COMMAND_ADD_FROM_CURRENT = "add current";

	/**
	 * a constructor requesting a reference to the albumlibrary
	 * 
	 */
	public SmartListRootPopupMenu() {
		initComponents();
	}

	/**
	 * 
	 */
	private void initComponents() {
		// add list
		JMenuItem addList = new JMenuItem("Add New Smartlist...");
		addList.setIcon(Icons.SMARTLIST_ADD);
		addList.setActionCommand(COMMAND_ADD);
		addList.addActionListener(this);
		add(addList);

		// add list using current filter
		JMenuItem addCurrent = new JMenuItem("Create Smartlist From Current Filter...");
		addCurrent.setIcon(Icons.SMARTLIST_ADD);
		addCurrent.setActionCommand(COMMAND_ADD_FROM_CURRENT);
		addCurrent.addActionListener(this);
		add(addCurrent);
	}

	/**
	 * handling menu actions
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (COMMAND_ADD.equals(command)) {
			performCreation();
		} else if (COMMAND_ADD_FROM_CURRENT.equals(command)) {
			performCreationByCurrentFilter();
		}
	}

	/**
	 * initiate create process
	 */
	private void performCreation() {
		SmartListControl control = SwingClient.getInstance().getSmartListControl();
		SmartList smartList = control.createNewSmartList();

		if (smartList != null) {
			SwingClient.getInstance().displaySmartListEditor(smartList);
		}
	}

	/**
	 * initiate create process
	 */
	private void performCreationByCurrentFilter() {
		SmartListControl control = SwingClient.getInstance().getSmartListControl();
		SmartList smartList = control.createNewSmartListFromCurrentFilter();

		if (smartList != null) {
			SwingClient.getInstance().displaySmartListEditor(smartList);
		}
	}

}
