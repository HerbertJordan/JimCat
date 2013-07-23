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

package org.jimcat.model.comparator.exifcomparator;

import java.util.Comparator;

import org.jimcat.model.Image;


/**
 * The ExifDateTakenComparator compares images concerning the date when they
 * were taken.
 * 
 * This class implements the Comparator interface and uses the information about
 * when a image was taken that is stored in the exifmetadata of the images to
 * compare them.
 * 
 * 
 * $Id$
 * 
 * @author Michael Handler
 */
public class ExifDateTakenComparator implements Comparator<Image> {

	/**
	 * This method uses the date when the image was taken to compare two images.
	 * @param o1 
	 * @param o2 
	 * @return the result of the compare mehtod as specified in java.util.Comparator
	 * 
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Image o1, Image o2) {
		if (o1 == null || o1.getExifMetadata() == null || o1.getExifMetadata().getDateTaken() == null) {
			if (o2 == null || o2.getExifMetadata() == null || o2.getExifMetadata().getDateTaken() == null)
				return 0;
			return -1;
		}
		if (o2 == null || o2.getExifMetadata() == null || o2.getExifMetadata().getDateTaken() == null)
			return 1;
		return o1.getExifMetadata().getDateTaken().compareTo(o2.getExifMetadata().getDateTaken());
	}

}
