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

import static org.jimcat.services.jobs.JobCommand.CANCEL;
import static org.jimcat.services.jobs.JobCommand.RESUME;
import static org.jimcat.services.jobs.JobCommand.ROLLBACK;
import static org.jimcat.services.jobs.JobCommand.START;
import static org.jimcat.services.jobs.JobCommand.SUSPEND;
import static org.jimcat.services.jobs.JobState.RUNNING;
import static org.jimcat.services.jobs.JobState.UNDOING;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;

import org.apache.commons.lang.ObjectUtils;
import org.jimcat.services.ServiceLocator;
import org.jimcat.services.failurefeedback.FailureDescription;

/**
 * This class forms a framework for all kind of jobs.
 * 
 * The job can be executed in a synchronous way or by using an JobManger. If the
 * jobManager is null by calling the start() methode it will run within the
 * current thread. Otherwise it will use the JobManager for execution, which
 * might be asynchron.
 * 
 * Inherit from this class if you wish to execute a job through the JobManager.
 * It is implementing necessary state transitions an execution controlls.
 * 
 * How To Subclass: If you want to implement a individual job you have two
 * choices:
 * <ul>
 * <li>implement nextStep and nextRollbackStep - simple, iterativ jobs</li>
 * <li>override run() by using provided jobmanaging methodes - complex type</li>
 * </ul>
 * 
 * 1. simple, iterativ jobs (recommended)
 * 
 * To implement a simple job which is just doing a small action plenty of times
 * override this methodes:
 * <ul>
 * <li>{@link #preExecution() preExectuion}</li>
 * <li>{@link #nextStep() nextStep}</li>
 * <li>{@link #nextRollbackStep() nextRollbackStep}</li>
 * <li>{@link #preExecution() postExectuion}</li>
 * <li>{@link #getPercentage() getPercentage}</li>
 * <li>{@link #supportsRollback() supportesRollback}</li>
 * </ul>
 * The default run methode is managing the rest.
 * 
 * Use the pre- and postExecution methodes for preparation and cleanups.
 * 
 * The methodes nextStep() and nextRollbackStep() are used like a loopbody. The
 * default run methode will call it as long as they require it by there return
 * value or the job isn't killed through cancel. It will also enforce suspension
 * times. If your job takes a longer period of time and you are able to split it
 * up into smaller atomar peaces try to perform just one single step per call.
 * 
 * To get userinteraction you can use the methodes failer and
 * requestFailerHandling. Please keep an eye on the differnce. The first is
 * non-blocking, the second is blocking, which might mostly be the better
 * solution.
 * 
 * If you want to support the suspended state at any other place within a step
 * you might call checkState().
 * 
 * 
 * 2. complex jobs
 * 
 * If your job is more complex than a simple loop or you wish to finetune
 * actions you can override the run methode, although you still will have to
 * implement some of the methodes mentioned before, you might leave them empty.
 * 
 * To control job-flow you should use checkState at least within every loop
 * iteration. You can also use the synchron and asynchron failure handling
 * methodes to interact with the user / other threads.
 * 
 * In addition to these possibilities, by implementing the complex job you are
 * also responsible for adapting your actions to the current state. Therefore,
 * you should do your work within the state RUNNING and you should revert it in
 * state UNDOING. You can get the current job from checkState() or getState().
 * 
 * You are also responsible to keep registerd listeners up to date on your
 * current progress. Therefore call
 * {@link #fireStateChangedEvent(JobState, JobState, JobCommand) fireStateChangedEvent}
 * once in a while.
 * 
 * Finally you also have to call the methodes finishedJob and finishedRollback
 * if your job has finished in eighter way.
 * 
 * 
 * $Id: Job.java 935 2007-06-15 09:21:09Z 07g1t1u2 $
 * 
 * @author Herbert
 */
public abstract class Job implements Runnable {

	/**
	 * a list of registered JobListeners
	 */
	private List<JobListener> listeners;

	/**
	 * the current state of this Job
	 * 
	 * @see org.jimcat.services.jobs.JobState
	 */
	private JobState state;

	/**
	 * this Semaphore is used to suspend a job.
	 */
	private Semaphore suspendSemaphore;

	/**
	 * a lock for critical Code segments concerning the state
	 */
	private Object stateLock;

	/**
	 * a lock for critical Code segments concerning the failerDescription
	 */
	private Object failerDescriptionLock;

	/**
	 * the jobmanager used by this Job. if null, no manager is used, job is
	 * executed synchron.
	 */
	private JobManager jobManager;

