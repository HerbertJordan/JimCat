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

package org.jimcat.gui.smartlisteditor;

import java.util.ArrayList;

import javax.swing.tree.TreeModel;

import org.jimcat.gui.smartlisteditor.model.AlbumFilterNode;
import org.jimcat.gui.smartlisteditor.model.ConstantFilterNode;
import org.jimcat.gui.smartlisteditor.model.DuplicateFilterNode;
import org.jimcat.gui.smartlisteditor.model.ExifMetadataFilterNode;
import org.jimcat.gui.smartlisteditor.model.FileSizeFilterNode;
import org.jimcat.gui.smartlisteditor.model.FilterTreeNode;
import org.jimcat.gui.smartlisteditor.model.GroupFilterTreeNode;
import org.jimcat.gui.smartlisteditor.model.HasTagsFilterNode;
import org.jimcat.gui.smartlisteditor.model.ImageSizeFilterNode;
import org.jimcat.gui.smartlisteditor.model.ImportFilterNode;
import org.jimcat.gui.smartlisteditor.model.IsPartOfAlbumFilterNode;
import org.jimcat.gui.smartlisteditor.model.MegaPixelFilterNode;
import org.jimcat.gui.smartlisteditor.model.PictureTakenFilterNode;
import org.jimcat.gui.smartlisteditor.model.RatingFilterNode;
import org.jimcat.gui.smartlisteditor.model.RelativeDateFilterNode;
import org.jimcat.gui.smartlisteditor.model.SmartListFilterNode;
import org.jimcat.gui.smartlisteditor.model.TagFilterNode;
import org.jimcat.gui.smartlisteditor.model.TextFilterNode;
import org.jimcat.gui.smartlisteditor.model.UnsupportedFilterNode;
import org.jimcat.model.filter.AlbumFilter;
import org.jimcat.model.filter.DuplicateFilter;
import org.jimcat.model.filter.Filter;
import org.jimcat.model.filter.HasTagsFilter;
import org.jimcat.model.filter.ImportFilter;
import org.jimcat.model.filter.IsPartOfAlbumFilter;
import org.jimcat.model.filter.RatingFilter;
import org.jimcat.model.filter.SmartListFilter;
import org.jimcat.model.filter.TagFilter;
import org.jimcat.model.filter.logical.AssociativeCombinationFilter;
import org.jimcat.model.filter.logical.NotFilter;
import org.jimcat.model.filter.metadata.ExifMetadataFilter;
import org.jimcat.model.filter.metadata.FileSizeFilter;
import org.jimcat.model.filter.metadata.ImageSizeFilter;
import org.jimcat.model.filter.metadata.MegaPixelFilter;
import org.jimcat.model.filter.metadata.PictureTakenFilter;
import org.jimcat.model.filter.metadata.RelativeDateFilter;
import org.jimcat.model.filter.metadata.TextFilter;

