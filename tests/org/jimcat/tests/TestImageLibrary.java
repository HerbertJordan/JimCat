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

import java.util.HashSet;
import java.util.Set;

import org.jimcat.model.Image;
import org.jimcat.model.libraries.ImageLibrary;

public class TestImageLibrary extends JimcatTestCase {

	private ImageLibrary library = ImageLibrary.getInstance();

	public void testAddImage() {

		Image newImage = createImage("foo");
		assertFalse(library.contains(newImage));

		library.add(newImage);

		assertTrue(library.contains(newImage));

		library.remove(newImage);
	}

	public void testRemoveImage() {

		int size = library.size();

		Image a = createImage("a");
		Image b = createImage("b");
		Image c = createImage("c");
		Image d = createImage("d");

		library.add(a);
		library.add(b);
		library.add(c);
		library.add(d);

		assertEquals(size + 4, library.size());

		library.remove(a);

		assertEquals(size + 3, library.size());

		Image x = createImage("x");
		library.remove(x);
		assertEquals(size + 3, library.size());

		// cleanup
		library.remove(b);
		library.remove(c);
		library.remove(d);
	}

	public void testRemoveAll() {

		Set<Image> images = new HashSet<Image>();

		Image a = createImage("a");
		Image b = createImage("b");
		Image c = createImage("c");
		Image d = createImage("d");

		int size = library.size();

		images.add(a);
		images.add(d);

		library.add(a);
		library.add(b);
		library.add(c);

		assertEquals(size + 3, library.size());

		library.remove(images);
		assertEquals(size + 2, library.size());

		// clean up
		library.remove(b);
		library.remove(c);
	}
}
