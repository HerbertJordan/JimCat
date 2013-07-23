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

package org.jimcat.gui.dialog.exportdialog;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import org.jimcat.gui.dialog.RenamePanel;

/**
 * The export rename panel is a rename panel with somehow different behaviour,
 * because it has to deal with filenames
 * 
 * $Id$
 * 
 * @author Michael
 */
public class ExportRenamePanel extends RenamePanel {

	private String[] unwantedStrings = { "/", "\\", ":", "*", "?", "\"", "<", ">", "|", File.pathSeparator,
	        File.separator };

	/**
	 * 
	 * default constructor setting the table model to export table model
	 */
	public ExportRenamePanel() {
		super();
		setTableModel(new ExportTableModel());
	}

	/**
	 * 
	 * update the table
	 */
	@Override
	public void updateTable() {
		super.updateTable();
		super.setNewNames(getClearFileNames(super.getNewNames()));
	}

	/**
	 * remove chars that must not be in a file name
	 * 
	 * @param newNames2
	 * @return the cleared file names (all characters not allowed in file names
	 *         removed)
	 */
	private List<String> getClearFileNames(List<String> newNames2) {
		boolean foundUnwantedChar = false;
		List<String> updatedStrings = new LinkedList<String>();
		for (String x : newNames2) {
			for (String s : unwantedStrings) {
				if (x.contains(s)) {
					x = x.replace(s, "");
					foundUnwantedChar = true;
				}
			}
			updatedStrings.add(x);
		}
		if (foundUnwantedChar) {
			// forbidden characters string
			StringBuffer buf = new StringBuffer();
			for (String s : unwantedStrings) {
				if (buf.indexOf(s) == -1) {
					buf.append(s + " ");
				}
			}
			String printableVersion = buf.substring(0, buf.length() - 1);

			// show error message
			JOptionPane.showMessageDialog(this, "Forbidden characters for filename!\nDo not use: " + printableVersion,
			        "Error", JOptionPane.ERROR_MESSAGE);
			// remove char if it is part of config string
			for (String s : unwantedStrings) {
				if (getConfigStringText().contains(s)) {
					setConfigStringText(getConfigStringText().replace(s, ""));
				}
			}
		}

		return updatedStrings;
	}

	private class ExportTableModel extends DefaultTableModel {

		/**
		 * 
		 * the default constructor for the table model for the renamer in the
		 * export dialog.
		 */
		public ExportTableModel() {
			setColumnIdentifiers(new String[] { "Title", "Old File Name", "New File Name" });
		}

		@Override
		@SuppressWarnings("unused")
		public Class<?> getColumnClass(int columnIndex) {
			return String.class;
		}

		@Override
		public int getRowCount() {
			return getSelectedImages().size();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (columnIndex == 0) {
				return getSelectedImages().get(rowIndex).getTitle();
			} else if (columnIndex == 1) {
				if (getSelectedImages().get(rowIndex).getMetadata() != null
				        && getSelectedImages().get(rowIndex).getMetadata().getPath() != null) {
					return getRenamer().removeFileType(getSelectedImages().get(rowIndex).getMetadata().getPath());
				}
				return "unknown";
			}
			return getNewNames().get(rowIndex);
		}

		@Override
		@SuppressWarnings("unused")
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}
	}

}
