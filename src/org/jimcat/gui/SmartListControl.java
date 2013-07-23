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

package org.jimcat.gui;

import javax.swing.JOptionPane;

import org.jimcat.model.SmartList;
import org.jimcat.model.libraries.SmartListLibrary;
import org.jimcat.services.OperationsLocator;


/**
 * a summary of all more komplex smartlist operations.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class SmartListControl {

	/**
	 * a reference to the smartlist library
	 */
	private SmartListLibrary library;

	/**
	 * a reference to the assosziated swing client
	 */
	private SwingClient client;

	/**
	 * a constructor requireing a swingclient.
	 * 
	 * this client will be used displaying messages.
	 * 
	 * @param client
	 */
	public SmartListControl(SwingClient client) {
		this.client = client;

		// get reference to AlbumLibrary
		library = OperationsLocator.getSmartListOperations().getSmartListLibrary();
	}

	/**
	 * this will initate a smartlist deletion
	 * 
	 * @param list -
	 *            the smartlist to remove
	 */
	public void deleteSmartList(SmartList list) {
		// aske user if he is sure
		String msg = "Should the smart list \"" + list.getName() + "\" really be deleted?";
		String titel = "Attention";
		int options = JOptionPane.YES_NO_OPTION;
		int typ = JOptionPane.WARNING_MESSAGE;

		int result = client.showConfirmDialog(msg, titel, options, typ);

		// if not confirmed return
		if (result != JOptionPane.YES_OPTION) {
			return;
		}

		// delete
		library.remove(list);
	}

	/**
	 * process to create a new smartlist
	 * @return the new smartlist
	 */
	public SmartList createNewSmartListFromCurrentFilter() {
		// ask for new name
		String msg = "Please enter the name of the new smart list: ";
		String title = "Create new SmartList";
		int typ = JOptionPane.QUESTION_MESSAGE;
		String name = client.showInputDialog(msg, title, typ);

		if (name == null || name.equals("")) {
			// aborted
			return null;
		}

		// create new smartlist
		SmartList list = new SmartList();
		list.setName(name);
		list.setFilter(client.getViewControl().buildFilter());

		// add to library
		library.add(list);

		// select new smartlist
		client.getViewControl().setSmartList(list);
		
		return list;
	}

	/**
	 * process to create a new smartlist
	 * @return the new smartlist
	 */
	public SmartList createNewSmartList() {
		// ask for new name
		String msg = "Please enter the name of the new smart list: ";
		String title = "Create new SmartList";
		int typ = JOptionPane.QUESTION_MESSAGE;
		String name = client.showInputDialog(msg, title, typ);

		if (name == null || name.equals("")) {
			// aborted
			return null;
		}

		// create new smartlist
		SmartList list = new SmartList();
		list.setName(name);

		// add to library
		library.add(list);
		
		return list;
	}
}
