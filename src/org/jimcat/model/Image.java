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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.jimcat.model.notification.BeanListener;
import org.jimcat.model.notification.BeanProperty;
import org.jimcat.model.notification.ListenerManager;
import org.jimcat.model.notification.ObservableBean;
import org.jimcat.model.tag.Tag;

/**
 * The main representation of a managed image within the system.
 * 
 * $Id: Image.java 999 2007-09-14 20:02:58Z cleiter $
 * 
 * @author Herbert
 */
public class Image implements ObservableBean<Image>, Comparable<Image> {

	private String title;

	private ImageRating rating = ImageRating.NONE;

	private String description;

	private String lastExportPath;

	private ImageRotation rotation = ImageRotation.ROTATION_0;

	private Thumbnail thumbnail;

	private ImageMetadata metadata;

	private ExifMetadata exifMetadata;

	private Set<Tag> tags = new HashSet<Tag>();

	private Set<Album> albums = new HashSet<Album>();

	private transient ListenerManager<Image> listenerManager = new ListenerManager<Image>(this);

	/**
	 * add a Tag to this image - will fire a propertyChanged event
	 * 
	 * @param tag
	 */
	public void addTag(Tag tag) {
		if (tags.add(tag)) {
			tag.addImage(this);
			getListenerManager().notifyListeners(BeanProperty.IMAGE_TAGS, null);
		}
	}

	/**
	 * test if this image has given tag assigned
	 * 
	 * @param tag
	 * @return true if tag is assigned, false else
	 */
	public boolean hasTag(Tag tag) {
		return tags.contains(tag);
	}

	/**
	 * get set of assigned tags
	 * 
	 * @return a unmodifiable set if tags assigened to this image
	 */
	public Set<Tag> getTags() {
		return Collections.unmodifiableSet(tags);
	}

	/**
	 * remove an assigned tag. If tag was present, an property Changed event
	 * will be fired
	 * 
	 * @param tag
	 *            the tag to remove
	 */
	public void removeTag(Tag tag) {
		boolean contains = tags.remove(tag);
		if (contains) {
			getListenerManager().notifyListeners(BeanProperty.IMAGE_TAGS, null);
		}
	}

	/**
	 * add a new BeanListener to this observable Bean
	 * 
	 * @see org.jimcat.model.notification.ObservableBean#addListener(org.jimcat.model.notification.BeanListener)
	 */
	public void addListener(BeanListener<Image> listener) {
		getListenerManager().addListener(listener);
	}

	/**
	 * remove a BeanListener from this observable Bean
	 * 
	 * @see org.jimcat.model.notification.ObservableBean#removeListener(org.jimcat.model.notification.BeanListener)
	 */
	public void removeListener(BeanListener<Image> listener) {
		getListenerManager().removeListener(listener);
	}

	/**
	 * Set the rating. Fires an event if the oldrating was different than the
	 * new one.
	 * 
	 * @param rating
	 */
	public void setRating(ImageRating rating) {
		ImageRating locRating = ImageRating.values()[rating.ordinal()];
		if (this.rating == locRating) {
			return;
		}

		ImageRating oldValue = ImageRating.values()[this.rating.ordinal()];
		this.rating = locRating;
		getListenerManager().notifyListeners(BeanProperty.IMAGE_RATING, this.rating, oldValue);
	}

	/**
	 * @return the titel
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the titel to set
	 */
	public void setTitle(String title) {
		String oldValue = this.title;
		this.title = title;
		if (!ObjectUtils.equals(oldValue, title)) {
			// just fire if there was realy a change
			getListenerManager().notifyListeners(BeanProperty.IMAGE_TITEL, title, oldValue);
		}
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		String oldValue = this.description;
		this.description = description;
		if (!ObjectUtils.equals(oldValue, description)) {
			// just fire if there was realy a change
			getListenerManager().notifyListeners(BeanProperty.IMAGE_DESCRIPTION, description, oldValue);
		}
	}

	/**
	 * @return the lastExportPath
	 */
	public String getLastExportPath() {
		return lastExportPath;
	}

	/**
	 * @param lastExportPath
	 *            the lastExportPath to set
	 */
	public void setLastExportPath(String lastExportPath) {
		String oldValue = this.lastExportPath;
		this.lastExportPath = lastExportPath;
		if (!ObjectUtils.equals(oldValue, lastExportPath)) {
			// just fire if there was realy a change
			getListenerManager().notifyListeners(BeanProperty.IMAGE_LAST_EXPORT_PATH, description, oldValue);
		}
	}

	/**
	 * @return the exifMetadata
	 */
	public ExifMetadata getExifMetadata() {
		return exifMetadata;
	}

	/**
	 * @param exifMetadata
	 *            the exifMetadata to set
	 */
	public void setExifMetadata(ExifMetadata exifMetadata) {
		ExifMetadata oldValue = this.exifMetadata;
		this.exifMetadata = exifMetadata;
		if (!ObjectUtils.equals(oldValue, description)) {
			// just fire if there was realy a change
			getListenerManager().notifyListeners(BeanProperty.IMAGE_EXIF_META, exifMetadata, oldValue);
		}
	}

	/**
	 * @return the metadata
	 */
	public ImageMetadata getMetadata() {
		return metadata;
	}

	/**
	 * @param metadata
	 *            the metadata to set
	 */
	public void setMetadata(ImageMetadata metadata) {
		ImageMetadata oldValue = this.metadata;
		this.metadata = metadata;
		if (!ObjectUtils.equals(oldValue, metadata)) {
			// just fire if there was realy a change
			getListenerManager().notifyListeners(BeanProperty.IMAGE_METADATA, metadata, oldValue);
		}
	}

