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

package org.jimcat.persistence.xstream;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.jimcat.model.Album;
import org.jimcat.persistence.AlbumRepository;

/**
 * Album repository for XStream backend.
 * 
 * $Id: DummyAlbumRepository.java 329 2007-04-18 13:01:15Z 07g1t1u1 $
 * 
 * @author Christoph
 */
public class XStreamAlbumRepository implements AlbumRepository {

	/**
	 * a reference to the album List
	 */
	private Set<Album> albumList = XStreamBackup.getInstance().albumList;

	/**
	 * Load all albums from the persistence layer.
	 * 
	 * @return a set with all albums
	 */
	public Set<Album> getAll() {
		return new HashSet<Album>(albumList);
	}

	/**
	 * Delete a collection of albums
	 * 
	 * @param albums
	 *            the albums to be deleted
	 */
	public void remove(Collection<Album> albums) {
		albumList.removeAll(albums);
	}

	/**
	 * Save a collection of albums
	 * 
	 * @param albums
	 *            the albums to be saved
	 */
	public void save(Collection<Album> albums) {
		albumList.addAll(albums);
	}
}
