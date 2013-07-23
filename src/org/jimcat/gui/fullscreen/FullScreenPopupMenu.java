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

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;

import org.jimcat.gui.SwingClient;
import org.jimcat.gui.ViewControl;
import org.jimcat.gui.icons.Icons;
import org.jimcat.gui.imagepopup.AddTagMenu;
import org.jimcat.gui.imagepopup.AddToAlbumMenu;
import org.jimcat.gui.imagepopup.ImagePopupRatingMenu;
import org.jimcat.gui.imagepopup.RemoveFromAlbumMenu;
import org.jimcat.gui.imagepopup.RemoveTagMenu;

/**
 * The popup menu for the fullscreen view.
 * 
 * 
 * $Id$
 * 
 * @author Michael
 */
public class FullScreenPopupMenu extends JPopupMenu implements ActionListener {

	/**
	 * command used to identify rotate clockwise action
	 */
	private static final String COMMAND_ROTATE_CW = "rotateCW";

	/**
	 * command used to identify rotate counter clockwise action
	 */
	private static final String COMMAND_ROTATE_CCW = "rotateCCW";

	/**
	 * command used to identify update action
	 */
	private static final String COMMAND_UPDATE = "update";

	/**
	 * command used to identify close action
	 */
	private static final String COMMAND_CLOSE = "close";

	/**
	 * the fullscreen to which this popup belongs
	 */
	private FullScreenView fullscreen;

	/**
	 * the singelton instance of this component
	 */
	private static final FullScreenPopupMenu INSTANCE = new FullScreenPopupMenu();

	/**
	 * the default constructor used to create the singelton instance
	 */
	private FullScreenPopupMenu() {
		super("FullScreen Operations");

		// init content
		initComponents();
	}

	/**
	 * get the singelton instance
	 * 
	 * @return an instance of the FullScreenPopupMenu
	 */
	public static FullScreenPopupMenu getInstance() {
		return INSTANCE;
	}

	/**
	 * build up content
	 */
	private void initComponents() {

		// rotation
		JMenuItem rotateRight = new JMenuItem("Rotate Right");
		rotateRight.setIcon(Icons.ROTATE_RIGHT);
		rotateRight.setActionCommand(COMMAND_ROTATE_CW);
		rotateRight.addActionListener(this);
		add(rotateRight);
		
		JMenuItem rotateLeft = new JMenuItem("Rotate Left");
		rotateLeft.setIcon(Icons.ROTATE_LEFT);
		rotateLeft.setActionCommand(COMMAND_ROTATE_CCW);
		rotateLeft.addActionListener(this);
		add(rotateLeft);

		addSeparator();

		JMenuItem update = new JMenuItem("Update Image");
		update.setIcon(null);
		update.setActionCommand(COMMAND_UPDATE);
		update.addActionListener(this);
		add(update);

		addSeparator();

		// rating submenu
		JMenu rating = new ImagePopupRatingMenu();
		rating.setText("Rate");
		add(rating);

		addSeparator();

		// add Tag group
		AddTagMenu addTag = new AddTagMenu();
		addTag.setText("Add Tag");
		addTag.setIcon(Icons.TAG_ADD);
		add(addTag);

		// remove tag group
		RemoveTagMenu removeTag = new RemoveTagMenu();
		removeTag.setText("Remove Tag");
		removeTag.setIcon(Icons.TAG_REMOVE);
		add(removeTag);

		addSeparator();

		// add to Album group
		AddToAlbumMenu addAlbum = new AddToAlbumMenu();
		addAlbum.setText("Add To Album");
		addAlbum.setIcon(Icons.ALBUM_ADD);
		add(addAlbum);

		// remove from album
		RemoveFromAlbumMenu removeAlbum = new RemoveFromAlbumMenu();
		removeAlbum.setText("Remove From Album");
		removeAlbum.setIcon(Icons.ALBUM_REMOVE);
		add(removeAlbum);

		addSeparator();

		// close function
		JMenuItem closeFullscreen = new JMenuItem("Close Fullscreen");
		closeFullscreen.setIcon(Icons.CLOSE_FULLSCREEN);
		closeFullscreen.setActionCommand(COMMAND_CLOSE);
		closeFullscreen.addActionListener(this);
		add(closeFullscreen);
	}

	/**
	 * this will show the context menu.
	 * 
	 * privously it will check the current selection and in case there is no
	 * selection or an unpleasend one, it will update the selection.
	 * 
	 * @param fullScreen -
	 *            the FullScreen  to which this menu belongs
	 * @param index -
	 *            the index of the element this popup is opened on (relative to
	 *            current view list)
	 * @param pos -
	 *            the position where to open the menu
	 */
	public void show(FullScreenView fullScreen, int index, Point pos) {
		this.fullscreen = fullScreen;
		if (fullScreen.isVisible()) {
			ViewControl control = SwingClient.getInstance().getViewControl();
			// set selection to current element
			ListSelectionModel model = control.getSelectionModel();
			model.setSelectionInterval(index, index);
			// open popup
			show(fullscreen, pos.x, pos.y);
		}
	}

	/**
	 * react on events
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		SwingClient client = SwingClient.getInstance();
		if (COMMAND_ROTATE_CW.equals(command)) {
			client.getImageControl().rotateSelectionCW();
		} else if (COMMAND_ROTATE_CCW.equals(command)) {
			client.getImageControl().rotateSelectionCCW();
		} else if (COMMAND_CLOSE.equals(command)) {
			this.fullscreen.closeFullScreenView();
		} else if (COMMAND_UPDATE.equals(command)) {
			client.getImageControl().updateSelection();
		}
	}

}
