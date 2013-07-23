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

package org.jimcat.services.operations;

import org.jimcat.persistence.RepositoryControl;
import org.jimcat.persistence.RepositoryLocator;
import org.jimcat.services.OperationsLocator;
import org.jimcat.services.ServiceLocator;
import org.jimcat.services.SystemOperations;
import org.jimcat.services.configuration.Configuration;
import org.jimcat.services.failurefeedback.FailureFeedbackListener;
import org.jimcat.services.failurefeedback.FailureFeedbackService;
import org.jimcat.services.imagemanager.ImageManager;
import org.jimcat.services.instancecontrol.InstanceControl;
import org.jimcat.services.instancecontrol.InstanceListener;
import org.jimcat.services.jobs.JobManager;

/**
 * This class is a simple implementation for the SystemOperations facade.
 * 
 * $Id: SystemOperationsImpl.java 614 2007-05-15 19:10:33Z 07g1t1u1 $
 * 
 * @author Herbert
 */
public class SystemOperationsImpl implements SystemOperations {

	/**
	 * the InstanceControl of this System.
	 */
	private InstanceControl instanceControl = ServiceLocator.getInstanceControl();

	/**
	 * the up and running FailureFeedback Service
	 */
	private FailureFeedbackService feedbackService = ServiceLocator.getFailureFeedbackService();
	
	/**
	 * Implementing this function through delegation to Configuration.
	 * 
	 * @see org.jimcat.services.SystemOperations#getProperty(java.lang.String)
	 * @see org.jimcat.services.configuration.Configuration
	 */
	public String getProperty(String key) {
		// i'm not sure if we need this
		if (true) {
			throw new UnsupportedOperationException();
		}

		return Configuration.getString(key);
	}

	/**
	 * Implementing this function through delegation to Configuration.
	 * 
	 * @see org.jimcat.services.SystemOperations#setProperty(java.lang.String,
	 *      java.lang.String)
	 * @see org.jimcat.services.configuration.Configuration
	 */
	public void setProperty(String key, String value) {
		// i'm not sure if we need this
		if (true) {
			throw new UnsupportedOperationException();
		}

		Configuration.set(key, value);
	}

	/**
	 * Implementing this funktion through delegation
	 * 
	 * @see org.jimcat.services.SystemOperations#addInstanceListener(org.jimcat.services.instancecontrol.InstanceListener)
	 * @see org.jimcat.services.instancecontrol.InstanceControl
	 */
	public void addInstanceListener(InstanceListener listener) {
		instanceControl.addInstanceListener(listener);
	}

	/**
	 * Implemented through delegation.
	 * 
	 * @see org.jimcat.services.SystemOperations#removeInstanceListener(org.jimcat.services.instancecontrol.InstanceListener)
	 * @see org.jimcat.services.instancecontrol.InstanceControl
	 */
	public void removeInstanceListener(InstanceListener listener) {
		instanceControl.removeInstanceListener(listener);
	}

	/**
	 * add FailureFeedbackListener
	 * @param listener
	 */
	public void addFailureFeedbackListener(FailureFeedbackListener listener) {
		feedbackService.addFeedbackListener(listener);
	}
	
	/**
	 * remove FailureFeedbackListener
	 * @param listener
	 */
	public void removeFailureFeedbackListener(FailureFeedbackListener listener) {
		feedbackService.removeFeedbackListener(listener);
	}
	
	/**
	 * Initating shutdown process ...
	 * 
	 * @see org.jimcat.services.SystemOperations#shutdown()
	 * 
	 */
	public void shutdown() throws IllegalStateException {
		// check Jobsystem - no running jobs are allowed
		JobManager jobManager = OperationsLocator.getJobOperations().getJobManager();

		if (jobManager.getActiveJobs().size() != 0) {
			throw new IllegalStateException("Couldn't perform shutdown, jobs still in progress.");
		}

		// shutdown job system
		jobManager.shutdown();

		// shutdown repository control
		RepositoryControl repositoryControl = RepositoryLocator.getRepositoryControl();
		repositoryControl.shutdown();

		//shutdown image manager
		ImageManager imageManager = ServiceLocator.getImageManager();
		imageManager.shutdown();
		
		// shutdown InstanceControl
		instanceControl.shutdown();
		
		System.exit(0);
	}

}
