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

import org.jimcat.model.Image;
import org.jimcat.persistence.ImageRepository;

/**
 * 
 * Image repository for XStream backend.
 * 
 * 
 * $Id$
 * 
 * @author Christoph
 */
public class XStreamImageRepository implements ImageRepository {

	/**
	 * Load all images from the persistence layer.
	 * 
	 * @return a set of all images
	 */
	public Set<Image> getAll() {
		return new HashSet<Image>(XStreamBackup.getInstance().images);
	}

	/**
	 * Remove a collection of images
	 * 
	 * @param images
	 *            the images to be removed
	 */
	public void remove(Collection<Image> images) {
		// remove from images
		Set<Image> library = XStreamBackup.getInstance().images;

		library.removeAll(images);
	}

	/**
	 * Save a collection of images
	 * 
	 * @param images
	 *            the images to be saved
	 */
	public void save(Collection<Image> images) {
		// add to library if not existing
		Set<Image> library = XStreamBackup.getInstance().images;

		library.addAll(images);
	}
}
