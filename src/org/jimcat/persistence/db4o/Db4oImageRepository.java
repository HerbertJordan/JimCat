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

package org.jimcat.persistence.db4o;

import java.util.Collection;
import java.util.Set;

import org.jimcat.model.Image;
import org.jimcat.persistence.ImageRepository;

/**
 * Image repository for DB4O backend.
 * 
 * $Id$
 * 
 * @author Christoph
 */
public class Db4oImageRepository extends Db4oDao implements ImageRepository {

	/**
	 * Load all images from the persistence layer.
	 * 
	 * @return a set of all images
	 */
	public Set<Image> getAll() {
		return getAll(Image.class);
	}

	/**
	 * Remove a collection of images
	 * 
	 * @param images
	 */
	public void remove(Collection<Image> images) {
		delete(images);
	}

	/**
	 * Save a collection of images
	 * 
	 * @param images
	 */
	public void save(final Collection<Image> images) {
		set(images);
	}
}
