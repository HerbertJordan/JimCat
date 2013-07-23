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
 * A common interface describing an histogram model used by the Histogram
 * component.
 * 
 * Each histogram model can containe several data dimensions accessed through
 * indizes. Each dimension can containe several resolutions (bucket - sizes).
 * 
 * Each dimension should provide an array containing a list of values for the
 * buckets.
 * 
 * Each dimension also provide a left / right limiter slot. This is used for the
 * establishment of filter criterias.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public interface HistogramModel {

	/**
	 * a list of constantes used to form scales.
	 * <ul>
	 * <li>NONE - no mark will be set</li>
	 * <li>SMALL - a small mark will be set (short line)</li>
	 * <li>BIG - a big mark will be set (long, fat line)</li>
	 * <li>LABEL - a big mark will be set and a label will be added</li>
	 * </ul>
	 */
	public enum ScaleMark {
		NONE, SMALL, BIG, LABEL;
	}

	/**
	 * constant for "no limit set"
	 */
	public int NO_LIMIT = -1;

	/**
	 * should add a new listener retrieving informations about model changes
	 * 
	 * @param listener
	 */
	public void addHistogramModelListener(HistogramModelListener listener);

	/**
	 * should remove a registered listener
	 * 
	 * @param listener
	 */
	public void removeHistogramModelListener(HistogramModelListener listener);

	/**
	 * get the number of contained data dimensions.
	 * 
	 * This must be at least one.
	 * 
	 * @return the number of dimensions (>=1)
	 */
	public int getCountDimension();

	/**
	 * get the number of resolutions within the given dimension.
	 * 
	 * Must be at least one.
	 * 
	 * @param dimension -
	 *            the dimension asked for (0 <= dimension < getCountDimension())
	 * @return - the number of resolutions (>=1)
	 */
	public int getCountResolutions(int dimension);

	/**
	 * the number of buckets (bars) the given dimension with the given
	 * resolution containes.
	 * 
	 * This value may change if new data is available.
	 * 
	 * @param dimension -
	 *            a dimension index supported by this model (0 <= dimension <
	 *            getCountDimension())
	 * @param resolution -
	 *            a resolution index supported by the given dimension (0 <=
	 *            resolution < getCountResolution(dimension))
	 * @return - the number of buckets. For values 0 <= x < getBucketCount(),
	 *         getValueAt() must return a Value between 0 and 1;
	 */
	public int getBucketCount(int dimension, int resolution);

	/**
	 * retrieves a String representation for the given dimension.
	 * 
	 * @param dimension -
	 *            a dimension supported by this model (0 <= dimension <
	 *            getCountDimension())
	 * @return - a string representaiton of this dimension
	 */
	public String getNameFor(int dimension);

	/**
	 * retrieves a String representation fot the given resolution
	 * 
	 * @param dimension -
	 *            a dimension index supported by this model (0 <= dimension <
	 *            getCountDimension())
	 * @param resolution -
	 *            a resolution index supported by the given dimension (0 <=
	 *            resolution < getCountResolution(dimension))
	 * @return - a string representation
	 */
	public String getNameFor(int dimension, int resolution);

	/**
	 * get a relative value for the bucket with the given index.
	 * 
	 * The value must be between 0 and 1 (May be used as 0% - 100% of maximum)
	 * for all indizes 0 <= index < getBucketCount();
	 * 
	 * @param dimension -
	 *            a dimension index supported by this model (0 <= dimension <
	 *            getCountDimension())
	 * @param resolution -
	 *            a resolution index supported by the given dimension (0 <=
	 *            resolution < getCountResolution(dimension))
	 * @param index -
	 *            the index requested (0 <= index < getBucketCount())
	 * @return - the relativ value (0 - 1)
	 * @throws IllegalArgumentException -
	 *             if one of the argument bounds isn't matched
	 */
	public float getValueAt(int dimension, int resolution, int index) throws IllegalArgumentException;

	/**
	 * get a absolute value for the bucket with the given index.
	 * 
	 * The value must be between 0 and 1 (May be used as 0% - 100% of maximum)
	 * for all indizes 0 <= index < getBucketCount();
	 * 
	 * @param dimension -
	 *            a dimension index supported by this model (0 <= dimension <
	 *            getCountDimension())
	 * @param resolution -
	 *            a resolution index supported by the given dimension (0 <=
	 *            resolution < getCountResolution(dimension))
	 * @param index -
	 *            the index requested (0 <= index < getBucketCount())
	 * @return - the absolute value for this bucket
	 * @throws IllegalArgumentException -
	 *             if one of the argument bounds isn't matched
	 */
	public int getAbsoluteValueAt(int dimension, int resolution, int index) throws IllegalArgumentException;

	/**
	 * get a value determining which kind of mark to use
	 * 
	 * @param dimension -
	 *            a dimension index supported by this model (0 <= dimension <
	 *            getCountDimension())
	 * @param resolution -
	 *            a resolution index supported by the given dimension (0 <=
	 *            resolution < getCountResolution(dimension))
	 * @param index -
	 *            the index to be labeled. (0 <= index <= getBucketCount())
	 * @return - the marking type to use
	 */
	public ScaleMark getMarkFor(int dimension, int resolution, int index);

	/**
	 * the model must provide a label for each element getMarkFor() is returning
	 * LABEL. This methode is used to retrieve those labels.
	 * 
	 * @param dimension -
	 *            a dimension index supported by this model (0 <= dimension <
	 *            getCountDimension())
	 * @param resolution -
	 *            a resolution index supported by the given dimension (0 <=
	 *            resolution < getCountResolution(dimension)) *
	 * @param index -
	 *            the index to be labeled. (0 <= index <= getBucketCount())
	 * @return - a label for this element
	 */
	public String getLabelForMark(int dimension, int resolution, int index);

	/**
	 * This methode is used to convert one index of one resolution into an
	 * adequate value of another resolution.
	 * 
	 * @param dimension -
	 *            the dimension of date addressed
	 * @param fromResolution -
	 *            the resolution the given index is based on
	 * @param toResolution -
	 *            the resolution of the index looked for
	 * @param index 
	 * @return - an adequate index in the to - resolution
	 */
	public int convertIndex(int dimension, int fromResolution, int toResolution, int index);

	/**
	 * This methode is used to setup a lower limiter mark for the given
	 * dimension. Limits are dimension based. The same limiter should be valid
	 * for all resolutions. (converted through convertIndex())
	 * 
	 * the lower limit must be less than the higher limit. Otherwise a
	 * IllegalArgumentException will be thrown.
	 * 
	 * @param dimension -
	 *            a dimension index supported by this model (0 <= dimension <
	 *            getCountDimension())
	 * @param resolution -
	 *            a resolution index supported by the given dimension (0 <=
	 *            resolution < getCountResolution(dimension))
	 * @param index -
	 *            an index valid for the given dimension / resolution this
	 *            should become the new index. if -1 then the old limit should
	 *            be removed.
	 * @throws IllegalArgumentException -
	 *             if the index isn't valid for the given dimension or it is
	 *             higher than the currently established higher limit
	 */
	public void setLowerLimiter(int dimension, int resolution, int index) throws IllegalArgumentException;

	/**
	 * This methode is used to setup a higher limiter mark for the given
	 * dimension. Limits are dimension based. The same limiter should be valid
	 * for all resolutions. (converted through convertIndex())
	 * 
	 * the higher limit must be higher than the lower limit. Otherwise a
	 * IllegalArgumentException will be thrown.
	 * 
	 * @param dimension -
	 *            a dimension index supported by this model (0 <= dimension <
	 *            getCountDimension())
	 * @param resolution -
	 *            a resolution index supported by the given dimension (0 <=
	 *            resolution < getCountResolution(dimension))
	 * @param index -
	 *            an index valid for the given dimension / resolution this
	 *            should become the new index. if -1 then the old limit should
	 *            be removed.
	 * @throws IllegalArgumentException -
	 *             if the index isn't valid for the given dimension or it is
	 *             lower than the currently established lower limit
	 */
	public void setHigherLimiter(int dimension, int resolution, int index) throws IllegalArgumentException;

	/**
	 * retrive the current lower limit for the given dimension using the given
	 * resolution.
	 * 
	 * @param dimension -
	 *            the dimension requested
	 * @param resolution -
	 *            the resolution the result should be in
	 * @return - the index of the lower limit within the given dimension
	 *         relative to the given resolution
	 */
	public int getLowerLimiter(int dimension, int resolution);

	/**
	 * retrive the current higher limit for the given dimension using the given
	 * resolution.
	 * 
	 * @param dimension -
	 *            the dimension requested
	 * @param resolution -
	 *            the resolution the result should be in
	 * @return - the index of the higher limit within the given dimension
	 *         relative to the given resolution
	 */
	public int getHigherLimiter(int dimension, int resolution);

	/**
	 * retrive the initial index to show. Value should be based on dimensions
	 * resolution 0.
	 * @param dimension 
	 * 
	 * @return - the index to show initially
	 */
	public int getInitialIndex(int dimension);

	/**
	 * get the path to the element initially be shown
	 * 
	 * @return teh path of the element initially shown
	 */
	public HistogramModelPath getInitialPath();
}
