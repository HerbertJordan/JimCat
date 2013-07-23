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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.jdesktop.swingx.JXTable;
import org.jimcat.gui.ViewControl;
import org.jimcat.gui.ViewSortingListener;
import org.jimcat.model.Image;
import org.jimcat.model.comparator.ComparatorChainProxy;
import org.jimcat.model.comparator.NullComparator;

/**
 * This class is responsible for sorting table rows.
 * 
 * It isn't a usable class, its just a static methode which will install sorting
 * capabilities to a DetailTable.
 * 
 * Thanks to TableSorter provieded by
 * 
 * http://java.sun.com/docs/books/tutorial/uiswing/components/examples/TableSorter.java
 * 
 * In Java 1.6 this wouldn't be needed.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public final class TableSortingHandler implements ViewSortingListener {

	/**
	 * constants for sorting direction
	 */
	private static final int DESCENDING = -1;

	private static final int NOT_SORTED = 0;

	private static final int ASCENDING = 1;

	/**
	 * a constant directive
	 */
	private static Directive EMPTY_DIRECTIVE = new Directive(-1, NOT_SORTED);

	/**
	 * the header supported by this sort mechanism
	 */
	private JTableHeader tableHeader;

	/**
	 * a list describing current sorting state
	 */
	private List<Directive> sortingColumns = new ArrayList<Directive>();

	/**
	 * the view control used to setup changed sorting orders
	 */
	private ViewControl control;

	/**
	 * to determine if a sorting change is caused by this
	 */
	private boolean causingChange = false;

	/**
	 * no one should be able to build objects of this class
	 * @param table 
	 * @param control 
	 */
	private TableSortingHandler(JXTable table, ViewControl control) {
		this.control = control;

		// register to control
		control.addViewSortingListener(this);

		// setup table
		// disable default sorting
		table.setSortable(false);

		// install listener and renderer to table header
		tableHeader = table.getTableHeader();
		tableHeader.addMouseListener(new MouseHandler());
		tableHeader.setDefaultRenderer(new SortableHeaderRenderer(tableHeader.getDefaultRenderer()));
	}

	/**
	 * use this methode to intall sorting capabilities to the given detail
	 * Table. Control must be the used ViewControl
	 * 
	 * @param detailTable
	 * @param control
	 */
	public static void installSorter(DetailTable detailTable, ViewControl control) {
		JXTable table = detailTable.getTable();

		// the rest will be done by the constructor
		new TableSortingHandler(table, control);
	}

	/**
	 * react on a changed sorting by flushing sorting list
	 * 
	 * @see org.jimcat.gui.ViewSortingListener#sortingChanged(org.jimcat.gui.ViewControl)
	 */
	@SuppressWarnings("unused")
	public void sortingChanged(ViewControl viewControl) {
		if (!causingChange) {
			// clear list
			sortingColumns.clear();
			// repaint header
			tableHeader.repaint();
		}
	}

	/**
	 * get the current sorting state of given column
	 * 
	 * @param column -
	 *            index of requested column
	 * @return - one of DESCENDING, ASCENDING or NOT_SORTED
	 */
	private int getSortingStatus(int column) {
		return getDirective(column).direction;
	}

	/**
	 * get the current directive concerning a certain column
	 * 
	 * @param column -
	 *            index of requested column
	 * @return - its directive or the EMPTY_DIRCECTIVE
	 */
	private Directive getDirective(int column) {
		// walk through directives and see if there is one
		for (Directive directive : sortingColumns) {
			if (directive.column == column) {
				return directive;
			}
		}
		// default =>
		return EMPTY_DIRECTIVE;
	}

	/**
	 * if sorting status is changing, the
	 */
	private void sortingStatusChanged() {
		// Build new comperator
		ComparatorChainProxy<Image> comparator = new ComparatorChainProxy<Image>();

		// build up chain from sorting columns
		for (Directive directive : sortingColumns) {
			if (directive != EMPTY_DIRECTIVE) {
				DetailTableColumn column = DetailTableColumn.values()[directive.column];
				comparator.addComparator(column.getSorter(), !directive.isAscending());
			} else {
				comparator.addComparator(new NullComparator());
			}
		}

		// if there is no special sorting
		if (comparator.size() == 0) {
			comparator.addComparator(new NullComparator());
		}

		// set chain as new sorting order
		causingChange = true;
		control.setSorting(comparator);
		causingChange = false;

		// repaint header
		tableHeader.repaint();
	}

	/**
	 * change sorting state for a column
	 * 
	 * @param column -
	 *            column index
	 * @param status -
	 *            state, one of DESCENDING, ASCENDING or NOT_SORTED
	 */
	private void setSortingStatus(int column, int status) {
		// get current directive
		Directive directive = getDirective(column);
		// if directive isn't default one => remove old
		if (directive != EMPTY_DIRECTIVE) {
			sortingColumns.remove(directive);
		}
		// if status isn't NOT_SORTED => add new one
		if (status != NOT_SORTED) {
			sortingColumns.add(new Directive(column, status));
		}
		// sorting has changed, react
		sortingStatusChanged();
	}

	/**
	 * get an icon used to indicate not_sorted, acending or decending order
	 * 
	 * @param column -
	 *            the colum name
	 * @param size -
	 *            size of icon
	 * @return the Icon for sorted, ascending or descending
	 */
	private Icon getHeaderRendererIcon(int column, int size) {
		Directive directive = getDirective(column);
		if (directive == EMPTY_DIRECTIVE) {
			return null;
		}
		return new Arrow(directive.direction == DESCENDING, size, sortingColumns.indexOf(directive));
	}

	/**
	 * This class is used as MouseListener registered to tables header.
	 * 
	 * $Id$
	 * 
	 * @author Herbert
	 */
	private class MouseHandler extends MouseAdapter {
		/**
		 * capture a click and react to it
		 * 
		 * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseClicked(MouseEvent e) {
			// get and parse source
			JTableHeader h = (JTableHeader) e.getSource();

			// get column index
			TableColumnModel columnModel = h.getColumnModel();
			int viewColumn = columnModel.getColumnIndexAtX(e.getX());
			int column = columnModel.getColumn(viewColumn).getModelIndex();

			// don not support ordinal sorting
			if (DetailTableColumn.COUNT.ordinal() == column) {
				return;
			}

			// add sorting
			if (column != -1) {

				// get current state
				int status = getSortingStatus(column);

				// chaining only by ctrl pressed
				if (!e.isControlDown()) {
					// reset state
					sortingColumns.clear();
				}

				// Cycle the sorting states through {NOT_SORTED, ASCENDING,
				// DESCENDING} or
				// {NOT_SORTED, DESCENDING, ASCENDING} depending on whether
				// shift is pressed.
				status = status + (e.isShiftDown() ? -1 : 1);
				// signed mod, returning {-1, 0, 1}
				status = (status + 4) % 3 - 1;

				// update sorting
				setSortingStatus(column, status);
			}
		}
	}

	/**
	 * A class describing an sorting directive
	 * 
	 * $Id$
	 * 
	 * @author Herbert
	 */
	private static class Directive {
		/**
		 * the column
		 */
		private int column;

		/**
		 * the dircection
		 */
		private int direction;

		/**
		 * constructor
		 * @param column 
		 * @param direction 
		 */
		public Directive(int column, int direction) {
			this.column = column;
			this.direction = direction;
		}

		/**
		 * is the directory ascending
		 * 
		 * @return true if sorting is ascending
		 */
		public boolean isAscending() {
			return direction == ASCENDING;
		}
	}

	/**
	 * used to render table header fields (add arrow)
	 * 
	 * $Id$
	 * 
	 * @author Herbert
	 */
	private class SortableHeaderRenderer implements TableCellRenderer {
		/**
		 * the default renderer
		 */
		private TableCellRenderer tableCellRenderer;

		/**
		 * constructor requesting a default table cell renderer for this header.
		 * 
		 * @param tableCellRenderer
		 */
		public SortableHeaderRenderer(TableCellRenderer tableCellRenderer) {
			this.tableCellRenderer = tableCellRenderer;
		}

		/**
		 * the rendere mode
		 * 
		 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable,
		 *      java.lang.Object, boolean, boolean, int, int)
		 */
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
		        boolean hasFocus, int row, int column) {

			// first delgate
			Component c = tableCellRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
			        column);

			// alter result - add icon on the right side of the text
			if (c instanceof JLabel) {
				JLabel l = (JLabel) c;
				l.setHorizontalTextPosition(SwingConstants.LEFT);
				int modelColumn = table.convertColumnIndexToModel(column);
				l.setIcon(getHeaderRendererIcon(modelColumn, l.getFont().getSize()));
			}
			return c;
		}
	}

	/**
	 * the icon indicating sorting direction
	 * 
	 * $Id$
	 * 
	 * @author Herbert
	 */
	private static class Arrow implements Icon {

		/**
		 * is it decending?
		 */
		private boolean descending;

		/**
		 * size of icon
		 */
		private int size;

		/**
		 * the priority of this column
		 */
		private int priority;

		/**
		 * direct constructor
		 * 
		 * @param descending
		 * @param size
		 * @param priority
		 */
		public Arrow(boolean descending, int size, int priority) {
			this.descending = descending;
			this.size = size;
			this.priority = priority;
		}

		/**
		 * implement the icon paint methode
		 * 
		 * @see javax.swing.Icon#paintIcon(java.awt.Component,
		 *      java.awt.Graphics, int, int)
		 */
		public void paintIcon(Component c, Graphics g, int posX, int posY) {
			// take input
			int x = posX;
			int y = posY;

			Color color = c == null ? Color.GRAY : c.getBackground();
			// In a compound sort, make each succesive triangle 20%
			// smaller than the previous one.
			int dx = (int) (size / 2 * Math.pow(0.8, priority));
			int dy = descending ? dx : -dx;
			// Align icon (roughly) with font baseline.
			y = y + 5 * size / 6 + (descending ? -dy : 0);
			int shift = descending ? 1 : -1;
			g.translate(x, y);

			// Right diagonal.
			g.setColor(color.darker());
			g.drawLine(dx / 2, dy, 0, 0);
			g.drawLine(dx / 2, dy + shift, 0, shift);

			// Left diagonal.
			g.setColor(color.brighter());
			g.drawLine(dx / 2, dy, dx, 0);
			g.drawLine(dx / 2, dy + shift, dx, shift);

			// Horizontal line.
			if (descending) {
				g.setColor(color.darker().darker());
			} else {
				g.setColor(color.brighter().brighter());
			}
			g.drawLine(dx, 0, 0, 0);

			g.setColor(color);
			g.translate(-x, -y);
		}

		/**
		 * @see javax.swing.Icon#getIconWidth()
		 */
		public int getIconWidth() {
			return size;
		}

		/**
		 * @see javax.swing.Icon#getIconHeight()
		 */
		public int getIconHeight() {
			return size;
		}
	}

}
