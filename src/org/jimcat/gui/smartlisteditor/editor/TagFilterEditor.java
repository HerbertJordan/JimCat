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
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;

import org.jimcat.gui.SwingClient;
import org.jimcat.gui.smartlisteditor.model.FilterTreeNode;
import org.jimcat.gui.smartlisteditor.model.TagFilterNode;
import org.jimcat.model.notification.BeanChangeEvent;
import org.jimcat.model.notification.BeanListener;
import org.jimcat.model.notification.CollectionListener;
import org.jimcat.model.tag.Tag;
import org.jimcat.model.tag.TagGroup;

/**
 * An editor for a TagFilterNode
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class TagFilterEditor extends BaseNodeEditor {

	/**
	 * a map between string representations and tags
	 */
	private Map<String, Tag> string2Tag = new HashMap<String, Tag>();

	/**
	 * a map between tag and string representation
	 */
	private Map<Tag, String> tag2String = new HashMap<Tag, String>();

	/**
	 * a set of tags this editor is observing
	 */
	private Set<TagGroup> observedTags = new LinkedHashSet<TagGroup>();

	/**
	 * a list of string representations (ordered)
	 */
	private List<String> tagList = new LinkedList<String>();

	/**
	 * a listener observing tags
	 */
	private TagListener listener = new TagListener();

	/**
	 * used to refresh the internal taglists
	 */
	private boolean tagListDirty = true;

	/**
	 * the component used for editing
	 */
	private JPanel editor;

	/**
	 * the tag chooser to select tags
	 */
	private JComboBox tagChooser;

	/**
	 * currently edited tag filter node
	 */
	private TagFilterNode currentNode;

	/**
	 * create a new tageditor
	 */
	public TagFilterEditor() {
		initComponents();
	}

	/**
	 * build up gui content
	 */
	private void initComponents() {
		// basic setup
		editor = new JPanel();
		editor.setOpaque(false);
		editor.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

		// add must / must not switch
		editor.add(getNegateComboBox());

		// add label
		JLabel text = new JLabel(" have tag ");
		text.setOpaque(false);
		editor.add(text);

		// tag chooser
		tagChooser = new JComboBox(tagList.toArray());
		tagChooser.addActionListener(new TagChooserListener());
		editor.add(tagChooser);

		// init tag list
		updateTagList();
	}

	/**
	 * prepaire and return editor
	 * 
	 * @see org.jimcat.gui.smartlisteditor.editor.BaseNodeEditor#getEditor(javax.swing.JTree,
	 *      org.jimcat.gui.smartlisteditor.model.FilterTreeNode)
	 */
	@Override
	@SuppressWarnings("unused")
	public JComponent getEditor(JTree tree, FilterTreeNode node) {

		// update tag list
		updateTagList();

		currentNode = (TagFilterNode) node;
		// select right tag
		if (currentNode.getTag() != null) {
			tagChooser.setSelectedItem(tag2String.get(currentNode.getTag()));
		} else {
			tagChooser.setSelectedIndex(-1);
		}
		return editor;
	}

	/**
	 * regenerate tag map and list
	 */
	private void updateTagList() {
		// check state
		if (!tagListDirty) {
			// update not necessary
			return;
		}

		// first unregister from old
		for (TagGroup group : new ArrayList<TagGroup>(observedTags)) {
			// unregister
			group.removeListener((BeanListener<TagGroup>) listener);
			if (!(group instanceof Tag)) {
				group.removeListener((CollectionListener<TagGroup, TagGroup>) listener);
			}
		}

		// clear old lists
		observedTags.clear();
		tagList.clear();
		string2Tag.clear();
		tag2String.clear();

		// get tag hierarchie root
		TagGroup root = SwingClient.getInstance().getTagControl().getTagTreeRoot();

		// recreate tagtree
		addTagGroupToList(root, null);

		// show new list
		tagChooser.setModel(new JComboBox(tagList.toArray()).getModel());

		// mark taglists as clean
		tagListDirty = false;
	}

	/**
	 * recursive tag tree build up
	 * 
	 * @param group -
	 *            a group (root of subtree)
	 * @param prefix -
	 *            a prefix for this subtree
	 */
	private void addTagGroupToList(TagGroup group, String prefix) {
		// register bean listener anyway
		observedTags.add(group);
		group.addListener((BeanListener<TagGroup>) listener);
		// add if it's a tag
		if (group instanceof Tag) {
			String repres = prefix + group.getName();
			tagList.add(repres);
			string2Tag.put(repres, (Tag) group);
			tag2String.put((Tag) group, repres);
		} else {
			// register listener
			group.addListener((CollectionListener<TagGroup, TagGroup>) listener);
			// if it's a group => recursive step
			String newPrefix = prefix + group.getName() + " > ";
			if (prefix == null) {
				newPrefix = "";
			}
			for (TagGroup subGroup : group.getSubTags()) {
				addTagGroupToList(subGroup, newPrefix);
			}
		}
	}

	/**
	 * listen to taghierarchie changes.
	 */
	private class TagListener implements CollectionListener<TagGroup, TagGroup>, BeanListener<TagGroup> {

		/**
		 * react on a tag tree change
		 * @param collection 
		 * 
		 * @see org.jimcat.model.notification.CollectionListener#basementChanged(org.jimcat.model.notification.ObservableCollection)
		 */
		@SuppressWarnings("unused")
		public void basementChanged(TagGroup collection) {
			tagListDirty = true;
		}

		/**
		 * react on bean changes
		 * 
		 * @see org.jimcat.model.notification.BeanListener#beanPropertyChanged(org.jimcat.model.notification.BeanChangeEvent)
		 */
		@SuppressWarnings("unused")
		public void beanPropertyChanged(BeanChangeEvent<TagGroup> event) {
			tagListDirty = true;
		}

		/**
		 * react on a tag tree change
		 * @param collection 
		 * @param elements 
		 * 
		 * @see org.jimcat.model.notification.CollectionListener#elementsAdded(org.jimcat.model.notification.ObservableCollection,
		 *      java.util.Set)
		 */
		@SuppressWarnings("unused")
		public void elementsAdded(TagGroup collection, Set<TagGroup> elements) {
			tagListDirty = true;
		}

		/**
		 * react on a tag tree change
		 * @param collection 
		 * @param elements 
		 * 
		 * @see org.jimcat.model.notification.CollectionListener#elementsRemoved(org.jimcat.model.notification.ObservableCollection,
		 *      java.util.Set)
		 */
		@SuppressWarnings("unused")
		public void elementsRemoved(TagGroup collection, Set<TagGroup> elements) {
			tagListDirty = true;
		}

		/**
		 * react on a tag tree change
		 * @param collection 
		 * @param events 
		 * 
		 * @see org.jimcat.model.notification.CollectionListener#elementsUpdated(org.jimcat.model.notification.ObservableCollection,
		 *      java.util.List)
		 */
		@SuppressWarnings("unused")
		public void elementsUpdated(TagGroup collection, List<BeanChangeEvent<TagGroup>> events) {
			tagListDirty = true;
		}

	}

	/**
	 * to react on a changed selection within the tag chooser
	 */
	private class TagChooserListener implements ActionListener {
		/**
		 * react on a tag selection
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@SuppressWarnings("unused")
		public void actionPerformed(ActionEvent e) {
			Tag selected = string2Tag.get(tagChooser.getSelectedItem());
			currentNode.setTag(selected);
		}
	}

}
