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

import java.util.TooManyListenersException;

import junit.framework.TestCase;

import org.jimcat.services.failurefeedback.FailureDescription;

/**
 * Small test to check FailureDescription functions
 * 
 * $Id$
 * 
 * @author Michael/Herbert
 */
public class TestFailureDescription extends TestCase {

	private static boolean PRINT_RESULT = false;

	public void testToString() {
		// build failure description
		Throwable cause = new IllegalArgumentException("some message", new TooManyListenersException(
		        "some other message"));
		String threadName = Thread.currentThread().getName();
		FailureDescription failure = new FailureDescription(cause, threadName, "test failure");

		// Try to string
		String report = failure.toString();

		if (PRINT_RESULT) {
			System.out.println(report);
		}
	}

}
