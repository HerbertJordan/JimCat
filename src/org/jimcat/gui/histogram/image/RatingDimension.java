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

import org.apache.commons.lang.ObjectUtils;
import org.jimcat.gui.ImageControl;
import org.jimcat.gui.ViewControl;
import org.jimcat.gui.ViewFilterListener;
import org.jimcat.gui.histogram.HistogramModel;
import org.jimcat.gui.histogram.HistogramModelEvent;
import org.jimcat.gui.histogram.HistogramModelPath;
import org.jimcat.gui.histogram.HistogramModel.ScaleMark;
import org.jimcat.model.Image;
import org.jimcat.model.ImageRating;
import org.jimcat.model.filter.RatingFilter;
import org.jimcat.model.filter.RatingFilter.Type;
import org.jimcat.model.libraries.ImageLibrary;
import org.jimcat.model.notification.BeanChangeEvent;
import org.jimcat.model.notification.BeanProperty;
import org.jimcat.model.notification.CollectionListener;

/**
 * This Dimension is used within the ImageHistogram widget to display image
 * rating spreading. It also controlles rating filter functions.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class RatingDimension extends Dimension {

	/**
	 * Hard lower limit
	 */
	private static final int LOWER_LIMIT = 0;

	/**
	 * Hard higher limit
	 */
	private static final int HIGHER_LIMIT = ImageRating.values().length;

	/**
	 * a reference to the viewControl used by the client
	 */
	private ViewControl viewControl;

	/**
	 * the rating values
	 */
	private int values[];

	/**
	 * the maximum of values
	 */
	private int max;

	/**
	 * the higher limit
	 */
	private ImageRating higherLimit;

	/**
	 * the lower limit
	 */
	private ImageRating lowerLimit;

	/**
	 * the number of this dimension for event notification
	 */
	private int dimNumber;

	/**
	 * default constructor for this dimension
	 * @param histogram 
	 * @param viewCtrl 
	 * @param imageCtrl 
	 * @param dimensionNumber 
	 */
	public RatingDimension(ImageHistogram histogram, ViewControl viewCtrl, ImageControl imageCtrl, int dimensionNumber) {
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

		// init values
		values = new int[ImageRating.values().length];

		// count ratings
		for (Image img : images) {
			values[img.getRating().ordinal()]++;
		}

		// find maximum
		max = values[0];
		for (int i = 1; i < values.length; i++) {
			if (max < values[i]) {
				max = values[i];
			}
		}
	}

	/**
	 * no resolutions supported, therefore from and to must be the same
	 * 
	 * @see org.jimcat.gui.histogram.image.Dimension#convertIndex(int, int, int)
	 */
	@Override
	@SuppressWarnings("unused")
	public int convertIndex(int fromResolution, int toResolution, int index) {
		return index;
	}

	/**
	 * this will return the constant nummber of image ratings
	 * 
	 * @see org.jimcat.gui.histogram.image.Dimension#getBucketCount(int)
	 */
	@Override
	@SuppressWarnings("unused")
	public int getBucketCount(int resolution) {
		return values.length;
	}

	/**
	 * there are no extra resolutions -> returns 1 - default one
	 * 
	 * @see org.jimcat.gui.histogram.image.Dimension#getCountResolutions()
	 */
	@Override
	public int getCountResolutions() {
		return 1;
	}

	/**
	 * get index of higher limit
	 * 
	 * @see org.jimcat.gui.histogram.image.Dimension#getHigherLimiter(int)
	 */
	@Override
	@SuppressWarnings("unused")
	public int getHigherLimiter(int resolution) {
		if (higherLimit == null) {
			if (lowerLimit == null) {
				return HistogramModel.NO_LIMIT;
			}
			return HIGHER_LIMIT;
		}
		return higherLimit.ordinal();
	}

	/**
	 * gets the center
	 * 
	 * @see org.jimcat.gui.histogram.image.Dimension#getInitialIndex()
	 */
	@Override
	public int getInitialIndex() {
		return values.length / 2;
	}

	/**
	 * get number of stars
	 * 
	 * @see org.jimcat.gui.histogram.image.Dimension#getLabelForMark(int, int)
	 */
	@Override
	@SuppressWarnings("unused")
	public String getLabelForMark(int resolution, int index) {
		return "" + index;
	}

	/**
	 * get lower limit
	 * 
	 * @see org.jimcat.gui.histogram.image.Dimension#getLowerLimiter(int)
	 */
	@Override
	@SuppressWarnings("unused")
	public int getLowerLimiter(int resolution) {
		if (lowerLimit == null) {
			if (higherLimit == null) {
				return HistogramModel.NO_LIMIT;
			}
			return LOWER_LIMIT;
		}
		return lowerLimit.ordinal();
	}

	/**
	 * all items are labeled
	 * 
	 * @see org.jimcat.gui.histogram.image.Dimension#getMarkFor(int, int)
	 */
	@Override
	@SuppressWarnings("unused")
	public ScaleMark getMarkFor(int resolution, int index) {
		return ScaleMark.LABEL;
	}

	/**
	 * get name for this dimension (konstant Rating)
	 * 
	 * @see org.jimcat.gui.histogram.image.Dimension#getName()
	 */
	@Override
	public String getName() {
		return "Rating";
	}

	/**
	 * there are no resolutions -> null will be returned
	 * 
	 * @see org.jimcat.gui.histogram.image.Dimension#getNameFor(int)
	 */
	@Override
	@SuppressWarnings("unused")
	public String getNameFor(int resolution) {
		return null;
	}

	/**
	 * get value for dimension
	 * 
	 * @see org.jimcat.gui.histogram.image.Dimension#getValueAt(int, int)
	 */
	@Override
	@SuppressWarnings("unused")
	public float getValueAt(int resolution, int index) throws IllegalArgumentException {
		return values[index] / (float) max;
	}

	/**
	 * get absolute value for given position
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
	 * change highlimit
	 * 
	 * @see org.jimcat.gui.histogram.image.Dimension#setHigherLimiter(int, int)
	 */
	@Override
	@SuppressWarnings("unused")
	public void setHigherLimiter(int resolution, int index) throws IllegalArgumentException {
		ImageRating newLimit = null;
		if (index != HistogramModel.NO_LIMIT && index < ImageRating.values().length) {
			newLimit = ImageRating.values()[index];
		}
		if (!ObjectUtils.equals(higherLimit, newLimit)) {
			higherLimit = newLimit;
			RatingFilter filter = null;
			if (newLimit != null) {
				filter = new RatingFilter(Type.UP_TO, newLimit);
			}
			viewControl.setHigherRatingFilter(filter);
		}
	}

	/**
	 * chang lower limit
	 * 
	 * @see org.jimcat.gui.histogram.image.Dimension#setLowerLimiter(int, int)
	 */
	@Override
	@SuppressWarnings("unused")
	public void setLowerLimiter(int resolution, int index) throws IllegalArgumentException {
		ImageRating newLimit = null;
		if (index != HistogramModel.NO_LIMIT && index < ImageRating.values().length) {
			newLimit = ImageRating.values()[index];
		}
		if (newLimit == ImageRating.NONE) {
			// a lower limit with no stars isn't usefull
			newLimit = null;
		}
		if (!ObjectUtils.equals(lowerLimit, newLimit)) {
			lowerLimit = newLimit;
			RatingFilter filter = null;
			if (newLimit != null) {
				filter = new RatingFilter(Type.AT_LEAST, newLimit);
			}
			viewControl.setLowerRatingFilter(filter);
		}
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
			RatingFilter lowerFilter = control.getLowerRatingFilter();
			RatingFilter higherFilter = control.getHigherRatingFilter();

			ImageRating lower = null;
			if (lowerFilter != null) {
				lower = lowerFilter.getRating();
			}

			ImageRating higher = null;
			if (higherFilter != null) {
				higher = higherFilter.getRating();
			}

			// update higher filter
			if (!ObjectUtils.equals(higherLimit, higher)) {
				higherLimit = higher;

				// inform listener
				int index = HistogramModel.NO_LIMIT;
				if (higherLimit != null) {
					index = higherLimit.ordinal();
				}
				HistogramModelPath path = new HistogramModelPath(dimNumber, 0, index);
				HistogramModelEvent event = new HistogramModelEvent(null, path, false);
				fireLimitChangedEvent(event);
			}

			// update lower filter
			if (!ObjectUtils.equals(lowerLimit, lower)) {
				lowerLimit = lower;

				// inform listener
				int index = HistogramModel.NO_LIMIT;
				if (higherLimit != null) {
					index = higherLimit.ordinal();
				}
				HistogramModelPath path = new HistogramModelPath(dimNumber, 0, index);
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
				ImageRating rating = element.getRating();

				// avoid null values
				if (rating == null) {
					continue;
				}

				// increment value
				int index = rating.ordinal();
				values[index]++;

				// update max
				max = Math.max(max, values[index]);

				// send information
				paths.add(new HistogramModelPath(dimNumber, 0, rating.ordinal()));
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
				ImageRating rating = element.getRating();

				// avoid null values
				if (rating == null) {
					continue;
				}

				// decrement value
				int index = rating.ordinal();
				values[index]--;

				// update maximum
				if ((values[index] + 1) == max) {
					max = values[0];
					for (int i = 1; i < values.length; i++) {
						if (max < values[i]) {
							max = values[i];
						}
					}
				}

				// send information
				paths.add(new HistogramModelPath(dimNumber, 0, rating.ordinal()));
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

			// list of changes
			Set<HistogramModelPath> paths = new HashSet<HistogramModelPath>();

			for (BeanChangeEvent<Image> event : events) {
				if (event.getProperty() == BeanProperty.IMAGE_RATING) {
					// get rating
					ImageRating oldRating = (ImageRating) event.getOldValue();
					ImageRating newRating = (ImageRating) event.getNewValue();

					int oldIndex = -1;
					int newIndex = -1;

					// avoid null values
					if (newRating != null) {
						// extract index
						oldIndex = newRating.ordinal();

						values[oldIndex]++;
						max = Math.max(max, values[oldIndex]);
					}

					if (oldRating != null) {
						// extract index
						newIndex = oldRating.ordinal();

						values[newIndex]--;

						// update maximum
						if ((values[newIndex] + 1) == max) {
							max = values[0];
							for (int i = 1; i < values.length; i++) {
								if (max < values[i]) {
									max = values[i];
								}
							}
						}
					}

					// add change information
					if (oldIndex != -1) {
						paths.add(new HistogramModelPath(dimNumber, 0, oldIndex));
					}
					if (newIndex != -1) {
						paths.add(new HistogramModelPath(dimNumber, 0, newIndex));
					}
				}
			}

			// send change event
			HistogramModelEvent modelEvent = new HistogramModelEvent(getHistogram(), paths);
			fireValueChangedEvent(modelEvent);
		}

	}
}
