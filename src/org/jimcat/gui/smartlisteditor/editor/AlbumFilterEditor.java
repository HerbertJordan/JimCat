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

package org.jimcat.gui.smartlisteditor.editor;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;

import org.jimcat.gui.smartlisteditor.model.AlbumFilterNode;
import org.jimcat.gui.smartlisteditor.model.FilterTreeNode;
import org.jimcat.model.Album;
import org.jimcat.model.comparator.AlbumComparator;
import org.jimcat.model.libraries.AlbumLibrary;
import org.jimcat.model.notification.BeanChangeEvent;
import org.jimcat.model.notification.CollectionListener;
import org.jimcat.services.OperationsLocator;

/**
 * A small editor for album filter nodes.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class AlbumFilterEditor extends BaseNodeEditor {

	/**
	 * the up and running album library
	 */
	private AlbumLibrary library;

	/**
	 * a list to map string representations to smartlists
	 */
	private Map<String, Album> string2Album = new HashMap<String, Album>();

	/**
	 * a list of all albums available
	 */
	private List<String> albumList = new LinkedList<String>();

	/**
	 * the editor component
	 */
	private JPanel editor;

	/**
	 * the album chooser
	 */
	private JComboBox albumChooser;

	/**
	 * currently edited node
	 */
	private AlbumFilterNode currentNode;

	/**
	 * to mark album list as dirty
	 */
	private boolean albumListDirty = true;

	/**
	 * default editor
	 */
	public AlbumFilterEditor() {
		// register smartlist listener
		library = OperationsLocator.getAlbumOperations().getAlbumLibrary();
		library.addListener(new AlbumListener());

		// build editor
		initComponents();
	}

	/**
	 * build up editor component
	 */
	private void initComponents() {
		// editor panel
		editor = new JPanel();
		editor.setOpaque(false);
		editor.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

		// add must / must not switch
		editor.add(getNegateComboBox());

		// linking text
		JLabel text = new JLabel(" be element of album ");
		text.setOpaque(false);
		editor.add(text);

		// add smartlist chooser
		albumChooser = new JComboBox();
		albumChooser.addActionListener(new ChooserListener());
		updateAlbumList();
		editor.add(albumChooser);
	}

	/**
	 * prepaire and return list
	 * 
	 * @see org.jimcat.gui.smartlisteditor.editor.BaseNodeEditor#getEditor(javax.swing.JTree,
	 *      org.jimcat.gui.smartlisteditor.model.FilterTreeNode)
	 */
	@Override
	@SuppressWarnings("unused")
	public JComponent getEditor(JTree tree, FilterTreeNode node) {

		// set current node
		currentNode = (AlbumFilterNode) node;

		// update smart list list if necessary
		updateAlbumList();

		// select right smartlist
		if (currentNode.getAlbum() == null) {
			albumChooser.setSelectedIndex(-1);
		} else {
			albumChooser.setSelectedItem(currentNode.getAlbum().getName());
		}

		return editor;
	}

	/**
	 * update a the list of albums displayed updateAlbumList
	 */
	private void updateAlbumList() {
		// check if lists are dirty
		if (!albumListDirty) {
			return;
		}

		// refresh list
		List<Album> list = new ArrayList<Album>(library.getAll());
		Collections.sort(list, new AlbumComparator());

		// clear old lists
		string2Album.clear();
		albumList.clear();

		// regenerate internal list
		for (Album album : list) {
			string2Album.put(album.getName(), album);
			albumList.add(album.getName());
		}

		// update smartlist chooser
		albumChooser.setModel(new JComboBox(albumList.toArray()).getModel());

		// mark as clean
		albumListDirty = false;
	}

	/**
	 * this class is listening to the album library.
	 * 
	 * If there are changes, the album list will be set invalid.
	 */
	private class AlbumListener implements CollectionListener<Album, AlbumLibrary> {

		/**
		 * mark local list as dirty
		 * @param collection 
		 * 
		 * @see org.jimcat.model.notification.CollectionListener#basementChanged(org.jimcat.model.notification.ObservableCollection)
		 */
		@SuppressWarnings("unused")
		public void basementChanged(AlbumLibrary collection) {
			albumListDirty = true;
		}

		/**
		 * mark local list as dirty
		 * @param collection 
		 * @param elements 
		 * 
		 * @see org.jimcat.model.notification.CollectionListener#elementsAdded(org.jimcat.model.notification.ObservableCollection,
		 *      java.util.Set)
		 */
		@SuppressWarnings("unused")
		public void elementsAdded(AlbumLibrary collection, Set<Album> elements) {
			albumListDirty = true;
		}

		/**
		 * mark local list as dirty
		 * @param collection 
		 * @param elements 
		 * 
		 * @see org.jimcat.model.notification.CollectionListener#elementsRemoved(org.jimcat.model.notification.ObservableCollection,
		 *      java.util.Set)
		 */
		@SuppressWarnings("unused")
		public void elementsRemoved(AlbumLibrary collection, Set<Album> elements) {
			albumListDirty = true;
		}

		/**
		 * mark local list as dirty
		 * @param collection 
		 * @param events 
		 * 
		 * @see org.jimcat.model.notification.CollectionListener#elementsUpdated(org.jimcat.model.notification.ObservableCollection,
		 *      java.util.List)
		 */
		@SuppressWarnings("unused")
		public void elementsUpdated(AlbumLibrary collection, List<BeanChangeEvent<Album>> events) {
			albumListDirty = true;
		}

	}

	/**
	 * small class reacting on smartlist chooser changes
	 */
	private class ChooserListener implements ActionListener {
		/**
		 * react on change
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@SuppressWarnings("unused")
		public void actionPerformed(ActionEvent e) {
			// update value
			currentNode.setAlbum(string2Album.get(albumChooser.getSelectedItem()));
		}
	}

}
