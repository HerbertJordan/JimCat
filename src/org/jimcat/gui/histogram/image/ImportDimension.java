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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jimcat.gui.ImageControl;
import org.jimcat.gui.ViewControl;
import org.jimcat.gui.ViewFilterListener;
import org.jimcat.gui.histogram.HistogramModel;
import org.jimcat.gui.histogram.HistogramModelEvent;
import org.jimcat.gui.histogram.HistogramModelPath;
import org.jimcat.gui.histogram.HistogramModel.ScaleMark;
import org.jimcat.model.Image;
import org.jimcat.model.libraries.ImageLibrary;
import org.jimcat.model.notification.BeanChangeEvent;
import org.jimcat.model.notification.CollectionListener;

/**
 * A dimension representing import spreading.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class ImportDimension extends Dimension {

	/**
	 * a reference to the viewControl used by the client
	 */
	private ViewControl viewControl;

	/**
	 * the import distributions
	 */
	private int values[];

	/**
	 * the maximum of values
	 */
	private int max;

	/**
	 * the higher limit
	 */
	private int higherLimit = HistogramModel.NO_LIMIT;

	/**
	 * the lower limit
	 */
	private int lowerLimit = HistogramModel.NO_LIMIT;

	/**
	 * the number of this dimension for event notification
	 */
	private int dimNumber;

	/**
	 * @param histogram 
	 * @param viewCtrl 
	 * @param imageCtrl 
	 * @param dimensionNumber 
	 * 
	 */
	public ImportDimension(ImageHistogram histogram, ViewControl viewCtrl, ImageControl imageCtrl, int dimensionNumber) {
		super(histogram);

		// init members
		this.dimNumber = dimensionNumber;
		this.viewControl = viewCtrl;
		viewCtrl.addViewFilterListener(new FilterListener());

		ImageLibrary library = imageCtrl.getLibrary();
		reloadValues(library);
		library.addListener(new ImageLibraryListener());
	}

	/**
	 * reload statistic form library
	 * 
	 * @param library
	 */
	private void reloadValues(ImageLibrary library) {
		// get image list
		List<Image> images = new ArrayList<Image>(library.getAll());

		// find max ID
		long maxID = -1;
		for (Image img : images) {
			long cur = img.getMetadata().getImportId();
			if (maxID < cur) {
				maxID = cur;
			}
		}

		// init values
		values = new int[(int) maxID + 1];

		// count ratings
		for (Image img : images) {
			values[(int) img.getMetadata().getImportId()]++;
		}

		// find maximum
		max = 0;
		for (int i = 0; i < values.length; i++) {
			if (max < values[i]) {
				max = values[i];
			}
		}
	}

	/**
	 * nothing to do - there is only one resolution
	 * 
	 * @see org.jimcat.gui.histogram.image.Dimension#convertIndex(int, int, int)
	 */
	@Override
	@SuppressWarnings("unused")
	public int convertIndex(int fromResolution, int toResolution, int index) {
		return index;
	}

	/**
	 * get number of buckets
	 * 
	 * @see org.jimcat.gui.histogram.image.Dimension#getBucketCount(int)
	 */
	@Override
	@SuppressWarnings("unused")
	public int getBucketCount(int resolution) {
		return values.length;
	}

	/**
	 * there is exactly one resolution
	 * 
	 * @see org.jimcat.gui.histogram.image.Dimension#getCountResolutions()
	 */
	@Override
	public int getCountResolutions() {
		return 1;
	}

	/**
	 * current higher limit or NO_LIMIT
	 * 
	 * @see org.jimcat.gui.histogram.image.Dimension#getHigherLimiter(int)
	 */
	@Override
	@SuppressWarnings("unused")
	public int getHigherLimiter(int resolution) {
		return higherLimit;
	}

	/**
	 * initaly set scale to last index
	 * 
	 * @see org.jimcat.gui.histogram.image.Dimension#getInitialIndex()
	 */
	@Override
	public int getInitialIndex() {
		return values.length - 1;
	}

	/**
	 * the label used for each mark should be the id itself
	 * 
	 * @see org.jimcat.gui.histogram.image.Dimension#getLabelForMark(int, int)
	 */
	@Override
	@SuppressWarnings("unused")
	public String getLabelForMark(int resolution, int index) {
		return Integer.toString(index);
	}

	/**
	 * returns lower limit or NO_LIMIT
	 * 
	 * @see org.jimcat.gui.histogram.image.Dimension#getLowerLimiter(int)
	 */
	@Override
	@SuppressWarnings("unused")
	public int getLowerLimiter(int resolution) {
		return lowerLimit;
	}

	/**
	 * mark every 5th and 10th import
	 * 
	 * @see org.jimcat.gui.histogram.image.Dimension#getMarkFor(int, int)
	 */
	@Override
	@SuppressWarnings("unused")
	public ScaleMark getMarkFor(int resolution, int index) {
		ScaleMark res = ScaleMark.SMALL;
		if (index % 5 == 0) {
			res = ScaleMark.LABEL;
		}
		return res;
	}

	/**
	 * get a name for this dimension
	 * 
	 * @see org.jimcat.gui.histogram.image.Dimension#getName()
	 */
	@Override
	public String getName() {
		return "Import Number";
	}

	/**
	 * there are no resolutions => returns null
	 * 
	 * @see org.jimcat.gui.histogram.image.Dimension#getNameFor(int)
	 */
	@Override
	@SuppressWarnings("unused")
	public String getNameFor(int resolution) {
		return null;
	}

	/**
	 * get the value for the given index
	 * 
	 * @see org.jimcat.gui.histogram.image.Dimension#getValueAt(int, int)
	 */
	@Override
	@SuppressWarnings("unused")
	public float getValueAt(int resolution, int index) throws IllegalArgumentException {
		return values[index] / (float) max;
	}

	/**
	 * get the absolute value at given index
	 * 
	 * @see org.jimcat.gui.histogram.image.Dimension#getAbsoluteValueAt(int,
	 *      int)
	 */
	@Override
	@SuppressWarnings("unused")
	public int getAbsoluteValueAt(int resolution, int index) throws IllegalArgumentException {
		return values[index];
	}

	/**
	 * set higher limit to given value
	 * 
	 * @see org.jimcat.gui.histogram.image.Dimension#setHigherLimiter(int, int)
	 */
	@Override
	@SuppressWarnings("unused")
	public void setHigherLimiter(int resolution, int index) throws IllegalArgumentException {
		// shourtcut
		if (higherLimit == index) {
			return;
		}
		higherLimit = index;

		viewControl.setHigherImportLimit(higherLimit);
	}

	/**
	 * update lower limit
	 * 
	 * @see org.jimcat.gui.histogram.image.Dimension#setLowerLimiter(int, int)
	 */
	@Override
	@SuppressWarnings("unused")
	public void setLowerLimiter(int resolution, int index) throws IllegalArgumentException {
		// shourtcut
		if (lowerLimit == index) {
			return;
		}
		lowerLimit = index;

		viewControl.setLowerImportLimit(lowerLimit);
	}

	/**
	 * small class listening to filter changes
	 */
	private class FilterListener implements ViewFilterListener {
		/**
		 * react on filter changes
		 * 
		 * @see org.jimcat.gui.ViewFilterListener#filterChanges(org.jimcat.gui.ViewControl)
		 */
		public void filterChanges(ViewControl control) {
			long lowerId = control.getLowerImportLimit();
			long higherId = control.getHigherImportLimit();

			// update higher filter
			if (higherLimit != higherId) {
				higherLimit = (int) higherId;

				// inform listener
				HistogramModelPath path = new HistogramModelPath(dimNumber, 0, higherLimit);
				HistogramModelEvent event = new HistogramModelEvent(null, path, false);
				fireLimitChangedEvent(event);
			}

			// update lower filter
			if (lowerLimit != lowerId) {
				lowerLimit = (int) lowerId;

				// inform listener
				HistogramModelPath path = new HistogramModelPath(dimNumber, 0, lowerLimit);
				HistogramModelEvent event = new HistogramModelEvent(null, path, false);
				fireLimitChangedEvent(event);
			}

		}
	}

	/**
	 * the listener observing images states
	 */
	private class ImageLibraryListener implements CollectionListener<Image, ImageLibrary> {

		/**
		 * @param collection 
		 * @see org.jimcat.model.notification.CollectionListener#basementChanged(org.jimcat.model.notification.ObservableCollection)
		 */
		public void basementChanged(ImageLibrary collection) {
			// no way => reload values
			reloadValues(collection);
		}

		/**
		 * react on a new image
		 * @param collection 
		 * @param elements 
		 * 
		 * @see org.jimcat.model.notification.CollectionListener#elementsAdded(org.jimcat.model.notification.ObservableCollection,
		 *      java.util.Set)
		 */
		@SuppressWarnings("unused")
		public void elementsAdded(ImageLibrary collection, Set<Image> elements) {
			// set of changes
			Set<HistogramModelPath> paths = new HashSet<HistogramModelPath>();

			for (Image element : elements) {
				// get rating
				int id = (int) element.getMetadata().getImportId();

				if (id >= values.length) {
					// expant list
					int newList[] = new int[id + 1];
					System.arraycopy(values, 0, newList, 0, values.length);
					values = newList;
				}

				// increment value
				values[id]++;

				// update max
				max = Math.max(max, values[id]);

				// send information
				paths.add(new HistogramModelPath(dimNumber, 0, id));
			}

			// send event
			HistogramModelEvent event = new HistogramModelEvent(getHistogram(), paths);
			fireValueChangedEvent(event);
		}

		/**
		 * react on removed elements
		 * @param collection 
		 * @param elements 
		 * 
		 * @see org.jimcat.model.notification.CollectionListener#elementsRemoved(org.jimcat.model.notification.ObservableCollection,
		 *      java.util.Set)
		 */
		@SuppressWarnings("unused")
		public void elementsRemoved(ImageLibrary collection, Set<Image> elements) {
			// set of changes
			Set<HistogramModelPath> paths = new HashSet<HistogramModelPath>();

			// process elements
			for (Image element : elements) {
				// get rating
				int id = (int) element.getMetadata().getImportId();

				// decrement value
				values[id]--;

				// update maximum
				if ((values[id] + 1) == max) {
					max = values[0];
					for (int i = 1; i < values.length; i++) {
						if (max < values[i]) {
							max = values[i];
						}
					}
				}

				// send information
				paths.add(new HistogramModelPath(dimNumber, 0, id));
			}

			// fire event
			HistogramModelEvent event = new HistogramModelEvent(getHistogram(), paths);
			fireValueChangedEvent(event);
		}

		/**
		 * react on updated elements
		 * @param collection 
		 * @param events 
		 * 
		 * @see org.jimcat.model.notification.CollectionListener#elementsUpdated(org.jimcat.model.notification.ObservableCollection,
		 *      java.util.List)
		 */
		@SuppressWarnings("unused")
		public void elementsUpdated(ImageLibrary collection, List<BeanChangeEvent<Image>> events) {
			// import id can't be updated
		}
	}
}
