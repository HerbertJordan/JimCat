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

package org.jimcat.gui.tagtree;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import org.jimcat.gui.SwingClient;
import org.jimcat.gui.dndutil.ExceptionCatchingTransferHandler;
import org.jimcat.gui.dndutil.ImageSetWrapper;
import org.jimcat.gui.dndutil.TagSetWrapper;
import org.jimcat.gui.dndutil.TagTransferable;
import org.jimcat.gui.dndutil.TagTreeNodeSetWrapper;
import org.jimcat.model.tag.Tag;
import org.jimcat.model.tag.TagGroup;

/**
 * The TagTreeTransferHandler is needed to achieve drag&drop for the Tag Tree.
 * 
 * It is responsible for the export and import of data by drag & drop.
 * 
 * $Id$
 * 
 * @author Michael
 */
public class TagTreeTransferHandler extends ExceptionCatchingTransferHandler {
	/**
	 * the supported DataFlavors
	 */
	private DataFlavor imageFlavor = new DataFlavor(ImageSetWrapper.class, "ImageSetWrapper");

	private DataFlavor tagNodeSetFlavor = new DataFlavor(TagTreeNodeSetWrapper.class, "TagTreeNodeSetWrapper");

	/**
	 * the TagTree to which this transferHandler belongs
	 */
	private TagTree tagTree;

	/**
	 * the list of selected tags to keep selection when shifting tags in the
	 * tagTree
	 */
	private Set<Tag> tempSelectionStore = new HashSet<Tag>();

	/**
	 * 
	 * Construct a new transfer handler for the TagTree tagTree
	 * 
	 * @param tagTree
	 */
	private TagTreeTransferHandler(TagTree tagTree) {
		this.tagTree = tagTree;
	}

	/**
	 * 
	 * Install a transfer handler to a tag tree
	 * 
	 * @param tagTree
	 */
	public static void installTagTreeTransferHandler(TagTree tagTree) {
		tagTree.getTree().setTransferHandler(new TagTreeTransferHandler(tagTree));
	}

