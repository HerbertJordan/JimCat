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

package org.jimcat.tests.jobs;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.jimcat.services.jobs.Job;
import org.jimcat.services.jobs.JobCommand;
import org.jimcat.services.jobs.JobFailureDescription;
import org.jimcat.services.jobs.JobListener;
import org.jimcat.services.jobs.JobListenerAdapter;
import org.jimcat.services.jobs.JobManager;
import org.jimcat.services.jobs.JobState;
import org.jimcat.tests.JimcatTestCase;

/**
 * 
 * $Id: TestJobs.java 935 2007-06-15 09:21:09Z 07g1t1u2 $
 * 
 * @author Herbert
 */
public class TestJobs extends JimcatTestCase {

	private static final int MULTIPLE_ITERATIONS = 3;

	/**
	 * two queues to hand jobs over to the job
	 */
	private BlockingQueue<Runnable> runQueue = new LinkedBlockingQueue<Runnable>();

	private BlockingQueue<Runnable> rollbackQueue = new LinkedBlockingQueue<Runnable>();

	/**
	 * to switch rollback support
	 */
	private boolean supportsRollback = false;

	/**
	 * marks if preExecution was fired
	 */
	private Semaphore preExecutionSemaphore = new Semaphore(0);

	/**
	 * marks if postExecution was fired
	 */
	private Semaphore postExecutionSemaphore = new Semaphore(0);

	private JobFailureDescription description = null;

	/**
	 * used for synchronisation
	 */
	private Semaphore mux = new Semaphore(0);

	private Semaphore mux2 = new Semaphore(0);

	private Semaphore mux3 = new Semaphore(0);

	/**
	 * to check if work was done
	 */
	private int value = 0;

	/**
	 * the jobmanager for execution
	 */
	private JobManager jobManager = new JobManager();

	/**
	 * the testjob running elements from the runQueue and rollbackQueue
	 */
	private TestJob testJob = new TestJob(jobManager);

	/**
	 * test creation and starting of a job and its Listener (just stateChanged
	 * Event). it also test if the job can be aborted
	 * 
	 * @throws InterruptedException
	 */
	public void testJobStart() throws InterruptedException {

		// Test initial JobState
		assertEquals(testJob.getState(), JobState.PREPARING);

		// Test jobListener
		JobListener listener = new JobListenerAdapter() {
			/**
			 * @see org.jimcat.services.jobs.JobListenerAdapter#stateChanged(org.jimcat.services.jobs.Job,
			 *      org.jimcat.services.jobs.JobState,
			 *      org.jimcat.services.jobs.JobState,
			 *      org.jimcat.services.jobs.JobCommand)
			 */
			@Override
			@SuppressWarnings("unused")
			public void stateChanged(Job job, JobState oldState, JobState newState, JobCommand command) {
				if (job == testJob && oldState == JobState.PREPARING && newState == JobState.RUNNING
				        && command == JobCommand.START) {
					value = 1;
				}
			}
		};

		testJob.addJobListener(listener);

		// Job starten
		testJob.start();
		assertEquals("New State should be RUNNING", testJob.getState(), JobState.RUNNING);

		// Test JobListener
		assertEquals("Listener should have changed value to 1", value, 1);
		// reset
		value = 0;
		testJob.removeJobListener(listener);

		// add a job releasing a lock
		runQueue.put(new Runnable() {
			public void run() {
				value = 1;
				mux.release();
			}
		});

		// if lock is released, the job was done
		mux.acquireUninterruptibly();
		assertTrue("PreExectution was fired", preExecutionSemaphore.tryAcquire(1, TimeUnit.SECONDS));
		assertEquals("Work was not done", value, 1);
		assertEquals("State should be RUNNING", testJob.getState(), JobState.RUNNING);

		// test if job can be aborted
		testJob.cancel();
		assertEquals("Job should be aborted", testJob.getState(), JobState.ABORTED);
	}

	/**
	 * test if a job can be suspended It also test a proper job finishing
	 * 
	 * @throws InterruptedException
	 */
	public void testJobSuspend() throws InterruptedException {

		// start testJob
		testJob.start();

		// it should worke several times
		for (int i = 0; i < MULTIPLE_ITERATIONS; i++) {
			// Bring job in a working state
			runQueue.put(new Runnable() {
				public void run() {
					mux2.release();
					mux.acquireUninterruptibly();
				}
			});
			mux2.acquireUninterruptibly();

			// => now job is in working state

			// suspend job
			testJob.suspend();
			assertEquals("new JobState should be SUSPENDED", testJob.getState(), JobState.SUSPENDED);

			value = 0;
			// add another job
			runQueue.put(new Runnable() {
				public void run() {
					value = 1;
					mux3.release();
				}
			});

			// free job
			mux.release();

			// now the job shouldn't have done anything
			assertEquals("value shouldn't be changed", value, 0);
			assertEquals("JobState should be SUSPENDED", testJob.getState(), JobState.SUSPENDED);

			// //////////
			// RESUME
			// //////////

			testJob.resume();
			assertEquals("New JobState should be RUNNING", testJob.getState(), JobState.RUNNING);

			mux3.acquireUninterruptibly();
			// work should be done now
			assertEquals("Value should now be 1", value, 1);
		}

		// Test if job can be finished
		runQueue.put(new Runnable() {
			public void run() {
				testJob.callFinishedJob();
				mux.release();
			}
		});

		mux.acquireUninterruptibly();
		Thread.yield();
		assertEquals("Job should be in FINISHED State", testJob.getState(), JobState.FINISHED);
		assertTrue("PostExectuion should be called", postExecutionSemaphore.tryAcquire(1, TimeUnit.SECONDS));
	}

