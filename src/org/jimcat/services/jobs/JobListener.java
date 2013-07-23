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

package org.jimcat.services.jobs;

/**
 * A interface describing a observer for a Job.
 * 
 * $Id: JobListener.java 329 2007-04-18 13:01:15Z 07g1t1u1 $
 * 
 * @author Herbert
 */
public interface JobListener {

	/**
	 * Informs about a state change.
	 * 
	 * @param job -
	 *            source of event
	 * @param oldState -
	 *            privious State
	 * @param newState -
	 *            current new State
	 * @param command -
	 *            command used to change state
	 */
	void stateChanged(Job job, JobState oldState, JobState newState, JobCommand command);

	/**
	 * Informs about a increased/decreased Process state
	 * 
	 * @param job -
	 *            the source
	 */
	void progressChanged(Job job);

	/**
	 * informes about a dicovert problem which requires user interaction.
	 * 
	 * @param job
	 * @param description
	 */
	void failerEmerged(Job job, JobFailureDescription description);

	/**
	 * informes the listener that the description has changed
	 * 
	 * @param job
	 */
	void descriptionChanged(Job job);
}
