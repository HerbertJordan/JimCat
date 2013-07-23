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

package org.jimcat.model.comparator;

import java.util.Comparator;

import org.jimcat.model.Album;
import org.jimcat.model.Image;

/**
 * The AlbumOrderComparator uses the index of two images within an album to
 * compare them.
 * 
 * This class implements the Comparator interface for Images and uses the index
 * of them within an album to compare them.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class AlbumOrderComparator implements Comparator<Image> {

	/**
	 * the album this comparator is based on
	 */
	private Album album;

	/**
	 * default constructor sets album to null
	 */
	public AlbumOrderComparator() {
		this(null);
	}

	/**
	 * a constructor requesting all needed fields
	 * 
	 * @param album
	 */
	public AlbumOrderComparator(Album album) {
		this.album = album;
	}

	/**
	 * @param album
	 *            the album to set
	 */
	public void setAlbum(Album album) {
		this.album = album;
	}

	/**
	 * @return the album
	 */
	public Album getAlbum() {
		return album;
	}

	/**
	 * The compare method of the AlbumOrderCompoarator uses the index of two
	 * images within a given library to compare them.
	 * @param o1 
	 * @param o2 
	 * @return the result of the compare mehtod as specified in java.util.Comparator
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Image o1, Image o2) {
		if (o1 == null || o1.getMetadata() == null) {
			if (o2 == null || o2.getMetadata() == null)
				return 0;
			return -1;
		}
		if (o2 == null || o2.getMetadata() == null)
			return 1;

		// extract checksums as string
		int index1 = album.indexOf(o1);
		int index2 = album.indexOf(o2);

		return index1 - index2;
	}

}
