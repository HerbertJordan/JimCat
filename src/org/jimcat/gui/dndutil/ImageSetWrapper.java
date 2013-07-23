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

package org.jimcat.gui.dndutil;

import java.util.Set;

import org.jimcat.model.Image;

/**
 * This class is used to wrap the storage of a list of images.
 * 
 * This is needed for drag&drop to make sure that the components only accept
 * sets of images and no sets of other things.
 * 
 * 
 * $Id$
 * 
 * @author Michael
 */
public class ImageSetWrapper {
	/**
	 * The data stored in this wrapper
	 */
	private Set<Image> images;

	/**
	 * construct a new wrapper
	 * 
	 * @param images
	 */
	public ImageSetWrapper(Set<Image> images) {
		this.images = images;
	}

	/**
	 * @return the images
	 */
	public Set<Image> getImages() {
		return images;
	}

	/**
	 * @param images
	 *            the images to set
	 */
	public void setImages(Set<Image> images) {
		this.images = images;
	}
}
