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

package org.jimcat.tests.gui.histogram;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import org.jimcat.gui.SwingClient;
import org.jimcat.gui.histogram.HistogramBar;
import org.jimcat.gui.histogram.HistogramModel;
import org.jimcat.gui.histogram.image.ImageHistogram;
import org.jimcat.persistence.RepositoryLocator;
import org.jimcat.persistence.RepositoryLocator.ConfigType;
import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.skin.OfficeSilver2007Skin;

/**
 * A small visual testing tool for Histogram widget.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class TestHistogram {

	private static final boolean USE_IMAGE_MODEL = false;

	/**
	 * just creating a JFrame containing a Histogram Component.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// Install LookAndFeel
		try {
			UIManager.setLookAndFeel(new SubstanceLookAndFeel());
			SubstanceLookAndFeel.setSkin(new OfficeSilver2007Skin());
			// comment this out if you like to choose themes
			UIManager.put(SubstanceLookAndFeel.NO_EXTRA_ELEMENTS, Boolean.TRUE);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		// Build up Frame - in right Thread
		EventQueue.invokeLater(new Runnable() {
			public void run() {

				JPanel content = new JPanel();
				content.setLayout(new BorderLayout());

				HistogramModel model = null;
				if (USE_IMAGE_MODEL) {
					RepositoryLocator.setConfigType(ConfigType.XSTREAM);
					SwingClient client = SwingClient.getInstance();
					model = new ImageHistogram(client.getViewControl(), client.getImageControl());
				} else {
					model = new TestHistogramModel();
				}

				HistogramBar histogram = new HistogramBar(model);

				content.add(histogram, BorderLayout.NORTH);
				content.setBorder(new EmptyBorder(5, 5, 5, 5));

				JFrame frame = new JFrame("Histogram Test");
				frame.getRootPane().setLayout(new BorderLayout());
				frame.getRootPane().add(content, BorderLayout.CENTER);

				// resize and place ...
				frame.pack();
				Dimension size = new Dimension(400, 80);
				Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
				frame.setSize(size);
				frame.setLocation((screensize.width - size.width) / 2, (screensize.height - size.height) / 2);

				frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

				frame.setVisible(true);
			}
		});
	}
}
