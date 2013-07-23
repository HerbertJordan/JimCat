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
 * An enumeration of possible jobstates.
 * 
 * Those are the states a job can have during execution.
 * <ul>
 * <li>Preparing - before execution</li>
 * <li>Running - job is ready to work or is working</li>
 * <li>Finished - job has finished its work</li>
 * <li>Failure - job required (user) interaction but will continue afterward</li>
 * <li>Suspended - job is suspended</li>
 * <li>Undoing - job is reverting allready executed modifications</li>
 * <li>UndoFailure - Failer during rollback</li>
 * <li>UndoSuspended - job is suspended during undo</li>
 * <li>Reverted - job finished rollback</li>
 * <li>Aborted - job has finished without finishing its work</li>
 * </ul>
 * 
 * $Id: JobState.java 935 2007-06-15 09:21:09Z 07g1t1u2 $
 * 
 * @author Herbert
 */
public enum JobState {
	PREPARING(false, true, false) {
		/**
		 * @see org.jimcat.services.jobs.JobState#getStateFollowingCommand(org.jimcat.services.jobs.JobCommand)
		 */
		@Override
		public JobState getStateFollowingCommand(JobCommand command) {
			if (command == JobCommand.START) {
				return RUNNING;
			}
			return null;
		}
	},
	RUNNING(false, true, false) {
		/**
		 * @see org.jimcat.services.jobs.JobState#getStateFollowingCommand(org.jimcat.services.jobs.JobCommand)
		 */
		@Override
		public JobState getStateFollowingCommand(JobCommand command) {
			switch (command) {
			case FAILURE:
				return FAILURE;
			case SUSPEND:
				return SUSPENDED;
			case ROLLBACK:
				return UNDOING;
			case CANCEL:
				return ABORTED;
			case FINISHJOB:
				return FINISHED;
			default:
				return null;
			}
		}
	},
	FINISHED(true, true, false) {
		/**
		 * @see org.jimcat.services.jobs.JobState#getStateFollowingCommand(org.jimcat.services.jobs.JobCommand)
		 */
		@Override
		@SuppressWarnings("unused")
		public JobState getStateFollowingCommand(JobCommand command) {
			// there is no allowed transistion
			return null;
		}
	},
	FAILURE(false, false, false) {
		/**
		 * @see org.jimcat.services.jobs.JobState#getStateFollowingCommand(org.jimcat.services.jobs.JobCommand)
		 */
		@Override
		public JobState getStateFollowingCommand(JobCommand command) {
			switch (command) {
			case RESUME:
				return RUNNING;
			case ROLLBACK:
				return UNDOING;
			case CANCEL:
				return ABORTED;
			default:
				return null;
			}
		}
	},
	SUSPENDED(false, false, false) {
		/**
		 * @see org.jimcat.services.jobs.JobState#getStateFollowingCommand(org.jimcat.services.jobs.JobCommand)
		 */
		@Override
		public JobState getStateFollowingCommand(JobCommand command) {
			switch (command) {
			case RESUME:
				return RUNNING;
			case ROLLBACK:
				return UNDOING;
			case CANCEL:
				return ABORTED;
			case FAILURE:
				return FAILURE;
			case FINISHJOB:
				return FINISHED;
			default:
				return null;
			}
		}
	},
	UNDOING(false, true, true) {
		/**
		 * @see org.jimcat.services.jobs.JobState#getStateFollowingCommand(org.jimcat.services.jobs.JobCommand)
		 */
		@Override
		public JobState getStateFollowingCommand(JobCommand command) {
			switch (command) {
			case FAILURE:
				return UNDOFAILURE;
			case SUSPEND:
				return UNDOSUSPENDED;
			case CANCEL:
				return ABORTED;
			case FINISHROLLBACK:
				return REVERTED;
			default:
				return null;
			}
		}
	},
	UNDOFAILURE(false, false, true) {
		/**
		 * @see org.jimcat.services.jobs.JobState#getStateFollowingCommand(org.jimcat.services.jobs.JobCommand)
		 */
		@Override
		public JobState getStateFollowingCommand(JobCommand command) {
			switch (command) {
			case RESUME:
				return UNDOING;
			case CANCEL:
				return ABORTED;
			default:
				return null;
			}
		}
	},
	UNDOSUSPENDED(false, false, true) {
		/**
		 * @see org.jimcat.services.jobs.JobState#getStateFollowingCommand(org.jimcat.services.jobs.JobCommand)
		 */
		@Override
		public JobState getStateFollowingCommand(JobCommand command) {
			switch (command) {
			case RESUME:
				return UNDOING;
			case CANCEL:
				return ABORTED;
			case FAILURE:
				return UNDOFAILURE;
			case FINISHROLLBACK:
				return REVERTED;
			default:
				return null;
			}
		}
	},
	REVERTED(true, true, true) {
		/**
		 * @see org.jimcat.services.jobs.JobState#getStateFollowingCommand(org.jimcat.services.jobs.JobCommand)
		 */
		@Override
		@SuppressWarnings("unused")
		public JobState getStateFollowingCommand(JobCommand command) {
			// no possible transistion
			return null;
		}
	},
	ABORTED(true, true, false) {
		/**
		 * @see org.jimcat.services.jobs.JobState#getStateFollowingCommand(org.jimcat.services.jobs.JobCommand)
		 */
		@Override
		@SuppressWarnings("unused")
		public JobState getStateFollowingCommand(JobCommand command) {
			switch (command) {
			case FINISHJOB:
				return FINISHED;
			case FINISHROLLBACK:
				return REVERTED;
			default:
				return null;
			}
		}
	};

	/**
	 * is this state a final state?
	 */
	private boolean isFinal;

	/**
	 * is this state a non-runnable state?
	 */
	private boolean runable;

	/**
	 * is this state a state within the undoing section?
	 */
	private boolean undoingState;

	/**
	 * internal constructor
	 * 
	 * @param isFinal
	 *            if the state is final and can't be changed
	 * @param runable
	 *            if the job is runable
	 * @param undoingState
	 *            if the job is currently undoing action
	 */
	private JobState(boolean isFinal, boolean runable, boolean undoingState) {
		this.isFinal = isFinal;
		this.runable = runable;
		this.undoingState = undoingState;
	}

	/**
	 * this methode will return the state the job shout change into if this
	 * state is processing the given command.
	 * 
	 * return null if this command isn't allowed in this state.
	 * 
	 * @param command -
	 *            the command processed within this state
	 * @return - the following states
	 */
	public abstract JobState getStateFollowingCommand(JobCommand command);

	/**
	 * @return isFinal
	 */
	public final boolean isFinal() {
		return isFinal;
	}

	/**
	 * @return the runable
	 */
	public boolean isRunable() {
		return runable;
	}

	/**
	 * @return the undoingState
	 */
	public boolean isUndoingState() {
		return undoingState;
	}

}
