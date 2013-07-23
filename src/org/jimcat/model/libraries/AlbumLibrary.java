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

package org.jimcat.model.libraries;

import org.jimcat.model.Album;
import org.jimcat.persistence.RepositoryLocator;

/**
 * this class is forming a central container for all persistently stored albums
 * of this system.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public final class AlbumLibrary extends AbstractLibrary<Album, AlbumLibrary> {

	private static AlbumLibrary INSTANCE;

	/**
	 * create a new albumlibrary.
	 * 
	 * This constructor is using informations proviede by the installed
	 * AlbumRepository.
	 */
	private AlbumLibrary() {
		super(RepositoryLocator.getAlbumRepository());
	}

	/**
	 * 
	 * @return the singelton factory
	 */
	public static AlbumLibrary getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new AlbumLibrary();
		}
		return INSTANCE;
	}

}
