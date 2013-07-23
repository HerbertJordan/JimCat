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

package org.jimcat.gui.albumlist;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.jdesktop.swingx.JXTree;
import org.jimcat.gui.SwingClient;
import org.jimcat.gui.ViewControl;
import org.jimcat.gui.ViewFilterListener;
import org.jimcat.gui.dndutil.TreeDropTargetListener;
import org.jimcat.model.filter.AlbumFilter;
import org.jvnet.substance.SubstanceLookAndFeel;

/**
 * This class is forming the albumlist within the sidebar.
 * 
 * it containes an entry for "all iamges", a list of Albums and a list of
 * Smartlists;
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class AlbumList extends JPanel implements TreeSelectionListener, ViewFilterListener {

	/**
	 * a reference to the view control
	 */
	private ViewControl control;

	/**
	 * the tree displayed
	 */
	private JXTree tree;

	/**
	 * used to determine it this component is source of a filter change
	 */
	private boolean changing = false;

	/**
	 * small constructor of this component
	 */
	public AlbumList() {
		// init members;
		control = SwingClient.getInstance().getViewControl();

		// register as listener
		control.addViewFilterListener(this);

		// build up content
		initComponents();
		
		// update selection
		filterChanges(control);
	}

	/**
	 * build up component hierarchie
	 */
	private void initComponents() {
		// basic settings
		setLayout(new BorderLayout());
		setOpaque(false);

		// build up tree
		tree = new JXTree();

		// install tree renderer
		tree.setCellRenderer(new AlbumTreeRenderer());
		tree.setCellEditor(new AlbumTreeEditor(tree));

		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setInvokesStopCellEditing(true);

		// register selection listener to react on events
		tree.getSelectionModel().addTreeSelectionListener(this);

		// Add Contextmenu handler
		AlbumTreePopupHandler handler = new AlbumTreePopupHandler(tree);
		tree.setTransferHandler(new AlbumTreeTransferHandler());
		tree.addMouseListener(handler);
		tree.addKeyListener(handler);

		tree.setDragEnabled(true);
		tree.setOpaque(false);
		tree.setEditable(true);
		tree.setModel(new AlbumTreeModel(tree));
		tree.setRootVisible(false);
		tree.setShowsRootHandles(false);
		tree.expandAll();
		tree.putClientProperty(SubstanceLookAndFeel.WATERMARK_TO_BLEED, Boolean.TRUE);

		TreeDropTargetListener.addNewTreeDropTargetListener(tree);

		add(tree, BorderLayout.CENTER);

	}

	/**
	 * react on selection
	 * 
	 * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
	 */
	public void valueChanged(TreeSelectionEvent e) {
		if (e.getPath() != null && e.isAddedPath()) {
			// exectute selection procedure for selected node
			AlbumTreeNode node = (AlbumTreeNode) e.getPath().getLastPathComponent();
			changing = true;
			node.performSelection(control);
			changing = false;
		}
	}

	/**
	 * to react on filter changes
	 * 
	 * @see org.jimcat.gui.ViewFilterListener#filterChanges(org.jimcat.gui.ViewControl)
	 */
	@SuppressWarnings("unused")
	public void filterChanges(ViewControl view) {
		// if this is the source of the change, do nothing
		if (changing) {
			return;
		}
		// get model
		AlbumTreeModel model = (AlbumTreeModel) tree.getModel();
		if (view.getAlbumFilter() != null) {
			AlbumFilter filter = view.getAlbumFilter();
			TreePath path = model.getPathForAlbum(filter.getAlbum());
			if (path != null) {
				tree.getSelectionModel().setSelectionPath(path);
			} else {
				tree.clearSelection();
			}
		} else if (view.getSmartList() != null) {
			TreePath path = model.getPathForSmartList(view.getSmartList());
			if (path != null) {
				tree.getSelectionModel().setSelectionPath(path);
			} else {
				tree.clearSelection();
			}
		} else if (view.allVisible()) {
			// select all images entry
			TreePath path = model.getPathForAllImages();
			// to prevent double execution
			changing = true;
			tree.getSelectionModel().setSelectionPath(path);
			changing = false;
		} else {
			tree.clearSelection();
		}

	}

}
