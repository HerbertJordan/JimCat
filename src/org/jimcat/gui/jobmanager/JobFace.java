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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

import org.jimcat.gui.icons.Icons;
import org.jimcat.services.jobs.Job;
import org.jimcat.services.jobs.JobCommand;
import org.jimcat.services.jobs.JobFailureDescription;
import org.jimcat.services.jobs.JobListener;
import org.jimcat.services.jobs.JobState;
import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.button.StandardButtonShaper;
import org.jvnet.substance.utils.SubstanceConstants;

/**
 * This class displayes a single job.
 * 
 * It will be used by delegation as JobRenderer and editor.
 * 
 * $Id: JobFace.java 935 2007-06-15 09:21:09Z 07g1t1u2 $
 * 
 * @author Herbert
 */
public class JobFace extends JPanel implements JobListener {

	/**
	 * the size of the Control buttons
	 */
	private static final Dimension BUTTON_SIZE = new Dimension(40, 20);

	/**
	 * the job displayed by this componenet
	 */
	private Job displayedJob;

	/**
	 * this label will show the job-name
	 */
	private JLabel name;

	/**
	 * this label will show the current job-description
	 */
	private JLabel description;

	/**
	 * a small state indicator
	 */
	private JLabel infoImage;

	/**
	 * bar used to indicate progress
	 */
	private JProgressBar bar;

	/**
	 * control button rollback
	 */
	private JButton rollback;

	/**
	 * control button run
	 */
	private JButton run;

	/**
	 * control button suspend
	 */
	private JButton suspend;

	/**
	 * control button cancel
	 */
	private JButton cancel;

	/**
	 * default contructor
	 */
	public JobFace() {
		initComponents();
	}

	/**
	 * build up components
	 */
	private void initComponents() {
		// generel settings
		setOpaque(false);

		// header panel
		JPanel header = new JPanel();
		header.setOpaque(false);
		header.setLayout(new BorderLayout());
		header.setBorder(new EmptyBorder(5, 15, 5, 15));

		// Name/Description within header
		JPanel infoHeader = new JPanel();
		infoHeader.setOpaque(false);
		infoHeader.setLayout(new GridLayout(2, 1));

		// Define Fonts
		Font jobDescriptionFont = new JLabel().getFont();
		Font jobTitelFont = jobDescriptionFont.deriveFont(Font.BOLD);

		// Field for name
		name = new JLabel();
		name.setOpaque(false);
		name.setFont(jobTitelFont);
		infoHeader.add(name);

		// Field for description
		description = new JLabel();
		description.setOpaque(false);
		description.setFont(jobDescriptionFont);
		infoHeader.add(description);

		header.add(infoHeader, BorderLayout.CENTER);

		// Info image
		infoImage = new JLabel();
		infoImage.setOpaque(false);
		header.add(infoImage, BorderLayout.EAST);

		// Progress Bar
		bar = new JProgressBar();
		bar.setOpaque(false);
		bar.setIndeterminate(false);
		bar.setMinimum(0);
		bar.setMaximum(100);
		bar.setValue(0);
		bar.setBorder(new EmptyBorder(5, 20, 5, 20));

		// Process control
		JPanel control = new JPanel();
		control.setOpaque(false);
		control.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		control.setBorder(new EmptyBorder(5, 20, 5, 20));

		// for special effects (rounded edges)
		StandardButtonShaper buttonShaper = new StandardButtonShaper();

		//rollback = new JButton("|<");
		rollback = new JButton();
		rollback.setIcon(Icons.JOB_ROLLBACK);
		rollback.setToolTipText("rollback");
		
		rollback.setPreferredSize(BUTTON_SIZE);
		rollback.setFocusable(false);
		rollback.putClientProperty(SubstanceLookAndFeel.BUTTON_SHAPER_PROPERTY, buttonShaper);
		rollback.putClientProperty(SubstanceLookAndFeel.BUTTON_SIDE_PROPERTY, SubstanceConstants.Side.RIGHT);
		rollback.addActionListener(new ActionListener() {
			@SuppressWarnings("unused")
			public void actionPerformed(ActionEvent e) {
				if (displayedJob != null) {
					displayedJob.rollback();
				}
			}
		});
		control.add(rollback);

		//run = new JButton(">");
		run = new JButton();
		run.setIcon(Icons.JOB_RUN);
		run.setToolTipText("run");
		
		run.setPreferredSize(BUTTON_SIZE);
		run.setFocusable(false);
		run.addActionListener(new ActionListener() {
			@SuppressWarnings("unused")
			public void actionPerformed(ActionEvent e) {
				if (displayedJob != null) {
					displayedJob.resume();
				}
			}
		});
		control.add(run);

		suspend = new JButton();
		suspend.setIcon(Icons.JOB_SUSPEND);
		suspend.setToolTipText("suspend");
		
		suspend.setPreferredSize(BUTTON_SIZE);
		suspend.setFocusable(false);
		suspend.addActionListener(new ActionListener() {
			@SuppressWarnings("unused")
			public void actionPerformed(ActionEvent e) {
				if (displayedJob != null) {
					displayedJob.suspend();
				}
			}
		});
		control.add(suspend);

		cancel = new JButton();
		cancel.setIcon(Icons.JOB_CANCEL);
		cancel.setToolTipText("cancel");
		
		cancel.setPreferredSize(BUTTON_SIZE);
		cancel.setFocusable(false);
		cancel.putClientProperty(SubstanceLookAndFeel.BUTTON_SHAPER_PROPERTY, buttonShaper);
		cancel.putClientProperty(SubstanceLookAndFeel.BUTTON_SIDE_PROPERTY, SubstanceConstants.Side.LEFT);
		cancel.addActionListener(new ActionListener() {
			@SuppressWarnings("unused")
			public void actionPerformed(ActionEvent e) {
				if (displayedJob != null) {
					displayedJob.cancel();
				}
			}
		});
		control.add(cancel);

		// controler container
		JPanel controlerPanel = new JPanel();
		controlerPanel.setOpaque(false);
		controlerPanel.setLayout(new BorderLayout());
		controlerPanel.add(control, BorderLayout.EAST);

		// assemble
		setLayout(new BorderLayout());
		add(header, BorderLayout.NORTH);
		add(bar, BorderLayout.CENTER);
		add(controlerPanel, BorderLayout.SOUTH);
	}

