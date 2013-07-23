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

package org.jimcat.services;

import org.jimcat.model.Album;
import org.jimcat.model.libraries.AlbumLibrary;

/**
 * This interface should represent a list of album-actions.
 * 
 * 
 * $Id: AlbumOperations.java 934 2007-06-15 08:40:58Z 07g1t1u2 $
 * 
 * @author Herbert
 */
public interface AlbumOperations {

	/**
	 * This methode will return the used AlbumLibrary.
	 * 
	 * @return the album library
	 */
	public AlbumLibrary getAlbumLibrary();

	/**
	 * This methode should be used to create a new album
	 * 
	 * @param album
	 */
	public void addAlbum(Album album);

	/**
	 * this will remove the given album from the system.
	 * 
	 * @param album
	 */
	public void deleteAlbum(Album album);

}