	/**
	 * a description for a failer
	 */
	private JobFailureDescription failureDescription;

	/**
	 * the name of this Job
	 */
	private String jobName;

	/**
	 * a short description of this job or Null
	 */
	private String jobDescription;

	/**
	 * Only constructor of a Job. It creates a Job in PREPAIRE State.
	 */
	public Job() {
		this(null, null, null);
	}

	/**
	 * constructor to hand over a jobmanager.
	 * 
	 * @param manager -
	 *            the jobManager used to execute
	 */
	public Job(JobManager manager) {
		this(manager, null, null);
	}

	/**
	 * 
	 * This constructor creats a new job using the given fields.
	 * 
	 * @param manager -
	 *            the jobManager used to execute this job.
	 * @param name -
	 *            the name of this job, e.g. Image Import
	 * @param description -
	 *            a short description for the job
	 */
	public Job(JobManager manager, String name, String description) {
		// Job values
		state = JobState.PREPARING;
		jobManager = manager;
		jobName = name;
		jobDescription = description;

		// Listener management
		listeners = new CopyOnWriteArrayList<JobListener>();

		// concurrency management
		suspendSemaphore = new Semaphore(1);
		stateLock = new Object();
		failerDescriptionLock = new Object();
	}

	/**
	 * should return an approximate value of the current progress state.
	 * 
	 * @return - a nummber between 0 and 100, -1 if not known
	 */
	public abstract int getPercentage();

	/**
	 * This methode is called before execution of any step. Subclasses may use
	 * it to do preparation like aquiring resources.
	 */
	public void preExecution() {
		// default nothing
	}

	/**
	 * A call to this methode should perform the next atomar step.
	 * 
	 * Try to keep required time to perform this step low. It has to be
	 * possible to revert all effects by calling nextRollbackStep().
	 * 
	 * If this has been the last step within this job return true. If more steps
	 * are needed return false.
	 * 
	 * @see Job#nextRollbackStep()
	 * 
	 * @return false - more steps needed, true - no more steps needed, job
	 *         finished
	 */
	public abstract boolean nextStep();

	/**
	 * A call to this methode should revert a priviously performed atomar step.
	 * 
	 * Try to keep required time to perform this step low. A repeated call to
	 * this methode should make all effects of priviouslly called nextStep
	 * Operations undone.
	 * 
	 * If this call has been the last necessare to complate the rollback return
	 * true. If more steps are required, return false;
	 * 
	 * @see Job#nextStep()
	 * 
	 * @return false - more steps needed, true - no more steps needed, job
	 *         finished
	 */
	public abstract boolean nextRollbackStep();

	/**
	 * This methode is called after execution of any step. It is the last
	 * Methode executed by the execution plan. Use it to release any resources
	 * aquired.
	 */
	public void postExecution() {
		// default nothing
	}

	/**
	 * This methode is used to determine if this Job is supporting a Rollback.
	 * 
	 * @return true if so, false if rollbacks arn't supported
	 */
	public abstract boolean supportsRollback();

	/**
	 * Main execution loop and disturbion control.
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public final void run() {
		try {
			JobState lastState = null;

			// 1. Prepair Job
			preExecution();

			// 2. Execution loop
			boolean finished = false;
			lastState = checkState();
			while (lastState == RUNNING && !finished) {
				// make a step
				finished = nextStep();

				// informe listeners about change
				fireProgressChangedEvent();

				// get current state and maybe wait
				lastState = checkState();
			}

			// if job is finished, update state
			if (finished) {
				try {
					finishedJob();
				} catch (IllegalStateException ise) {
					// there has been a rollback command meanwhile
					// => do rollback
				}
			}

			finished = false;
			lastState = checkState();
			while (lastState == UNDOING && !finished) {
				// make a RollbackStep
				finished = nextRollbackStep();

				// informe listeners about change
				fireProgressChangedEvent();

				// get current state and maybe wait
				lastState = checkState();
			}

			// set to final state
			if (finished) {
				// set to finish state
				// Any fired exception would be an error
				finishedRollback();
			}

			// 3. Cleanup
			postExecution();

		} catch (Throwable e) {
			// inform user
			setJobDescription("Unsuspected Error occured");
			// cancel job
			if (!getState().isFinal()) {
				cancel();
			}
			// send error report
			String msg = "Unsuspected Error occured in job: " + getJobName();
			FailureDescription description = new FailureDescription(e,"Job " + getJobName(), msg);
			ServiceLocator.getFailureFeedbackService().reportFailure(description);
		}
	}

	/**
	 * call this methode to start execution.
	 * 
	 * @throws IllegalStateException
	 */
	public final void start() throws IllegalStateException {
		// change state
		makeStateTransition(START);

		// run job
		if (jobManager != null) {
			// use jobmanager to run job
			jobManager.excecuteJob(this);
		} else {
			// call synchron
			run();
		}
	}

