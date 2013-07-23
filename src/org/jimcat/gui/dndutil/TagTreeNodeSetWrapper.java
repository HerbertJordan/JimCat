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

package org.jimcat.gui.dndutil;

import java.util.Set;

import org.jimcat.gui.tagtree.TagTreeNode;

/**
 * A wrapper for a set of tagTreeNodes
 * 
 * 
 * $Id$
 * 
 * @author Michael
 */
public class TagTreeNodeSetWrapper {
	/**
	 * the data stored in this wrapper
	 */
	private Set<TagTreeNode> nodes;

	/**
	 * construct a new wrapper
	 * 
	 * @param nodes
	 */
	public TagTreeNodeSetWrapper(Set<TagTreeNode> nodes) {
		this.nodes = nodes;
	}

	/**
	 * @return the tagtree nodes
	 */
	public Set<TagTreeNode> getTagTreeNodes() {
		return nodes;
	}

	/**
	 * @param nodes
	 *            the tagtree nodes to set
	 */
	public void setTagTreeNodes(Set<TagTreeNode> nodes) {
		this.nodes = nodes;
	}
}
