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

package org.jimcat.gui.fullscreen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.jimcat.gui.ImageControl;
import org.jimcat.gui.SwingClient;
import org.jimcat.gui.ViewControl;
import org.jimcat.gui.imageviewer.ImageViewer;
import org.jimcat.gui.imageviewer.ProgressiveImageViewer;
import org.jimcat.model.Image;
import org.jimcat.model.libraries.LibraryView;

/**
 * This class is showing the current selection in full screen.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class FullScreenView extends JDialog {

	/**
	 * the view control responsible for current client state
	 */
	private ViewControl control;

	/**
	 * the image control used to load images
	 */
	private ImageControl imgControl;

	/**
	 * a reference to the swing client
	 */
	private SwingClient client;

	/**
	 * the label used to display images
	 */
	private ImageViewer image;

	/**
	 * the index of the currently displayed image (relative to the current
	 * libraryView)
	 */
	private int index = -1;

	/**
	 * default constructor
	 */
	public FullScreenView() {
		// init members
		client = SwingClient.getInstance();
		control = client.getViewControl();
		imgControl = client.getImageControl();

		// build components
		initComponents();
	}

	/**
	 * build up content
	 */
	private void initComponents() {
		// just add a big Image to the center
		image = new ProgressiveImageViewer();

		// assemble
		setAlwaysOnTop(true);
		setUndecorated(true);
		setLayout(new BorderLayout());
		setBackground(Color.BLACK);
		addKeyListener(new FullScreenKeyListener());
		addMouseListener(new FullScreenMouseListener());
		addMouseWheelListener(new FullScreenMouseWheelListener());

		JPanel container = new JPanel();
		container.setLayout(new BorderLayout());
		container.setOpaque(true);
		container.setBackground(Color.BLACK);
		container.add(image, BorderLayout.CENTER);

		add(container, BorderLayout.CENTER);
	}

	/**
	 * this will display this window
	 */
	public void display() {
		if (isVisible()) {
			return;
		}

		if (control.getLibraryView().size() <= 0) {
			client.showMessage("No images available in current view", "Error", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		index = control.getSelectionModel().getMinSelectionIndex();
		index = Math.max(index, 0);

		// resize and place
		Rectangle rect = client.getMainFrame().getGraphicsConfiguration().getBounds();
		setLocation(rect.x, rect.y);

		Dimension screensize = new Dimension(rect.width, rect.height);
		setSize(screensize);

		updateImage();
		setVisible(true);
	}

	/**
	 * update the currently shown image
	 */
	private void updateImage() {
		LibraryView view = control.getLibraryView();
		Image img = view.getImage(index);
		image.setImage(img);

		// load prev and next to cache
		Dimension size = image.getRequiredGraphicSize();
		if (size.width <= 0) {
			// layout isn't done yet
			size = getSize();
		}

		int anz = view.size();
		Image prev = view.getImage((index + anz - 1) % anz);
		Image next = view.getImage((index + 1) % anz);

		// send preload order
		imgControl.preloadImage(prev, size);
		imgControl.preloadImage(next, size);
	}

	/**
	 * go back one image
	 */
	private void displayPreviousImage() {
		int size = control.getLibraryView().size();
		index = (index + size - 1) % size;
		updateImage();
	}

	/**
	 * go to the next image
	 */
	private void displayNextImage() {
		int size = control.getLibraryView().size();
		index = (index + 1) % size;
		updateImage();
	}

	/**
	 * show popup menu
	 * 
	 * @param position
	 */

	private void showPopupMenu(Point position) {
		FullScreenPopupMenu menu = FullScreenPopupMenu.getInstance();
		menu.show(this, index, position);
	}

	/**
	 * quit fullscreen session
	 */
	public void closeFullScreenView() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				paint(getGraphics());
			}
		});
		setVisible(false);
	}

	/**
	 * private observer for key events with the fullscreen view
	 */
	private class FullScreenKeyListener extends KeyAdapter {
		/**
		 * used to jump to next image
		 * 
		 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
		 */
		@Override
		public void keyPressed(KeyEvent e) {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_ESCAPE:
			case KeyEvent.VK_Q:
				closeFullScreenView();
				break;
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_UP:
				displayPreviousImage();
				break;
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_ENTER:
			case KeyEvent.VK_SPACE:
				displayNextImage();
				break;
			}
		}
	}

	/**
	 * private observer of fullscreen mouse events
	 */
	private class FullScreenMouseListener extends MouseAdapter {
		/**
		 * used to switch image leftclick++, rightclick--
		 * 
		 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
		 */

		@Override
		public void mouseClicked(MouseEvent e) {

			if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
				closeFullScreenView();
				return;
			}

			if (e.getButton() == MouseEvent.BUTTON1) {
				// displayNextImage();
			}

		}

		/**
		 * react to mouse released - show popup menu if it was a poput trigger
		 * 
		 * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseReleased(MouseEvent e) {
			checkForPopup(e);
		}

		/**
		 * call check for popup
		 * 
		 * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
		 */
		@Override
		public void mousePressed(MouseEvent e) {
			checkForPopup(e);
		}

		/**
		 * 
		 * test if popup is triggered and show it
		 * 
		 * @param e
		 */
		private void checkForPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				showPopupMenu(e.getPoint());
			}
		}

	}

	/**
	 * private observer of fullscreen mouse wheel events
	 */
	private class FullScreenMouseWheelListener implements MouseWheelListener {
		/**
		 * on mouse wheel movement switch to next or previous image.
		 * 
		 * @see java.awt.event.MouseWheelListener#mouseWheelMoved(java.awt.event.MouseWheelEvent)
		 */
		public void mouseWheelMoved(MouseWheelEvent e) {
			if (e.getWheelRotation() < 0) {
				displayPreviousImage();
			} else {
				displayNextImage();
			}
		}
	}
}
