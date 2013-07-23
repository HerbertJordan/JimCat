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

import org.jimcat.model.ImageRating;
import org.jimcat.model.filter.Filter;
import org.jimcat.model.filter.RatingFilter;
import org.jimcat.model.filter.RatingFilter.Type;
import org.jimcat.model.filter.logical.NotFilter;

/**
 * A filter tree node representing a rating filter node
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class RatingFilterNode extends FilterTreeNode {

	/**
	 * the filter type
	 */
	private Type type;

	/**
	 * the rating type
	 */
	private ImageRating rating = ImageRating.NONE;

	/**
	 * direct constructor
	 * 
	 * @param parent
	 * @param filter - the filter to represent
	 */
	public RatingFilterNode(GroupFilterTreeNode parent, RatingFilter filter) {
		super(parent, true);
		this.type = filter.getRatingCompareMode();
		this.rating = filter.getRating();
	}
	
	/**
	 * generate new titel
	 * 
	 * @see org.jimcat.gui.smartlisteditor.model.FilterTreeNode#generateTitle()
	 */
	@Override
	public String generateTitle() {
		String link = "";
		switch (type) {
		case AT_LEAST:
			link = "at least";
			break;
		case EXACT:
			link = "exactly";
			break;
		case UP_TO:
			link = "less than";
			break;
		}
		
		String ratingName = new SmartlistEditorRatingRepresentation(rating).getName();
		String stars = "stars";
		
		if (rating.ordinal() == 1) {
			stars = "star";
		}
		
		return getPrefix() + "be rated with " + link + " " + ratingName + " " + stars;
	}

	/**
	 * build filter from node
	 * 
	 * @see org.jimcat.gui.smartlisteditor.model.FilterTreeNode#getFilter()
	 */
	@Override
	public Filter getFilter() {
		Filter result = new RatingFilter(type, rating);
		if (isNegate()) {
			result = new NotFilter(result);
		}
		return result;
	}

	/**
     * @return the rating
     */
    public ImageRating getRating() {
    	return rating;
    }

	/**
     * @param rating the rating to set
     */
    public void setRating(ImageRating rating) {
    	ImageRating oldValue = this.rating;
    	this.rating = rating;
    	if (oldValue!=rating) {
    		fireTreeNodeChange(this);
    	}
    }

	/**
     * @return the type
     */
    public Type getType() {
    	return type;
    }

	/**
     * @param type the type to set
     */
    public void setType(Type type) {
    	Type oldValue = this.type;
    	this.type = type;
    	if (oldValue!=type) {
    		fireTreeNodeChange(this);
    	}
    }

}
