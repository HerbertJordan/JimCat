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
 * An empty implementation of an JobListener
 * 
 * $Id: JobListenerAdapter.java 329 2007-04-18 13:01:15Z 07g1t1u1 $
 * 
 * @author Herbert
 */
public class JobListenerAdapter implements JobListener {

	/**
	 * @see org.jimcat.services.jobs.JobListener#progressChanged(org.jimcat.services.jobs.Job)
	 */
	@SuppressWarnings("unused")
	public void progressChanged(Job job) {
		// leaf empty
	}

	/**
	 * @see org.jimcat.services.jobs.JobListener#stateChanged(org.jimcat.services.jobs.Job,
	 *      org.jimcat.services.jobs.JobState, org.jimcat.services.jobs.JobState,
	 *      org.jimcat.services.jobs.JobCommand)
	 */
	@SuppressWarnings("unused")
	public void stateChanged(Job job, JobState oldState, JobState newState, JobCommand command) {
		// leaf empty
	}

	/**
	 * @see org.jimcat.services.jobs.JobListener#failerEmerged(org.jimcat.services.jobs.Job,
	 *      org.jimcat.services.jobs.JobFailureDescription)
	 */
	@SuppressWarnings("unused")
	public void failerEmerged(Job job, JobFailureDescription description) {
		// leaf empty
	}

	/**
	 * @see org.jimcat.services.jobs.JobListener#descriptionChanged(org.jimcat.services.jobs.Job)
	 */
	@SuppressWarnings("unused")
	public void descriptionChanged(Job job) {
		// leaf empty
	}

}
