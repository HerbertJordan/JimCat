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

package org.jimcat.gui.imagepopup;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import org.jimcat.gui.AlbumControl;
import org.jimcat.gui.ImageControl;
import org.jimcat.gui.SwingClient;
import org.jimcat.model.Album;

/**
 * The submenu used to add the currently selected images to a selectable album.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class AddToAlbumMenu extends ImagePopupAlbumMenu {

	/**
	 * the creator used to create new tag and add images to new tag
	 */
	private AlbumCreator creator = new AlbumCreator();

	/**
	 * a reference to the installed ImageControl
	 */
	private ImageControl control = SwingClient.getInstance().getImageControl();

	/**
	 * a reference to the album control
	 */
	private AlbumControl albumControl = SwingClient.getInstance().getAlbumControl();

	/**
	 * do the magic - add images to an album
	 * 
	 * @see org.jimcat.gui.imagepopup.ImagePopupAlbumMenu#elementSelected(org.jimcat.model.Album)
	 */
	@Override
	public void elementSelected(Album album) {
		// delegate
		control.addSelectionToAlbum(album);
	}

	/**
	 * use basic implementation + new item
	 * 
	 * @see org.jimcat.gui.imagepopup.ImagePopupAlbumMenu#buildNewList()
	 */
	@Override
	protected void buildNewList() {
		// add list
		super.buildNewList();

		// add new item
		addSeparator();
		JMenuItem item = new JMenuItem("<html><i>New Album...");
		item.addActionListener(getCreator());
		add(item);
	}

	/**
	 * get tag creation listener
	 * 
	 * @return the AlbumCreator
	 */
	private AlbumCreator getCreator() {
		if (creator == null) {
			creator = new AlbumCreator();
		}
		return creator;
	}

	/**
	 * used to generate and assign new tags
	 */
	private class AlbumCreator implements ActionListener {
		/**
		 * react on a click
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@SuppressWarnings("unused")
		public void actionPerformed(ActionEvent e) {
			// first create new Album
			Album album = albumControl.createNewAlbum();

			if (album == null) {
				// action aborted
				return;
			}
			// assign values
			elementSelected(album);
		}
	}
}