	/**
	 * replace the displayed job
	 * @param job 
	 */
	public void setJob(Job job) {
		// check if there is a change
		if (displayedJob == job) {
			return;
		}

		// remove listener
		if (displayedJob != null) {
			displayedJob.removeJobListener(this);
		}
		// exchange
		displayedJob = job;
		// add listener
		if (displayedJob != null) {
			displayedJob.addJobListener(this);

			// update fields
			name.setText(job.getJobName());
			description.setText(job.getJobDescription());
			// TODO: replace with image
			infoImage.setText(job.getState().toString());
			updateButtonState();
		}
	}

	/**
	 * The job represented by this component
	 * 
	 * @return the displayed Job
	 */
	public Job getJob() {
		return displayedJob;
	}

	/**
	 * this will update button states
	 */
	private void updateButtonState() {
		// if there is no job, no update possible
		if (displayedJob == null) {
			return;
		}

		// udpate buttonstates
		JobState state = displayedJob.getState();

		// rollback
		boolean possible = true;
		if (!displayedJob.supportsRollback() || state.isFinal() || state == JobState.UNDOFAILURE
		        || state == JobState.UNDOING || state == JobState.UNDOSUSPENDED) {
			possible = false;
		}
		rollback.setEnabled(possible);

		// run
		possible = true;
		if (state.isFinal() || state.isRunable()) {
			possible = false;
		}
		run.setEnabled(possible);

		// suspend
		suspend.setEnabled(!state.isFinal() && state.isRunable());

		// cancel
		cancel.setEnabled(!state.isFinal());

		// progressbar
		bar.setEnabled(!state.isFinal());

	}

	/**
	 * This method is called when a failure occurs during job execution
	 * 
	 * @see org.jimcat.services.jobs.JobListener#failerEmerged(org.jimcat.services.jobs.Job,
	 *      org.jimcat.services.jobs.JobFailureDescription)
	 */
	@SuppressWarnings("unused")
	public void failerEmerged(Job job, JobFailureDescription desc) {
		// ignore
	}

	/**
	 * update percentage
	 * 
	 * @see org.jimcat.services.jobs.JobListener#progressChanged(org.jimcat.services.jobs.Job)
	 */
	public void progressChanged(Job job) {
		// only update if there was a real change
		if (bar.getValue() != job.getPercentage()) {
			bar.setValue(job.getPercentage());
		}
	}

	/**
	 * update button states and infoImage
	 * 
	 * @see org.jimcat.services.jobs.JobListener#stateChanged(org.jimcat.services.jobs.Job,
	 *      org.jimcat.services.jobs.JobState, org.jimcat.services.jobs.JobState,
	 *      org.jimcat.services.jobs.JobCommand)
	 */
	@SuppressWarnings("unused")
	public void stateChanged(Job job, JobState oldState, JobState newState, JobCommand command) {
		infoImage.setText(job.getState().toString());
		updateButtonState();
	}

	/**
	 * replace current description
	 * 
	 * @see org.jimcat.services.jobs.JobListener#descriptionChanged(org.jimcat.services.jobs.Job)
	 */
	public void descriptionChanged(Job job) {
		description.setText(job.getJobDescription());
	}

}