	/**
	 * test if the failer trap is working
	 * @throws InterruptedException
	 */
	public void testJobFailer() throws InterruptedException {

		// start TestJob
		value = 0;
		testJob.addJobListener(new JobListenerAdapter() {
			/**
			 * @see org.jimcat.services.jobs.JobListenerAdapter#failerEmerged(org.jimcat.services.jobs.Job,
			 *      org.jimcat.services.jobs.JobFailureDescription)
			 */
			@Override
			public void failerEmerged(Job job, JobFailureDescription desc) {
				if (job == testJob && description == desc) {
					value = 1;
				}
			}
		});

		testJob.start();

		for (int i = 0; i < MULTIPLE_ITERATIONS; i++) {
			value = 0;
			// simple Failer
			description = new JobFailureDescription();
			description.setDescription("Test Failer");
			runQueue.put(new Runnable() {
				public void run() {
					testJob.callFailer(description);
					mux.release();
				}
			});
			mux.acquireUninterruptibly();

			// Failer should have been posted
			assertEquals("Job should be in FAILURE state", testJob.getState(), JobState.FAILURE);
			assertEquals("No failer was discovered throw listener", value, 1);
			assertEquals("Wrong job description", testJob.getFailerDescription(), description);

			// resume should be possible
			value = 0;
			runQueue.put(new Runnable() {
				public void run() {
					value = 1;
					mux2.release();
				}
			});

			testJob.resume();
			assertEquals("State should have changed to RUNNING", testJob.getState(), JobState.RUNNING);

			mux2.acquireUninterruptibly();
			// work should have been done
			assertEquals("Work should have changed value to 1", value, 1);
		}
	}

	/**
	 * tests the change to rollback states
	 * 
	 * @throws InterruptedException
	 */
	public void testRollback() throws InterruptedException {

		// start job
		testJob.start();

		// test if Unsupported Rollback is checked
		supportsRollback = false;
		try {
			testJob.rollback();
			fail("Job shouldn't allow this");
		} catch (UnsupportedOperationException uoe) {
			// all fine
		}
		supportsRollback = true;

		// testJob might run nextStep first => insert empty job
		// to avoid deadlock
		runQueue.put(new Runnable() {
			public void run() {
				// nothing
				mux.acquireUninterruptibly();
			}
		});

		// now enable rollback
		testJob.rollback();
		mux.release();

		// now Job should be in UNDOING state
		assertEquals("Job should be in undoing state", testJob.getState(), JobState.UNDOING);

		// test if rollback look is executed
		for (int i = 0; i < MULTIPLE_ITERATIONS; i++) {
			value = 0;
			rollbackQueue.put(new Runnable() {
				public void run() {
					value = 1;
					mux2.release();
				}
			});
			mux2.acquireUninterruptibly();
			// now rollback should be done
			assertEquals("Rollback should change this value to 1", value, 1);
		}

		// Finish rollback
		try {
			testJob.callFinishedJob();
			fail("Call finishedJob shouldn't be allowed");
		} catch (IllegalStateException ise) {
			// all fine
		}
		testJob.callFinishedRollback();
		assertEquals("TestJob should be in state UNDONE", testJob.getState(), JobState.REVERTED);
	}

	/**
	 * This job is used to test the job system
	 * 
	 * $Id: TestJobs.java 935 2007-06-15 09:21:09Z 07g1t1u2 $
	 * 
	 * @author Herbert
	 */
	private class TestJob extends Job {

		/**
		 * only constructor required
		 * @param manager
		 */
		public TestJob(JobManager manager) {
			super(manager);
		}

		@Override
		public int getPercentage() {
			return -1;
		}

		/**
		 * @see org.jimcat.services.jobs.Job#preExecution()
		 */
		@Override
		public void preExecution() {
			preExecutionSemaphore.release();
		}

		/**
		 * @see org.jimcat.services.jobs.Job#postExecution()
		 */
		@Override
		public void postExecution() {
			postExecutionSemaphore.release();
		}

		@Override
		public boolean nextStep() {
			boolean jobdone = false;
			while (!jobdone) {
				try {
					runQueue.take().run();
					jobdone = true;
				} catch (InterruptedException ie) {
					// nothing
				}
			}
			return getState().isFinal();
		}

		@Override
		public boolean nextRollbackStep() {
			boolean jobdone = false;
			while (!jobdone) {
				try {
					rollbackQueue.take().run();
					jobdone = true;
				} catch (InterruptedException ie) {
					// nothing
				}
			}
			return getState().isFinal();
		}

		@Override
		public boolean supportsRollback() {
			return supportsRollback;
		}

		/**
		 * delegate call
		 * 
		 * @param desc the job failure description object
		 */
		public void callFailer(JobFailureDescription desc) {
			failer(desc);
		}

		/**
		 * delegate call
		 * 
		 * @param desc
		 */
		public void callHandleFailer(JobFailureDescription desc) {
			requestFailureHandling(desc);
		}

		/**
		 * delegate call
		 */
		public void callFinishedJob() {
			finishedJob();
		}

		/**
		 * delegate call
		 */
		public void callFinishedRollback() {
			finishedRollback();
		}
	}

}