	/**
	 * 
	 * The can import tests wheter c is an instance of JTree and if true it
	 * iterates over the flavors contained in the drag & drop to test if the
	 * supported flavor is part of it.
	 * 
	 * @see javax.swing.TransferHandler#canImport(javax.swing.JComponent,
	 *      java.awt.datatransfer.DataFlavor[])
	 */
	@Override
	public boolean safeCanImport(JComponent c, DataFlavor[] flavors) {
		if (c instanceof JTree) {
			JTree tree = (JTree) c;
			Point pos = tree.getMousePosition();
			if (pos != null) {
				TreePath path = tree.getClosestPathForLocation(pos.x, pos.y);
				TagTreeNode node = (TagTreeNode) path.getLastPathComponent();
				if (node.getTag() instanceof Tag) {
					// if current node is a tag then images and
					// tags can be dropped there
					for (int i = 0; i < flavors.length; i++) {
						if (imageFlavor.equals(flavors[i]) || tagNodeSetFlavor.equals(flavors[i])) {
							return true;
						}
					}
				} else {
					// if current node is a taggroup then only
					// tags can be dropped there
					for (int i = 0; i < flavors.length; i++) {
						if (tagNodeSetFlavor.equals(flavors[i])) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * Create a Transferable containing the tags that shall be transported.
	 * 
	 * @see javax.swing.TransferHandler#createTransferable(javax.swing.JComponent)
	 */
	@Override
	protected Transferable safeCreateTransferable(JComponent c) {
		Set<Tag> tagsToTransfer = new HashSet<Tag>();
		Set<TagTreeNode> nodesToTransfer = new HashSet<TagTreeNode>();
		if (c instanceof JTree) {
			tempSelectionStore = new HashSet<Tag>(tagTree.getSelectedTags());
			JTree tree = (JTree) c;
			TreePath path = tree.getSelectionPath();
			TagTreeNode selectedTag = (TagTreeNode) path.getLastPathComponent();
			Tag tag = null;
			if (selectedTag.getTag() instanceof Tag) {
				tag = (Tag) selectedTag.getTag();
				tagsToTransfer.add(tag);
				nodesToTransfer.add(selectedTag);
				return new TagTransferable(new TagTreeNodeSetWrapper(nodesToTransfer),
				        new TagSetWrapper(tagsToTransfer));
			}
			nodesToTransfer.add(selectedTag);
			return new TagTransferable(new TagTreeNodeSetWrapper(nodesToTransfer));
		}
		return null;
	}

	/**
	 * Only move is supported at the moment.
	 * 
	 * @see javax.swing.TransferHandler#getSourceActions(javax.swing.JComponent)
	 */
	@Override
	public int safeGetSourceActions(@SuppressWarnings("unused")
	JComponent c) {
		return MOVE;
	}

	/**
	 * 
	 * the importData method here checks wheter c is a JTree and checks if the
	 * drag & drop would work (i.e. Contains a ImageSetWrapper) by using the can
	 * import method. If yes it tests if the current selection is an TagTreeNode
	 * and if this is also true it adds the images to the album of the
	 * AlbumTreeAlbumNode.
	 * 
	 * @see javax.swing.TransferHandler#importData(javax.swing.JComponent,
	 *      java.awt.datatransfer.Transferable)
	 */
	@Override
	public boolean safeImportData(JComponent c, Transferable t) {
		boolean result = false;
		JTree tree;
		Tag tag;
		try {
			if (canImport(c, t.getTransferDataFlavors())) {
				tree = (JTree) c;
				Point mousePosition = tree.getMousePosition();
				if (mousePosition == null) {
					return false;
				}
				TreePath path = tree.getClosestPathForLocation(mousePosition.x, mousePosition.y);
				// TreePath path = localTree.getSelectionPath();
				TagTreeNode mouseReleasedOver = (TagTreeNode) path.getLastPathComponent();
				tag = null;
				if (mouseReleasedOver.getTag() instanceof Tag) {
					tag = (Tag) mouseReleasedOver.getTag();
					for (int i = 0; i < t.getTransferDataFlavors().length; i++) {
						if (imageFlavor.equals(t.getTransferDataFlavors()[i])) {
							return importImageData(tag, t);
						} else if (tagNodeSetFlavor.equals(t.getTransferDataFlavors()[i])) {
							result = importTagsOnTag(mouseReleasedOver, t);
							tagTree.selectTags(this.tempSelectionStore);
							return result;
						}
					}

				} else {
					// mouse is released over TagGroup - make sure it is
					// expanded
					tree.expandPath(path);
					for (int i = 0; i < t.getTransferDataFlavors().length; i++) {
						if (tagNodeSetFlavor.equals(t.getTransferDataFlavors()[i])) {
							result = importTagsOnTagGroup(mouseReleasedOver, t);
							tagTree.selectTags(this.tempSelectionStore);
							return result;
						}
					}
				}
			}
		} catch (UnsupportedFlavorException ufe) {
			// should not happen
			throw new RuntimeException("Failure when importing data in tag tree.", ufe);
		} catch (IOException ioe) {
			// should not happen
			throw new RuntimeException("Failure when importing data in tag tree.", ioe);
		}
		return false;
	}

	/**
	 * 
	 * Import images on a tag means tagging this images.
	 * 
	 * @param tag
	 * @param t
	 * @return true if successfull
	 * @throws UnsupportedFlavorException
	 * @throws IOException
	 */
	private boolean importImageData(Tag tag, Transferable t) throws UnsupportedFlavorException, IOException {
		ImageSetWrapper wrapper = (ImageSetWrapper) t.getTransferData(imageFlavor);
		SwingClient.getInstance().getImageControl().addTagToImages(wrapper.getImages(), tag);
		return true;
	}

	/**
	 * This method is called when tags are dropped above a tag. The tags brought
	 * by the drag will be added in the tagGroup of the target Tag at the
	 * position where they were dropped and will also be removed from the
	 * position where they have been.
	 * 
	 * @param mouseReleasedOver
	 * @param t
	 * @return true if successfull
	 * @throws UnsupportedFlavorException
	 * @throws IOException
	 */
	private boolean importTagsOnTag(TagTreeNode mouseReleasedOver, Transferable t) throws UnsupportedFlavorException,
	        IOException {
		// tag dropped on a tag
		TagTreeNodeSetWrapper draggedNodes;
		draggedNodes = (TagTreeNodeSetWrapper) t.getTransferData(tagNodeSetFlavor);
		for (TagTreeNode draggedNode : draggedNodes.getTagTreeNodes()) {
			// remove tag where he was and add him in the
			// group
			// where he was dropped
			TagTreeNode oldParent = (TagTreeNode) draggedNode.getParent();
			TagTreeNode newParent = (TagTreeNode) mouseReleasedOver.getParent();

			if (!(draggedNode.getTag() instanceof Tag)) {
				if (isPartOfPath(draggedNode, newParent)) {
					return false;
				}
			}
			int pos = mouseReleasedOver.getParent().getIndex(mouseReleasedOver);
			Set<TagGroup> tags = new HashSet<TagGroup>();
			tags.add(draggedNode.getTag());
			TagGroup.moveTags(oldParent.getTag(), tags, newParent.getTag(), pos);
		}
		return true;
	}

	/**
	 * 
	 * This method is called when tags are dropped on a tagGroup. It will remove
	 * the tags where they have been and add them to the group on which they
	 * were dropped.
	 * 
	 * @param mouseReleasedOver
	 * @param t
	 * @return true if successfull
	 * @throws UnsupportedFlavorException
	 * @throws IOException
	 */
	private boolean importTagsOnTagGroup(TagTreeNode mouseReleasedOver, Transferable t)
	        throws UnsupportedFlavorException, IOException {
		// tags dropped on a tagroup
		TagTreeNodeSetWrapper draggedNodes;
		draggedNodes = (TagTreeNodeSetWrapper) t.getTransferData(tagNodeSetFlavor);
		for (TagTreeNode draggedNode : draggedNodes.getTagTreeNodes()) {
			// remove tag where he was and add him in the
			// group
			// where he was dropped
			TagTreeNode oldParent = (TagTreeNode) draggedNode.getParent();
			TagTreeNode newParent = mouseReleasedOver;
			if (!(draggedNode.getTag() instanceof Tag)) {
				if (isPartOfPath(draggedNode, newParent)) {
					return false;
				}
			}
			int pos = 0;
			if (mouseReleasedOver.getTag() instanceof Tag) {
				pos = mouseReleasedOver.getParent().getIndex(mouseReleasedOver);
			}
			Set<TagGroup> tags = new HashSet<TagGroup>();
			tags.add(draggedNode.getTag());
			TagGroup.moveTags(oldParent.getTag(), tags, newParent.getTag(), pos);
		}
		return true;
	}

	/**
	 * This method checks wheter the oldParent is part of upwards path of the
	 * newParent.
	 * 
	 * @param oldParent
	 * @param newParent
	 * @return true if the given oldParent is ancestor or the new parent
	 */
	private boolean isPartOfPath(TagTreeNode oldParent, TagTreeNode newParent) {
		TagTreeNode recursiveCheck = newParent;
		while (recursiveCheck != null) {
			if (recursiveCheck.equals(oldParent)) {
				return true;
			}
			recursiveCheck = (TagTreeNode) recursiveCheck.getParent();
		}
		return false;
	}

}
