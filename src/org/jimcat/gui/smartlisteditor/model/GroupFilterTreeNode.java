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

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jimcat.model.filter.Filter;
import org.jimcat.model.filter.logical.AndFilter;
import org.jimcat.model.filter.logical.AssociativeCombinationFilter;
import org.jimcat.model.filter.logical.NotFilter;
import org.jimcat.model.filter.logical.OrFilter;

/**
 * This node represents a list of filters conected by a logic AND or OR.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class GroupFilterTreeNode extends FilterTreeNode {

	/**
	 * the type of this conection
	 */
	public enum Type {
		ALL, ANY;
	}

	/**
	 * the type of this logic filter node
	 */
	private Type type;

	/**
	 * create a new TreeNode filter
	 * 
	 * @param parent
	 * @param filter -
	 *            type is determind by filter
	 */
	public GroupFilterTreeNode(GroupFilterTreeNode parent, AssociativeCombinationFilter filter) {
		super(parent, false);

		// set type
		setTypeByFilter(filter);
	}

	/**
	 * create a new TreeNode filter
	 * 
	 * @param parent
	 * @param type -
	 *            given type
	 */
	public GroupFilterTreeNode(GroupFilterTreeNode parent, Type type) {
		super(parent, false);

		// set type
		setType(type);
	}

	/**
	 * allow to add child
	 * 
	 * @see org.jimcat.gui.smartlisteditor.model.FilterTreeNode#addChild(org.jimcat.gui.smartlisteditor.model.FilterTreeNode)
	 */
	@Override
	public void addChild(FilterTreeNode node) {
		super.addChild(node);
	}

	/**
	 * allow to remove child
	 * 
	 * @see org.jimcat.gui.smartlisteditor.model.FilterTreeNode#removeChild(org.jimcat.gui.smartlisteditor.model.FilterTreeNode)
	 */
	@Override
	public void removeChild(FilterTreeNode node) {
		super.removeChild(node);
	}

	/**
	 * generate a filter of this subtree
	 * 
	 * @see org.jimcat.gui.smartlisteditor.model.FilterTreeNode#getFilter()
	 */
	@Override
	public Filter getFilter() {

		Filter result = null;

		// build filter
		// start with the end

		// start tree
		Type internal = type;
		// invert if negativ
		if (isNegate()) {
			if (internal == Type.ALL) {
				internal = Type.ANY;
			} else {
				internal = Type.ALL;
			}
		}

		// work on to the top
		for (int i = 0; i < getChildrenCount(); i++) {
			if (internal == Type.ALL) {
				result = AndFilter.create(getChildrenAt(i).getFilter(), result);
			} else {
				result = OrFilter.create(getChildrenAt(i).getFilter(), result);
			}
		}

		// neagate if necessary
		if (isNegate()) {
			result = new NotFilter(result);
		}
		return result;
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
	 * @throws IllegalArgumentException 
	 */
	public void setType(Type type) throws IllegalArgumentException {
		if (type == null) {
			throw new IllegalArgumentException("type must not be null");
		}
		Type oldType = this.type;
		this.type = type;

		// if there was a change
		if (!type.equals(oldType)) {
			// inform listeners
			fireTreeNodeChange(this);
		}
	}

	/**
	 * choose type by given filter
	 * 
	 * @param filter
	 */
	public void setTypeByFilter(AssociativeCombinationFilter filter) {
		if (filter instanceof AndFilter) {
			setType(Type.ALL);
		} else if (filter instanceof OrFilter) {
			setType(Type.ANY);
		} else {
			throw new IllegalArgumentException("filter type not supported: " + filter.getClass().getSimpleName());
		}
	}

	/**
	 * regenerate a representive titel for this node
	 * 
	 * @see org.jimcat.gui.smartlisteditor.model.FilterTreeNode#generateTitle()
	 */
	@Override
	public String generateTitle() {
		if (isNegate()) {
			if (type == Type.ALL) {
				return "do not match " + Type.ANY + " of ...";
			}
			return "do not match " + Type.ALL + " of ...";
		}
		return "match " + type + " of ...";
	}

	/**
	 * Add a set of children to this node at the given index
	 * 
	 * @param nodesToAdd
	 * @param index
	 */
	public void addChildrenAtIndex(Set<FilterTreeNode> nodesToAdd, int index) {

		int pos = index;
		if (pos < 0) {
			pos = 0;
		}
		// remove the nodes that are already children here
		for (FilterTreeNode nodeToRemove : nodesToAdd) {
			if (nodeToRemove.getParent().equals(this)) {
				this.removeChild(nodeToRemove);
			}
		}
		// find the nodes that shall be shifted
		List<FilterTreeNode> tempChildren = new LinkedList<FilterTreeNode>();
		for (int i = pos; i < getChildrenCount(); i++) {
			FilterTreeNode childAtIndex = (FilterTreeNode) getChild(this, i);
			tempChildren.add(childAtIndex);
		}

		// remove them
		for (FilterTreeNode nodeToShift : tempChildren) {
			this.removeChild(nodeToShift);
		}

		// the nodes that shall be added
		for (FilterTreeNode nodeToAdd : nodesToAdd) {
			this.addChild(nodeToAdd);
		}
		// readd the shifted ones
		for (FilterTreeNode nodeToShift : tempChildren) {
			this.addChild(nodeToShift);
		}
	}
}
