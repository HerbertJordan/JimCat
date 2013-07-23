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

import org.joda.time.DateTime;

/**
 * 
 * The metadata of an image.
 * 
 * 
 * $Id: ImageMetadata.java 999 2007-09-14 20:02:58Z cleiter $
 * 
 * @author Christoph
 */
public class ImageMetadata {

	private String path;

	private transient File file;

	private int width;

	private int height;

	/**
	 * filesize in byte
	 */
	private long sizeBytes;

	/**
	 * a checksum for this image (should be MD5)
	 */
	private String checksum;

	private long importId;

	private DateTime modificationDate;

	private DateTime dateAdded;

	/**
	 * create a new ImageMetadata object with given informations. The bean is
	 * immutable.
	 * 
	 * @param path
	 * @param width
	 * @param height
	 * @param size
	 * @param checksum
	 * @param importId
	 * @param modificationDate
	 * @param dateAdded
	 */
	public ImageMetadata(File path, int width, int height, long size, String checksum, long importId,
	        DateTime modificationDate, DateTime dateAdded) {
		if (path != null) {
			this.path = path.getAbsolutePath();
		}
		this.width = width;
		this.height = height;
		this.sizeBytes = size;
		this.checksum = checksum;
		this.importId = importId;
		this.modificationDate = modificationDate;
		this.dateAdded = dateAdded;
	}

	/**
	 * @return the checksum
	 */
	public String getChecksum() {
		return checksum;
	}

	/**
	 * @return the dateAdded
	 */
	public DateTime getDateAdded() {
		return dateAdded;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @return the importId
	 */
	public long getImportId() {
		return importId;
	}

	/**
	 * @return the modificationDate
	 */
	public DateTime getModificationDate() {
		return modificationDate;
	}

	/**
	 * @return the path
	 */
	public File getPath() {
		if (file == null && path != null) {
			file = new File(path);
		}
		return file;
	}

	/**
	 * @return the sizeBytes
	 */
	public long getSize() {
		return sizeBytes;
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

}
