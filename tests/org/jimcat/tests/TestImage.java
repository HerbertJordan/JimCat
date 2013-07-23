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

package org.jimcat.tests;

import org.jimcat.model.Image;
import org.jimcat.model.tag.Tag;

/**
 * 
 * 
 * $Id$
 * 
 * @author Christoph
 */
public class TestImage extends JimcatTestCase {

	public void testTagAssignment() {

		Image image = new Image();
		Tag tag = new Tag();

		assertFalse(image.hasTag(tag));

		image.addTag(tag);
		assertTrue(image.hasTag(tag));

		// try to assign it once more
		image.addTag(tag);
		image.removeTag(tag);
		// now the tag should be removed even if assigned twice
		assertFalse(image.hasTag(tag));
	}
}
