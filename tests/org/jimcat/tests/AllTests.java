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

import junit.framework.Test;
import junit.framework.TestSuite;

import org.jimcat.tests.services.configuration.AllConfigTests;
import org.jimcat.tests.services.failurefeedback.AllFailureTests;
import org.jimcat.tests.services.instancecontrol.AllInstanceControlTests;
import org.jimcat.tests.services.jobs.AllJobTests;

public class AllTests {

	public static Test suite() {

		TestSuite suite = new TestSuite("All jimcat tests");

		suite.addTestSuite(TestProperties.class);

		suite.addTestSuite(TestImageLibrary.class);
		suite.addTestSuite(TestFilter.class);
		suite.addTestSuite(TestComparator.class);
		suite.addTestSuite(TestImage.class);
		suite.addTestSuite(TestRenamer.class);
		suite.addTestSuite(TestBeanListener.class);
		suite.addTestSuite(TestLibraryView.class);

		suite.addTest(AllJobTests.suite());
		suite.addTest(AllConfigTests.suite());
		suite.addTest(AllInstanceControlTests.suite());

		suite.addTest(AllFailureTests.suite());
		return suite;
	}
}
