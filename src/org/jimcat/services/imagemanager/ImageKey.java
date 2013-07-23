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

package org.jimcat.services.imagemanager;

import java.awt.Dimension;
import java.io.File;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.jimcat.model.Image;
import org.jimcat.model.ImageRotation;

/**
 * a small databean used as image cache key.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class ImageKey {
	/**
	 * the image object describing the identified image
	 */
	private org.jimcat.model.Image img;

	/**
	 * dimension of identified image, null if it is original size
	 */
	private Dimension dim;

	/**
	 * the rotation of images referenced by this key
	 */
	private ImageRotation rotation;

	/**
	 * a direct constructor
	 * 
	 * @param img
	 * @param dim
	 */
	public ImageKey(Image img, Dimension dim) {
		this(img, dim, img.getRotation());
	}

	/**
	 * a constructor requesting all fields
	 * 
	 * @param img
	 * @param dim
	 * @param rotation
	 */
	public ImageKey(Image img, Dimension dim, ImageRotation rotation) {
		this.img = img;
		this.dim = dim;
		this.rotation = rotation;
	}

	/**
	 * @return the dim
	 */
	public Dimension getDim() {
		return dim;
	}

	/**
	 * @return the img
	 */
	public org.jimcat.model.Image getImg() {
		return img;
	}

	/**
	 * @return the rotation
	 */
	public ImageRotation getRotation() {
		return rotation;
	}

	/**
	 * get a hashcode for this class
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(img);
		builder.append(dim);
		builder.append(rotation);
		return builder.toHashCode();
	}

	/**
	 * check if this this and another key is equal.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		// if both references are equal
		if (obj == this) {
			// the objects are equal
			return true;
		}
		// other obj must be of this type
		if (!(obj instanceof ImageKey)) {
			return false;
		}

		// check content
		ImageKey other = (ImageKey) obj;

		// start with dimension
		if (!ObjectUtils.equals(this.dim, other.dim)) {
			return false;
		}

		// end with represented file.
		File file1 = this.img.getMetadata().getPath();
		File file2 = other.img.getMetadata().getPath();
		if (!ObjectUtils.equals(file1, file2)) {
			return false;
		}

		// check rotation
		if (!ObjectUtils.equals(rotation, other.rotation)) {
			return false;
		}

		// all tests passed => equal
		return true;

	}
}
