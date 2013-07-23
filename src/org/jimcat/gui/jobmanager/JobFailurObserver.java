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

package org.jimcat.gui.jobmanager;

import org.jimcat.gui.SwingClient;
import org.jimcat.services.jobs.Job;
import org.jimcat.services.jobs.JobCommand;
import org.jimcat.services.jobs.JobFailureDescription;
import org.jimcat.services.jobs.JobListener;
import org.jimcat.services.jobs.JobState;

/**
 * this listener is just waiting for a job-failure to handle.
 *
 * $Id$
 * @author Herbert
 */
public class JobFailurObserver implements JobListener {

	/**
	 * just to implement the interface, does nothing
	 * @see org.jimcat.services.jobs.JobListener#descriptionChanged(org.jimcat.services.jobs.Job)
	 */
	@SuppressWarnings("unused")
	public void descriptionChanged(Job job) {
		//do nothing
	}

	/**
	 * react on failure
	 * @see org.jimcat.services.jobs.JobListener#failerEmerged(org.jimcat.services.jobs.Job, org.jimcat.services.jobs.JobFailureDescription)
	 */
	@SuppressWarnings("unused")
	public void failerEmerged(Job job, JobFailureDescription description) {
		//select response
		SwingClient.getInstance().showJobFailure(description);
		//resume job
		job.resume();
	}

	/**
	 * just to implement the interface, does nothing
	 * @see org.jimcat.services.jobs.JobListener#progressChanged(org.jimcat.services.jobs.Job)
	 */
	@SuppressWarnings("unused")
	public void progressChanged(Job job) {
		// do nothing
	}

	/**
	 * just to implement the interface, does nothing
	 * @see org.jimcat.services.jobs.JobListener#stateChanged(org.jimcat.services.jobs.Job, org.jimcat.services.jobs.JobState, org.jimcat.services.jobs.JobState, org.jimcat.services.jobs.JobCommand)
	 */
	@SuppressWarnings("unused")
	public void stateChanged(Job job, JobState oldState, JobState newState, JobCommand command) {
		// do nothing
	}

}
