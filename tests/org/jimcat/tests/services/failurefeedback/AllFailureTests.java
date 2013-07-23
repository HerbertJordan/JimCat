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

package org.jimcat.tests.services.failurefeedback;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Collection of Failure-Service tests
 * 
 * $Id$
 * 
 * @author Michael/Herbert
 */
public class AllFailureTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.jimcat.tests.services.failurefeedback");
		// $JUnit-BEGIN$
		suite.addTestSuite(TestFailureDescription.class);
		// $JUnit-END$
		return suite;
	}

}
