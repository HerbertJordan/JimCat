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

package org.jimcat.tests.services.jobs;

import java.util.concurrent.Semaphore;

import junit.framework.TestCase;

import org.jimcat.services.jobs.Job;
import org.jimcat.services.jobs.JobManager;
import org.jimcat.services.jobs.JobManagerListener;
import org.jimcat.services.jobs.JobState;

/**
 * This TestCase should test the JobManager and its associated Listeners.
 * 
 * $Id: TestJobManager.java 926 2007-06-14 14:22:05Z 07g1t1u2 $
 * 
 * @author Herbert
 */
public class TestJobManager extends TestCase {

	private JobManager myManager = new JobManager();

	/**
	 * attributes needed for listener checks counts active jobs
	 */
	private int countActive = 0;

	/**
	 * counts finished jobs
	 */
	private int countFinished = 0;

	/**
	 * last seen active jobs (by listener)
	 */
	private Job lastActiveJob = null;

	/**
	 * last seen finished job (by listener)
	 */
	private Job lastFinishedJob = null;

	/**
	 * was the commited manager the right one?
	 */
	private boolean rightManager = false;

	/**
	 * checks the JobManger functionality
	 */
	public void testJobManager() {

		// create some jobs
		TestJob jobs[] = { new TestJob(), new TestJob(), new TestJob() };

		// add manager Listener
		myManager.addJobManagerListener(new JobManagerListener() {

			public void finishedListFlushed(JobManager manager) {
				rightManager = (manager == myManager);
				countFinished = 0;
			}

			public void jobAddedToActiveList(JobManager manager, Job job) {
				rightManager = (manager == myManager);
				countActive++;
				lastActiveJob = job;
			}

			public void jobAddedToFinishedList(JobManager manager, Job job) {
				rightManager = (manager == myManager);
				countFinished++;
				lastFinishedJob = job;
			}

			public void jobRemovedFromActiveList(JobManager manager, Job job) {
				rightManager = (manager == myManager);
				countActive--;
				lastActiveJob = job;
			}

		});

		// test start state
		assertEquals("Shouldn't have any jobs running", myManager.getActiveJobs().size(), 0);
		assertEquals("Shouldn't have any jobs finished", myManager.getFinishedJobs().size(), 0);

		// add jobs
		for (int i = 0; i < jobs.length; i++) {
			// execute job with jobmanager
			myManager.excecuteJob(jobs[i]);

			// check reactions
			assertEquals("Should have " + (i + 1) + " jobs running", myManager.getActiveJobs().size(), i + 1);
			assertEquals("Shouldn't have any jobs finished", myManager.getFinishedJobs().size(), 0);
			assertEquals("Listener should have count " + (i + 1) + " active Jobs", countActive, i + 1);
			assertEquals("Listener shouldn't have count any finished jobs", countFinished, 0);
			assertTrue("Listener should have seen right manager", rightManager);
			assertEquals("Listener should have seen right active Job", lastActiveJob, jobs[i]);
			assertTrue("Manager should know job", myManager.getActiveJobs().contains(jobs[i]));

			// checkJob
			assertEquals("Job should be running", jobs[i].getState(), JobState.RUNNING);
		}

		// start finishing jobs
		int active = jobs.length;
		int finished = 0;
		for (int i = 0; i < jobs.length - 1; i++) {
			// finish job
			jobs[i].finishJob();
			active--;
			finished++;

			// check reactions
			assertEquals("Should have " + active + " jobs running", myManager.getActiveJobs().size(), active);
			assertEquals("Should have " + finished + " jobs finished", myManager.getFinishedJobs().size(), finished);
			assertEquals("Listener should have count " + active + " active Jobs", countActive, active);
			assertEquals("Listener should have count " + finished + " finished jobs", countFinished, finished);
			assertTrue("Listener should have seen right manager", rightManager);
			assertEquals("Listener should have seen right active Job", lastActiveJob, jobs[i]);
			assertEquals("Listener should have seen right finished Job", lastFinishedJob, jobs[i]);
			assertFalse("Job shouldn't be within active list", myManager.getActiveJobs().contains(jobs[i]));
			assertTrue("Job should be within finished list", myManager.getFinishedJobs().contains(jobs[i]));

			// checkJobState
			assertEquals("Job should be running", jobs[i].getState(), JobState.FINISHED);
		}

		// finally flush jobs
		myManager.clearFinishedJobs();
		finished = 0;
		assertEquals("Should have " + active + " jobs running", myManager.getActiveJobs().size(), active);
		assertEquals("Should have " + finished + " jobs finished", myManager.getFinishedJobs().size(), finished);
		assertEquals("Listener should have count " + active + " active Jobs", countActive, active);
		assertEquals("Listener should have count " + finished + " finished jobs", countFinished, finished);
		assertTrue("Listener should have seen right manager", rightManager);

	}

	/**
	 * a small Job for test purposes it will block until finishJob is called.
	 * 
	 * $Id: TestJobManager.java 926 2007-06-14 14:22:05Z 07g1t1u2 $
	 * 
	 * @author Herbert
	 */
	private class TestJob extends Job {

		/**
		 * semaphore need to controll test-Jobs
		 */
		private Semaphore finishJob = new Semaphore(0);

		/**
		 * need for testJob-response
		 */
		private Semaphore finishedJob = new Semaphore(0);

		/**
		 * use this methode to finish this job
		 */
		public void finishJob() {
			if (!getState().isFinal()) {
				finishJob.release();
				finishedJob.acquireUninterruptibly();
			}
		}

		@Override
		public int getPercentage() {
			// not important
			return 0;
		}

		@Override
		public boolean nextRollbackStep() {
			// isn't supported, shouldn't work
			return false;
		}

		@Override
		public boolean nextStep() {
			finishJob.acquireUninterruptibly();
			return true;
		}

		/**
		 * marks the end of the job
		 * 
		 * @see org.jimcat.services.jobs.Job#postExecution()
		 */
		@Override
		public void postExecution() {
			finishedJob.release();
		}

		@Override
		public boolean supportsRollback() {
			return false;
		}
	}
}
