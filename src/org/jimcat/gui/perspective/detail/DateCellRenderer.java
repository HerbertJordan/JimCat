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
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

import org.joda.time.DateTime;

/**
 * Helps displaying a DateTime object.
 * 
 * $Id: DateCellRenderer.java 329 2007-04-18 13:01:15Z 07g1t1u1 $
 * 
 * @author Herbert
 */
public class DateCellRenderer implements TableCellRenderer {

	/**
	 * the formatter used to display date
	 */
	// private static String format = "dd.MM.yyyy HH:mm:ss";
	private static String format = "yyyy/MM/dd";

	/**
	 * actual renderer
	 */
	private JPanel renderer;

	/**
	 * the display-label
	 */
	private JLabel date;

	/**
	 * creates a new one, no parameter
	 */
	public DateCellRenderer() {
		renderer = new JPanel();
		renderer.setLayout(new BorderLayout());

		date = new JLabel();
		date.setHorizontalAlignment(SwingConstants.CENTER);
		renderer.add(date, BorderLayout.CENTER);
	}

	/**
	 * prepair renderer for display
	 * 
	 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable,
	 *      java.lang.Object, boolean, boolean, int, int)
	 */
	@SuppressWarnings("unused")
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
	        int row, int column) {
		String str = "";

		if (value instanceof DateTime) {
			DateTime time = (DateTime) value;
			str = time.toString(format);
		}

		date.setText(str);
		return renderer;
	}

}