	/**
	 * this will suspend the current Job. This methode can be called if the Job
	 * is in RUNNING or UNDOING state.
	 * 
	 * @throws IllegalStateException
	 */
	public final void suspend() throws IllegalStateException {
		makeStateTransition(SUSPEND);
	}

	/**
	 * this will cause the job to continue. The methode can be called if the job
	 * is in state SUSPENDED or UNDOSUSPENDED.
	 * 
	 * @throws IllegalStateException
	 */
	public final void resume() throws IllegalStateException {
		makeStateTransition(RESUME);
	}

	/**
	 * this will cause the Job to abourt its work and start undoing priviously
	 * performed operations. Can be called if the job is in state RUNNING,
	 * FAILER or SUSPENDED
	 * 
	 * @throws IllegalStateException
	 * @throws UnsupportedOperationException -
	 *             if rollback isn't supported
	 */
	public final void rollback() throws IllegalStateException, UnsupportedOperationException {
		makeStateTransition(ROLLBACK);
	}

	/**
	 * this will cause the Job to cancel. No rollback activision will be
	 * performed.
	 * 
	 * Can be called in any state
	 * 
	 * @throws IllegalStateException
	 */
	public final void cancel() throws IllegalStateException {
		makeStateTransition(CANCEL);
	}

	/**
	 * this will bring the job to a failer state. use the discription to
	 * describe the error. The job will remaine unexecutable until resume will
	 * be called.
	 * 
	 * The methode is none-blocking. Therefore use checkState() to suspend job
	 * until the failer might be corrected.
	 * 
	 * This event could only be fired from the job itself.
	 * 
	 * Can be called in any non-final, non Prepair state.
	 * 
	 * @param description
	 * @throws IllegalStateException
	 */
	protected final void failer(JobFailureDescription description) throws IllegalStateException {
		// set failerdescription
		synchronized (failerDescriptionLock) {
			failureDescription = description;
		}

		// make state transistion
		makeStateTransition(JobCommand.FAILURE);

		// inform listeners
		for (JobListener listener : listeners) {
			listener.failerEmerged(this, description);
		}
	}

	/**
	 * This will bring the job to a failer state. Use the description to
	 * describe the error. The job will remaine unexecutable until resume will
	 * be called.
	 * 
	 * The methode is blocking. Therefore, the current job will be suspended
	 * until another user / thread / job is calling resume (hopefully after
	 * handling the problem). Nevertheless you can not count on this. You have
	 * to check if the problem was solved while the job was suspended afterward.
	 * 
	 * This event could only be fired from the job itself.
	 * 
	 * Can be called in any non-final, non Prepair state.
	 * 
	 * @param description
	 * @throws IllegalStateException
	 */
	protected final void requestFailureHandling(JobFailureDescription description) throws IllegalStateException {
		// commit failer
		failer(description);

		// wait until runable
		suspendSemaphore.acquireUninterruptibly();
		suspendSemaphore.release();
	}

	/**
	 * This methode is called if all work is done and the job should get into a
	 * finished state.
	 * 
	 * This event could only be fired from the job itself.
	 * 
	 * @throws IllegalStateException
	 */
	protected final void finishedJob() throws IllegalStateException {
		makeStateTransition(JobCommand.FINISHJOB);
	}

	/**
	 * This methode is called if all rollback work is done and the job should
	 * get into a finished state.
	 * 
	 * This event could only be fired from the job itself.
	 * 
	 * @throws IllegalStateException
	 */
	protected final void finishedRollback() throws IllegalStateException {
		makeStateTransition(JobCommand.FINISHROLLBACK);
	}

	/**
	 * checks if the this job is still in PREPAIRING state
	 * 
	 * @throws IllegalStateException -
	 *             it job isn't in right state
	 */
	protected void checkConfigState() throws IllegalStateException {
		if (getState() != JobState.PREPARING) {
			throw new IllegalStateException("Only PREPARING state allowes configuration of job");
		}
	}

	/**
	 * register a new JobListener
	 * 
	 * @param listener -
	 *            the new listener
	 */
	public void addJobListener(JobListener listener) {
		listeners.add(listener);
	}