/**
 * A util class for smartlist model <-> filter transformation
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class FilterTreeUtil {

	/**
	 * generate a useable SmartListModel from a given filter
	 * 
	 * @param filter -
	 *            the filter to copy
	 * @return the root of a filter tree
	 */
	public static FilterTreeNode generateModel(Filter filter) {
		if (filter == null) {
			return new ConstantFilterNode(null);
		}
		return generateFilterTree(null, filter);
	}

	/**
	 * recursive methode to build up a filter tree around the given filter
	 * 
	 * @param parent -
	 *            the parent - use null on first call
	 * @param filter -
	 *            the subtree of the filter
	 * @return the root of a filter tree
	 */
	private static FilterTreeNode generateFilterTree(GroupFilterTreeNode parent, Filter filter) {
		FilterTreeNode result = null;
		if (filter instanceof AssociativeCombinationFilter) {
			AssociativeCombinationFilter current = (AssociativeCombinationFilter) filter;
			Filter first = current.getFirst();
			Filter second = current.getSecond();

			// create new logic filter node
			GroupFilterTreeNode logicNode = new GroupFilterTreeNode(parent, current);

			// handle left side
			FilterTreeNode subtree = generateFilterTree(null, first);
			mergeSubTree(logicNode, subtree);

			// the same for the right side
			subtree = generateFilterTree(null, second);
			mergeSubTree(logicNode, subtree);

			result = logicNode;
		} else if (filter instanceof NotFilter) {
			// not filters have to be handeld with care
			result = resolveNotFilter((NotFilter) filter);
			result.setParent(parent);
		} else if (filter instanceof TagFilter) {
			// just generate new TagFilterNode
			TagFilterNode node = new TagFilterNode(parent);
			node.setTag(((TagFilter) filter).getTag());
			result = node;
		} else if (filter instanceof ImportFilter) {
			// generate Import Filter Node
			ImportFilterNode node = new ImportFilterNode(parent, (ImportFilter) filter);
			result = node;
		} else if (filter instanceof DuplicateFilter) {
			// generate Duplicate Filter node
			result = new DuplicateFilterNode(parent);
		} else if (filter == null) {
			// generate constant filter node
			result = new ConstantFilterNode(parent);
		} else if (filter instanceof FileSizeFilter) {
			// generate fileSizeFilter node
			result = new FileSizeFilterNode(parent, (FileSizeFilter) filter);
		} else if (filter instanceof ImageSizeFilter) {
			// gnerate imageSizeFilter node
			result = new ImageSizeFilterNode(parent, (ImageSizeFilter) filter);
		} else if (filter instanceof PictureTakenFilter) {
			// generate picture taken filter
			result = new PictureTakenFilterNode(parent, (PictureTakenFilter) filter);
		} else if (filter instanceof TextFilter) {
			// generate text filter node
			result = new TextFilterNode(parent, (TextFilter) filter);
		} else if (filter instanceof RatingFilter) {
			// generate rating filter node
			result = new RatingFilterNode(parent, (RatingFilter) filter);
		} else if (filter instanceof MegaPixelFilter) {
			// generate megapixel filter node
			result = new MegaPixelFilterNode(parent, (MegaPixelFilter) filter);
		} else if (filter instanceof HasTagsFilter) {
			// generate has tag filter node
			result = new HasTagsFilterNode(parent);
		} else if (filter instanceof IsPartOfAlbumFilter) {
			// generate is part of album filter
			result = new IsPartOfAlbumFilterNode(parent);
		} else if (filter instanceof RelativeDateFilter) {
			// generate relative date filter node
			result = new RelativeDateFilterNode(parent, (RelativeDateFilter) filter);
		} else if (filter instanceof ExifMetadataFilter) {
			// generate exif metadata filter representation
			result = new ExifMetadataFilterNode(parent, (ExifMetadataFilter) filter);
		} else if (filter instanceof AlbumFilter) {
			// generate new album filter node
			result = new AlbumFilterNode(parent, (AlbumFilter) filter);
		} else if (filter instanceof SmartListFilter) {
			// generate new smartlist filter node
			result = new SmartListFilterNode(parent, (SmartListFilter) filter);
		} else {
			// unknown filter
			result = new UnsupportedFilterNode(parent, filter);
		}

		return result;
	}

	/**
	 * meld a subtree with the given allNode tree if subtree is based on an and
	 * filter.
	 * 
	 * @param rootNode
	 * @param subtree
	 */
	private static void mergeSubTree(GroupFilterTreeNode rootNode, FilterTreeNode subtree) {
		// check if subtree is still a logic link
		if (subtree instanceof GroupFilterTreeNode) {
			// if so, check if it is the same type
			GroupFilterTreeNode sub = (GroupFilterTreeNode) subtree;
			if (sub.getType() == rootNode.getType()) {
				// melt nodes
				for (FilterTreeNode node : new ArrayList<FilterTreeNode>(subtree.getChildren())) {
					// simple version: rootNode.addChild(node);
					// just add if they are no constant values
					if (!(node instanceof ConstantFilterNode)) {
						rootNode.addChild(node);
					} else {
						ConstantFilterNode constant = (ConstantFilterNode) node;
						boolean isAll = rootNode.getType() == GroupFilterTreeNode.Type.ALL;
						if (rootNode.isNegate()) {
							isAll = !isAll;
						}
						boolean isNeagted = constant.isNegate();
						if (isAll == isNeagted) {
							rootNode.addChild(node);
						}
					}
				}
				return;
			}
		}
		// otherwise, just add subtree
		rootNode.addChild(subtree);
	}

	/**
	 * this is to resolve special NotFilter scenarious
	 * 
	 * @param filter
	 * @return the resolved filter tree node
	 */
	private static FilterTreeNode resolveNotFilter(NotFilter filter) {
		// generate subtree by subfilter
		Filter subFilter = filter.getSubFilter();
		FilterTreeNode subNode = generateFilterTree(null, subFilter);
		// negate result
		subNode.setNegate(true);
		return subNode;
	}

	/**
	 * generate a filter from a given SmartListModel
	 * 
	 * @param model -
	 *            the model to converte
	 * @return the generated root of the filter tree
	 */
	public static Filter generateFilter(TreeModel model) {
		return ((FilterTreeNode) model).getFilter();
	}

	/**
	 * for debugging purposes
	 * 
	 * @param root
	 * @param offset
	 */
	@Deprecated
	public static void printTree(FilterTreeNode root, String offset) {
		if (root instanceof GroupFilterTreeNode) {
			GroupFilterTreeNode node = (GroupFilterTreeNode) root;
			System.out.println(offset + " - " + node.getType());
			for (FilterTreeNode child : node.getChildren()) {
				printTree(child, offset + "    ");
			}
		} else {
			System.out.println(offset + root.getTitel());
		}
	}
}
