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

import org.jimcat.model.filter.Filter;
import org.jimcat.model.filter.ImportFilter;
import org.jimcat.model.filter.ImportFilter.Type;
import org.jimcat.model.filter.logical.NotFilter;

/**
 * A filter tree node representing an import filter
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class ImportFilterNode extends FilterTreeNode {

	/**
	 * the import id to filter for
	 */
	private long importId;

	/**
	 * the type of the represented Filter
	 */
	private Type type;

	/**
	 * direct constructor
	 * 
	 * @param parent
	 * @param filter -
	 *            to represent
	 */
	public ImportFilterNode(GroupFilterTreeNode parent, ImportFilter filter) {
		super(parent, true);
		this.importId = filter.getImportID();
		this.type = filter.getImportCompareMode();
	}

	/**
	 * rebuild filter
	 * 
	 * @see org.jimcat.gui.smartlisteditor.model.FilterTreeNode#getFilter()
	 */
	@Override
	public Filter getFilter() {
		Filter result = new ImportFilter(type, importId);
		if (isNegate()) {
			result = new NotFilter(result);
		}
		return result;
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
			link = "bigger or equal than ";
			break;
		case EXACT:
			link ="";
			break;
		case UP_TO:
			link = "smaller than ";
			break;
		}
		
		return getPrefix() + "have import id " + link + importId;
	}

	/**
	 * @return the importId
	 */
	public long getImportId() {
		return importId;
	}

	/**
	 * @param importId
	 *            the importId to set
	 */
	public void setImportId(long importId) {
		long oldValue = this.importId;
		this.importId = importId;

		// inform listener
		if (oldValue != importId) {
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
    	
    	// inform listener
		if (oldValue != type) {
			fireTreeNodeChange(this);
		}
    }

}
