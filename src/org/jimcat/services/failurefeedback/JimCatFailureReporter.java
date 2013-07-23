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

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.jimcat.services.ServiceLocator;

/**
 * This appender is used to report failures recived through log4j to the sytem
 * internal FailureFeedbackService.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class JimCatFailureReporter extends AppenderSkeleton {

	/**
	 * Report a new message to the Failure Feedback Service
	 * 
	 * @see org.apache.log4j.AppenderSkeleton#append(org.apache.log4j.spi.LoggingEvent)
	 */
	@Override
	protected void append(LoggingEvent event) {
		Level level = event.getLevel();
		// report just errors and fatal failures
		if (Level.ERROR == level || Level.FATAL == level) {
			FailureDescription desc = new FailureDescription(event, "JimCat has discovered an error.");
			ServiceLocator.getFailureFeedbackService().reportFailure(desc);
		}
	}

	/**
	 * shutdown appender
	 * 
	 * @see org.apache.log4j.Appender#close()
	 */
	public void close() {
		// no operation needed
	}

	/**
	 * This Appender requires a layout
	 * 
	 * @see org.apache.log4j.Appender#requiresLayout()
	 */
	public boolean requiresLayout() {
		return false;
	}

}
