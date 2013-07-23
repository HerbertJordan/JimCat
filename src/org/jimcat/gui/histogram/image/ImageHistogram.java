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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jimcat.gui.ImageControl;
import org.jimcat.gui.ViewControl;
import org.jimcat.gui.histogram.HistogramModel;
import org.jimcat.gui.histogram.HistogramModelEvent;
import org.jimcat.gui.histogram.HistogramModelListener;
import org.jimcat.gui.histogram.HistogramModelPath;

/**
 * Represents the histogram model used to show image informations.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class ImageHistogram implements HistogramModel {

	/**
	 * a list of listeners
	 */
	private List<HistogramModelListener> listeners = new CopyOnWriteArrayList<HistogramModelListener>();

	/**
	 * a list of installed dimensions
	 */
	private List<Dimension> dimensions;

	/**
	 * create a new ImageHistogram model using given ViewControl
	 * 
	 * @param viewControl
	 * @param imageControl 
	 */
	public ImageHistogram(ViewControl viewControl, ImageControl imageControl) {
		// create dimensions list
		int i = 0;
		dimensions = new ArrayList<Dimension>();
		dimensions.add(new DateTakenDimension(this, viewControl, imageControl, i++));
		dimensions.add(new RatingDimension(this, viewControl, imageControl, i++));
		dimensions.add(new MegaPixelDimension(this, viewControl, imageControl, i++));
		dimensions.add(new ImportDimension(this, viewControl, imageControl, i++));
	}

	/**
	 * add a new histogram model listener
	 * 
	 * @see org.jimcat.gui.histogram.HistogramModel#addHistogramModelListener(org.jimcat.gui.histogram.HistogramModelListener)
	 */
	public void addHistogramModelListener(HistogramModelListener listener) {
		listeners.add(listener);
	}

	/**
	 * delegate to dimension instance
	 * 
	 * @see org.jimcat.gui.histogram.HistogramModel#convertIndex(int, int, int,
	 *      int)
	 */
	public int convertIndex(int dimension, int fromResolution, int toResolution, int index) {
		return dimensions.get(dimension).convertIndex(fromResolution, toResolution, index);
	}

	/**
	 * delegate to dimension instance
	 * 
	 * @see org.jimcat.gui.histogram.HistogramModel#getBucketCount(int, int)
	 */
	public int getBucketCount(int dimension, int resolution) {
		return dimensions.get(dimension).getBucketCount(resolution);
	}

	/**
	 * get the number of supported dimensions
	 * 
	 * @see org.jimcat.gui.histogram.HistogramModel#getCountDimension()
	 */
	public int getCountDimension() {
		return dimensions.size();
	}

	/**
	 * count resolutions of dimension
	 * 
	 * @see org.jimcat.gui.histogram.HistogramModel#getCountResolutions(int)
	 */
	public int getCountResolutions(int dimension) {
		return dimensions.get(dimension).getCountResolutions();
	}

	/**
	 * get higher limit of dimension
	 * 
	 * @see org.jimcat.gui.histogram.HistogramModel#getHigherLimiter(int, int)
	 */
	public int getHigherLimiter(int dimension, int resolution) {
		return dimensions.get(dimension).getHigherLimiter(resolution);
	}

	/**
	 * delegeta to right dimension
	 * 
	 * @see org.jimcat.gui.histogram.HistogramModel#getLabelForMark(int, int,
	 *      int)
	 */
	public String getLabelForMark(int dimension, int resolution, int index) {
		return dimensions.get(dimension).getLabelForMark(resolution, index);
	}

	/**
	 * delegate to right dimension
	 * 
	 * @see org.jimcat.gui.histogram.HistogramModel#getLowerLimiter(int, int)
	 */
	public int getLowerLimiter(int dimension, int resolution) {
		return dimensions.get(dimension).getLowerLimiter(resolution);
	}

	/**
	 * delegate to right dimension
	 * 
	 * @see org.jimcat.gui.histogram.HistogramModel#getMarkFor(int, int, int)
	 */
	public ScaleMark getMarkFor(int dimension, int resolution, int index) {
		return dimensions.get(dimension).getMarkFor(resolution, index);
	}

	/**
	 * delegated to right dimension
	 * 
	 * @see org.jimcat.gui.histogram.HistogramModel#getNameFor(int)
	 */
	public String getNameFor(int dimension) {
		return dimensions.get(dimension).getName();
	}

	/**
	 * delegated to right dimension
	 * 
	 * @see org.jimcat.gui.histogram.HistogramModel#getNameFor(int, int)
	 */
	public String getNameFor(int dimension, int resolution) {
		return dimensions.get(dimension).getNameFor(resolution);
	}

	/**
	 * delegated to right dimension
	 * 
	 * @see org.jimcat.gui.histogram.HistogramModel#getValueAt(int, int, int)
	 */
	public float getValueAt(int dimension, int resolution, int index) throws IllegalArgumentException {
		return dimensions.get(dimension).getValueAt(resolution, index);
	}

	/**
	 * delegate to right dimension
	 * 
	 * @see org.jimcat.gui.histogram.HistogramModel#getAbsoluteValueAt(int, int,
	 *      int)
	 */
	public int getAbsoluteValueAt(int dimension, int resolution, int index) throws IllegalArgumentException {
		return dimensions.get(dimension).getAbsoluteValueAt(resolution, index);
	}

	/**
	 * remove a model listener
	 * 
	 * @see org.jimcat.gui.histogram.HistogramModel#removeHistogramModelListener(org.jimcat.gui.histogram.HistogramModelListener)
	 */
	public void removeHistogramModelListener(HistogramModelListener listener) {
		listeners.remove(listener);
	}

	/**
	 * delegate to right dimension
	 * 
	 * @see org.jimcat.gui.histogram.HistogramModel#setHigherLimiter(int, int,
	 *      int)
	 */
	public void setHigherLimiter(int dimension, int resolution, int index) throws IllegalArgumentException {
		dimensions.get(dimension).setHigherLimiter(resolution, index);
	}

	/**
	 * delegated to right dimension
	 * 
	 * @see org.jimcat.gui.histogram.HistogramModel#setLowerLimiter(int, int,
	 *      int)
	 */
	public void setLowerLimiter(int dimension, int resolution, int index) throws IllegalArgumentException {
		dimensions.get(dimension).setLowerLimiter(resolution, index);
	}

	/**
	 * delegated to right dimension
	 * 
	 * @see org.jimcat.gui.histogram.HistogramModel#getInitialIndex(int)
	 */
	public int getInitialIndex(int dimension) {
		return dimensions.get(dimension).getInitialIndex();
	}

	/**
	 * by default the last element in the month scale will be returned
	 * 
	 * @see org.jimcat.gui.histogram.HistogramModel#getInitialPath()
	 */
	public HistogramModelPath getInitialPath() {
		int index = getBucketCount(0, DateTakenDimension.MONTHS) - 1;
		return new HistogramModelPath(0, DateTakenDimension.MONTHS, index);
	}

	/**
	 * inform all listeners about a change
	 * 
	 * @param event
	 */
	protected void fireValueChangedEvent(HistogramModelEvent event) {
		for (HistogramModelListener listener : listeners) {
			listener.valueChanged(event);
		}
	}

	/**
	 * inform all listeners about a changed limit
	 * 
	 * @param event
	 */
	protected void fireLimitChangedEvent(HistogramModelEvent event) {
		for (HistogramModelListener listener : listeners) {
			listener.limitChanged(event);
		}
	}

	/**
	 * inform all listeners about a changed structure
	 * 
	 */
	protected void fireStructureChangedEvent() {
		for (HistogramModelListener listener : listeners) {
			listener.structureChanged(this);
		}
	}
}
