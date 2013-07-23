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
import org.jimcat.model.SmartList;
import org.jimcat.model.filter.Filter;
import org.jimcat.model.filter.FilterCircleException;
import org.jimcat.model.filter.SmartListFilter;
import org.jimcat.model.filter.TagFilter;
import org.jimcat.model.filter.logical.AndFilter;
import org.jimcat.model.filter.logical.NotFilter;
import org.jimcat.model.filter.logical.OrFilter;
import org.jimcat.model.filter.metadata.TextFilter;
import org.jimcat.model.libraries.ImageLibrary;
import org.jimcat.model.libraries.LibraryView;
import org.jimcat.model.tag.Tag;
import org.jimcat.model.tag.TagGroup;
import org.jimcat.persistence.TagRepository;
import org.jimcat.persistence.mock.MockTagRepository;

/**
 * Filter test cases.
 * 
 * There are four images: a, b, c, d and three tags: x, y, z
 * 
 * a has tag x b has tag y c has tag x and y d has no tags
 * 
 * 
 * @author Christoph
 * 
 */
public class TestFilter extends JimcatTestCase {

	private Image a = createImage("a");

	private Image b = createImage("b");

	private Image c = createImage("c");

	private Image d = createImage("d");

	private Tag x = new Tag();

	private Tag y = new Tag();

	private Tag z = new Tag();

	private TagFilter filterX = new TagFilter(x);

	private TagFilter filterY = new TagFilter(y);

	private TagFilter filterZ = new TagFilter(z);

	private LibraryView view;

	private ImageLibrary library = ImageLibrary.getInstance();

	private TagRepository tagRepository = new MockTagRepository();

	private TagGroup originalTree;

	@Override
	protected void setUp() throws Exception {
		view = new LibraryView(ImageLibrary.getInstance());

		TagGroup tagTree = tagRepository.getTagTree();

		tagTree.addSubTag(x);
		tagTree.addSubTag(y);
		tagTree.addSubTag(z);

		tagRepository.save(tagTree);

		a.addTag(x);
		b.addTag(y);
		c.addTag(x);
		c.addTag(y);

		library.add(a);
		library.add(b);
		library.add(c);
		library.add(d);
	}

	/**
	 * 
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();

		library.remove(a);
		library.remove(b);
		library.remove(c);
		library.remove(d);

		tagRepository.save(originalTree);
	}

	public void testSimpleTagFilter() {

		view.setFilter(filterX);

		assertEquals(2, view.size());
		assertTrue(view.contains(a));
		assertTrue(view.contains(c));
		assertFalse(view.contains(b));
		assertFalse(view.contains(d));

		view.setFilter(new AndFilter(filterZ, filterX));
		assertEquals(0, view.size());
	}

	public void testNotFilter() {

		Filter notFilter = new NotFilter(filterX);

		view.setFilter(notFilter);

		assertEquals(2, view.size());
		assertFalse(view.contains(a));
		assertFalse(view.contains(c));
		assertTrue(view.contains(b));
		assertTrue(view.contains(d));
	}

	public void testAndFilter() {
		Filter andFilter = new AndFilter(filterX, filterY);

		view.setFilter(andFilter);

		assertEquals(1, view.size());
		assertTrue(view.contains(c));
	}

	public void testOrFilter() {
		Filter orFilter = new OrFilter(filterX, filterY);

		view.setFilter(orFilter);

		assertEquals(3, view.size());
		assertTrue(view.contains(a));
		assertTrue(view.contains(b));
		assertTrue(view.contains(c));
	}

	public void testNotAndFilter() {
		Filter andFilter = new AndFilter(filterX, filterY);
		Filter notAndFilter = new NotFilter(andFilter);

		view.setFilter(notAndFilter);

		assertEquals(3, view.size());
		assertTrue(view.contains(a));
		assertTrue(view.contains(b));
		assertTrue(view.contains(d));
	}

	public void testFilterLoop() {

		SmartList sl = new SmartList();
		SmartListFilter slf = new SmartListFilter(sl);

		Filter root = new AndFilter(slf, new TextFilter("a"));

		try {
			sl.setFilter(root);
			fail("circle");
		} catch (FilterCircleException e) {
			// empty
		}

		try {
			sl.setFilter(slf);
			fail("circle");
		} catch (FilterCircleException e) {
			// empty
		}

		sl.setFilter(new TextFilter("a"));
	}

	public void testX() {
		Filter andFilter = new AndFilter(new TextFilter("a"), null);
		SmartList sl = new SmartList();
		sl.setFilter(andFilter);
	}

}
