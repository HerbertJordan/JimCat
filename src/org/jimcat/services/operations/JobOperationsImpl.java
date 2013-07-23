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

import org.jimcat.services.ServiceLocator;
import org.jimcat.services.jobs.JobManager;

/**
 * Concreate implementation of the JobOperations interface.
 * 
 * $Id: JobOperationsImpl.java 554 2007-05-09 22:20:37Z 07g1t1u3 $
 * 
 * @author Herbert
 */
public class JobOperationsImpl implements org.jimcat.services.JobOperations {

	/**
	 * Returns the internal used JobManager
	 * 
	 * @see org.jimcat.services.JobOperations#getJobManager()
	 */
	public JobManager getJobManager() {
		return ServiceLocator.getJobManager();
	}

}
