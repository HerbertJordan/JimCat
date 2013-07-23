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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreePath;

import org.jimcat.gui.smartlisteditor.editor.AlbumFilterEditor;
import org.jimcat.gui.smartlisteditor.editor.ConstantFilterEditor;
import org.jimcat.gui.smartlisteditor.editor.DuplicateFilterEditor;
import org.jimcat.gui.smartlisteditor.editor.ExifMetadataNodeEditor;
import org.jimcat.gui.smartlisteditor.editor.FileSizeFilterEditor;
import org.jimcat.gui.smartlisteditor.editor.GroupFilterEditor;
import org.jimcat.gui.smartlisteditor.editor.HasTagsFilterEditor;
import org.jimcat.gui.smartlisteditor.editor.ImageSizeFilterEditor;
import org.jimcat.gui.smartlisteditor.editor.ImportFilterEditor;
import org.jimcat.gui.smartlisteditor.editor.IsPartOfAlbumFilterEditor;
import org.jimcat.gui.smartlisteditor.editor.MegaPixelFilterEditor;
import org.jimcat.gui.smartlisteditor.editor.PictureTakenFilterEditor;
import org.jimcat.gui.smartlisteditor.editor.RatingFilterEditor;
import org.jimcat.gui.smartlisteditor.editor.RelativeDateFilterEditor;
import org.jimcat.gui.smartlisteditor.editor.SmartListFilterEditor;
import org.jimcat.gui.smartlisteditor.editor.TagFilterEditor;
import org.jimcat.gui.smartlisteditor.editor.TextFilterEditor;
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

