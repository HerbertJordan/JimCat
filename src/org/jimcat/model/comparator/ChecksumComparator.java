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
 * The ChecksumComparator uses the checksum of two images to compare them.
 * 
 * This class implements the Comparator interface for Images and uses the
 * checksum of them, stored in the metadata of the images to compare them.
 * 
 * 
 * $Id$
 * 
 * @author Michael Handler
 */
public class ChecksumComparator implements Comparator<Image> {

	/**
	 * The compare method of the ChecksumComparator uses the checksum of two
	 * images to compare them. To do so it uses the compareTo method of the
	 * class Long.
	 * @param o1 
	 * @param o2 
	 * @return the result of the compare mehtod as specified in java.util.Comparator
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Image o1, Image o2) {
		if (o1 == null || o1.getMetadata() == null) {
			if (o2 == null || o2.getMetadata() == null)
				return 0;
			return -1;
		}
		if (o2 == null || o2.getMetadata() == null)
			return 1;
		
		//extract checksums as string
		String checkSum1 = o1.getMetadata().getChecksum();
		String checkSum2 = o2.getMetadata().getChecksum();
		
		if (checkSum1 == null && checkSum2 == null) {
			return 0;
		}
		if (checkSum1 == null) {
			return -1;
		}
		if (checkSum2 == null) {
			return 1;
		}
		
		return checkSum1.compareTo(checkSum2);
	}

}
