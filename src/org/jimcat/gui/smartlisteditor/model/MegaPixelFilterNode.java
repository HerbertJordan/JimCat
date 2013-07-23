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
import org.jimcat.model.filter.metadata.MegaPixelFilter;
import org.jimcat.model.filter.metadata.MegaPixelFilter.Type;

/**
 * A MegaPixelFilterNode in the FilterTree.
 * 
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class MegaPixelFilterNode extends FilterTreeNode {

	/**
	 * the type of the represented filter
	 */
	private Type type;

	/**
	 * the megapixel limit
	 */
	private float mp;

	/**
	 * a direct constructor
	 * 
	 * @param parent
	 * @param filter 
	 */
	public MegaPixelFilterNode(GroupFilterTreeNode parent, MegaPixelFilter filter) {
		super(parent, true);

		// setup members
		type = filter.getMegaPixelCompareType();
		mp = filter.getMegaPixel();
	}

	/**
	 * generate titel for this node
	 * 
	 * @see org.jimcat.gui.smartlisteditor.model.FilterTreeNode#generateTitle()
	 */
	@Override
	public String generateTitle() {
		String linkingWord = "";
		switch (type) {
		case BIGGER_OR_EQUAL:
			linkingWord = "at least ";
			break;
		case SMALLER_THEN:
			linkingWord = "less than ";
			break;
		}
		return "must have " + linkingWord + mp + " MegaPixel";
	}

	/**
	 * create the represented Filter
	 * 
	 * @see org.jimcat.gui.smartlisteditor.model.FilterTreeNode#getFilter()
	 */
	@Override
	public Filter getFilter() {
		return new MegaPixelFilter(type, mp);
	}

	/**
	 * negating means actually switching type
	 * 
	 * @see org.jimcat.gui.smartlisteditor.model.FilterTreeNode#setNegate(boolean)
	 */
	@Override
	public void setNegate(boolean negate) {
		if (isNegate() != negate) {
			// switch type
			if (type == Type.BIGGER_OR_EQUAL) {
				type = Type.SMALLER_THEN;
			} else {
				type = Type.BIGGER_OR_EQUAL;
			}
		}
		// rest is done by super implementation
		super.setNegate(negate);
	}

	/**
	 * @return the mp
	 */
	public float getMp() {
		return mp;
	}

	/**
	 * @param mp
	 *            the mp to set
	 */
	public void setMp(float mp) {
		float oldValue = this.mp;
		this.mp = mp;
		if (oldValue != mp) {
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
	 * @param type
	 *            the type to set
	 */
	public void setType(Type type) {
		Type oldValue = this.type;
		this.type = type;
		// inform listeners
		if (!ObjectUtils.equals(oldValue, type)) {
			fireTreeNodeChange(this);
		}
	}

}