/**
 * This class is a general manager for varous filter editors used within the
 * smartlist editor.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class FilterTreeEditor implements TreeCellEditor, CellEditorListener, TreeSelectionListener {

	/**
	 * a list of registered editors
	 */
	private Map<Class<? extends FilterTreeNode>, TreeCellEditor> editors;

	/**
	 * a list of listeners
	 */
	private List<CellEditorListener> listeners = new CopyOnWriteArrayList<CellEditorListener>();

	/**
	 * the currently used editor
	 */
	private TreeCellEditor current;

	/**
	 * the actual rendering component
	 */
	private JComponent wrapper;

	/**
	 * the field for the icon
	 */
	private JLabel icon;

	/**
	 * the panel containing the editor
	 */
	private JPanel editor;

	/**
	 * the tree this editor is installed on
	 */
	private JTree tree;

	/**
	 * Last path that was selected.
	 */
	private TreePath lastPath;

	/**
	 * default constructor for this editor
	 * @param tree 
	 */
	public FilterTreeEditor(JTree tree) {
		setTree(tree);

		// create map
		editors = new HashMap<Class<? extends FilterTreeNode>, TreeCellEditor>();

		// register editors
		editors.put(GroupFilterTreeNode.class, new GroupFilterEditor());
		editors.put(DuplicateFilterNode.class, new DuplicateFilterEditor());
		editors.put(ImageSizeFilterNode.class, new ImageSizeFilterEditor());
		editors.put(ImportFilterNode.class, new ImportFilterEditor());
		editors.put(TagFilterNode.class, new TagFilterEditor());
		editors.put(FileSizeFilterNode.class, new FileSizeFilterEditor());
		editors.put(PictureTakenFilterNode.class, new PictureTakenFilterEditor());
		editors.put(TextFilterNode.class, new TextFilterEditor());
		editors.put(ConstantFilterNode.class, new ConstantFilterEditor());
		editors.put(SmartListFilterNode.class, new SmartListFilterEditor());
		editors.put(AlbumFilterNode.class, new AlbumFilterEditor());
		editors.put(RatingFilterNode.class, new RatingFilterEditor());
		editors.put(MegaPixelFilterNode.class, new MegaPixelFilterEditor());
		editors.put(HasTagsFilterNode.class, new HasTagsFilterEditor());
		editors.put(IsPartOfAlbumFilterNode.class, new IsPartOfAlbumFilterEditor());
		editors.put(RelativeDateFilterNode.class, new RelativeDateFilterEditor());
		editors.put(ExifMetadataFilterNode.class, new ExifMetadataNodeEditor());

		// build editor wrapper
		initComponents();
	}

	/**
	 * create editor wrapper
	 */
	private void initComponents() {
		wrapper = new JPanel();
		wrapper.setOpaque(false);
		wrapper.setLayout(new BorderLayout());

		icon = new JLabel();
		icon.setOpaque(false);
		icon.setText("");
		wrapper.add(icon, BorderLayout.WEST);

		editor = new JPanel();
		editor.setOpaque(false);
		editor.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		editor.setBorder(new EmptyBorder(0, 5, 0, 0));
		wrapper.add(editor, BorderLayout.CENTER);
	}

	/**
	 * Sets the tree currently editing for. This is needed to add a selection
	 * listener.
	 * 
	 * @param newTree
	 *            the new tree to be edited
	 */
	private void setTree(JTree newTree) {
		if (tree != newTree) {
			if (tree != null)
				tree.removeTreeSelectionListener(this);
			tree = newTree;
			if (tree != null)
				tree.addTreeSelectionListener(this);
		}
	}

	/**
	 * exchange currently used editor
	 * 
	 * @param newCurrent
	 */
	private void setCurrent(TreeCellEditor newCurrent) {
		if (current != null) {
			current.removeCellEditorListener(this);
		}
		current = newCurrent;
		if (current != null) {
			current.addCellEditorListener(this);
		}
	}

	/**
	 * get the treeCellEditor required by the requested node.
	 * 
	 * @see javax.swing.tree.TreeCellEditor#getTreeCellEditorComponent(javax.swing.JTree,
	 *      java.lang.Object, boolean, boolean, boolean, int)
	 */
	@SuppressWarnings("unused")
	public Component getTreeCellEditorComponent(JTree jtree, Object value, boolean isSelected, boolean expanded,
	        boolean leaf, int row) {

		// try to get editor for type
		setCurrent(editors.get(value.getClass()));

		Component editorComponent = null;
		if (current != null) {
			// delegate request
			editorComponent = current.getTreeCellEditorComponent(tree, value, isSelected, expanded, leaf, row);
		} else {
			JLabel label = new JLabel("Unsupported Filter - no editor found");
		}

		// mount editor
		editor.removeAll();
		editor.add(editorComponent);

		// finish wrapper
		FilterTreeNode node = (FilterTreeNode) value;
		icon.setIcon(node.getIcon());
		return wrapper;
	}

	/**
	 * add a new CellEditorListener
	 * 
	 * @see javax.swing.CellEditor#addCellEditorListener(javax.swing.event.CellEditorListener)
	 */
	public void addCellEditorListener(CellEditorListener l) {
		listeners.add(l);
	}

	/**
	 * cancel current edititing
	 * 
	 * @see javax.swing.CellEditor#cancelCellEditing()
	 */
	public void cancelCellEditing() {
		// delegate command
		if (current != null) {
			current.cancelCellEditing();
		}
	}

	/**
	 * get currend editing value
	 * 
	 * @see javax.swing.CellEditor#getCellEditorValue()
	 */
	public Object getCellEditorValue() {
		// delegate if possible
		if (current != null) {
			return current.getCellEditorValue();
		}
		return null;
	}

	/**
	 * check if a given cell is editable
	 * 
	 * @see javax.swing.CellEditor#isCellEditable(java.util.EventObject)
	 */
	public boolean isCellEditable(EventObject event) {
		// cann't be initated by a mouse event
		if (event instanceof MouseEvent) {
			return false;
		}
		// check if lastPath was editiable
		if (lastPath != null) {
			FilterTreeNode node = (FilterTreeNode) lastPath.getLastPathComponent();
			return isCellEditable(node);
		}
		// otherwise, editing couldn't be supported
		return false;
	}

	/**
	 * check if the given node is editable by this editor
	 * 
	 * @param node
	 * @return true if the cell is editable
	 */
	public boolean isCellEditable(FilterTreeNode node) {
		// check if node is editable
		if (!node.isEditable()) {
			return false;
		}
		// check if there is an editor installed
		return editors.get(node.getClass()) != null;
	}

	/**
	 * remove a CellEditorListener
	 * 
	 * @see javax.swing.CellEditor#removeCellEditorListener(javax.swing.event.CellEditorListener)
	 */
	public void removeCellEditorListener(CellEditorListener l) {
		listeners.remove(l);
	}

	/**
	 * A user has to select the cell first
	 * 
	 * @see javax.swing.CellEditor#shouldSelectCell(java.util.EventObject)
	 */
	@SuppressWarnings("unused")
	public boolean shouldSelectCell(EventObject anEvent) {
		return true;
	}

	/**
	 * delegate to current editor
	 * 
	 * @see javax.swing.CellEditor#stopCellEditing()
	 */
	public boolean stopCellEditing() {
		// delegate command
		if (current != null) {
			return current.stopCellEditing();
		}
		// allow stopedit
		return true;
	}

	/**
	 * relay message to listeners
	 * 
	 * @see javax.swing.event.CellEditorListener#editingCanceled(javax.swing.event.ChangeEvent)
	 */
	public void editingCanceled(ChangeEvent e) {
		// just relay message
		for (CellEditorListener listener : listeners) {
			listener.editingCanceled(e);
		}
	}

	/**
	 * realy message to listeners
	 * 
	 * @see javax.swing.event.CellEditorListener#editingStopped(javax.swing.event.ChangeEvent)
	 */
	public void editingStopped(ChangeEvent e) {
		// just inform listeners
		for (CellEditorListener listener : listeners) {
			listener.editingStopped(e);
		}
	}

	/**
	 * updates lastPath
	 * 
	 * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
	 */
	@SuppressWarnings("unused")
	public void valueChanged(TreeSelectionEvent e) {
		if (tree != null) {
			if (tree.getSelectionCount() == 1)
				lastPath = tree.getSelectionPath();
			else
				lastPath = null;
		}
	}

}
