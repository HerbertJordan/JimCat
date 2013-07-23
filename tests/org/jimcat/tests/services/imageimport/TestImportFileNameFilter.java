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

package org.jimcat.tests.services.imageimport;

import java.io.File;

import org.jimcat.services.imageimport.ImportFileNameFilter;
import org.jimcat.tests.JimcatTestCase;

/**
 * 
 * 
 * $Id$
 * 
 * @author Christoph
 */
public class TestImportFileNameFilter extends JimcatTestCase {

	public void testAccept() {
		ImportFileNameFilter filter = new ImportFileNameFilter();

		File dir = new File(getProperty("testdirectory"));

		assertTrue(filter.accept(dir, "foo.jpg"));
		assertTrue(filter.accept(dir, "foo.JPG"));
		assertTrue(filter.accept(dir, "foo.JpG"));
		assertTrue(filter.accept(dir, "file name with lots of...spaces and dots... .jpg"));

		assertFalse(filter.accept(null, "A.exe"));
	}
}
