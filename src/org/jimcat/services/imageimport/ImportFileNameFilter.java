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

package org.jimcat.services.imageimport;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashSet;
import java.util.Set;

/**
 * This helps filtering images within the FileTree.
 * 
 * $Id: ImportFileNameFilter.java 329 2007-04-18 13:01:15Z 07g1t1u1 $
 * 
 * @author Herbert
 */
public class ImportFileNameFilter implements FilenameFilter {

	private static Set<String> allowedExtensions = new HashSet<String>();

	static {
		allowedExtensions.add("jpg");
		allowedExtensions.add("jpeg");
		allowedExtensions.add("gif");
		allowedExtensions.add("png");
	}

	/**
	 * Filter all import except directories and image files
	 * 
	 * @param dir
	 *            the directory in which the file was found.
	 * @param name
	 *            the name of the file.
	 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
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

		String imageName = name;
		int dotPosition = imageName.lastIndexOf('.');
		String extension = imageName.substring(dotPosition + 1, imageName.length());
		extension = extension.toLowerCase();

		return allowedExtensions.contains(extension);
	}

}
