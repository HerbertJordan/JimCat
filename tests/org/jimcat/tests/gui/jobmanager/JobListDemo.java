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

package org.jimcat.tests.gui.jobmanager;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

import org.jimcat.gui.jobmanager.JobList;
import org.jimcat.services.jobs.Job;
import org.jimcat.services.jobs.JobManager;
import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.skin.OfficeSilver2007Skin;

/**
 * A small program to demonstrate and test a JobFace.
 * 
 * $Id: JobListDemo.java 935 2007-06-15 09:21:09Z 07g1t1u2 $
 * 
 * @author Herbert
 */
public class JobListDemo {

	/**
	 * just for testing purposes
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);
		try {
			UIManager.setLookAndFeel(new SubstanceLookAndFeel());
		} catch (Exception e) {
			// ignore
		}
		SubstanceLookAndFeel.setSkin(new OfficeSilver2007Skin());

		JFrame test = new JFrame("JobFace Test");

		JobManager manager = new JobManager();

		for (int i = 0; i < 10; i++) {
			manager.excecuteJob(new DemoJob(manager));
		}

		JobList list = new JobList(manager);
		JScrollPane pane = new JScrollPane();
		pane.setViewportView(list);
		test.add(pane);
		test.setBackground(Color.WHITE);
		test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		test.pack();
		test.setLocation(500, 500);
		test.setSize(new Dimension(400, 300));
		test.setVisible(true);
	}

}

class DemoJob extends Job {

	/**
	 * creates a small jobDemo visualisation
	 * @param manager
	 */
	public DemoJob(JobManager manager) {
		super(manager, "TestJob", "A small job used to test JobFace ...");
	}

	private int state = 0;

	@Override
	public int getPercentage() {
		return state * 10;
	}

	@Override
	public boolean nextRollbackStep() {
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			// ignore
		}
		state--;
		if (state == 0) {
			return true;
		}
		return false;
	}

	@Override
	public boolean nextStep() {
		setJobDescription("Performing " + (state + 1) + " step ...");
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			// ignore
		}
		setJobDescription((state + 1) + ". step done");
		state++;
		if (state == 10) {
			return true;
		}
		return false;
	}

	@Override
	public boolean supportsRollback() {
		return true;
	}

}
