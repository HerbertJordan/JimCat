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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import org.jimcat.services.jobs.JobManager;

/**
 * The JobManager Dialog used by the user to control jobs.
 * 
 * $Id: JobManagerDialog.java 934 2007-06-15 08:40:58Z 07g1t1u2 $
 * 
 * @author Herbert
 */
public class JobManagerDialog extends JFrame {

	/**
	 * a reference to the observed manager
	 */
	private JobManager myManager;

	/**
	 * Constructor for this dialog
	 * 
	 * @param manager -
	 *            the jobManager to observe
	 * @throws HeadlessException
	 * 
	 * {@link JDialog#JDialog(Frame)}
	 */
	public JobManagerDialog(JobManager manager) throws HeadlessException {
		super("Job Manager");
		// initate members
		myManager = manager;

		// create content
		initComponents();
	}

	/**
	 * initating components
	 */
	private void initComponents() {
		// general settings
		setLayout(new BorderLayout());

		// Headline
		JLabel headline = new JLabel("Job overview:");
		headline.setBorder(new EmptyBorder(5, 10, 5, 10));
		add(headline, BorderLayout.NORTH);

		// joblist
		JobList list = new JobList(myManager);
		JScrollPane jobPane = new JScrollPane();
		jobPane.setBackground(Color.WHITE);
		jobPane.setViewportView(list);
		jobPane.getViewport().setBackground(Color.WHITE);

		add(jobPane, BorderLayout.CENTER);

		// flush-Button
		JPanel bottom = new JPanel();
		bottom.setLayout(new BorderLayout());
		bottom.setBorder(new EmptyBorder(5, 20, 5, 20));

		// the clear finished button
		JButton clear = new JButton("Clean Up");
		clear.setFocusable(false);
		clear.setMnemonic('c');
		clear.addActionListener(new ActionListener() {
			@SuppressWarnings("unused")
			public void actionPerformed(ActionEvent e) {
				myManager.clearFinishedJobs();
			}
		});

		JButton close = new JButton("Close");
		close.setFocusable(false);
		close.setMnemonic('l');
		close.addActionListener(new ActionListener() {
			@SuppressWarnings("unused")
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});

		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttons.add(clear);
		buttons.add(close);

		bottom.add(buttons, BorderLayout.EAST);

		// add to Dialog
		add(bottom, BorderLayout.SOUTH);

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					setVisible(false);
				}
			}
		});

		Dimension size = new Dimension(450, 300);
		Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(size);
		setLocation((screensize.width - size.width) / 10 * 9, (screensize.height - size.height) / 4 * 3);
	}
}
