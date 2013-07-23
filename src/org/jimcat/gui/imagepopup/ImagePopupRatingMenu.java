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

package org.jimcat.gui.imagepopup;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.jimcat.gui.ImageControl;
import org.jimcat.gui.SwingClient;
import org.jimcat.gui.rating.RatingRepresentation;
import org.jimcat.model.ImageRating;

/**
 * A menu allowing to rate the currently selected images.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class ImagePopupRatingMenu extends JMenu implements ActionListener {

	/**
	 * a reference to the view control
	 */
	private ImageControl control;

	/**
	 * default constructor
	 */
	public ImagePopupRatingMenu() {
		// get control references
		control = SwingClient.getInstance().getImageControl();

		// build up submenu
		initComponents();
	}

	/**
	 * build up gui content
	 */
	private void initComponents() {
		// just iterate through ratings
		for (ImageRating rating : ImageRating.values()) {
			JMenuItem item = new JMenuItem();
			item.setIcon(RatingRepresentation.getIcon(rating));
			item.setActionCommand("" + rating.ordinal());
			item.addActionListener(this);
			item.setPreferredSize(new Dimension(80, 20));

			add(item);
		}
	}

	/**
	 * react on an action
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		// get selected rating
		int ordinal = Integer.parseInt(e.getActionCommand());
		ImageRating rating = ImageRating.values()[ordinal];

		// set rating
		control.rateSelected(rating);
	}

}
