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

import java.util.Set;

import org.jimcat.model.tag.TagGroup;
import org.jimcat.persistence.TagRepository;

import com.db4o.query.Predicate;

/**
 * Tag repository for DB4O backend.
 * 
 * 
 * $Id$
 * 
 * @author Christoph
 */
public class Db4oTagRepository extends Db4oDao implements TagRepository {

	/**
	 * Get the root element of the tag tree
	 * 
	 * @return the root tagGroup of the tag tree
	 */
	public TagGroup getTagTree() {
		Set<TagGroup> result = get(new Predicate<TagGroup>() {
			@Override
			public boolean match(TagGroup tagGroup) {
				return tagGroup.isRoot();
			}
		});

		if (result.size() == 0) {
			return null;
		} else if (result.size() > 1) {
			throw new IllegalStateException("You have more than one Tag Root");
		}

		TagGroup root = result.iterator().next();
		return root;
	}

	/**
	 * Save the tag tree
	 * 
	 * @param tagGroup
	 */
	public void save(final TagGroup tagGroup) {
		set(tagGroup);
	}
}
