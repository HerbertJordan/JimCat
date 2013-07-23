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

import org.jimcat.gui.smartlisteditor.model.FilterTreeNode;
import org.jimcat.gui.smartlisteditor.model.SmartListFilterNode;
import org.jimcat.model.SmartList;
import org.jimcat.model.comparator.SmartListComparator;
import org.jimcat.model.libraries.SmartListLibrary;
import org.jimcat.model.notification.BeanChangeEvent;
import org.jimcat.model.notification.CollectionListener;
import org.jimcat.services.OperationsLocator;

/**
 * A small editor for smartlist filter nodes.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class SmartListFilterEditor extends BaseNodeEditor {

	/**
	 * the up and running smartlist library
	 */
	private SmartListLibrary library;

	/**
	 * a list to map string representations to smartlists
	 */
	private Map<String, SmartList> string2List = new HashMap<String, SmartList>();

	/**
	 * a list of all smartlists available
	 */
	private List<String> smartListList = new LinkedList<String>();

	/**
	 * the editor component
	 */
	private JPanel editor;

	/**
	 * the smartlist chooser
	 */
	private JComboBox smartListChooser;

	/**
	 * currently edited node
	 */
	private SmartListFilterNode currentNode;

	/**
	 * to mark smart list as dirty
	 */
	private boolean smartListDirty = true;

	/**
	 * default editor
	 */
	public SmartListFilterEditor() {
		// register smartlist listener
		library = OperationsLocator.getSmartListOperations().getSmartListLibrary();
		library.addListener(new SmartListListener());

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
		JLabel text = new JLabel(" match SmartList ");
		text.setOpaque(false);
		editor.add(text);

		// add smartlist chooser
		smartListChooser = new JComboBox();
		smartListChooser.addActionListener(new ChooserListener());
		updateSmartListList();
		editor.add(smartListChooser);
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
		currentNode = (SmartListFilterNode) node;

		// update smart list list if necessary
		updateSmartListList();

		// select right smartlist
		if (currentNode.getSmartList() == null) {
			smartListChooser.setSelectedIndex(-1);
		} else {
			smartListChooser.setSelectedItem(currentNode.getSmartList().getName());
		}

		return editor;
	}

	/**
	 * update list of displayed smart lists
	 */
	private void updateSmartListList() {
		// check if lists are dirty
		if (!smartListDirty) {
			return;
		}

		// refresh list
		List<SmartList> list = new ArrayList<SmartList>(library.getAll());
		Collections.sort(list, new SmartListComparator());

		// clear old lists
		string2List.clear();
		smartListList.clear();

		// regenerate internal list
		for (SmartList sl : list) {
			string2List.put(sl.getName(), sl);
			smartListList.add(sl.getName());
		}

		// update smartlist chooser
		smartListChooser.setModel(new JComboBox(smartListList.toArray()).getModel());

		// mark as clean
		smartListDirty = false;
	}

	/**
	 * this class is listening to the smartlist library.
	 * 
	 * If there are changes, the smartlist list will be set invalid.
	 */
	private class SmartListListener implements CollectionListener<SmartList, SmartListLibrary> {

		/**
		 * mark local list as dirty
		 * @param collection 
		 * 
		 * @see org.jimcat.model.notification.CollectionListener#basementChanged(org.jimcat.model.notification.ObservableCollection)
		 */
		@SuppressWarnings("unused")
		public void basementChanged(SmartListLibrary collection) {
			smartListDirty = true;
		}

		/**
         * mark local list as dirty
		 * @param collection 
		 * @param elements 
		 * 
		 * @see org.jimcat.model.notification.CollectionListener#elementsAdded(org.jimcat.model.notification.ObservableCollection, java.util.Set)
         */
		@SuppressWarnings("unused")
		public void elementsAdded(SmartListLibrary collection, Set<SmartList> elements) {
			smartListDirty = true;
		}

		/**
         * mark local list as dirty
		 * @param collection 
		 * @param elements 
		 * 
		 * @see org.jimcat.model.notification.CollectionListener#elementsRemoved(org.jimcat.model.notification.ObservableCollection, java.util.Set)
         */
        @SuppressWarnings("unused")
		public void elementsRemoved(SmartListLibrary collection, Set<SmartList> elements) {
        	smartListDirty = true;
    	}

		/**
         * mark local list as dirty
		 * @param collection 
		 * @param events 
		 * 
		 * @see org.jimcat.model.notification.CollectionListener#elementsUpdated(org.jimcat.model.notification.ObservableCollection, java.util.List)
         */
        @SuppressWarnings("unused")
		public void elementsUpdated(SmartListLibrary collection, List<BeanChangeEvent<SmartList>> events) {
        	smartListDirty = true;
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
			currentNode.setSmartList(string2List.get(smartListChooser.getSelectedItem()));
		}
	}

}
