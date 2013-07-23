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

import org.jimcat.model.tag.Tag;

/**
 * Wrapps a Set of Tags.
 * 
 * 
 * $Id$
 * 
 * @author Michael
 */
public class TagSetWrapper {
	/**
	 * the data stored in this wrapper
	 */
	private Set<Tag> tags;

	/**
	 * construct a new wrapper
	 * 
	 * @param tags
	 */
	public TagSetWrapper(Set<Tag> tags) {
		this.tags = tags;
	}

	/**
	 * @return the tags
	 */
	public Set<Tag> getTags() {
		return tags;
	}

	/**
	 * @param tags
	 *            the tags to set
	 */
	public void setTags(Set<Tag> tags) {
		this.tags = tags;
	}
}
