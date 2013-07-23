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

package org.jimcat.gui.splashscreen;

/**
 * The splashscreen shown on startup.
 * 
 * 
 * $Id$
 * 
 * @author Michael
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;

/**
 * The JWindow used as splash screen.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class SplashScreen extends JWindow {

	/**
	 * The splash image
	 */
	private static ImageIcon image;

	/**
	 * The progress bar
	 */
	// private static JProgressBar progBar;
	private static JLabel progInfo;

	private static SplashScreen splashScreen = new SplashScreen("splashscreen.png");

	// private static Timer progFader;

	/**
	 * 
	 * consturct a splash screen showing the image specified in path
	 * 
	 * @param path
	 */
	private SplashScreen(String path) {

		JPanel contentPane = new JPanel();

		contentPane.setLayout(null);
		image = new ImageIcon(getClass().getResource(path));
		Dimension size = new Dimension(image.getIconWidth(), image.getIconHeight());

		// first Image

		JLabel img = new JLabel(image);
		img.setSize(size);
		img.setLocation(0, 0);
		contentPane.add(img);

		// second progress bar (neverending)
		JPanel progPanel = new JPanel(new FlowLayout());

		/*
		 * progBar = new JProgressBar(); // progBar.setIndeterminate(true);
		 * progBar.setBackground(Color.BLACK); progBar.setBorderPainted(false);
		 * progBar.setForeground(Color.GRAY); progBar.setStringPainted(true);
		 * progBar.setOpaque(true); progBar.setDoubleBuffered(true);
		 * 
		 * progPanel.add(progBar);
		 */
		progInfo = new JLabel();
		progInfo.setForeground(Color.WHITE);
		progInfo.setOpaque(false);
		progPanel.add(progInfo);
		progPanel.setBackground(Color.BLACK);
		progPanel.setOpaque(true);

		Dimension barSize = new Dimension((int) (size.width * 0.8f), 20);
		progPanel.setSize(barSize);

		progPanel.setLocation((size.width - barSize.width) / 2, size.height - 27);
		contentPane.add(progPanel);

		// set prog info in front of image
		contentPane.setComponentZOrder(img, 1);
		contentPane.setComponentZOrder(progPanel, 0);

		this.setContentPane(contentPane);

		// set size and place
		Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screensize.width - size.width) / 2, (screensize.height - size.height) / 2);
		setSize(size);
		setAlwaysOnTop(false);
	}

	/**
	 * The Method displays the Window
	 * 
	 */
	public static void showSplash() {
		if (splashScreen != null) {
			splashScreen.setVisible(true);
		}
	}

	/**
	 * 
	 * set the progress Text
	 * 
	 * @param newText
	 */
	public static void setProgressText(String newText) {
		if (progInfo != null) {
			progInfo.setText(newText);
		}
	}

	/*
	 * 
	 * sets wheter progress bar is running endlessly or not. Note that this does
	 * not work with Mac operating system.
	 * 
	 * @param interminate
	 */
	/*
	 * public static void setProgressInterminate(boolean interminate) { if
	 * (progBar != null && !System.getProperty("os.name").startsWith("Mac")) {
	 * progBar.setIndeterminate(interminate); } }
	 */

	/*
	 * 
	 * set the value of the progress bar
	 * 
	 * @param value
	 */
	/*
	 * public static void setProgressValue(int value) { if (progBar != null) {
	 * progBar.setValue(value); } }
	 */

	/*
	 * initiate the fade out effect for the progress bar
	 */
	/*
	 * public static void fadeProgressBar() { if (progBar != null) { progFader =
	 * new Timer(); progFader.scheduleAtFixedRate(splashScreen.new
	 * ProgBarFader(), 0, 30); } }
	 */

	/**
	 * 
	 * hide the SplashScreen and dispose it to free memory.
	 * 
	 */
	public static void hideSplashScreen() {
		/*
		 * if (progFader != null) { // make sure timer is cancelled
		 * progFader.cancel(); }
		 */
		if (splashScreen != null) {
			splashScreen.setVisible(false);
			splashScreen.dispose();
			splashScreen = null;
		}
	}

	/*
	 * private class ProgBarFader extends TimerTask {
	 * 
	 * /** fade the progress bar
	 * 
	 * @see java.util.TimerTask#run()
	 * 
	 * @Override public void run() { if (progBar != null) { Color c =
	 * progBar.getForeground(); int newAlpha = c.getAlpha() - 15; if (newAlpha <=
	 * 0) { progFader.cancel(); progBar.setIndeterminate(false);
	 * progBar.setValue(0); return; } progBar.setForeground(new
	 * Color(c.getRed(), c.getGreen(), c.getBlue(), newAlpha)); } } }
	 */
}
