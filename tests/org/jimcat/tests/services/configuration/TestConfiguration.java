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

package org.jimcat.tests.services.configuration;

import junit.framework.TestCase;

import org.jimcat.services.configuration.Configuration;
import org.jimcat.services.configuration.Configuration.ConfigurationValueNotFoundException;

/**
 * A small testcase to test if the configuration is working. It isn't testing
 * the database subsystem.
 * 
 * $Id: TestConfiguration.java 554 2007-05-09 22:20:37Z 07g1t1u3 $
 * 
 * @author Christoph
 */
public class TestConfiguration extends TestCase {

	/**
	 * mal test only tests the two getValue() methodes of an entry
	 */
	public void testEntry() {
		
//		assertEquals(12245, Configuration.getInt("instance.controlport"));

		Configuration.set("test.foo", "lala");
		Configuration.set("test.bar", 42);
		
		assertEquals("lala", Configuration.getString("test.foo"));
		assertEquals(42, Configuration.getInt("test.bar"));
		
		try {
			Configuration.getString("test.nonexistant");
			Configuration.getInt("test.nonexistant");
			fail();
		} catch (ConfigurationValueNotFoundException e) {
			/* ok */
		}
		
		
		Configuration.remove("test.foo");
		
		try {
			Configuration.getString("test.foo");
			fail();
		} catch (ConfigurationValueNotFoundException e) {
			/* ok */
		}
		
		
	}
}
