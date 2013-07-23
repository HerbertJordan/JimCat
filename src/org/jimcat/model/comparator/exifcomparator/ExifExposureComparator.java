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
 * The ExifExposureComparator compares images concerning their exposure.
 * 
 * This class uses the information stored in the exif metadata about exposure to
 * compare images.
 * 
 * 
 * $Id$
 * 
 * @author Michael Handler
 */
public class ExifExposureComparator implements Comparator<Image> {

	/**
	 * This method compares two images concerning the information about exposure
	 * stored in the exif metadata.
	 * @param o1 
	 * @param o2 
	 * @return the result of the compare mehtod as specified in java.util.Comparator
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Image o1, Image o2) {
		if (o1 == null || o1.getExifMetadata() == null || o1.getExifMetadata().getExposure() == null) {
			if (o2 == null || o2.getExifMetadata() == null || o2.getExifMetadata().getExposure() == null)
				return 0;
			return -1;
		}
		if (o2 == null || o2.getExifMetadata() == null || o2.getExifMetadata().getExposure() == null)
			return 1;

		Float exposure1 = toFloat(o1.getExifMetadata().getExposure());
		Float exposure2 = toFloat(o2.getExifMetadata().getExposure());

		return exposure1.compareTo(exposure2);
	}

	private Float toFloat(String exposure) {
		String[] split = exposure.split("/");
		Float f1 = Float.valueOf(split[0]);

		if (split.length == 1) {
			return f1;
		}

		Float f2 = Float.valueOf(split[1]);

		return new Float(f1.floatValue() / f2.floatValue());
	}
}
