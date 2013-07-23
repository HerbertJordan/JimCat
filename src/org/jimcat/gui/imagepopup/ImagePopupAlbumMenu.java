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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.jimcat.model.Album;
import org.jimcat.model.Image;
import org.jimcat.model.comparator.AlbumComparator;
import org.jimcat.model.libraries.AlbumLibrary;
import org.jimcat.model.notification.BeanChangeEvent;
import org.jimcat.model.notification.BeanProperty;
import org.jimcat.model.notification.CollectionListener;
import org.jimcat.services.OperationsLocator;

/**
 * This menu is the base structor of adding and removing images to/from an album
 * by popup menus.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public abstract class ImagePopupAlbumMenu extends JMenu implements CollectionListener<Album, AlbumLibrary>,
        ActionListener {

	/**
	 * a map used to associate menu items with albums used to resolve actions to
	 * albums
	 */
	private Map<JMenuItem, Album> menu2Album;

	/**
	 * the list of albums, orderd.
	 */
	private List<Album> albumlist;

	/**
	 * an album comparator used to order list
	 */
	private AlbumComparator comparator = new AlbumComparator();

	/**
	 * If the popup menu is customized this is set to the specific image
	 */
	private Image image;

	/**
	 * determines if the entry has to be shown if the image is contained in the
	 * album or not
	 */
	private boolean hasToBeContained;

	/**
	 * default constructor
	 */
	public ImagePopupAlbumMenu() {
		// init members
		menu2Album = new LinkedHashMap<JMenuItem, Album>();
		albumlist = new ArrayList<Album>();

		// register as listener to Album library
		AlbumLibrary library = OperationsLocator.getAlbumOperations().getAlbumLibrary();
		library.addListener(this);

		// build up components
		initComponents();
	}

	/**
	 * build up content
	 */
	private void initComponents() {
		// Build up itemlist
		buildNewList();
	}

	/**
	 * refresch the album list
	 */
	private void refreshList() {
		// clear old list
		clearLists();

		// generate new one
		buildNewList();
	}

	/**
	 * build up a new menu list - you may clean the old one first
	 */
	protected void buildNewList() {
		// get sorted list of albums
		AlbumLibrary library = OperationsLocator.getAlbumOperations().getAlbumLibrary();
		albumlist.addAll(library.getAll());
		Collections.sort(albumlist, comparator);

		boolean empty = true;

		// wrap all albums in an item
		for (Album album : albumlist) {

			if (image != null) {
				if (album.contains(image) != hasToBeContained) {
					continue;
				}
			}
			JMenuItem item = new JMenuItem(album.getName());
			item.addActionListener(this);
			menu2Album.put(item, album);
			add(item);
			empty = false;
		}

		setEnabled(!empty);
	}

	/**
	 * clear current album list
	 */
	protected void clearLists() {
		// unregister listeners
		for (JMenuItem item : menu2Album.keySet()) {
			item.removeActionListener(this);
		}
		// clear lists
		menu2Album.clear();
		albumlist.clear();

		// clear submenu
		removeAll();
	}

	/**
	 * Customize popup menu for a specific image, i.e. if it's in album X then
	 * don't create an entry to add it to X.
	 * 
	 * @param img
	 * @param b
	 */
	public void customizeForImage(Image img, boolean b) {
		this.image = img;
		this.hasToBeContained = b;

		clearLists();
		buildNewList();
	}

	/**
	 * Resets to a general popup menu
	 */
	public void resetCustomize() {
		this.image = null;
	}

	/**
	 * this methode is called when a element is selected. it should be overriden
	 * by subclasses to execute certain tasks. (remove/add)
	 * 
	 * @param album
	 */
	public abstract void elementSelected(Album album);

	/**
	 * the whole library has exchanged, exchange list too
	 * 
	 * @param collection
	 * 
	 * @see org.jimcat.model.notification.CollectionListener#basementChanged(org.jimcat.model.notification.ObservableCollection)
	 */
	@SuppressWarnings("unused")
	public void basementChanged(AlbumLibrary collection) {
		// just refresh compleat list
		refreshList();

	}

	/**
	 * a new element was added to the list => update list
	 * 
	 * @param collection
	 * @param elements
	 * 
	 * @see org.jimcat.model.notification.CollectionListener#elementsAdded(org.jimcat.model.notification.ObservableCollection,
	 *      java.util.Set)
	 */
	@SuppressWarnings("unused")
	public void elementsAdded(AlbumLibrary collection, Set<Album> elements) {
		// just refresh compleat list
		refreshList();
	}

	/**
	 * an element was removed => update list
	 * 
	 * @param collection
	 * @param elements
	 * 
	 * @see org.jimcat.model.notification.CollectionListener#elementsRemoved(org.jimcat.model.notification.ObservableCollection,
	 *      java.util.Set)
	 */
	@SuppressWarnings("unused")
	public void elementsRemoved(AlbumLibrary collection, Set<Album> elements) {
		// just refresh compleat list
		refreshList();
	}

	/**
	 * an element has changed one of its properties => if it is its name, uddate
	 * representation
	 * 
	 * @param collection
	 * @param events
	 * 
	 * @see org.jimcat.model.notification.CollectionListener#elementsUpdated(org.jimcat.model.notification.ObservableCollection,
	 *      java.util.List)
	 */
	@SuppressWarnings("unused")
	public void elementsUpdated(AlbumLibrary collection, List<BeanChangeEvent<Album>> events) {
		for (BeanChangeEvent<Album> event : events) {
			// was the name changing?
			if (event.getProperty() == BeanProperty.ALBUM_NAME) {
				// update element => name may change sortorder => recreate list
				refreshList();
				return;
			}
		}
	}

	/**
	 * a menu element was selected, perform action
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		// get associated Album
		Album album = menu2Album.get(e.getSource());
		elementSelected(album);
	}

}
