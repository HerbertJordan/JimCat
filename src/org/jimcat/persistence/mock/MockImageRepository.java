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

package org.jimcat.persistence.mock;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jimcat.model.Image;
import org.jimcat.persistence.ImageRepository;

/**
 * The MockImageRepository is used for Testing.
 * 
 * 
 * $Id$
 * 
 * @author Christoph
 */
public class MockImageRepository implements ImageRepository {

	private Set<Image> imageList = new HashSet<Image>();

	/**
	 * 
	 * delete a single image
	 * 
	 * @param image
	 */
	public void remove(Image image) {
		remove(Collections.singleton(image));
	}

	/**
	 * 
	 * return all images in the repository
	 * 
	 * @see org.jimcat.persistence.Repository#getAll()
	 */
	public Set<Image> getAll() {
		return Collections.unmodifiableSet(imageList);
	}

	/**
	 * 
	 * save an image
	 * 
	 * @param image
	 */
	public void save(Image image) {
		save(Collections.singleton(image));
	}

	/**
	 * remove a collection of images
	 * 
	 * @see org.jimcat.persistence.ImageRepository#remove(java.util.Collection)
	 */
	public void remove(Collection<Image> images) {
		for (Image image : images) {
			imageList.remove(image);
		}
	}

	/**
	 * save a collection of images
	 * 
	 * @see org.jimcat.persistence.ImageRepository#save(java.util.Collection)
	 */
	public void save(Collection<Image> images) {
		for (Image image : images) {
			imageList.add(image);
		}
	}

}
