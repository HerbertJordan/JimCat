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

package org.jimcat.gui.rating;

import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jimcat.model.Image;
import org.jimcat.model.ImageRating;
import org.jimcat.model.notification.BeanChangeEvent;
import org.jimcat.model.notification.BeanListener;
import org.jimcat.model.notification.BeanProperty;

/**
 * The rating representation within the sidebar.
 * 
 * this will allow the user to edit the rating through the sidebar.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class RatingEditor extends JPanel implements MouseListener, MouseMotionListener, BeanListener<Image> {

	/**
	 * the label used to display current rating
	 */
	private JLabel display;

	/**
	 * the image containing the currently displayed rating
	 */
	private Image image;

	/**
	 * a simple constructor, now parameter
	 */
	public RatingEditor() {
		initComponents();
	}

	private void initComponents() {
		// the rating display
		display = new JLabel();
		display.setOpaque(false);
		display.setText(null);
		display.setIcon(RatingRepresentation.getIcon(ImageRating.NONE));
		display.addMouseListener(this);
		display.addMouseMotionListener(this);

		// assemble
		FlowLayout layout = new FlowLayout();
		layout.setAlignment(FlowLayout.CENTER);
		layout.setHgap(0);
		layout.setVgap(0);
		setLayout(layout);
		setOpaque(false);

		add(display);
	}

	/**
	 * exchange the image this editor is working on
	 * 
	 * @param img
	 */
	public void setImage(Image img) {
		// is there a change?
		if (img == image) {
			return;
		}

		// unregister from old
		if (image != null) {
			image.removeListener(this);
		}

		// exchange
		image = img;

		// register to new one
		if (image != null) {
			image.addListener(this);
		}

		// update display
		if (image != null) {
			display.setIcon(RatingRepresentation.getIcon(image.getRating()));
		} else {
			display.setIcon(RatingRepresentation.getIcon(ImageRating.NONE));
		}
	}

	/**
	 * does nothing, just to implement interface
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@SuppressWarnings("unused")
	public void mouseClicked(MouseEvent e) {
		// do nothing
	}

	/**
	 * does nothing, just to implement interface
	 * 
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@SuppressWarnings("unused")
	public void mouseEntered(MouseEvent e) {
		// do nothing
	}

	/**
	 * does nothing, just to implement interface
	 * 
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@SuppressWarnings("unused")
	public void mouseExited(MouseEvent e) {
		// do nothing
	}

	/**
	 * react on a mouse pressed event
	 * 
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			updateValue(e);
		}
	}

	/**
	 * does nothing, just to implement interface
	 * 
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@SuppressWarnings("unused")
	public void mouseReleased(MouseEvent e) {
		// do nothing
	}

	/**
	 * allow dragging of rating
	 * 
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	public void mouseDragged(MouseEvent e) {
		updateValue(e);
	}

	/**
	 * does nothing, just to implement interface
	 * 
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	@SuppressWarnings("unused")
	public void mouseMoved(MouseEvent e) {
		// do nothing
	}

	/**
	 * Update display if value has changed
	 * 
	 * @see org.jimcat.model.notification.BeanListener#beanPropertyChanged(org.jimcat.model.notification.BeanChangeEvent)
	 */
	public void beanPropertyChanged(BeanChangeEvent<Image> event) {
		Image img = event.getSource();
		if (img == image) {
			if (event.getProperty() == BeanProperty.IMAGE_RATING) {
				display.setIcon(RatingRepresentation.getIcon(img.getRating()));
			}
		} else {
			img.removeListener(this);
		}
	}

	/**
	 * this will update the current rating value by using a mouse event
	 * 
	 * @param e
	 */
	private void updateValue(MouseEvent e) {
		int range = display.getWidth();
		ImageRating newRating;
		if (e.getX() <= 0) {
			newRating = ImageRating.NONE;
		} else {
			int anz = ImageRating.values().length - 1;
			newRating = ImageRating.values()[Math.min((e.getX() * 5 / range) + 1, anz)];
		}
		if (image != null) {
			image.setRating(newRating);
		}
	}
}
