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

package org.jimcat.gui.histogram;

/**
 * This class is used to address an element thin an histogram model.
 * 
 * This object is immutable.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class HistogramModelPath {

	/**
	 * addressing the dimension
	 */
	private int dimension;

	/**
	 * addressing the resolution
	 */
	private int resolution;

	/**
	 * addressing the index
	 */
	private int index;

	/**
	 * a constructor requesting all fields to address a value.
	 * 
	 * @param dimension
	 * @param resolution
	 * @param index
	 */
	public HistogramModelPath(int dimension, int resolution, int index) {
		super();
		this.dimension = dimension;
		this.resolution = resolution;
		this.index = index;
	}

	/**
	 * @return the dimension
	 */
	public int getDimension() {
		return dimension;
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @return the resolution
	 */
	public int getResolution() {
		return resolution;
	}

	/**
	 * check if to HistogramModelPaths are equal
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof HistogramModelPath) {
			HistogramModelPath other = (HistogramModelPath) obj;
			if (other.dimension != this.dimension) {
				return false;
			}
			if (other.resolution != this.resolution) {
				return false;
			}
			if (other.index != this.index) {
				return false;
			}
			return true;
		}
		return false;
	}
}
