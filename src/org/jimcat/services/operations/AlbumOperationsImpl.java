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

package org.jimcat.services.operations;

import org.jimcat.model.Album;
import org.jimcat.model.libraries.AlbumLibrary;
import org.jimcat.services.AlbumOperations;

/**
 * Implements features promissed by the AlbumOperations interface.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class AlbumOperationsImpl implements AlbumOperations {

	/**
	 * internal albumlibrary used to proviede features
	 */
	private AlbumLibrary library = AlbumLibrary.getInstance();

	/**
	 * add a new Album to the persistant storage
	 * 
	 * @see org.jimcat.services.AlbumOperations#addAlbum(org.jimcat.model.Album)
	 */
	public void addAlbum(Album album) {
		// just added to library
		library.add(album);
	}

	/**
	 * methode used to delete an album
	 * 
	 * @see org.jimcat.services.AlbumOperations#deleteAlbum(org.jimcat.model.Album)
	 */
	public void deleteAlbum(Album album) {
		// just delete from library
		library.remove(album);
	}

	/**
	 * return the underlaying library
	 * 
	 * @see org.jimcat.services.AlbumOperations#getAlbumLibrary()
	 */
	public AlbumLibrary getAlbumLibrary() {
		return library;
	}

}
