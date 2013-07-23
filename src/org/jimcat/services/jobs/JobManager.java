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

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class is responsible for executiong jobs.
 * 
 * This is done by delegation to an internal executor. It also maintanes two job
 * lists. Active jobs and finished jobs.
 * 
 * $Id: JobManager.java 934 2007-06-15 08:40:58Z 07g1t1u2 $
 * 
 * @author Herbert
 */
public class JobManager {

	/**
	 * executor internally used for execution of jobs.
	 */
	private ExecutorService myExecutor;

	private List<JobManagerListener> subscribers;

	/**
	 * a list of active Jobs.
	 */
	private List<Job> activJobs;

	/**
	 * a list of finished jobs.
	 */
	private List<Job> finishedJobs;

	/**
	 * a JobListener responsible for bringing finished jobs to the done List
	 */
	private JobListener finishedJobHandler = new JobListenerAdapter() {
		/**
		 * Implement its automated removal
		 * 
		 * @see org.jimcat.services.jobs.JobListenerAdapter#stateChanged(org.jimcat.services.jobs.Job,
		 *      org.jimcat.services.jobs.JobState,
		 *      org.jimcat.services.jobs.JobState,
		 *      org.jimcat.services.jobs.JobCommand)
		 */
		@Override
		@SuppressWarnings("unused")
		public void stateChanged(Job source, JobState oldState, JobState newState, JobCommand command) {
			if (newState.isFinal()) {
				remActivJob(source);
				addFinishedJob(source);
			}
		}
	};

	/**
	 * default constructor
	 */
	public JobManager() {
		myExecutor = Executors.newCachedThreadPool();
		activJobs = new CopyOnWriteArrayList<Job>();
		finishedJobs = new CopyOnWriteArrayList<Job>();
		subscribers = new CopyOnWriteArrayList<JobManagerListener>();
	}

	/**
	 * registers and executes a Job
	 * 
	 * @param job
	 * @throws IllegalStateException -
	 *             if job isn't in state RUNNING
	 * 
	 */
	public final void excecuteJob(Job job) throws IllegalStateException {
		JobState state = job.getState();

		// if executiong a job without starting it => start it
		if (state == JobState.PREPARING) {
			job.setJobManager(this);
			job.start();
			// start will call register
			return;
		}

		// Running is the only state after Prepair
		if (state != JobState.RUNNING) {
			throw new IllegalStateException("Job isn't runnable, current state: " + state);
		}

		// add job to joblist
		addActivJob(job);

		// Add automated unregister to job
		job.addJobListener(finishedJobHandler);

		// execute job
		myExecutor.execute(job);
	}

	/**
	 * register a new JobManagerListener
	 * 
	 * @param listener -
	 *            the new Listner
	 * @see JobManagerListener
	 */
	public void addJobManagerListener(JobManagerListener listener) {
		subscribers.add(listener);
	}

	/**
	 * unregisters a JobManagerListener
	 * 
	 * @param listener -
	 *            the Listner
	 * @see JobManagerListener
	 */
	public void remJobManagerListener(JobManagerListener listener) {
		subscribers.remove(listener);
	}

	/**
	 * returns a list of active jobs. use this list to control the running jobs.
	 * 
	 * @return a list of all active jobs
	 */
	public List<Job> getActiveJobs() {
		return Collections.unmodifiableList(activJobs);
	}

	/**
	 * get a list of finished jobs
	 * 
	 * @return a list of finished jobs
	 */
	public List<Job> getFinishedJobs() {
		return Collections.unmodifiableList(finishedJobs);
	}

	/**
	 * clears the list of finished jobs
	 */
	public void clearFinishedJobs() {
		finishedJobs.clear();
		for (JobManagerListener listener : subscribers) {
			listener.finishedListFlushed(this);
		}
	}

	/**
	 * this will shutdown this jobmanager. it will through an
	 * IllegalStateException if there are still unfinished jobs.
	 * 
	 * @throws IllegalStateException -
	 *             if there are unfinished jobs
	 */
	public void shutdown() throws IllegalStateException {
		if (getActiveJobs().size() > 0) {
			throw new IllegalStateException("Jobs still running");
		}
		myExecutor.shutdown();
	}

	/**
	 * add an active job for the list
	 * 
	 * @param job
	 */
	private void addActivJob(Job job) {
		if (activJobs.contains(job)) {
			return;
		}
		activJobs.add(job);
		for (JobManagerListener listener : subscribers) {
			listener.jobAddedToActiveList(this, job);
		}
	}

	/**
	 * rem an active job
	 * 
	 * @param job
	 */
	private void remActivJob(Job job) {
		if (!activJobs.contains(job)) {
			return;
		}
		activJobs.remove(job);
		for (JobManagerListener listener : subscribers) {
			listener.jobRemovedFromActiveList(this, job);
		}
	}

	/**
	 * add a finished job for the list
	 * 
	 * @param job
	 */
	private void addFinishedJob(Job job) {
		if (finishedJobs.contains(job)) {
			return;
		}
		finishedJobs.add(job);
		for (JobManagerListener listener : subscribers) {
			listener.jobAddedToFinishedList(this, job);
		}
	}
}
