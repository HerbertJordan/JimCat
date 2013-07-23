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

package org.jimcat.model.filter;

import java.util.HashSet;
import java.util.Set;

import org.jimcat.model.Album;
import org.jimcat.model.Image;
import org.jimcat.model.libraries.AlbumLibrary;

/**
 * This filter matches all images contained in a specified album.
 * 
 * $Id: AlbumFilter.java 998 2007-08-29 20:36:25Z cleiter $
 * 
 * @author Herbert
 */
public class AlbumFilter extends Filter {

	/**
	 * a reference to the associated Album
	 */
	private Album album;

	/**
	 * creates a new Filter, fitlering for given album
	 * 
	 * @param album
	 */
	public AlbumFilter(Album album) {
		setAlbum(album);
	}

	/**
	 * 
	 * @see org.jimcat.model.filter.Filter#matches(org.jimcat.model.Image)
	 */
	@Override
	public boolean matches(Image image) {
		return image.getAlbums().contains(album);
	}

	/**
	 * @param album
	 *            the album to set
	 */
	private void setAlbum(Album album) {
		this.album = album;
	}

	/**
	 * @return the album
	 */
	public Album getAlbum() {
		return album;
	}

	/**
	 * checks if the stored album still exists
	 * 
	 * @see org.jimcat.model.filter.Filter#getCleanVersion()
	 */
	@Override
	public Filter getCleanVersion() {
		if (AlbumLibrary.getInstance().contains(album)) {
			// filter is still ok
			return new AlbumFilter(album);
		}
		// filter must be deleted
		return null;
	}

	/**
	 * the search set may be reduced by just searching elements of filtered
	 * album
	 * 
	 * @see org.jimcat.model.filter.Filter#possibleMembers()
	 */
	@Override
	public Set<Image> possibleMembers() {
		return new HashSet<Image>(album.getImages());
	}
}
