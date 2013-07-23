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

package org.jimcat.gui.smartlisteditor.model;

import org.apache.commons.lang.ObjectUtils;
import org.jimcat.model.filter.Filter;
import org.jimcat.model.filter.logical.NotFilter;
import org.jimcat.model.filter.metadata.ExifMetadataFilter;
import org.jimcat.model.filter.metadata.ExifMetadataFilter.ExifMetadataProperty;

/**
 * A node within a filter Tree representing a ExifMetadata Filter.
 *
 * $Id$
 * @author Herbert
 */
public class ExifMetadataFilterNode extends FilterTreeNode {

	/**
	 * the property to filter for
	 */
	private ExifMetadataProperty property;
	
	/**
	 * the pattern to search for
	 */
	private String pattern;
	
	/**
     * create a new node representing given filter
     * @param parent
     * @param filter - to represent
     */
    public ExifMetadataFilterNode(GroupFilterTreeNode parent, ExifMetadataFilter filter) {
	    super(parent, true);
	    
	    // init members
	    this.property = filter.getProperty();
	    this.pattern = filter.getPattern();
    }

	/**
	 * generate a titel for this node
	 * @see org.jimcat.gui.smartlisteditor.model.FilterTreeNode#generateTitle()
	 */
	@Override
	public String generateTitle() {
		String item = getNameForProperty(property);
		return "exif property \"" + item + "\" " + getPrefix() + "contain \"" + pattern + "\""; 
	}

	/**
	 * get name for property
	 * 
	 * @param p
	 * @return the name of the property as String
	 */
	public static String getNameForProperty(ExifMetadataProperty p) {
		String result = null;
		if (p==ExifMetadataProperty.MANUFACTURER) {
			result = "producer";
		} else {
			result = p.toString().toLowerCase();
		}
		return result;
	}
	
	/**
	 * get exif metadate property for name
	 * 
	 * @param name
	 * @return the name of the exif property as String
	 */
	public static ExifMetadataProperty getPropertyForName(String name) {
		if (name.equals("producer")) {
			return ExifMetadataProperty.MANUFACTURER;
		}
		return ExifMetadataProperty.valueOf(name.toUpperCase());
	}
	
	/**
	 * generate a filter from this representation
	 * @see org.jimcat.gui.smartlisteditor.model.FilterTreeNode#getFilter()
	 */
	@Override
	public Filter getFilter() {
		Filter result = new ExifMetadataFilter(property, pattern);
		if (isNegate()) {
			result = new NotFilter(result);
		}
		return result;
	}

	/**
     * @return the pattern
     */
    public String getPattern() {
    	return pattern;
    }

	/**
     * @param pattern the pattern to set
     */
    public void setPattern(String pattern) {
    	String oldValue = this.pattern;
    	this.pattern = pattern;
    	
    	if (!ObjectUtils.equals(oldValue, pattern)) {
    		fireTreeNodeChange(this);
    	}
    }

	/**
     * @return the property
     */
    public ExifMetadataProperty getProperty() {
    	return property;
    }

	/**
     * @param property the property to set
     */
    public void setProperty(ExifMetadataProperty property) {
    	ExifMetadataProperty oldValue = this.property;
    	this.property = property;
    	
    	if (!ObjectUtils.equals(oldValue, property)) {
    		fireTreeNodeChange(this);
    	}
    }

}
