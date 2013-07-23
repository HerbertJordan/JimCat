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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.Scrollable;

import org.jimcat.services.jobs.Job;
import org.jimcat.services.jobs.JobManager;
import org.jimcat.services.jobs.JobManagerListener;

/**
 * This class will show a list of active and finished jobs.
 * 
 * $Id: JobList.java 869 2007-06-09 06:34:39Z 07g1t1u2 $
 * 
 * @author Herbert
 */
public class JobList extends JPanel implements Scrollable, JobManagerListener {

	/**
	 * a reference to the JobManager displayed
	 */
	private JobManager myManager;

	/**
	 * the list of currently displayed active jobs
	 */
	private List<JobFace> activeJobFaces = new LinkedList<JobFace>();

	/**
	 * the list of currently displayed finished jobs
	 */
	private List<JobFace> finishedJobFaces = new LinkedList<JobFace>();

	/**
	 * direct contstructor
	 * 
	 * @param manager -
	 *            observed JobManager
	 */
	public JobList(JobManager manager) {
		// set members
		this.myManager = manager;

		// register listener
		manager.addJobManagerListener(this);

		// build components
		initComponents();
	}

	/**
	 * build up components
	 */
	private void initComponents() {
		// basic setup
		setLayout(new GridLayout(0, 1));
		setOpaque(true);
		setBackground(Color.WHITE);

		if (myManager != null) {
			// load active jobs from manager
			for (Job job : new ArrayList<Job>(myManager.getActiveJobs())) {
				addJobToTopOfList(job, activeJobFaces);
			}
			// load finished jobs
			for (Job job : new ArrayList<Job>(myManager.getFinishedJobs())) {
				addJobToTopOfList(job, finishedJobFaces);
			}
		}

		// update List
		updateDisplay();
	}

	/**
	 * this will update the current list
	 */
	private void updateDisplay() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				updateDisplayInternal();
			}
		});
	}

	/**
	 * the actuall update process
	 */
	private void updateDisplayInternal() {
		// remove all visibleJobs
		removeAll();

		// add active Jobs first
		for (JobFace face : new ArrayList<JobFace>(activeJobFaces)) {
			add(face);
		}

		// add finished Jobs
		for (JobFace face : new ArrayList<JobFace>(finishedJobFaces)) {
			add(face);
		}

		// update view
		revalidate();
	}

	/**
	 * adds a new JobFace to the top of the given list
	 * 
	 * @param job
	 * @param faces
	 */
	private void addJobToTopOfList(Job job, List<JobFace> faces) {
		JobFace newFace = new JobFace();
		newFace.setJob(job);
		// add to the top of the list
		faces.add(0, newFace);
		// update the current display
		updateDisplay();
	}

	/**
	 * this event indicates that the finished-Job list should be cleared
	 * 
	 * @see org.jimcat.services.jobs.JobManagerListener#finishedListFlushed(org.jimcat.services.jobs.JobManager)
	 */
	@SuppressWarnings("unused")
	public void finishedListFlushed(JobManager manager) {
		// remove jobs from faces
		for (JobFace face : finishedJobFaces) {
			face.setJob(null);
		}

		// clear list
		finishedJobFaces.clear();

		// update view
		updateDisplay();
	}

	/**
	 * this event indicates a new job is added to the jobmanager.
	 * 
	 * @see org.jimcat.services.jobs.JobManagerListener#jobAddedToActiveList(org.jimcat.services.jobs.JobManager,
	 *      org.jimcat.services.jobs.Job)
	 */
	@SuppressWarnings("unused")
	public void jobAddedToActiveList(JobManager manager, Job job) {
		addJobToTopOfList(job, activeJobFaces);
		job.addJobListener(new JobFailurObserver());
	}

	/**
	 * add a job to the finished list
	 * 
	 * @see org.jimcat.services.jobs.JobManagerListener#jobAddedToFinishedList(org.jimcat.services.jobs.JobManager,
	 *      org.jimcat.services.jobs.Job)
	 */
	@SuppressWarnings("unused")
	public void jobAddedToFinishedList(JobManager manager, Job job) {
		addJobToTopOfList(job, finishedJobFaces);
	}

	/**
	 * remove a job from the active list
	 * 
	 * @see org.jimcat.services.jobs.JobManagerListener#jobRemovedFromActiveList(org.jimcat.services.jobs.JobManager,
	 *      org.jimcat.services.jobs.Job)
	 */
	@SuppressWarnings("unused")
	public void jobRemovedFromActiveList(JobManager manager, Job job) {
		// find corresponding JobFace
		JobFace target = null;
		Iterator<JobFace> iter = activeJobFaces.iterator();
		while (iter.hasNext() && target == null) {
			JobFace face = iter.next();
			if (face.getJob() == job) {
				target = face;
			}
		}

		// remove face from list
		activeJobFaces.remove(target);

		// update view
		updateDisplay();
	}

	// ////////////////////////////////////
	// Scrollable interface
	// ////////////////////////////////////

	/**
	 * no special interrest => just return preferred Size
	 * 
	 * @see javax.swing.Scrollable#getPreferredScrollableViewportSize()
	 */
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	/**
	 * it should jump for a compleat block => use blockheight
	 * 
	 * @see javax.swing.Scrollable#getScrollableBlockIncrement(java.awt.Rectangle,
	 *      int, int)
	 */
	@SuppressWarnings("unused")
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		if (activeJobFaces.size() > 0) {
			return activeJobFaces.get(0).getSize().height;
		} else if (finishedJobFaces.size() > 0) {
			return finishedJobFaces.get(0).getSize().height;
		}
		return 10;
	}

	/**
	 * we would like to scroll vertically => return false
	 * 
	 * @see javax.swing.Scrollable#getScrollableTracksViewportHeight()
	 */
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	/**
	 * we do not like to scroll horizontal => return true
	 * 
	 * @see javax.swing.Scrollable#getScrollableTracksViewportWidth()
	 */
	public boolean getScrollableTracksViewportWidth() {
		return true;
	}

	/**
	 * no special value ...
	 * 
	 * @see javax.swing.Scrollable#getScrollableUnitIncrement(java.awt.Rectangle,
	 *      int, int)
	 */
	@SuppressWarnings("unused")
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 10;
	}

}
