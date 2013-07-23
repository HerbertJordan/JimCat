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

package org.jimcat.gui.smartlisteditor.model;

import static org.jimcat.model.filter.metadata.ImageSizeFilter.Type.BIGGER_THAN;
import static org.jimcat.model.filter.metadata.ImageSizeFilter.Type.HEIGHER_THAN;
import static org.jimcat.model.filter.metadata.ImageSizeFilter.Type.LOWER_THAN;
import static org.jimcat.model.filter.metadata.ImageSizeFilter.Type.SMALLER_THAN;
import static org.jimcat.model.filter.metadata.ImageSizeFilter.Type.THINER_THAN;
import static org.jimcat.model.filter.metadata.ImageSizeFilter.Type.WIDER_THAN;

import org.apache.commons.lang.ObjectUtils;
import org.jimcat.model.filter.Filter;
import org.jimcat.model.filter.metadata.ImageSizeFilter;
import org.jimcat.model.filter.metadata.ImageSizeFilter.Type;

/**
 * A node representing a image size filter
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class ImageSizeFilterNode extends FilterTreeNode {

	/**
	 * the type of the represented filter
	 */
	private Type type;

	/**
	 * the value of width
	 */
	private int width;

	/**
	 * the value of height
	 */
	private int height;

	/**
	 * generate a new node using the given filter
	 * 
	 * @param parent
	 * @param filter
	 */
	public ImageSizeFilterNode(GroupFilterTreeNode parent, ImageSizeFilter filter) {
		super(parent, true);
		this.type = filter.getImageSizeCompareMode();
		this.width = filter.getWidth();
		this.height = filter.getHeight();
	}

	/**
	 * regenerate the titel of this filter node
	 * 
	 * @see org.jimcat.gui.smartlisteditor.model.FilterTreeNode#generateTitle()
	 */
	@Override
	public String generateTitle() {
		String linkingWord = "";
		if (BIGGER_THAN.equals(type) || SMALLER_THAN.equals(type)) {
			// both dimensions
			if (BIGGER_THAN.equals(type)) {
				linkingWord = "bigger";
			} else {
				linkingWord = "smaller";
			}
			return "must be " + linkingWord + " than " + width + "/" + height + " pixel";
		} else if (WIDER_THAN.equals(type) || THINER_THAN.equals(type)) {
			// only width
			if (WIDER_THAN.equals(type)) {
				linkingWord = "wider";
			} else {
				linkingWord = "thinner";
			}
			return "must be " + linkingWord + " than " + width + " pixel";
		} else {
			// only height
			if (HEIGHER_THAN.equals(type)) {
				linkingWord = "heigher";
			} else {
				linkingWord = "lower";
			}
			return "must be " + linkingWord + " than " + height + " pixel";
		}
	}

	/**
	 * generate filter from node
	 * 
	 * @see org.jimcat.gui.smartlisteditor.model.FilterTreeNode#getFilter()
	 */
	@Override
	public Filter getFilter() {
		// just generate filter from members
		return new ImageSizeFilter(type, width, height);
	}

	/**
	 * negating means actually switching type
	 * 
	 * @see org.jimcat.gui.smartlisteditor.model.FilterTreeNode#setNegate(boolean)
	 */
	@Override
	public void setNegate(boolean negate) {
		if (isNegate() != negate) {
			// switch type
			if (BIGGER_THAN.equals(type)) {
				type = SMALLER_THAN;
			} else if (SMALLER_THAN.equals(type)) {
				type = BIGGER_THAN;
			} else if (HEIGHER_THAN.equals(type)) {
				type = LOWER_THAN;
			} else if (LOWER_THAN.equals(type)) {
				type = HEIGHER_THAN;
			} else if (WIDER_THAN.equals(type)) {
				type = THINER_THAN;
			} else if (THINER_THAN.equals(type)) {
				type = WIDER_THAN;
			}
		}
		// rest is done by super implementation
		super.setNegate(negate);
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @param height
	 *            the height to set
	 */
	public void setHeight(int height) {
		int oldValue = this.height;
		this.height = height;

		// inform listeners
		if (oldValue != height) {
			fireTreeNodeChange(this);
		}
	}

	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(Type type) {
		Type oldValue = this.type;
		this.type = type;

		// inform listeners
		if (!ObjectUtils.equals(oldValue, type)) {
			fireTreeNodeChange(this);
		}
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @param width
	 *            the width to set
	 */
	public void setWidth(int width) {
		int oldValue = this.width;
		this.width = width;

		// inform listeners
		if (oldValue != width) {
			fireTreeNodeChange(this);
		}
	}

}
