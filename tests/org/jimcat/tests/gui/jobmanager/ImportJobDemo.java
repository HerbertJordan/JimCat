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
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIManager;

import org.jimcat.gui.jobmanager.JobFace;
import org.jimcat.services.imageimport.ImportJob;
import org.jimcat.services.jobs.JobManager;
import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.skin.OfficeSilver2007Skin;

/**
 * A small program to demonstrate and test a JobFace.
 * 
 * $Id: ImportJobDemo.java 935 2007-06-15 09:21:09Z 07g1t1u2 $
 * 
 * @author Herbert
 */
public class ImportJobDemo {

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

		JFrame test = new JFrame("ImportJob Test");

		JobManager manager = new JobManager();

		List<File> files = new LinkedList<File>();
		files.add(new File("G:/temp/Photos"));
		files.add(new File("G:/temp/"));
		files.add(new File("G:/temp/test.txt"));
		files.add(new File("G:/temp/vpsuite.zvpl"));
		files.add(new File("G:/temp/use-case-diagram.jpg"));
		// files.add(new File("F:/"));

		JobFace face = new JobFace();
		ImportJob job = new ImportJob(manager);
		job.setFiles(files);
		job.setRecursive(true);
		job.setDestination(new File("G:/images"));
		job.setCopyImages(true);

		face.setJob(job);
		test.add(face);
		test.setBackground(Color.WHITE);
		test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		test.pack();
		test.setLocation(500, 500);
		test.setVisible(true);
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			// just wait
		}
		job.start();
	}

}