	/**
	 * @return the rotation
	 */
	public ImageRotation getRotation() {
		// to ensure correct enumeration item
		return ImageRotation.values()[rotation.ordinal()];
	}

	/**
	 * @param rotation
	 *            the rotation to set
	 */
	public void setRotation(ImageRotation rotation) {
		ImageRotation oldValue = ImageRotation.values()[this.rotation.ordinal()];
		this.rotation = ImageRotation.values()[rotation.ordinal()];
		if (rotation != oldValue) {
			// just fire if there was really a change
			getListenerManager().notifyListeners(BeanProperty.IMAGE_ROTATION, rotation, oldValue);
		}
	}

	/**
	 * @return the thumbnail
	 */
	public Thumbnail getThumbnail() {
		return thumbnail;
	}

	/**
	 * @param thumbnail
	 *            the thumbnail to set
	 */
	public void setThumbnail(Thumbnail thumbnail) {
		Thumbnail oldValue = this.thumbnail;
		this.thumbnail = thumbnail;
		if (thumbnail != oldValue) {
			// just fire if there was really a change
			getListenerManager().notifyListeners(BeanProperty.IMAGE_THUMBNAIL, thumbnail, oldValue);
		}
	}

	/**
	 * @return the rating
	 */
	public ImageRating getRating() {
		return ImageRating.values()[rating.ordinal()];
	}

	/**
	 * private methode to overcome transient ListenerManager
	 * 
	 * @return the listener manager of this image
	 */
	private ListenerManager<Image> getListenerManager() {
		if (listenerManager == null) {
			listenerManager = new ListenerManager<Image>(this);
		}
		return listenerManager;
	}

	/**
	 * 
	 * The equals method of Image checks wheter the absolute file of two images
	 * is equal to determine if two images are equal. So it uses the equals
	 * method of the absolute files derived from the file object stored in the
	 * metadata of the images. This is because there should only be one image
	 * with a given absolute path in the database. If any of the needed
	 * parameters is null in this or the other Image this method delegetes the
	 * equals to the equals in the superclass.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		if (!(obj instanceof Image)) {
			return false;
		}

		Image other = (Image) obj;

		if (this.getMetadata() == null || other.getMetadata() == null) {
			return super.equals(other);
		}

		if (this.getMetadata().getPath() == null || other.getMetadata().getPath() == null) {
			return super.equals(other);
		}

		File thisPath = this.getMetadata().getPath().getAbsoluteFile();
		File otherPath = other.getMetadata().getPath().getAbsoluteFile();

		return thisPath.equals(otherPath);
	}

	/**
	 * create a hash code for this image - to by synchron with equals
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		// get hashcode from path
		if (getMetadata() != null && getMetadata().getPath() != null) {
			return getMetadata().getPath().hashCode();
		}
		return super.hashCode();
	}

	/**
	 * Return a string representation of an image
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(albums);
	}

	/**
	 * get a set of albums containing this image
	 * 
	 * @return an unmodifiable set containing this imaged - backed by local set
	 */
	public Set<Album> getAlbums() {
		return Collections.unmodifiableSet(albums);
	}

	/**
	 * Remove this image from the given album. Fires a album change event.
	 * 
	 * @param album
	 */
	public void removeFromAlbum(Album album) {
		// make changes
		boolean wasPresent = albums.remove(album);
		// bidirectional
		if (album.contains(this)) {
			album.removeImage(this);
		}

		// notify listeners
		if (wasPresent) {
			getListenerManager().notifyListeners(BeanProperty.IMAGE_ALBUMS, album);
		}
	}

	/**
	 * Add this image to this album. There is a bi-directional link between
	 * albums and images. It fires a AlbumChange Event.
	 * 
	 * @param album
	 */
	public void addToAlbum(Album album) {
		// make changes
		boolean isNew = albums.add(album);
		// bidirectional
		if (!album.contains(this)) {
			album.addImage(this);
		}

		// notify listeners
		if (isNew) {
			getListenerManager().notifyListeners(BeanProperty.IMAGE_ALBUMS, album);
		}
	}

	/**
	 * this will clear all bidirectional links
	 * 
	 * @see org.jimcat.model.notification.ObservableBean#prepaireDelete()
	 */
	public void prepaireDelete() {
		// inform albums about removed image
		List<Album> albumList = new ArrayList<Album>(albums);
		albums.clear();
		for (Album album : albumList) {
			album.removeImage(this);
		}

		// inform tags about removed image
		List<Tag> tagList = new ArrayList<Tag>(tags);
		tags.clear();
		for (Tag tag : tagList) {
			tag.removeImage(this);
		}
	}

	/**
	 * compares two Images in there natural order (file path)
	 * 
	 * @param o
	 * @return the result of the compareTo method as specified in java
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Image o) {
		// fast decision
		if (this == o) {
			return 0;
		}

		// get the data
		File path1 = (getMetadata() != null) ? getMetadata().getPath() : null;
		File path2 = (o.getMetadata() != null) ? o.getMetadata().getPath() : null;
		// test if first is null
		if (path1 == null) {
			// if yes test if second is null too
			if (path2 == null)
				return -1;
			// only first is null
			return -1;
		}
		// test if only second is null
		if (path2 == null)
			return 1;

		// none of them is null, so delegate to compareTo from String
		return path1.compareTo(path2);
	}
}
