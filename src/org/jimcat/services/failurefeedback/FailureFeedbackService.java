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

package org.jimcat.services.failurefeedback;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The synchron service used to report errors.
 * 
 * $Id$
 * 
 * @author Michael/Herbert
 */
public final class FailureFeedbackService implements UncaughtExceptionHandler {

	/**
	 * singelton instance
	 */
	private static final FailureFeedbackService INSTANCE = new FailureFeedbackService();

	/**
	 * list of subscribers
	 */
	private List<FailureFeedbackListener> listeners;

	/**
	 * private default constructor
	 */
	private FailureFeedbackService() {
		// setup members
		listeners = new CopyOnWriteArrayList<FailureFeedbackListener>();
	}

	/**
	 * startup and install FailureFeedback Service
	 */
	public static void startUp() {
		// install consol listener
		INSTANCE.addFeedbackListener(new ConsoleFailureFeedbackListner());
		INSTANCE.addFeedbackListener(new LogFileFailureListener("jimcat_errors.txt"));

		// setup as default uncaught exception handler
		Thread.setDefaultUncaughtExceptionHandler(INSTANCE);
	}

	/**
	 * singelton getInstance
	 * 
	 * @return an instance of FailureFeedbackService
	 */
	public static FailureFeedbackService getInstance() {
		return INSTANCE;
	}

	/**
	 * report an emerged failure
	 * 
	 * @param description
	 */
	public void reportFailure(FailureDescription description) {
		// check if reported error is null => failure on its own
		if (description == null) {
			FailureDescription nullFailure = new FailureDescription(new NullPointerException(), "failure service",
			        "An internal error occured");
			notifyListeners(nullFailure);
		} else {
			// dispatch it
			notifyListeners(description);
		}
	}

	/**
	 * react on an uncaught exception
	 * 
	 * @see java.lang.Thread.UncaughtExceptionHandler#uncaughtException(java.lang.Thread,
	 *      java.lang.Throwable)
	 */
	public void uncaughtException(Thread t, Throwable e) {
		// build failuredescription
		String msg = e.toString();
		// e.getMessage();
		FailureDescription failure = new FailureDescription(e, t.getName(), msg);

		reportFailure(failure);
	}

	/**
	 * add a new listener
	 * 
	 * @param listener
	 */
	public void addFeedbackListener(FailureFeedbackListener listener) {
		listeners.add(listener);
	}

	/**
	 * remove a listener
	 * 
	 * @param listener
	 */
	public void removeFeedbackListener(FailureFeedbackListener listener) {
		listeners.remove(listener);
	}

	/**
	 * used to inform listeners
	 * 
	 * @param failure
	 */
	private void notifyListeners(FailureDescription failure) {
		for (FailureFeedbackListener listener : listeners) {
			listener.failureEmerged(failure);
		}
	}
}
