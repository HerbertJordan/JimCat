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

import junit.framework.TestCase;

import org.jimcat.model.Image;
import org.jimcat.model.ImageMetadata;
import org.jimcat.model.comparator.ChecksumComparator;
import org.jimcat.model.comparator.ComparatorChainProxy;
import org.jimcat.model.comparator.DateAddedComparator;
import org.jimcat.model.comparator.RatingComparator;
import org.joda.time.DateTime;

/**
 * 
 * 
 * $Id$
 * 
 * @author Michael Handler
 */
public class TestComparator extends TestCase {

	public void testAddComparatorComparatorOfT() {
		ComparatorChainProxy<Image> testChain = new ComparatorChainProxy<Image>();
		Image im1 = new Image();
		Image im2 = new Image();
		im1.setMetadata(new ImageMetadata(null, 10, 10, 20, null, 1, new DateTime(), new DateTime()));
		try {
			testChain.compare(im1, im2);
			fail("No comparator did differ between images.");
		} catch (UnsupportedOperationException ue) {
			// go on
		}

		testChain.addComparator(new ChecksumComparator());
		if (testChain.compare(im1, im2) <= 0)
			fail("Adding of Comparator did not work.");
	}

	/**
	 */
	public void testAddComparatorComparatorOfTBoolean() {
		ComparatorChainProxy<Image> testChain = new ComparatorChainProxy<Image>();
		Image im1 = new Image();
		Image im2 = new Image();
		im1.setMetadata(new ImageMetadata(null, 10, 10, 20, null, 1, new DateTime(), new DateTime()));
		im2.setMetadata(null);
		testChain.addComparator(new DateAddedComparator(), true);
		if (testChain.compare(im1, im2) >= 0)
			fail("Adding of Comparator with reverse comparing did not work.");
	}

	/**
	 */
	public void testSetComparatorIntComparatorOfT() {
		ComparatorChainProxy<Image> testChain = new ComparatorChainProxy<Image>();
		Image im1 = new Image();
		Image im2 = new Image();
		im1.setMetadata(new ImageMetadata(null, 10, 10, 20, null, 1, new DateTime(), new DateTime()));
		testChain.addComparator(new ChecksumComparator());
		if (testChain.compare(im1, im2) <= 0)
			fail("Adding of Comparator did not work.");
		testChain = new ComparatorChainProxy<Image>();
		testChain.addComparator(new RatingComparator());
	}

}
