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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeSelectionModel;

import org.jdesktop.swingx.JXTree;
import org.jimcat.gui.SwingClient;
import org.jimcat.gui.ViewControl;
import org.jimcat.gui.frame.JimCatDialog;
import org.jimcat.gui.frame.JimCatFrame;
import org.jimcat.gui.smartlisteditor.model.FilterTreeNode;
import org.jimcat.model.SmartList;
import org.jimcat.model.filter.Filter;
import org.jimcat.model.filter.FilterCircleException;
import org.jvnet.substance.SubstanceLookAndFeel;

/**
 * The GUI element to edit smartlists.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class SmartListEditor extends JimCatDialog {

	/**
	 * enable / disable live editing
	 */
	private static final boolean LIVE_EDITING_ENABLED = true;

	/**
	 * command for save action
	 */
	private static final String SAVE = "save";

	/**
	 * command for cancel action
	 */
	private static final String CANCEL = "cancel";

	/**
	 * the filter Observer used by this editor
	 */
	private FilterObserver filterObserver;

	/**
	 * the smartList editing
	 */
	private SmartList smartList;

	/**
	 * the filed for editing smartlist name
	 */
	private JTextField name;

	/**
	 * the name of this smartList before editing
	 */
	private String nameBackup;

	/**
	 * the filter of this smartlist before editing
	 */
	private Filter filterBackup;

	/**
	 * the tree used to display filter expressions
	 */
	private JXTree tree;

	/**
	 * default constructor
	 * @param mainframe 
	 */
	public SmartListEditor(JimCatFrame mainframe) {
		super(mainframe, "SmartList Editor", true);

		filterObserver = new FilterObserver();

		// build up content
		initComponents();
	}

	/**
	 * build up swing components
	 */
	private void initComponents() {
		// setup layout
		setLayout(new BorderLayout());

		// header
		JPanel header = new JPanel();
		header.setOpaque(false);
		header.setLayout(new GridLayout(1, 2));
		header.setBorder(new EmptyBorder(5, 20, 5, 20));

		JLabel titel = new JLabel("SmartList: ");
		titel.setOpaque(false);
		titel.setFont(titel.getFont().deriveFont(Font.BOLD));
		header.add(titel);

		name = new JTextField();
		header.add(name);

		add(header, BorderLayout.NORTH);

		// build up tree
		tree = new JXTree();

		// install tree renderer
		tree.setCellRenderer(new FilterTreeRenderer());
		tree.setCellEditor(new FilterTreeEditor(tree));

		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setInvokesStopCellEditing(true);

		// Add Contextmenu handler
		FilterTreePopupHandler handler = new FilterTreePopupHandler(this, tree);
		tree.addMouseListener(handler);
		tree.addKeyListener(handler);

		// enable double click editing
		tree.addMouseListener(new MouseHandler());

		tree.setOpaque(false);
		tree.setEditable(true);
		tree.setRootVisible(true);
		tree.setShowsRootHandles(true);
		tree.expandAll();
		tree.putClientProperty(SubstanceLookAndFeel.WATERMARK_TO_BLEED, Boolean.TRUE);

		// enable drag&drop and install TransferHandler
		tree.setDragEnabled(true);
		tree.setTransferHandler(new FilterTreeTransferHandler());

		// put table into a scrolling pane
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.putClientProperty(SubstanceLookAndFeel.WATERMARK_TO_BLEED, Boolean.TRUE);
		scrollPane.setViewportView(tree);

		// white background
		JPanel scrollHolder = new JPanel();
		scrollHolder.setBackground(Color.WHITE);
		scrollHolder.setLayout(new BorderLayout());
		scrollHolder.add(scrollPane, BorderLayout.CENTER);
		scrollHolder.setBorder(new EmptyBorder(0, 5, 0, 5));

		// add table to view
		add(scrollHolder, BorderLayout.CENTER);

		// botton
		JPanel bottom = new JPanel();
		bottom.setOpaque(false);
		bottom.setLayout(new BorderLayout());

		// button panel
		JPanel buttons = new JPanel();
		buttons.setBorder(new EmptyBorder(5, 10, 10, 10));
		buttons.setLayout(new GridLayout(1, 2, 5, 5));

		ButtonListener listener = new ButtonListener();
		// save button
		JButton ok = new JButton("OK");
		ok.setActionCommand(SAVE);
		ok.addActionListener(listener);
		ok.setFocusable(false);
		ok.setMnemonic('o');
		buttons.add(ok);

		// cancel button
		JButton cancel = new JButton("Cancel");
		cancel.setActionCommand(CANCEL);
		cancel.addActionListener(listener);
		cancel.setFocusable(false);
		cancel.setMnemonic('c');
		buttons.add(cancel);

		// mount bottom panel
		bottom.add(buttons, BorderLayout.EAST);
		add(bottom, BorderLayout.SOUTH);

		// resize and place ...
		Dimension size = new Dimension(400, 450);
		Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(size);
		setLocation((screensize.width - size.width) / 2, (screensize.height - size.height) / 2);

	}

	/**
	 * checks if a smartList is set before making dialog visible => throws
	 * IllegalStateException if smartList is null
	 * 
	 * @see java.awt.Component#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean b) {
		// check if there is a smartlist installed
		if (b && smartList == null) {
			throw new IllegalStateException("Smartlist must be set");
		}
		super.setVisible(b);
	}

	/**
	 * @return the smartList
	 */
	public SmartList getSmartList() {
		return smartList;
	}

	/**
	 * @param smartList
	 *            the smartList to set
	 */
	public void setSmartList(SmartList smartList) {
		this.smartList = smartList;

		// Backup
		nameBackup = smartList.getName();
		filterBackup = smartList.getFilter();

		// setup fields
		name.setText(smartList.getName());

		// exchange filter observer
		tree.getModel().removeTreeModelListener(filterObserver);
		tree.setModel(FilterTreeUtil.generateModel(smartList.getFilter()));
		tree.getModel().addTreeModelListener(filterObserver);

		tree.expandAll();
	}

	/**
	 * exchange current visible filter root
	 * 
	 * Filter tree models have to be exchange through this methode.
	 * 
	 * @param newRoot
	 */
	protected void setNewFilterRoot(FilterTreeNode newRoot) {
		// remove listener from old model
		tree.getModel().removeTreeModelListener(filterObserver);
		// exchange model
		tree.setModel(newRoot);
		// add listener to new model
		tree.getModel().addTreeModelListener(filterObserver);

		// react on changed filter
		filterChanged();
	}

	/**
	 * react on button save click
	 */
	private void performSave() {
		// save changes
		SmartList list = getSmartList();

		// save name
		list.setName(name.getText());

		// fitler ist updated on the fly
		if (!LIVE_EDITING_ENABLED) {
			updateFilter();
		}

		// hide editor
		closeEditor();
	}

	/**
	 * perform cancel
	 */
	private void performCancel() {

		// revert changes
		SmartList list = getSmartList();

		// load backup values
		if (LIVE_EDITING_ENABLED) {
			list.setName(nameBackup);
			if (filterBackup!=null) {
				filterBackup = filterBackup.getCleanVersion();
			}
			list.setFilter(filterBackup);
			ViewControl control = SwingClient.getInstance().getViewControl();
			control.setSmartList(list);
		}

		// hide editor
		closeEditor();
	}

	/**
	 * close editor proper
	 */
	private void closeEditor() {
		// close window
		setVisible(false);
	}

	/**
	 * react on a filter change event => change filter in smartList
	 */
	private void filterChanged() {
		if (LIVE_EDITING_ENABLED) {
			// generate filter and set it to the smartlist
			updateFilter();
		}
	}

	/**
	 * update edited filter by building new one
	 */
	private void updateFilter() {
		// get list
		SmartList list = getSmartList();
		// save old filter
		Filter oldFilter = list.getFilter();

		try {
			// update filter
			list.setFilter(FilterTreeUtil.generateFilter(tree.getModel()));
		} catch (FilterCircleException fce) {
			// undo last change
			JOptionPane.showMessageDialog(this,
			        "You have just created a filter cycle. Therefore your last step was reverted.",
			        "Error building Filter", JOptionPane.ERROR_MESSAGE);
			list.setFilter(oldFilter);
			setSmartList(list);
		}
	}

	/**
	 * private button listener to react on button clicks
	 */
	private class ButtonListener implements ActionListener {
		/**
		 * dispatche functions
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			if (SAVE.equals(command)) {
				performSave();
			} else {
				performCancel();
			}
		}
	}

	/**
	 * private mouse handler to starte editing by double click.
	 */
	private class MouseHandler extends MouseAdapter {
		/**
		 * react on double click
		 * 
		 * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseClicked(MouseEvent e) {
			// if its a double click event => start editing
			if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
				tree.startEditingAtPath(tree.getSelectionPath());
			}
		}
	}

	/**
	 * private listener reporting model changes
	 */
	private class FilterObserver implements TreeModelListener {

		/**
		 * react on event
		 * 
		 * @see javax.swing.event.TreeModelListener#treeNodesChanged(javax.swing.event.TreeModelEvent)
		 */
		@SuppressWarnings("unused")
		public void treeNodesChanged(TreeModelEvent e) {
			// report change
			filterChanged();
		}

		/**
		 * This method is called when a new tree node is inserted. it reports
		 * the change of the current filter.
		 * 
		 * @see javax.swing.event.TreeModelListener#treeNodesInserted(javax.swing.event.TreeModelEvent)
		 */
		@SuppressWarnings("unused")
		public void treeNodesInserted(TreeModelEvent e) {
			// report change
			filterChanged();
		}

		/**
		 * This method is called when a tree node is removed. it reports the
		 * change of the current filter.
		 * 
		 * @see javax.swing.event.TreeModelListener#treeNodesRemoved(javax.swing.event.TreeModelEvent)
		 */
		@SuppressWarnings("unused")
		public void treeNodesRemoved(TreeModelEvent e) {
			// report change
			filterChanged();
		}

		/**
		 * This method is called when the treeStructure has changed. It reports
		 * the change of the current filter.
		 * 
		 * @see javax.swing.event.TreeModelListener#treeStructureChanged(javax.swing.event.TreeModelEvent)
		 */
		@SuppressWarnings("unused")
		public void treeStructureChanged(TreeModelEvent e) {
			// report change
			filterChanged();
		}

	}

}
