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

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * A testsuite co combine all test for the configuration modul.
 * 
 * $Id: AllConfigTests.java 329 2007-04-18 13:01:15Z 07g1t1u1 $
 * 
 * @author Herbert
 */
public class AllConfigTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for jimcat.tests.services.configuration");
		// $JUnit-BEGIN$
		suite.addTestSuite(TestConfiguration.class);
		// $JUnit-END$
		return suite;
	}

}
