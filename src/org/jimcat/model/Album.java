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

package org.jimcat.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.jimcat.model.notification.BeanListener;
import org.jimcat.model.notification.BeanProperty;
import org.jimcat.model.notification.ListenerManager;
import org.jimcat.model.notification.ObservableBean;

/**
 * Representing an album. The contained images are bidirectional linked. An
 * album is like other elements an observeable Bean.
 * 
 * $Id: Album.java 999 2007-09-14 20:02:58Z cleiter $
 * 
 * @author Herbert
 */
public class Album implements ObservableBean<Album> {

	@SuppressWarnings("unused")
	private Long id;

	/**
	 * a Listener Manager for this bean
	 */
	private transient ListenerManager<Album> manager;

	/**
	 * a name for this Album
	 */
	private String name;

	/**
	 * the list of images included. it also containes the album sorting order
	 */
	private List<Image> images = new LinkedList<Image>();

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
			getManager().notifyListeners(BeanProperty.ALBUM_NAME, name, oldValue);
		}
	}

	/**
	 * adds an image to this album
	 * 
	 * @param img
	 */
	public void addImage(Image img) {
		if (!images.contains(img)) {
			images.add(img);
			img.addToAlbum(this);
			getManager().notifyListeners(BeanProperty.ALBUM_IMAGES, null);
		}
	}

	/**
	 * removes an image from this album
	 * 
	 * @param img
	 */
	public void removeImage(Image img) {
		boolean contained = images.remove(img);
		if (contained) {
			img.removeFromAlbum(this);
			getManager().notifyListeners(BeanProperty.ALBUM_IMAGES, null);
		}
	}

	/**
	 * Test if a certain image is part of this album
	 * 
	 * @param img -
	 *            image to test
	 * @return true if the image is part of the album
	 */
	public boolean contains(Image img) {
		return images.contains(img);
	}

	/**
	 * returns a list of images contained in this Album.
	 * 
	 * @return a list of the images in this album
	 */
	public List<Image> getImages() {
		return Collections.unmodifiableList(images);
	}

	/**
	 * This will move the given list of images within the album to the specified
	 * position.
	 * 
	 * @param list -
	 *            a list of images
	 * @param pos -
	 *            the target position. &lt0 will bekome 0, &gt size() will
	 *            bekome size()
	 * @throws IllegalArgumentException -
	 *             if some of the images arn't elements of this album
	 */
	public void moveToIndex(List<Image> list, int pos) throws IllegalArgumentException {

		// check if arguments are allowd
		if (!images.containsAll(list)) {
			throw new IllegalArgumentException("Given images aren't within this album");
		}

		// correct index if it is out of bound
		int index = pos;
		if (index < 0) {
			index = 0;
		} else if (index > images.size()) {
			index = images.size();
		}

		// alter index if elements befor the insert point are removed
		boolean priv = false;
		for (Image img : list) {
			if (images.indexOf(img) < index) {
				index--;
				priv = true;
			}
		}
		// if there where changes, there was one to much
		if (priv) {
			index++;
		}

		// remove all images from list
		images.removeAll(list);

		// add images at new position
		images.addAll(Math.min(index, images.size()), list);

		// inform listeners
		if (list.size() > 0) {
			getManager().notifyListeners(BeanProperty.ALBUM_IMAGES, null);
		}
	}

	/**
	 * @param o
	 * @return the index of the given image
	 * @see java.util.List#indexOf(java.lang.Object)
	 */
	public int indexOf(Image o) {
		return images.indexOf(o);
	}

	/**
	 * provide null-save access to the listener manager
	 * 
	 * @return the listener manager of this album
	 */
	private ListenerManager<Album> getManager() {
		if (manager == null) {
			manager = new ListenerManager<Album>(this);
		}
		return manager;
	}

	/**
	 * add a listener to this manager
	 * 
	 * @see org.jimcat.model.notification.ObservableBean#addListener(org.jimcat.model.notification.BeanListener)
	 */
	public void addListener(BeanListener<Album> listener) {
		getManager().addListener(listener);
	}

	/**
	 * remove a listener from this bean
	 * 
	 * @see org.jimcat.model.notification.ObservableBean#removeListener(org.jimcat.model.notification.BeanListener)
	 */
	public void removeListener(BeanListener<Album> listener) {
		getManager().removeListener(listener);
	}

	/**
	 * removes linkes to Album
	 * 
	 * @see org.jimcat.model.notification.ObservableBean#prepaireDelete()
	 */
	public void prepaireDelete() {
		// inform all images about removed album
		List<Image> imgs = new ArrayList<Image>(images);
		images.clear();
		for (Image img : imgs) {
			img.removeFromAlbum(this);
		}
	}
}
