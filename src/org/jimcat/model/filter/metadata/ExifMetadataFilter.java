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

package org.jimcat.model.filter.metadata;

import org.jimcat.model.ExifMetadata;
import org.jimcat.model.Image;
import org.jimcat.model.filter.Filter;

/**
 * Used to filter special exif data properties
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class ExifMetadataFilter extends Filter {

	/**
	 * list of filterable exif data
	 */
	public enum ExifMetadataProperty {
		MANUFACTURER, MODEL, EXPOSURE, APERTURE, FLASH, FOCAL, ISO;
	}

	/**
	 * the item to look for
	 */
	private ExifMetadataProperty property;

	/**
	 * the pattern to search for
	 */
	private String pattern;

	/**
	 * creates a new ExifMetadataFilter using the given pattern
	 * 
	 * @param property
	 * @param pattern
	 */
	public ExifMetadataFilter(ExifMetadataProperty property, String pattern) {
		this.property = property;
		this.pattern = pattern.toLowerCase();
	}

	/**
	 * check if the given image matches this pattern
	 * 
	 * @see org.jimcat.model.filter.Filter#matches(org.jimcat.model.Image)
	 */
	@Override
	public boolean matches(Image image) {
		String compare = getCompareValue(image);
		if (compare == null) {
			return false;
		}
		return compare.toLowerCase().contains(pattern);
	}

	/**
	 * extract value to compare from given image
	 * 
	 * @param image
	 * @return - compare value or null
	 */
	private String getCompareValue(Image image) {
		if (image == null || image.getExifMetadata() == null) {
			return null;
		}
		ExifMetadata metadata = image.getExifMetadata();

		switch (property) {
		case MANUFACTURER:
			return metadata.getManufacturer();
		case MODEL:
			return metadata.getModel();
		case EXPOSURE:
			return metadata.getExposure();
		case APERTURE:
			return metadata.getAperture();
		case FLASH:
			return metadata.getFlash();
		case FOCAL:
			return metadata.getFocal();
		case ISO:
			return metadata.getIso();
		}
		return null;
	}

	/**
	 * @return the pattern
	 */
	public String getPattern() {
		return pattern;
	}

	/**
	 * @return the property
	 */
	public ExifMetadataProperty getProperty() {
		return property;
	}

	/**
	 * Get a new version of this filter which must be a new reference.
	 */
	@Override
	public Filter getCleanVersion() {
		return new ExifMetadataFilter(property, pattern);
	}

}
