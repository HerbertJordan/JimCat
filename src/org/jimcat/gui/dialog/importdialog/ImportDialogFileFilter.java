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

package org.jimcat.gui.dialog.importdialog;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import org.jimcat.services.imageimport.ImportFileNameFilter;


/**
 * Used to filter files within the file chooser
 * 
 * $Id: ImportDialogFileFilter.java 761 2007-05-22 16:54:35Z 07g1t1u1 $
 * 
 * @author Herbert
 */
public class ImportDialogFileFilter extends FileFilter {

	/**
	 * internally used file filter
	 */
	private ImportFileNameFilter filter = new ImportFileNameFilter();

	/**
	 * test files
	 * 
	 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
	 */
	@Override
	public boolean accept(File f) {
		// check for null
		if (f == null) {
			return false;
		}

		// accept all directories
		if (f.isDirectory()) {
			return true;
		}

		// include linkes
		if (f.getName().toLowerCase().endsWith("lnk")) {
			return true;
		}

		// delegate to internal filter
		return filter.accept(f.getParentFile(), f.getName());
	}

	/**
	 * @see javax.swing.filechooser.FileFilter#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Images";
	}

}
