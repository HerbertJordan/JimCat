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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jimcat.model.Image;
import org.jimcat.model.notification.CollectionListener;

/**
 * representing a tag. Tags can be assigend to images (bidirectional link) and
 * are organized within an hierarchy. Nodes within the hierarchy tree are made
 * of TagGroup objects, leaves are modelled as Tags.
 * 
 * $Id: Tag.java 999 2007-09-14 20:02:58Z cleiter $
 * 
 * @author Herbert
 */
public class Tag extends TagGroup {

	/**
	 * a set of images owning this tag
	 */
	private Set<Image> images = new HashSet<Image>();

	/**
	 * Overriden cause in this special case it makes no sense
	 * 
	 * @see org.jimcat.model.tag.TagGroup#addListener(org.jimcat.model.notification.CollectionListener)
	 */
	@Override
	@SuppressWarnings("unused")
	public void addListener(CollectionListener<TagGroup, TagGroup> listener) throws UnsupportedOperationException {
		// listeners are ignored
	}

	/**
	 * Overriden cause it makes no sense in spezial case
	 * 
	 * @see org.jimcat.model.tag.TagGroup#addSubTag(org.jimcat.model.tag.TagGroup)
	 */
	@Override
	@SuppressWarnings("unused")
	public void addSubTag(TagGroup group) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Tags don't support subtags");
	}

	/**
	 * Overridden cause a Tag could never be root
	 * 
	 * @see org.jimcat.model.tag.TagGroup#isRoot()
	 */
	@Override
	public boolean isRoot() {
		return false;
	}

	/**
	 * Overriden cause in this special case it makes no sense
	 * 
	 * @see org.jimcat.model.tag.TagGroup#removeListener(org.jimcat.model.notification.CollectionListener)
	 */
	@Override
	@SuppressWarnings("unused")
	public void removeListener(CollectionListener<TagGroup, TagGroup> listener) {
		// listeners are ignored
	}

	/**
	 * Overriden cause Tags doesn't support Subtags
	 * 
	 * @see org.jimcat.model.tag.TagGroup#removeSubTag(org.jimcat.model.tag.TagGroup)
	 */
	@Override
	@SuppressWarnings("unused")
	public void removeSubTag(TagGroup group) {
		throw new UnsupportedOperationException("Tags don't support subtags");
	}

	/**
	 * associate an image with this tag
	 * 
	 * @see org.jimcat.model.tag.TagGroup#addImage(org.jimcat.model.Image)
	 */
	@Override
	public void addImage(Image image) {
		if (images.add(image)) {
			if (!image.hasTag(this)) {
				image.addTag(this);
			}
		}
	}

	/**
	 * remove a images associated to this tag
	 * 
	 * @param image
	 */
	@Override
	public void removeImage(Image image) {
		if (images.remove(image)) {
			if (image.hasTag(this)) {
				image.removeTag(this);
			}
		}
	}

	/**
	 * Return the images to which this tag has been added. There is a
	 * bi-directional dependency between images and tags.
	 * 
	 * @see org.jimcat.model.tag.TagGroup#getImages()
	 */
	@Override
	public Set<Image> getImages() {
		return Collections.unmodifiableSet(images);
	}

	/**
	 * test if this tag is part of the given tagList
	 * 
	 * @see org.jimcat.model.tag.TagGroup#containsAnyOf(java.util.Set)
	 */
	@Override
	public boolean containsAnyOf(Set<Tag> taglist) {
		// taglist must not be null
		if (taglist == null) {
			return false;
		}

		// check - quite simple
		return taglist.contains(this);
	}

	/**
	 * removes bidirectional link to images owning this tag
	 * 
	 * @see org.jimcat.model.tag.TagGroup#prepaireDelete()
	 */
	@Override
	public void prepaireDelete() {
		super.prepaireDelete();

		// inform images about removed tag
		List<Image> imageList = new ArrayList<Image>(images);
		images.clear();
		for (Image img : imageList) {
			if (img!=null) {
				img.removeTag(this);
			}
		}
	}
}
