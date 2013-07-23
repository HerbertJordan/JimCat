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

import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

import org.jimcat.model.Album;
import org.jimcat.model.Image;
import org.jimcat.model.filter.AlbumFilter;
import org.jimcat.model.libraries.AlbumLibrary;
import org.jimcat.services.OperationsLocator;

/**
 * a summary of all more komplex album operations.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class AlbumControl {

	/**
	 * a reference to the album library
	 */
	private AlbumLibrary library;

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
	public AlbumControl(SwingClient client) {
		this.client = client;

		// get reference to AlbumLibrary
		library = OperationsLocator.getAlbumOperations().getAlbumLibrary();
	}

	/**
	 * get all albums currently stored within the library
	 * 
	 * @return all Albums store within the library
	 */
	public Set<Album> getAllAlbums() {
		return library.getAll();
	}

	/**
	 * this will initate an album deletion
	 * 
	 * @param album -
	 *            the album to remove
	 */
	public void deleteAlbum(Album album) {
		// aske user if he is sure
		String msg = "Should the album \"" + album.getName() + "\" really be deleted?";
		String titel = "Attention";
		int options = JOptionPane.YES_NO_OPTION;
		int typ = JOptionPane.WARNING_MESSAGE;

		int result = client.showConfirmDialog(msg, titel, options, typ);

		// if not confirmed return
		if (result != JOptionPane.YES_OPTION) {
			return;
		}

		// delete
		library.remove(album);
	}

	/**
	 * process to create a new smartlist
	 */
	public void createNewAlbumFromCurrentSelection() {
		// ask for new name
		String msg = "Please enter the name of the new album: ";
		String title = "Create new Album";
		int typ = JOptionPane.QUESTION_MESSAGE;
		String name = client.showInputDialog(msg, title, typ);

		if (name == null || name.equals("")) {
			// aborted
			return;
		}

		// create new album
		Album album = new Album();
		album.setName(name);

		ViewControl view = client.getViewControl();
		// get selected image set
		List<Image> images = view.getSelectedImages();
		if (images.size() > 0) {
			// if there are 1 or more selected images
			// => use selection for new album
			client.getImageControl().addImagesToAlbum(images, album);
		} else {
			// if there less than 1 images selected
			// => use all visible images
			images = view.getLibraryView().getSnapshot();
			client.getImageControl().addImagesToAlbum(images, album);
		}

		// add to library
		library.add(album);

		// select new album
		view.setAlbumFilter(new AlbumFilter(album));
	}

	/**
	 * process to create a new album
	 * @return the new album or null if aborted
	 * 
	 */
	public Album createNewAlbum() {
		// ask for new name
		String msg = "Please enter the name of the new album: ";
		String title = "Create new Album";
		int typ = JOptionPane.QUESTION_MESSAGE;
		String name = client.showInputDialog(msg, title, typ);

		if (name == null || name.equals("")) {
			// aborted
			return null;
		}

		return createNewAlbum(name);
	}

	/**
	 * create a new album with given name
	 * 
	 * @param name
	 * @return the new album
	 */
	public Album createNewAlbum(String name) {
		// create new album
		Album album = new Album();
		album.setName(name);

		// add to library
		library.add(album);

		return album;
	}
}
