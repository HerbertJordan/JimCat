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

package org.jimcat.gui.histogram.image;

import org.jimcat.gui.histogram.HistogramModel;
import org.jimcat.gui.histogram.HistogramModelEvent;
import org.jimcat.gui.histogram.HistogramModel.ScaleMark;

/**
 * A dimension of data used by the ImageHistogram.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public abstract class Dimension {

	/**
	 * the histogram this dimension is used in
	 */
	private ImageHistogram histogram;

	/**
	 * constructor using histogram this dimension is used in
	 * 
	 * @param histogram
	 */
	public Dimension(ImageHistogram histogram) {
		this.histogram = histogram;
	}

	/**
	 * get the number of supported resolutions.
	 * 
	 * Must be at least one.
	 * 
	 * @see HistogramModel#getCountResolutions(int)
	 * @return - the number of resolutions (>=1)
	 */
	public abstract int getCountResolutions();

	/**
	 * the number of buckets (bars) the given resolution containes.
	 * 
	 * This value may change if new data is available.
	 * 
	 * @param resolution -
	 *            a resolution index supported by the given dimension (0 <=
	 *            resolution < getCountResolution())
	 * @return - the number of buckets. For values 0 <= x < getBucketCount(),
	 *         getValueAt() must return a Value between 0 and 1;
	 * @see HistogramModel#getBucketCount(int, int)
	 */
	public abstract int getBucketCount(int resolution);

	/**
	 * retrieves a String representation for the this dimension.
	 * 
	 * @return - a string representaiton of this dimension
	 */
	public abstract String getName();

	/**
	 * retrieves a String representation fot the given resolution
	 * 
	 * @param resolution -
	 *            a resolution index supported by the given dimension (0 <=
	 *            resolution < getCountResolution())
	 * @return - a string representation
	 * @see HistogramModel#getNameFor(int, int)
	 */
	public abstract String getNameFor(int resolution);

	/**
	 * get a absolute value for the bucket with the given index.
	 * 
	 * The value must be between 0 and 1 (May be used as 0% - 100% of maximum)
	 * for all indizes 0 <= index < getBucketCount();
	 * 
	 * @param resolution -
	 *            a resolution index supported by the given dimension (0 <=
	 *            resolution < getCountResolution())
	 * @param index -
	 *            the index requested (0 <= index < getBucketCount())
	 * @return - the relativ value (0 - 1)
	 * @throws IllegalArgumentException -
	 *             if one of the argument bounds isn't matched
	 * @see HistogramModel#getValueAt(int, int, int)
	 */
	public abstract float getValueAt(int resolution, int index) throws IllegalArgumentException;

	/**
	 * get a absolute value for the bucket with the given index.
	 * 
	 * The value must be between 0 and 1 (May be used as 0% - 100% of maximum)
	 * for all indizes 0 <= index < getBucketCount();
	 * 
	 * @param resolution -
	 *            a resolution index supported by the given dimension (0 <=
	 *            resolution < getCountResolution(dimension))
	 * @param index -
	 *            the index requested (0 <= index < getBucketCount())
	 * @return - the absolute value for this bucket
	 * @throws IllegalArgumentException -
	 *             if one of the argument bounds isn't matched
	 * @see HistogramModel#getAbsoluteValueAt(int, int, int)
	 */
	public abstract int getAbsoluteValueAt(int resolution, int index) throws IllegalArgumentException;

	/**
	 * get a value determining which kind of mark to use
	 * 
	 * @param resolution -
	 *            a resolution index supported by the given dimension (0 <=
	 *            resolution < getCountResolution())
	 * @param index -
	 *            the index to be labeled. (0 <= index <= getBucketCount())
	 * @return - the marking type to use
	 * @see HistogramModel#getMarkFor(int, int, int)
	 */
	public abstract ScaleMark getMarkFor(int resolution, int index);

	/**
	 * the dimension must provide a label for each element getMarkFor() is
	 * returning LABEL. This methode is used to retrieve those labels.
	 * 
	 * @param resolution -
	 *            a resolution index supported by the given dimension (0 <=
	 *            resolution < getCountResolution())
	 * @param index -
	 *            the index to be labeled. (0 <= index <= getBucketCount())
	 * @return - a label for this element
	 * @see HistogramModel#getLabelForMark(int, int, int)
	 */
	public abstract String getLabelForMark(int resolution, int index);

	/**
	 * This methode is used to convert one index of one resolution into an
	 * adequate value of another resolution.
	 * 
	 * @param fromResolution -
	 *            the resolution the given index is based on
	 * @param toResolution -
	 *            the resolution of the index looked for
	 * @param index 
	 * @return - an adequate index in the to - resolution
	 * @see HistogramModel#convertIndex(int, int, int, int)
	 */
	public abstract int convertIndex(int fromResolution, int toResolution, int index);

	/**
	 * This methode is used to setup a lower limiter mark for this dimension.
	 * The same limiter should be valid for all resolutions. (converted through
	 * convertIndex())
	 * 
	 * the lower limit must be less than the higher limit. Otherwise a
	 * IllegalArgumentException will be thrown.
	 * 
	 * @param resolution -
	 *            a resolution index supported by the given dimension (0 <=
	 *            resolution < getCountResolution())
	 * @param index -
	 *            an index valid for the given dimension / resolution this
	 *            should become the new index. if -1 then the old limit should
	 *            be removed.
	 * @throws IllegalArgumentException -
	 *             if the index isn't valid for the given dimension or it is
	 *             higher than the currently established higher limit
	 */
	public abstract void setLowerLimiter(int resolution, int index) throws IllegalArgumentException;

	/**
	 * This methode is used to setup a higher limiter mark for this dimension.
	 * The same limiter should be valid for all resolutions. (converted through
	 * convertIndex())
	 * 
	 * the higher limit must be higher than the lower limit. Otherwise a
	 * IllegalArgumentException will be thrown.
	 * 
	 * @param resolution -
	 *            a resolution index supported by the given dimension (0 <=
	 *            resolution < getCountResolution())
	 * @param index -
	 *            an index valid for the given dimension / resolution this
	 *            should become the new index. if -1 then the old limit should
	 *            be removed.
	 * @throws IllegalArgumentException -
	 *             if the index isn't valid for the given dimension or it is
	 *             lower than the currently established lower limit
	 */
	public abstract void setHigherLimiter(int resolution, int index) throws IllegalArgumentException;

	/**
	 * retrive the current lower limit for this dimension using the given
	 * resolution.
	 * 
	 * @param resolution -
	 *            the resolution the result should be in
	 * @return - the index of the lower limit within the given dimension
	 *         relative to the given resolution
	 */
	public abstract int getLowerLimiter(int resolution);

	/**
	 * retrive the current higher limit for this dimension using the given
	 * resolution.
	 * 
	 * @param resolution -
	 *            the resolution the result should be in
	 * @return - the index of the higher limit within the given dimension
	 *         relative to the given resolution
	 */
	public abstract int getHigherLimiter(int resolution);

	/**
	 * retrive the initial index to show. Value should be based on dimensions
	 * resolution 0.
	 * 
	 * @return - the index to show initially
	 */
	public abstract int getInitialIndex();

	/**
	 * @return the histogram
	 */
	protected ImageHistogram getHistogram() {
		return histogram;
	}

	/**
	 * @param event
	 * @see org.jimcat.gui.histogram.image.ImageHistogram#fireLimitChangedEvent(org.jimcat.gui.histogram.HistogramModelEvent)
	 */
	protected void fireLimitChangedEvent(HistogramModelEvent event) {
		histogram.fireLimitChangedEvent(event);
	}

	/**
	 * @param event
	 * @see org.jimcat.gui.histogram.image.ImageHistogram#fireValueChangedEvent(org.jimcat.gui.histogram.HistogramModelEvent)
	 */
	protected void fireValueChangedEvent(HistogramModelEvent event) {
		histogram.fireValueChangedEvent(event);
	}

	/**
	 * 
	 * @see org.jimcat.gui.histogram.image.ImageHistogram#fireStructureChangedEvent()
	 */
	protected void fireStructureChangedEvent() {
		histogram.fireStructureChangedEvent();
	}

}
