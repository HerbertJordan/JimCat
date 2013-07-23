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

package org.jimcat.gui.perspective.detail;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.AlternateRowHighlighter;
import org.jdesktop.swingx.decorator.HighlighterPipeline;
import org.jimcat.gui.SwingClient;
import org.jimcat.gui.ViewControl;
import org.jimcat.gui.imagepopup.ImagePopupMenu;
import org.jimcat.model.ImageRating;
import org.joda.time.DateTime;

/**
 * The detail table within the detail perspective
 * 
 * $Id: DetailTable.java 935 2007-06-15 09:21:09Z 07g1t1u2 $
 * 
 * @author Herbert
 */
public class DetailTable extends JPanel implements ListSelectionListener {

	/**
	 * a reference to the library view
	 */
	private ViewControl control;

	/**
	 * a reference to the shown table
	 */
	private JXTable table;

	/**
	 * used by list selection listener part to avoid duplicated calls
	 */
	private int index = -1;

	/**
	 * a simple constructor
	 * @param control 
	 */
	public DetailTable(ViewControl control) {
		this.control = control;
		initComponents();
	}

	/**
	 * building up swing components
	 */
	private void initComponents() {
		// setup layout
		setLayout(new BorderLayout());

		// create table
		table = new JXTable();
		// table.setAutoCreateRowSorter(true);

		// assign selection model
		table.setSelectionModel(control.getSelectionModel());

		// create model
		table.setModel(new DetailTableModel(control.getLibraryView()));

		// general settings
		table.setAutoscrolls(false);
		table.setColumnControlVisible(true);
		table.setHorizontalScrollEnabled(true);
		table.setDragEnabled(true);
		table.setTransferHandler(new TableTransferHandler(this));

		HighlighterPipeline rowHighlighters = new HighlighterPipeline();
		AlternateRowHighlighter highlighter = new AlternateRowHighlighter();
		highlighter.setEvenRowBackground(new Color(245, 245, 245));
		rowHighlighters.addHighlighter(highlighter);
		table.setHighlighters(rowHighlighters);
		table.setFillsViewportHeight(true);
		table.setShowHorizontalLines(false);
		table.setShowVerticalLines(true);

		// hide description and path - do not exchange order
		table.getColumnExt(DetailTableColumn.EXIF_MODEL.ordinal()).setVisible(false);
		table.getColumnExt(DetailTableColumn.EXIF_MANUFACTURER.ordinal()).setVisible(false);
		table.getColumnExt(DetailTableColumn.EXIF_ISO.ordinal()).setVisible(false);
		table.getColumnExt(DetailTableColumn.EXIF_FOCAL.ordinal()).setVisible(false);
		table.getColumnExt(DetailTableColumn.EXIF_FLASH.ordinal()).setVisible(false);
		table.getColumnExt(DetailTableColumn.EXIF_EXPOSURE.ordinal()).setVisible(false);
		table.getColumnExt(DetailTableColumn.EXIF_DATE_TAKEN.ordinal()).setVisible(true);
		table.getColumnExt(DetailTableColumn.EXIF_APERTURE.ordinal()).setVisible(false);
		table.getColumnExt(DetailTableColumn.IMAGE_WIDTH.ordinal()).setVisible(false);
		table.getColumnExt(DetailTableColumn.IMAGE_HEIGHT.ordinal()).setVisible(false);
		table.getColumnExt(DetailTableColumn.FILE_SIZE.ordinal()).setVisible(false);
		table.getColumnExt(DetailTableColumn.IMPORT_ID.ordinal()).setVisible(false);
		table.getColumnExt(DetailTableColumn.LAST_EXPORT_PATH.ordinal()).setVisible(false);
		table.getColumnExt(DetailTableColumn.PATH.ordinal()).setVisible(false);
		table.getColumnExt(DetailTableColumn.DESCRIPTION.ordinal()).setVisible(false);

		// add spezial renderer and editor
		table.setDefaultRenderer(ImageRating.class, new RatingCellRenderer());
		table.setDefaultRenderer(DateTime.class, new DateCellRenderer());
		table.setDefaultEditor(ImageRating.class, new RatingCellEditor());
		table.setDefaultEditor(String.class, new TitelEditor(table));
		table.addKeyListener(new TableKeyAdapter());

		table.getSelectionModel().addListSelectionListener(this);

		// resize table columnen
		TableColumn num = table.getColumnModel().getColumn(0);
		num.setMaxWidth(40);
		num.setMinWidth(40);
		num.setResizable(false);

		// reformat tables
		TableColumn name = table.getColumnModel().getColumn(1);
		name.setWidth(400);

		// put table into a scrolling pane
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(table);

		// add table to view
		add(scrollPane, BorderLayout.CENTER);

		// add mouse listener
		// add fullscreen trigger + popup menu handler
		table.addMouseListener(new TableMouseAdapter());

		// sorting
		TableSortingHandler.installSorter(this, control);
	}

	/**
	 * get the contained table
	 * @return the table
	 */
	public JXTable getTable() {
		return table;
	}

	/**
	 * move Scrollpane to right position
	 * 
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting()) {
			if (index != e.getLastIndex()) {
				index = e.getLastIndex();
				// cosmetic correction on upscrolling
				Rectangle rect = table.getCellRect(index, 1, true);
				rect.add(table.getCellRect(index - 1, 1, true));

				// make cell visible
				table.scrollRectToVisible(rect);
			}
		}
	}

	/**
	 * To react on Enter events in a similar way as the other perpectives do, a
	 * key listener has to be installed who listens for enter events and opens
	 * fullscreen.
	 * 
	 * 
	 * $Id: DetailTable.java 935 2007-06-15 09:21:09Z 07g1t1u2 $
	 * 
	 * @author Michael
	 */
	private final class TableKeyAdapter extends KeyAdapter {
		/**
		 * Open fullscreen on enter event and consume the event to stop the
		 * table from reacting in a default manner on enter events (Setting
		 * selection to the next line).
		 * 
		 * @see java.awt.event.KeyAdapter#keyPressed(java.awt.event.KeyEvent)
		 */
		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				SwingClient.getInstance().showFullScreen();
				e.consume();
			}
		}
	}

	/**
	 * An adapter for Table mouse events
	 * 
	 * $Id: DetailTable.java 935 2007-06-15 09:21:09Z 07g1t1u2 $
	 * 
	 * @author Herbert
	 */
	private class TableMouseAdapter extends MouseAdapter {

		/**
		 * used to display fullscreen mode on double click
		 * 
		 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseClicked(MouseEvent e) {
			// check source
			if (e.getSource() != table) {
				return;
			}
			// if it was a left double click
			if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
				SwingClient.getInstance().showFullScreen();
			}
		}

		/**
		 * open popup menu
		 * 
		 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
		 */
		@Override
		public void mousePressed(MouseEvent e) {
			checkForPopup(e);
		}

		/**
		 * may open popup menu
		 * 
		 * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseReleased(MouseEvent e) {
			checkForPopup(e);
		}

		/**
		 * internal methode to open popup
		 * 
		 * @param e
		 */
		private void checkForPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				// open popup
				ImagePopupMenu menu = ImagePopupMenu.getInstance();
				int row = table.rowAtPoint(e.getPoint());
				menu.show(table, row, e.getPoint());
			}
		}

	}

	/**
	 * @return the control
	 */
	public ViewControl getControl() {
		return control;
	}

	/**
	 * update active state
	 * 
	 * @param active
	 *            new state
	 */
	public void setActive(boolean active) {
		DetailTableModel model = (DetailTableModel) table.getModel();
		model.setActive(active);
	}
}
