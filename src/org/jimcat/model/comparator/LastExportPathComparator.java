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

package org.jimcat.model.comparator;

import java.util.Comparator;

import org.jimcat.model.Image;

/**
 * The LastExportPathComparator compares two images according to their last
 * export path.
 * 
 * Two images are compared by comparing the path stored when last exported.
 * 
 * 
 * $Id$
 * 
 * @author Michael
 */
public class LastExportPathComparator implements Comparator<Image> {

	/**
	 * The compare method of the LastExportPathComparator class is used to
	 * compare two images according to their last export path.
	 * @param o1 
	 * @param o2 
	 * @return the result of the compare mehtod as specified in java.util.Comparator
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Image o1, Image o2) {
		// get the data
		String o1ExportPath = o1.getLastExportPath();
		String o2ExportPath = o2.getLastExportPath();
		// test if first is null
		if (o1ExportPath == null) {
			// if yes test if second is null too
			if (o2ExportPath == null)
				return 0;
			// only first is null
			return -1;
		}
		// test if only second is null
		if (o2ExportPath == null)
			return 1;
		// none of them is null, so delegate to compareTo from String
		return o1ExportPath.compareTo(o2ExportPath);
	}

}
