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

import java.util.List;
import java.util.Set;

import org.jimcat.model.Image;
import org.jimcat.model.filter.TagFilter;
import org.jimcat.model.libraries.ImageLibrary;
import org.jimcat.model.libraries.LibraryView;
import org.jimcat.model.notification.BeanChangeEvent;
import org.jimcat.model.notification.CollectionListener;
import org.jimcat.model.tag.Tag;

/**
 * 
 * 
 * $Id: TestLibraryView.java 999 2007-09-14 20:02:58Z cleiter $
 * 
 * @author Christoph
 */
public class TestLibraryView extends JimcatTestCase {

	private ImageLibrary library = ImageLibrary.getInstance();

	private boolean added = false;

	private boolean removed = false;

	private boolean exchanged = false;

	private boolean updated = false;

	@Override
	protected void setUp() throws Exception {
		library.removeAll();
	}

	public void testAddNotification() {
		LibraryView ic = new LibraryView(ImageLibrary.getInstance());

		assertEquals(0, ic.size());

		library.add(new Image());

		assertEquals(1, ic.size());
	}

	public void testNotificationListener() {
		final LibraryView ic = new LibraryView(ImageLibrary.getInstance());

		assertEquals(0, ic.size());
		final Image image = new Image();

		ic.addListener(new CollectionListener<Image, LibraryView>() {

			@SuppressWarnings("unused")
			public void elementsAdded(LibraryView collection, Set<Image> elements) {
				if (elements.iterator().next().equals(image)) {
					added = true;
				}
			}

			@SuppressWarnings("unused")
			public void elementsRemoved(LibraryView collection, Set<Image> elements) {
				if (elements.iterator().next().equals(image)) {
					removed = true;
				}
			}

			public void basementChanged(LibraryView collection) {
				if (collection.equals(library)) {
					exchanged = true;
				}
			}

			public void elementsUpdated(LibraryView collection, List<BeanChangeEvent<Image>> events) {
				if (collection.equals(ic) && events.get(0).getSource().equals(image)) {
					updated = !updated;
				}
			}

		});

		library.add(image);
		assertEquals(1, ic.size());
		assertTrue(added);
		assertFalse(removed);
		assertFalse(exchanged);
		assertFalse(updated);

		image.setTitle("test");
		assertEquals(1, ic.size());
		assertTrue(added);
		assertFalse(removed);
		assertFalse(exchanged);
		assertTrue(updated);

		library.remove(image);
		assertTrue(removed);
		assertFalse(exchanged);
		assertTrue(updated);
		assertEquals(0, ic.size());

	}

	public void testFilteredAdd() {

		// create a tag and a corresponding filter
		Tag tag = new Tag();
		TagFilter tagFilter = new TagFilter(tag);

		// and two images, one having the tag
		Image matchImage = new Image();
		Image notMatchImage = new Image();
		matchImage.addTag(tag);

		// a new IC with the filter
		LibraryView ic = new LibraryView(ImageLibrary.getInstance());
		ic.setFilter(tagFilter);

		assertEquals(0, ic.size());

		// if we add an image to the library only the filtered one must
		// be added
		library.add(matchImage);
		assertEquals(1, ic.size());
		library.add(notMatchImage);
		assertEquals(1, ic.size());

		// if we reset the filter we shall see all images
		ic.setFilter(null);
		assertEquals(2, ic.size());

		// and setting the filter again
		ic.setFilter(tagFilter);
		assertEquals(1, ic.size());
	}
}