	/**
	 * unregister a JobListener
	 * 
	 * @param listener -
	 *            the listener to unregister
	 */
	public void removeJobListener(JobListener listener) {
		listeners.remove(listener);
	}

	/**
	 * @return the jobManager
	 */
	public JobManager getJobManager() {
		return jobManager;
	}

	/**
	 * Retrievs the current failerDescription Object. It might become invalide
	 * imediatly after retreaving
	 * 
	 * @return the failerDescription
	 */
	public Object getFailerDescription() {
		synchronized (failerDescriptionLock) {
			return failureDescription;
		}
	}

	/**
	 * Gets the current state of this job. It may become invalid soon.
	 * 
	 * @return the state
	 */
	public JobState getState() {
		synchronized (stateLock) {
			return state;
		}
	}

	/**
	 * @param jobManager
	 *            the jobManager to set
	 * @throws IllegalStateException
	 *             if this job isn't in Prepair states
	 */
	public void setJobManager(JobManager jobManager) throws IllegalStateException {
		if (getState() != JobState.PREPARING) {
			throw new IllegalStateException("JobManager can only be changed in state " + JobState.PREPARING);
		}
		this.jobManager = jobManager;
	}

	/**
	 * @return the jobDescription
	 */
	public String getJobDescription() {
		return jobDescription;
	}

	/**
	 * updates the JobDescription of this job - an event will be fired
	 * @param description the job description
	 */
	public void setJobDescription(String description) {
		// if there is no change, do nothing
		if (ObjectUtils.equals(jobDescription, description)) {
			return;
		}

		// exchange
		jobDescription = description;

		// inform listeners
		for (JobListener listener : listeners) {
			listener.descriptionChanged(this);
		}
	}

	/**
	 * @return the jobName
	 */
	public String getJobName() {
		return jobName;
	}

	/**
	 * helper methode to check running state. it may block.
	 * 
	 * @return true - job can continue, false if not
	 */
	protected JobState checkState() {
		// check if job is suspended
		// to test if the job is suspended try aquireing the lock
		// if suspended the job will automatically wait
		suspendSemaphore.acquireUninterruptibly();
		suspendSemaphore.release();

		// check state after suspension
		synchronized (stateLock) {
			// be fair
			Thread.yield();
			return state;
		}
	}

	/**
	 * Central state transition helper. This methode will perform a
	 * synchronized state-change. It will also modify the suspendSemaphore
	 * appropriate.
	 * 
	 * @param command -
	 *            a Command triggering the change
	 * @throws IllegalStateException -
	 *             if current state doesn't support this command
	 * @throws UnsupportedOperationException -
	 *             if this command isn't supported by any state
	 */
	private void makeStateTransition(JobCommand command) throws IllegalStateException, UnsupportedOperationException {
		// Check if rollbacks are supported
		if (command == ROLLBACK && !supportsRollback()) {
			throw new UnsupportedOperationException("Rollback isn't supported by this job");
		}

		JobState newState;
		JobState oldState;
		// get new State - modeling transitions
		synchronized (stateLock) {
			oldState = state;
			newState = oldState.getStateFollowingCommand(command);

			// if there is no change (shouldn't be)
			if (newState == oldState) {
				return;
			}

			// if command is not supported
			if (newState == null) {
				throw new IllegalStateException("Job Command " + command + " not supported by state " + state);
			}

			// suspend or unsuspend
			if (oldState.isRunable() && !newState.isRunable()) {
				// not runable since now
				suspendSemaphore.acquireUninterruptibly();
			} else if (!oldState.isRunable() && newState.isRunable()) {
				// now again runable
				suspendSemaphore.release();
			}

			// set new state
			state = newState;

			// inform listeners - must be inside the lock to keep order
			fireStateChangedEvent(oldState, newState, command);
		}
	}

	/**
	 * informs all listeners about a job status change.
	 * 
	 * @param oldState -
	 *            the old state
	 * @param newState -
	 *            the new state
	 * @param command -
	 *            triggering command
	 */
	private void fireStateChangedEvent(JobState oldState, JobState newState, JobCommand command) {
		for (JobListener listener : listeners) {
			listener.stateChanged(this, oldState, newState, command);
		}
	}

	/**
	 * informs all listeners about a progress change
	 */
	protected void fireProgressChangedEvent() {
		for (JobListener listener : listeners) {
			listener.progressChanged(this);
		}
	}
}
