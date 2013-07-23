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
 * Interface describing an observer for a JobManager.
 * 
 * $Id: JobManagerListener.java 329 2007-04-18 13:01:15Z 07g1t1u1 $
 * 
 * @author Herbert
 */
public interface JobManagerListener {

	/**
	 * Should be called if a new job is added to execution list
	 * 
	 * @param manager -
	 *            source
	 * @param job -
	 *            the job
	 */
	void jobAddedToActiveList(JobManager manager, Job job);

	/**
	 * Should be called if a job was removed from the execution list
	 * 
	 * @param manager -
	 *            source
	 * @param job -
	 *            the job
	 */
	void jobRemovedFromActiveList(JobManager manager, Job job);

	/**
	 * should be called if a job was added to the done list
	 * 
	 * @param manager -
	 *            source
	 * @param job -
	 *            the job
	 */
	void jobAddedToFinishedList(JobManager manager, Job job);

	/**
	 * should be called if the done List was flushed
	 * 
	 * @param manager
	 */
	void finishedListFlushed(JobManager manager);

}
