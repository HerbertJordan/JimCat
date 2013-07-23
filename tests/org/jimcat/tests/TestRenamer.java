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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jimcat.model.ExifMetadata;
import org.jimcat.model.Image;
import org.jimcat.model.ImageMetadata;
import org.jimcat.model.ImageRating;
import org.jimcat.services.rename.Renamer;
import org.joda.time.DateTime;

/**
 * 
 * 
 * $Id$
 * 
 * @author Christoph
 */
public class TestRenamer extends JimcatTestCase {

	private Image imageA = new Image();

	private Image imageB = new Image();

	private Image imageC = new Image();

	private List<Image> images;

	private Renamer renamer;

	private List<String> newNames;

	private String escapeCharacter;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		images = new ArrayList<Image>();
		renamer = new Renamer();
		escapeCharacter = renamer.getEscapeCharacter();
		newNames = null;

		imageA.setTitle("titleA");
		imageB.setTitle("titleB");
		imageC.setTitle("titleC");

		imageA.setRating(ImageRating.ONE);
		imageB.setRating(ImageRating.TWO);
		imageC.setRating(ImageRating.THREE);

		imageA.setMetadata(getDummyMetadata("a.jpg"));
		imageB.setMetadata(getDummyMetadata("b.GIF"));
		imageC.setMetadata(getDummyMetadata("c.png"));

		imageA.setExifMetadata(getDummyExifMetadata());
		imageB.setExifMetadata(getDummyExifMetadata());
		imageC.setExifMetadata(getDummyExifMetadata());

		images.add(imageA);
		images.add(imageB);
		images.add(imageC);
	}

	public void testN() {

		renamer.setConfigString("foo " + escapeCharacter + "n");
		renamer.setDigits(3);

		newNames = renamer.getNewNames(images);

		assertEquals("foo 001", newNames.get(0));
		assertEquals("foo 002", newNames.get(1));
		assertEquals("foo 003", newNames.get(2));
	}

	public void testWH() {
		renamer.setConfigString("foo " + escapeCharacter + "wx" + escapeCharacter + "h new");
		newNames = getNewNames();

		assertEquals("foo 1024x768 new", newNames.get(0));
	}

	public void testDateTime() {
		renamer.setConfigString(escapeCharacter + "d." + escapeCharacter + "m." + escapeCharacter + "y "
		        + escapeCharacter + "H:" + escapeCharacter + "M:" + escapeCharacter + "S");

		newNames = getNewNames();

		assertEquals("16.05.2007 09:08:03", newNames.get(0));
	}

	public void testModificationDate() {
		renamer.setConfigString(escapeCharacter + "d." + escapeCharacter + "m." + escapeCharacter + "y "
		        + escapeCharacter + "H:" + escapeCharacter + "M:" + escapeCharacter + "S");
		renamer.useModificationDate();

		newNames = getNewNames();

		assertEquals("02.01.2007 23:23:23", newNames.get(0));
	}

	public void testDateAdded() {
		renamer.setConfigString(escapeCharacter + "d." + escapeCharacter + "m." + escapeCharacter + "y "
		        + escapeCharacter + "H:" + escapeCharacter + "M:" + escapeCharacter + "S");
		renamer.useDateAdded();

		newNames = getNewNames();

		assertEquals("03.01.2007 16:16:16", newNames.get(0));
	}

	public void testT() {
		renamer.setConfigString(escapeCharacter+"t new!");

		newNames = getNewNames();

		assertEquals("titleA new!", newNames.get(0));
		assertEquals("titleB new!", newNames.get(1));
		assertEquals("titleC new!", newNames.get(2));

		imageA.setTitle(escapeCharacter+"h "+escapeCharacter+"w");
		renamer.setConfigString(escapeCharacter+"t (new)");

		newNames = getNewNames();

		assertEquals(escapeCharacter+"h "+escapeCharacter+"w (new)", newNames.get(0));
	}

	public void testR() {
		renamer.setConfigString("rating: "+escapeCharacter+"r!");

		newNames = getNewNames();

		assertEquals("rating: 1!", newNames.get(0));
		assertEquals("rating: 2!", newNames.get(1));
		assertEquals("rating: 3!", newNames.get(2));
	}

	public void testPercent() {
		renamer.setConfigString(escapeCharacter+escapeCharacter+"n");
		newNames = getNewNames();
		assertEquals(escapeCharacter+"n", newNames.get(0));

		renamer.setConfigString(escapeCharacter+escapeCharacter);
		newNames = getNewNames();
		assertEquals(escapeCharacter, newNames.get(0));

		renamer.setConfigString("a"+escapeCharacter+escapeCharacter);
		newNames = getNewNames();
		assertEquals("a"+escapeCharacter, newNames.get(0));

		renamer.setConfigString(escapeCharacter);
		newNames = getNewNames();
		assertEquals("", newNames.get(0));
	}

	public void testFull() {
		renamer.setConfigString(escapeCharacter+escapeCharacter+escapeCharacter+"y-"+escapeCharacter+"m-"+escapeCharacter+"d "+escapeCharacter+"H:"+escapeCharacter+"M:"+escapeCharacter+"S, "+escapeCharacter+"wx"+escapeCharacter+"h "+escapeCharacter+"n, "+escapeCharacter+"r ("+escapeCharacter+"t)"+escapeCharacter+escapeCharacter+"new"+escapeCharacter+escapeCharacter+escapeCharacter+escapeCharacter);

		newNames = getNewNames();

		assertEquals(escapeCharacter+"2007-05-16 09:08:03, 1024x768 1, 1 (titleA)"+escapeCharacter+"new"+escapeCharacter+escapeCharacter, newNames.get(0));
		assertEquals(escapeCharacter+"2007-05-16 09:08:03, 1024x768 2, 2 (titleB)"+escapeCharacter+"new"+escapeCharacter+escapeCharacter, newNames.get(1));
		assertEquals(escapeCharacter+"2007-05-16 09:08:03, 1024x768 3, 3 (titleC)"+escapeCharacter+"new"+escapeCharacter+escapeCharacter, newNames.get(2));
	}

	public void testEnd() {
		renamer.setConfigString("foo "+escapeCharacter);
		newNames = getNewNames();

		assertEquals("foo ", newNames.get(0));
	}

	private List<String> getNewNames() {
		return renamer.getNewNames(images);
	}

	private ImageMetadata getDummyMetadata(String path) {
		DateTime modificationDate = new DateTime(2007, 1, 2, 23, 23, 23, 0); // 2.1.2007
		// 23:23:23.0
		DateTime dateAdded = new DateTime(2007, 1, 3, 16, 16, 16, 0); // 3.1.2007
		// 16:16:16.0
		return new ImageMetadata(new File(path), 1024, 768, 0, "", 0, modificationDate, dateAdded);
	}

	private ExifMetadata getDummyExifMetadata() {
		DateTime dateTaken = new DateTime(2007, 5, 16, 9, 8, 3, 0); // 16.5.2007
		// 09:08:03.0

		return new ExifMetadata("canon", "350d", dateTaken, "1", "2", "on", "2", "100");
	}
}
