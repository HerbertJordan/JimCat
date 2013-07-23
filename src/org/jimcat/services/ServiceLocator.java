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

package org.jimcat.services;

import org.jimcat.services.failurefeedback.FailureFeedbackService;
import org.jimcat.services.imagemanager.ImageManager;
import org.jimcat.services.imagemanager.ImageManagerImpl;
import org.jimcat.services.instancecontrol.InstanceControl;
import org.jimcat.services.jobs.JobManager;

/**
 * A common class to configure running services.
 * 
 * All code segments should use this locator to access services. Through this
 * developers may be able to exchange modules.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public final class ServiceLocator {

	/**
     * to hide utility class
     */
    private ServiceLocator() {
	    // to block class instanciation 
    }
    
	/**
	 * the image manager implementation used by this system
	 */
	private static final ImageManager imageManager = ImageManagerImpl.getInstance();

	/**
	 * the instance control used by this system
	 */
	private static final InstanceControl instanceControl = InstanceControl.getInstance();

	/**
	 * the failure feedback service used by this system
	 */
	private static final FailureFeedbackService failureFeedbackService = FailureFeedbackService.getInstance();
	
	/**
	 * the job manager used by this system
	 */
	private static final JobManager jobManager = new JobManager();

	/**
     * @return the imageManager
     */
    public static ImageManager getImageManager() {
    	return imageManager;
    }

	/**
     * @return the instanceControl
     */
    public static InstanceControl getInstanceControl() {
    	return instanceControl;
    }

	/**
     * @return the jobManager
     */
    public static JobManager getJobManager() {
    	return jobManager;
    }

	/**
     * @return the failureFeedbackService
     */
    public static FailureFeedbackService getFailureFeedbackService() {
    	return failureFeedbackService;
    }
	
	
}
