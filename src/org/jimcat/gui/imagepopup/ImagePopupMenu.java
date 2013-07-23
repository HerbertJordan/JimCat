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

import java.awt.Container;
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
import org.jimcat.model.Image;

/**
 * This class represents the imagePopup Menu for all persepctives.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public final class ImagePopupMenu extends JPopupMenu implements ActionListener {

	/**
	 * command to identify action
	 */
	private static final String COMMAND_SHOW = "show";

	private static final String COMMAND_ROTATE_CW = "rotateCW";

	private static final String COMMAND_ROTATE_CCW = "rotateCCW";

	private static final String COMMAND_IMPORT = "import";

	private static final String COMMAND_EXPORT = "export";

	private static final String COMMAND_DELETE_FROM_LIBRARY = "deleteLibrary";

	private static final String COMMAND_DELETE_FROM_DISK = "deleteDisc";

	private static final String COMMAND_RENAME = "rename";

	private static final String COMMAND_UPDATE = "update";

	private static final String COMMAND_EDIT = "edit";

	private final ViewControl view = SwingClient.getInstance().getViewControl();

	private RemoveFromAlbumMenu removeAlbum;

	private AddToAlbumMenu addAlbum;

	private RemoveTagMenu removeTag;

	private AddTagMenu addTag;

	/**
	 * the singelton instance of this component
	 */
	private static final ImagePopupMenu INSTANCE = new ImagePopupMenu();

	/**
	 * the default constructor used to create the singelton instance
	 */
	private ImagePopupMenu() {
		super("Image Operations");

		// init content
		initComponents();
	}

	/**
	 * get the singelton instance
	 * 
	 * @return an instance of the ImagePopupMenu
	 */
	public static ImagePopupMenu getInstance() {
		return INSTANCE;
	}

	/**
	 * build up content
	 */
	private void initComponents() {
		// to element show image
		JMenuItem showImage = new JMenuItem("Show");
		showImage.setIcon(Icons.FULLSCREEN);
		showImage.setActionCommand(COMMAND_SHOW);
		showImage.addActionListener(this);
		add(showImage);

		addSeparator();

		// import / export
		JMenuItem importItem = new JMenuItem("Import New...");
		importItem.setIcon(Icons.IMPORT);
		importItem.setActionCommand(COMMAND_IMPORT);
		importItem.addActionListener(this);
		add(importItem);

		JMenuItem exportItem = new JMenuItem("Export Images...");
		exportItem.setIcon(Icons.EXPORT);
		exportItem.setActionCommand(COMMAND_EXPORT);
		exportItem.addActionListener(this);
		add(exportItem);

		addSeparator();

		if (SwingClient.getInstance().getImageControl().quickEditPossible()) {
			// quick edit - (system editor)
			JMenuItem editItem = new JMenuItem("Edit Images");
			editItem.setIcon(Icons.IMAGE_EDIT);
			editItem.setActionCommand(COMMAND_EDIT);
			editItem.addActionListener(this);
			add(editItem);

			addSeparator();
		}

		// rating submenu
		JMenu rating = new ImagePopupRatingMenu();
		rating.setText("Rate");
		add(rating);

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

		JMenuItem rename = new JMenuItem("Rename...");
		rename.setIcon(Icons.RENAME);
		rename.setActionCommand(COMMAND_RENAME);
		rename.addActionListener(this);
		add(rename);

		// update images
		JMenuItem update = new JMenuItem("Update");
		update.setIcon(Icons.UPDATE_IMAGE);
		update.setActionCommand(COMMAND_UPDATE);
		update.addActionListener(this);
		add(update);

		addSeparator();

		// sorting submenu
		JMenu sorting = new ImagePopupSortingMenu();
		sorting.setText("Sort Images By");
		add(sorting);

		addSeparator();

		// add Tag group
		addTag = new AddTagMenu();
		addTag.setText("Add Tag");
		addTag.setIcon(Icons.TAG_ADD);
		add(addTag);

		// remove tag group
		removeTag = new RemoveTagMenu();
		removeTag.setText("Remove Tag");
		removeTag.setIcon(Icons.TAG_REMOVE);
		add(removeTag);

		addSeparator();

		// add to Album group
		addAlbum = new AddToAlbumMenu();
		addAlbum.setText("Add To Album");
		addAlbum.setIcon(Icons.ALBUM_ADD);
		add(addAlbum);

		// remove from album
		removeAlbum = new RemoveFromAlbumMenu();
		removeAlbum.setText("Remove From Album");
		removeAlbum.setIcon(Icons.ALBUM_REMOVE);
		add(removeAlbum);

		addSeparator();

		// delete functions
		JMenuItem deleteFromLibrary = new JMenuItem("Remove From Library...");
		deleteFromLibrary.setIcon(Icons.DELETE_FROM_LIBRARY);
		deleteFromLibrary.setActionCommand(COMMAND_DELETE_FROM_LIBRARY);
		deleteFromLibrary.addActionListener(this);
		add(deleteFromLibrary);

		JMenuItem deleteFromDisc = new JMenuItem("Delete From Disk...");
		deleteFromDisc.setIcon(Icons.DELETE_FROM_DISK);
		deleteFromDisc.setActionCommand(COMMAND_DELETE_FROM_DISK);
		deleteFromDisc.addActionListener(this);
		add(deleteFromDisc);
	}

	/**
	 * this will show the context menu.
	 * 
	 * privously it will check the current selection and in case there is no
	 * selection or an unpleasend one, it will update the selection.
	 * 
	 * @param parent -
	 *            the component whichon this menu schould be displayed
	 * @param index -
	 *            the index of the element this popup is opened on (relative to
	 *            current view list)
	 * @param pos -
	 *            the position where to open the menu
	 */
	public void show(Container parent, int index, Point pos) {
		ViewControl control = SwingClient.getInstance().getViewControl();
		// if there is no element selected, make currently hit element selected
		ListSelectionModel model = control.getSelectionModel();
		if (!model.isSelectedIndex(index)) {
			model.setSelectionInterval(index, index);
		}

		if (view.getSelectedImages().size() == 1) {
			Image img = view.getSelectedImages().get(0);
			removeAlbum.customizeForImage(img, true);
			addAlbum.customizeForImage(img, false);
			removeTag.customizeForImage(img, true);
			addTag.customizeForImage(img, false);
		} else {
			removeAlbum.resetCustomize();
			addAlbum.resetCustomize();
			removeTag.resetCustomize();
			addTag.resetCustomize();
		}

		// open popup
		show(parent, pos.x, pos.y);
	}

	/**
	 * react on events
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		SwingClient client = SwingClient.getInstance();
		if (COMMAND_SHOW.equals(command)) {
			client.showFullScreen();
		} else if (COMMAND_DELETE_FROM_LIBRARY.equals(command)) {
			client.getImageControl().removeSelectionFromLibrary();
		} else if (COMMAND_DELETE_FROM_DISK.equals(command)) {
			client.getImageControl().removeSelectionFromDisc();
		} else if (COMMAND_IMPORT.equals(command)) {
			client.displayImportDialog();
		} else if (COMMAND_EXPORT.equals(command)) {
			client.displayExportDialog(true);
		} else if (COMMAND_ROTATE_CW.equals(command)) {
			client.getImageControl().rotateSelectionCW();
		} else if (COMMAND_ROTATE_CCW.equals(command)) {
			client.getImageControl().rotateSelectionCCW();
		} else if (COMMAND_RENAME.equals(command)) {
			client.displayRenameDialog();
		} else if (COMMAND_UPDATE.equals(command)) {
			client.getImageControl().updateSelection();
		} else if (COMMAND_EDIT.equals(command)) {
			client.getImageControl().editSelection();
		}
	}

}
