/*
 *  This file is part of jimcat.
 *
 *  jimcat is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation version 2.
 *
 *  jimcat is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with jimcat; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.jimcat.gui.frame;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.jimcat.gui.hyperlink.HyperLink;

/**
 * Displays an about dialog with information of this piece of software.
 * 
 * 
 * $Id$
 * 
 * @author Christoph
 */
public class AboutDialog extends JimCatDialog {

	/**
	 * create a new about dialog
	 * 
	 * @param owner
	 *            should be the main frame
	 */
	public AboutDialog(JimCatFrame owner) {
		super(owner, "About JimCat", true);

		// build up content
		initComponents();

		// add this as keylistener
		addKeyListener(new AboutDialogKeyListener());
	}

	/**
	 * build up component hierarchie
	 */
	private void initComponents() {
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		
		Container contentPane = getContentPane();
		contentPane.setLayout(new GridLayout(0, 1));
		
		Border defaultBorder = BorderFactory.createEmptyBorder(5, 5, 0, 5);
		
		String devs = "Michael Handler, Stefan Hofbauer, Herbert Jordan,<BR>"
		        + "Christoph Leiter, Friedrich Wachter";
		String photographer = "Andreas Kinter";

		String version = getClass().getPackage().getImplementationVersion();

		if (version == null) {
			version = "Development Build";
		}

		JLabel buildLabel = new JLabel("<HTML><B>Build:</B><BR>" + version);
		JLabel developersLabel = new JLabel("<HTML><B>Development Team:</B><BR>" + devs);
		JLabel photographerLabel = new JLabel("<HTML><B>Logo:</B><BR>" + photographer);

		buildLabel.setBorder(defaultBorder);
		developersLabel.setBorder(defaultBorder);
		photographerLabel.setBorder(defaultBorder);
		
		// link to our web site
		HyperLink website = new HyperLink("http://jimcat.org/", "http://jimcat.org/");
		website.setBorder(new EmptyBorder(5, 0, 20, 0));

		contentPane.add(buildLabel);
		contentPane.add(developersLabel);
		contentPane.add(photographerLabel);
		contentPane.add(website);

		pack();
		Dimension size = this.getSize();
		Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(size);
		setLocation((screensize.width - size.width) / 2, (screensize.height - size.height) / 2);
		setResizable(false);
	}

	private class AboutDialogKeyListener extends KeyAdapter {
		/**
		 * just to support ESC
		 * 
		 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
		 */
		@Override
        public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				setVisible(false);
			}
		}
	}
}
