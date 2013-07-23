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

package org.jimcat.model.tag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.jimcat.model.Image;
import org.jimcat.model.notification.BeanListener;
import org.jimcat.model.notification.BeanProperty;
import org.jimcat.model.notification.CollectionListener;
import org.jimcat.model.notification.CollectionListenerManager;
import org.jimcat.model.notification.ListenerManager;
import org.jimcat.model.notification.ObservableBean;
import org.jimcat.model.notification.ObservableCollection;

/**
 * A TagGroup forming a Tag - Tree node.
 * 
 * A TagGroup is both, an ObservableCollection and an Observable Bean.
 * 
 * $Id: TagGroup.java 999 2007-09-14 20:02:58Z cleiter $
 * 
 * @author Herbert
 */
public class TagGroup implements ObservableCollection<TagGroup, TagGroup>, ObservableBean<TagGroup> {

	/**
	 * the name of this tag
	 */
	private String name;

	/**
	 * is this node the root of the TagTree?
	 */
	private boolean root = false;

	/**
	 * list of contained Tag(Groups)
	 */
	private List<TagGroup> tags = new ArrayList<TagGroup>();

	/**
	 * private listenermanager for notifications (Bean)
	 */
	private transient ListenerManager<TagGroup> beanListenerManager;

	/**
	 * private collectionlistenerManger for notifications (Collection)
	 */
	private transient CollectionListenerManager<TagGroup, TagGroup> collectionListenerManager;

	/**
	 * default constructore
	 */
	public TagGroup() {
		/* empty */
	}

	/**
	 * create a new TagGroup with given root flag
	 * 
	 * @param isRoot
	 */
	public TagGroup(boolean isRoot) {
		this.root = isRoot;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		String oldValue = this.name;
		this.name = name;
		if (!ObjectUtils.equals(oldValue, name)) {
			getManager().notifyListeners(BeanProperty.TAG_NAME, name, oldValue);
		}
	}

	/**
	 * @return the root
	 */
	public boolean isRoot() {
		return root;
	}

	/**
	 * this methode allows to associate an image with this taggroup
	 * 
	 * @param image
	 */
	@SuppressWarnings("unused")
	public void addImage(Image image) {
		throw new UnsupportedOperationException("You can't assign an image to a TagGroup");
	}

	/**
	 * this methode allows remove an image from this taggroup
	 * 
	 * @param image
	 */
	@SuppressWarnings("unused")
	public void removeImage(Image image) {
		throw new UnsupportedOperationException("You can't assign an image to a TagGroup");
	}

	/**
	 * get a list of images owning this tag
	 * 
	 * @return the images assigned to this tag group
	 */
	public Set<Image> getImages() {
		throw new UnsupportedOperationException("A TagGroup has no assigned images.");
	}

	/**
	 * adds another subtag
	 * 
	 * @param group
	 */
	public void addSubTag(TagGroup group) {
		if (tags.add(group)) {
			getCollectionManager().notifyAdded(Collections.singleton(group));
		}
	}

	/**
	 * removes a subtag
	 * 
	 * @param group
	 */
	public void removeSubTag(TagGroup group) {
		if (tags.remove(group)) {
			getCollectionManager().notifyRemoved(Collections.singleton(group));
		}
	}

	/**
	 * method to move some subtags from one TagGroup to another.
	 * 
	 * @param src
	 *            the source group
	 * @param tags
	 *            the tags to move
	 * @param dest
	 *            the destination group
	 * @param pos
	 *            the insertion point
	 */
	public static void moveTags(TagGroup src, Set<TagGroup> tags, TagGroup dest, int pos) {

		// correct index if it is out of bound
		int index = pos;
		if (index < 0) {
			index = 0;
		} else if (index > dest.tags.size()) {
			index = dest.tags.size();
		}

		// special case - src = dest
		if (src == dest) {
			// alter index if elements before the insert point are removed
			boolean priv = false;
			for (TagGroup tag : tags) {
				if (src.tags.indexOf(tag) < index) {
					index--;
					priv = true;
				}
			}
			// if there were changes, there was one to much
			if (priv) {
				index++;
			}
		}

		// make changes
		src.tags.removeAll(tags);
		dest.tags.addAll(index, tags);

		// notify listeners
		src.getCollectionManager().notifyRemoved(tags);
		dest.getCollectionManager().notifyAdded(tags);
	}

	/**
	 * test if this or any subelement includes one of those tags given
	 * 
	 * @param taglist
	 *            a list of tages
	 * @return - true if one of the tags is a leaf of this subtree, false
	 *         otherwise
	 */
	public boolean containsAnyOf(Set<Tag> taglist) {
		if (taglist == null) {
			return false;
		}

		boolean found = false;

		for (TagGroup group : tags) {
			found = group.containsAnyOf(taglist);
			if (found) {
				return true;
			}
		}

		return false;
	}

	/**
	 * returns a sealed list of current subtags
	 * 
	 * @return a list of subtags of this tag group
	 */
	public List<TagGroup> getSubTags() {
		return Collections.unmodifiableList(tags);
	}

	/**
	 * 
	 * @param group
	 * @return the index of an element contained in this group
	 * 
	 * @see List#indexOf(Object)
	 */
	public int indexOf(TagGroup group) {
		return tags.indexOf(group);
	}

	/**
	 * Add another listener to this collection
	 * 
	 * @see org.jimcat.model.notification.ObservableCollection#addListener(org.jimcat.model.notification.CollectionListener)
	 */
	public void addListener(CollectionListener<TagGroup, TagGroup> listener) {
		getCollectionManager().addListener(listener);
	}

	/**
	 * removes this collectionlistener
	 * 
	 * @see org.jimcat.model.notification.ObservableCollection#removeListener(org.jimcat.model.notification.CollectionListener)
	 */
	public void removeListener(CollectionListener<TagGroup, TagGroup> listener) {
		getCollectionManager().removeListener(listener);
	}

	/**
	 * Add another bean listener
	 * 
	 * @see org.jimcat.model.notification.ObservableBean#addListener(org.jimcat.model.notification.BeanListener)
	 */
	public void addListener(BeanListener<TagGroup> listener) {
		getManager().addListener(listener);
	}

	/**
	 * removes a bean listener
	 * 
	 * @see org.jimcat.model.notification.ObservableBean#removeListener(org.jimcat.model.notification.BeanListener)
	 */
	public void removeListener(BeanListener<TagGroup> listener) {
		getManager().addListener(listener);
	}

	/**
	 * just to implement interface - this bean doesn't manage bidirectional
	 * links
	 * 
	 * @see org.jimcat.model.notification.ObservableBean#prepaireDelete()
	 */
	public void prepaireDelete() {
		// just to implement interface
	}

	/**
	 * this methode is hiding the transient nature of the
	 * collectionListenerManager
	 * 
	 * @return the collection listener manager for this tag group
	 */
	private CollectionListenerManager<TagGroup, TagGroup> getCollectionManager() {
		if (collectionListenerManager == null) {
			collectionListenerManager = new CollectionListenerManager<TagGroup, TagGroup>(this);
		}
		return collectionListenerManager;
	}

	/**
	 * this methode is hiding the transient nature of the listener manager
	 * 
	 * @return the listener manager for this tag group
	 */
	private ListenerManager<TagGroup> getManager() {
		if (beanListenerManager == null) {
			beanListenerManager = new ListenerManager<TagGroup>(this);
		}
		return beanListenerManager;
	}

	/**
	 * Return a string representation of a TagGroupS
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
