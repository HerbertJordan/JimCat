/*
 *  This file is part of jimcat.
 *
 *  jimcat is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation version 2.
 *
 *  jimcat is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with jimcat; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.jimcat.tests;

/**
 * Tests loading of properties. If you add a property needed for a testcase
 * please add it to this test so it fails hard and fast.
 * 
 * 
 * $Id$
 * 
 * @author Christoph
 */
public class TestProperties extends JimcatTestCase {

	public void testForConfigValues() {

		try {
			getProperty("importdirectory");
			getProperty("testdirectory");
		} catch (IllegalStateException e) {
			String msg = e.getMessage() + " Please add this config setting to your tests.properties";
			fail(msg);
		}

		try {
			getProperty("weirdproperty");
			fail("There should be an exception for properties not found");
		} catch (IllegalStateException e) {
			// this should happen
		}
	}
}
