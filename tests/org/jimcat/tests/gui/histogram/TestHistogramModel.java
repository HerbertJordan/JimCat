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

package org.jimcat.tests.gui.histogram;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jimcat.gui.histogram.HistogramModel;
import org.jimcat.gui.histogram.HistogramModelEvent;
import org.jimcat.gui.histogram.HistogramModelListener;
import org.jimcat.gui.histogram.HistogramModelPath;

/**
 * A histogram model providing randum values in 3 dimensions and several
 * resolutions.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class TestHistogramModel implements HistogramModel {

	/**
	 * switch if values should change
	 */
	private static final boolean ANIMATED = true;

	/**
	 * constant to access limit vector
	 */
	private static final int LOWER = 0;

	/**
	 * constant to access limit vector
	 */
	private static final int HIGHER = 1;

	/**
	 * a list of registered model listeners
	 */
	private List<HistogramModelListener> listeners;

	/**
	 * the test data block
	 */
	private float[][][] values;

	/**
	 * an array containing limits the indizes are all based on the highest
	 * resolution
	 */
	private int[][] limits;

	/**
	 * reates random test values
	 */
	public TestHistogramModel() {
		// init listeners list
		listeners = new CopyOnWriteArrayList<HistogramModelListener>();

		// create test values
		values = new float[3][][];

		// first dimension => several resolutions
		values[0] = new float[3][];
		values[0][0] = new float[80];
		values[0][1] = new float[50];
		values[0][2] = new float[20];

		// second dimension => one resolution
		values[1] = new float[1][];
		values[1][0] = new float[130];

		// third dimension => small value set;
		values[2] = new float[1][];
		values[2][0] = new float[6];

		// fill in random values
		for (int i = 0; i < values.length; i++) {
			for (int j = 0; j < values[i].length; j++) {
				for (int k = 0; k < values[i][j].length; k++) {
					values[i][j][k] = (float) Math.random();
				}
			}
		}

		// reset limits
		limits = new int[3][2];
		for (int i = 0; i < limits.length; i++) {
			Arrays.fill(limits[i], NO_LIMIT);
		}

		// start random changes
		if (ANIMATED) {
			Timer timer = new Timer(true);
			timer.schedule(new RandomChange(), 2000, 500);
		}
	}

	/**
	 * register a new listener
	 * 
	 * @see org.jimcat.gui.histogram.HistogramModel#addHistogramModelListener(org.jimcat.gui.histogram.HistogramModelListener)
	 */
	public void addHistogramModelListener(HistogramModelListener listener) {
		listeners.add(listener);
	}

	/**
	 * converte an index from one dimension to another
	 * 
	 * @see org.jimcat.gui.histogram.HistogramModel#convertIndex(int, int, int,
	 *      int)
	 */
	public int convertIndex(int dimension, int fromResolution, int toResolution, int index) {
		// assuming a linear coherence
		int countFrom = values[dimension][fromResolution].length;
		int countTo = values[dimension][toResolution].length;

		// linear coherence
		return Math.round(((index * countTo) / (float) countFrom));
	}

	/**
	 * get number of buckets on given dimension
	 * 
	 * @see org.jimcat.gui.histogram.HistogramModel#getBucketCount(int, int)
	 */
	public int getBucketCount(int dimension, int resolution) {
		return values[dimension][resolution].length;
	}

	/**
	 * get number of dimensions
	 * 
	 * @see org.jimcat.gui.histogram.HistogramModel#getCountDimension()
	 */
	public int getCountDimension() {
		return values.length;
	}

	/**
	 * get number of resolutions of a certain dimension
	 * 
	 * @see org.jimcat.gui.histogram.HistogramModel#getCountResolutions(int)
	 */
	public int getCountResolutions(int dimension) {
		return values[dimension].length;
	}

	/**
	 * get higher limit
	 * 
	 * @see org.jimcat.gui.histogram.HistogramModel#getHigherLimiter(int, int)
	 */
	public int getHigherLimiter(int dimension, int resolution) {
		return getLimit(dimension, resolution, HIGHER);
	}

	/**
	 * 
	 * default every tenth elemnt is labeled
	 * 
	 * @see org.jimcat.gui.histogram.HistogramModel#getLabelForMark(int, int,
	 *      int)
	 */
	public String getLabelForMark(int dimension, int resolution, int index) {
		if (dimension == 2) {
			// its the small scale - label everything
			return Integer.toString(index);
		}

		// otherwise, label every 10th element
		if (index % 10 == 0) {
			return "group " + index + "/" + resolution;
		}
		// otherwise, show no label
		return null;
	}

	/**
	 * get lower limit
	 * 
	 * @see org.jimcat.gui.histogram.HistogramModel#getLowerLimiter(int, int)
	 */
	public int getLowerLimiter(int dimension, int resolution) {
		return getLimit(dimension, resolution, LOWER);
	}

	/**
	 * tryout of all kind of scaling marks
	 * 
	 * @see org.jimcat.gui.histogram.HistogramModel#getMarkFor(int, int, int)
	 */
	@SuppressWarnings("unused")
	public ScaleMark getMarkFor(int dimension, int resolution, int index) {
		if (dimension == 2) {
			return ScaleMark.LABEL;
		}

		// by default there is no mark
		ScaleMark res = ScaleMark.NONE;

		// every second should have a small mark
		if (index % 2 == 0) {
			res = ScaleMark.SMALL;
		}

		// every 5th should have a big mark
		if (index % 5 == 0) {
			res = ScaleMark.BIG;
		}

		// every 10th should have a label
		if (index % 10 == 0) {
			res = ScaleMark.LABEL;
		}

		// return result
		return res;
	}

	/**
	 * get a name for dimension
	 * 
	 * @see org.jimcat.gui.histogram.HistogramModel#getNameFor(int)
	 */
	public String getNameFor(int dimension) {
		return "dimension " + dimension;
	}

	/**
	 * get name for resolution
	 * 
	 * @see org.jimcat.gui.histogram.HistogramModel#getNameFor(int, int)
	 */
	public String getNameFor(int dimension, int resolution) {
		return "dim: " + dimension + " res: " + resolution;
	}

	/**
	 * read a value from this model
	 * 
	 * @see org.jimcat.gui.histogram.HistogramModel#getValueAt(int, int, int)
	 */
	public float getValueAt(int dimension, int resolution, int index) throws IllegalArgumentException {
		// just read from array
		try {
			return values[dimension][resolution][index];
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * read absolute value for given position
	 * 
	 * @see org.jimcat.gui.histogram.HistogramModel#getAbsoluteValueAt(int, int,
	 *      int)
	 */
	public int getAbsoluteValueAt(int dimension, int resolution, int index) throws IllegalArgumentException {
		// just read from array
		try {
			return (int) (values[dimension][resolution][index] * 100);
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * unregister listener
	 * 
	 * @see org.jimcat.gui.histogram.HistogramModel#removeHistogramModelListener(org.jimcat.gui.histogram.HistogramModelListener)
	 */
	public void removeHistogramModelListener(HistogramModelListener listener) {
		listeners.remove(listener);
	}

	/**
	 * set a new limit
	 * 
	 * @see org.jimcat.gui.histogram.HistogramModel#setHigherLimiter(int, int,
	 *      int)
	 */
	public void setHigherLimiter(int dimension, int resolution, int index) throws IllegalArgumentException {
		try {
			setLimit(dimension, resolution, index, HIGHER);
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * exchange lower limit
	 * 
	 * @see org.jimcat.gui.histogram.HistogramModel#setLowerLimiter(int, int,
	 *      int)
	 */
	public void setLowerLimiter(int dimension, int resolution, int index) throws IllegalArgumentException {
		try {
			setLimit(dimension, resolution, index, LOWER);
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * internaly used to read limits
	 * 
	 * @param dimension -
	 *            the dimension
	 * @param resolution -
	 *            the target dimension
	 * @param type -
	 *            the limit type (HIGHER,LOWER);
	 * @return - the limit for the given resolution
	 */
	private int getLimit(int dimension, int resolution, int type) {
		int limit = limits[dimension][type];
		if (limit == NO_LIMIT) {
			return limit;
		}

		if (resolution == 0) {
			// its the highest resolution
			return limit;
		}

		// convert limit to resolution
		return convertIndex(dimension, 0, resolution, limit);
	}

	/**
	 * set a new limit for given dimension
	 * 
	 * @param dimension -
	 *            the dimension to set
	 * @param resolution -
	 *            the resolution the limit is indexed in
	 * @param limit -
	 *            the limit index
	 * @param type -
	 *            the type of this limit (HIGHER,LOWER)
	 */
	private void setLimit(int dimension, int resolution, int limit, int type) {
		// is it a clear?
		if (limit == NO_LIMIT) {
			// clear it
			limits[dimension][type] = NO_LIMIT;
			return;
		}

		// convert limit to highest resolution
		int res = limit;
		if (resolution != 0) {
			res = convertIndex(dimension, resolution, 0, res);
		}

		// save limit
		limits[dimension][type] = res;

		// send notifications
		HistogramModelPath path = new HistogramModelPath(dimension, 0, res);
		HistogramModelEvent event = new HistogramModelEvent(this, path, type == HIGHER);
		for (HistogramModelListener listener : listeners) {
			listener.limitChanged(event);
		}
	}

	/**
	 * returns 0 for all dimensions except dimension 2
	 * 
	 * @see org.jimcat.gui.histogram.HistogramModel#getInitialIndex(int)
	 */
	public int getInitialIndex(int dimension) {
		if (dimension == 2) {
			return 3;
		}
		return 0;
	}

	/**
	 * by default element 0/0/0 will be returned
	 * 
	 * @see org.jimcat.gui.histogram.HistogramModel#getInitialPath()
	 */
	public HistogramModelPath getInitialPath() {
		return new HistogramModelPath(0, 0, 0);
	}

	/**
	 * performe random changes
	 */
	private class RandomChange extends TimerTask {
		/**
		 * make changes
		 * 
		 * @see java.util.TimerTask#run()
		 */
		@Override
		public void run() {
			Set<HistogramModelPath> changes = new HashSet<HistogramModelPath>();
			for (int h = 0; h < 10; h++) {
				int i = (int) (Math.random() * values.length);
				int j = (int) (Math.random() * values[i].length);
				int k = (int) (Math.random() * values[i][j].length);

				values[i][j][k] = (float) Math.random();

				changes.add(new HistogramModelPath(i, j, k));
			}

			// send notification
			HistogramModelEvent event = new HistogramModelEvent(TestHistogramModel.this, changes);
			for (HistogramModelListener listener : listeners) {
				listener.valueChanged(event);
			}
		}
	}
}
