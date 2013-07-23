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

package org.jimcat.services.imageexport;

import java.io.File;

/**
 * Filter the chosen export directory to test if it is valid.
 * 
 * 
 * $Id$
 * 
 * @author Michael
 */
public class ExportDirectoryFilter {
	/**
	 * This method is used to validate the choosen directory
	 * 
	 * @param dir
	 *            the directory in which the chosen directory is
	 * @param name
	 *            the name of the directory
	 * @return if the dirctory is accepted or not
	 */
	public boolean accept(File dir, String name) {
		if (dir == null && name.equals("")) {
			// its a root directory
			return true;
		}

		// directory must exist
		if (dir != null && !dir.exists()) {
			return false;
		}

		// if it is a directory, allow it
		File child = new File(dir, name);
		if (child.isDirectory()) {
			return true;
		}
		return false;
	}

}
