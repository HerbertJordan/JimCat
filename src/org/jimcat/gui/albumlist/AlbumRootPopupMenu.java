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

import org.jimcat.gui.AlbumControl;
import org.jimcat.gui.SwingClient;
import org.jimcat.gui.icons.Icons;


/**
 * the popup menu for the album root
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class AlbumRootPopupMenu extends JPopupMenu implements ActionListener {

	/**
	 * constant to identify add action
	 */
	private final static String COMMAND_ADD = "add album";
	
	/**
	 * constant to identify add current action
	 */
	private final static String COMMAND_ADD_CURRENT = "add current";

	/**
	 * a constructor requesting a reference to the albumlibrary
	 */
	public AlbumRootPopupMenu() {
		initComponents();
	}

	/**
	 * 
	 */
	private void initComponents() {
		// add item
		JMenuItem addAlbum = new JMenuItem("Add New Album...");
		addAlbum.setIcon(Icons.ALBUM_ADD);
		addAlbum.setActionCommand(COMMAND_ADD);
		addAlbum.addActionListener(this);
		add(addAlbum);

		// add item
		JMenuItem addAlbumFromSelection = new JMenuItem("Add New Album From Current Selection...");
		addAlbumFromSelection.setIcon(Icons.ALBUM_ADD);
		addAlbumFromSelection.setActionCommand(COMMAND_ADD_CURRENT);
		addAlbumFromSelection.addActionListener(this);
		add(addAlbumFromSelection);

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
		} else if (COMMAND_ADD_CURRENT.equals(command)) {
			performCreationFromSelected();
		}
	}

	/**
	 * initiate create process
	 */
	private void performCreation() {
		AlbumControl control = SwingClient.getInstance().getAlbumControl();
		control.createNewAlbum();
	}

	/**
	 * create a new Album using current selection
	 */
	private void performCreationFromSelected() {
		AlbumControl control = SwingClient.getInstance().getAlbumControl();
		control.createNewAlbumFromCurrentSelection();
	}
}
