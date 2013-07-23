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

import org.apache.commons.lang.ObjectUtils;
import org.jimcat.model.Image;
import org.jimcat.model.libraries.TagHierarchy;
import org.jimcat.model.tag.Tag;

/**
 * 
 * A Tag filter is a filter that checks if an image has a a tag or not.
 * 
 * 
 * $Id: TagFilter.java 998 2007-08-29 20:36:25Z cleiter $
 * 
 * @author Herbert
 */

public class TagFilter extends Filter {

	private Tag tag;

	/**
	 * 
	 * construct a new TagFilter for given tag
	 * 
	 * @param tag
	 */
	public TagFilter(Tag tag) {
		this.tag = tag;
	}

	/**
	 * test if given image matches this filter
	 * 
	 * @see org.jimcat.model.filter.Filter#matches(org.jimcat.model.Image)
	 */
	@Override
	public boolean matches(Image image) {
		return image.hasTag(tag);
	}

	/**
	 * 
	 * @return the tag associated to this filter
	 */
	public Tag getTag() {
		return tag;
	}

	/**
	 * Overrides the equal methode to support comparison
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof TagFilter) {
			TagFilter other = (TagFilter) obj;
			return ObjectUtils.equals(other.tag, this.tag);
		}
		return false;
	}

	/**
	 * checks if the stored album still exists
	 * 
	 * @see org.jimcat.model.filter.Filter#getCleanVersion()
	 */
	@Override
	public Filter getCleanVersion() {
		if (TagHierarchy.getInstance().contains(tag)) {
			// filter is still ok
			return new TagFilter(tag);
		}
		// filter must be deleted
		return null;
	}

	/**
	 * get a set of images owning this tag
	 * 
	 * @see org.jimcat.model.filter.Filter#possibleMembers()
	 */
	@Override
	public Set<Image> possibleMembers() {
		return new HashSet<Image>(tag.getImages());
	}
}
