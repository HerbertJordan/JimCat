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
import org.jimcat.model.ImageMetadata;
import org.jimcat.model.libraries.ImageLibrary;
import org.jimcat.model.notification.BeanChangeEvent;
import org.jimcat.model.notification.BeanProperty;
import org.jimcat.model.notification.CollectionListener;

/**
 * A dimension representing a histogram showing pixel number spreading.
 * 
 * The scale is fixed to the following categories:
 * 
 * <0.05 <0.1 <0.5 <1 <2 <3 ... <11 <12 >=12
 * 
 * (Megapixel)
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class MegaPixelDimension extends Dimension {

	/**
	 * a reference to the viewControl used by the client
	 */
	private ViewControl viewControl;

	/**
	 * the mega pixel distributions
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
	 * Create a MegaPixelDimension for the given histogram, ViewControl,
	 * ImageControl and with the given dimensionNumber
	 * 
	 * @param histogram
	 * @param viewCtrl
	 * @param imageCtrl
	 * @param dimensionNumber
	 */
	public MegaPixelDimension(ImageHistogram histogram, ViewControl viewCtrl, ImageControl imageCtrl,
	        int dimensionNumber) {
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
		values = new int[16];

		// count ratings
		for (Image img : images) {
			float mp = getMPFromImage(img);
			values[toIndex(mp)]++;
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
	 * there is only one resolutions => return given index
	 * 
	 * @see org.jimcat.gui.histogram.image.Dimension#convertIndex(int, int, int)
	 */
	@Override
	@SuppressWarnings("unused")
	public int convertIndex(int fromResolution, int toResolution, int index) {
		return index;
	}

	/**
	 * returns static number of categories
	 * 
	 * @see org.jimcat.gui.histogram.image.Dimension#getBucketCount(int)
	 */
	@Override
	@SuppressWarnings("unused")
	public int getBucketCount(int resolution) {
		return values.length;
	}

	/**
	 * there is just one resolution => returns 1
	 * 
	 * @see org.jimcat.gui.histogram.image.Dimension#getCountResolutions()
	 */
	@Override
	@SuppressWarnings("unused")
	public int getCountResolutions() {
		return 1;
	}

	/**
	 * get higher limit index
	 * 
	 * @see org.jimcat.gui.histogram.image.Dimension#getHigherLimiter(int)
	 */
	@Override
	@SuppressWarnings("unused")
	public int getHigherLimiter(int resolution) {
		return higherLimit;
	}

	/**
	 * initally the view should be centered => returns getBucketCount/2
	 * 
	 * @see org.jimcat.gui.histogram.image.Dimension#getInitialIndex()
	 */
	@Override
	public int getInitialIndex() {
		return values.length / 2;
	}

	/**
	 * returns a label for
	 * 
	 * @see org.jimcat.gui.histogram.image.Dimension#getLabelForMark(int, int)
	 */
	@Override
	@SuppressWarnings("unused")
	public String getLabelForMark(int resolution, int index) {
		if (index < values.length - 1) {
			float value = toMegaPixel(index + 1);
			if (value == (int) value) {
				return "<" + (int) value;
			}
			return "<" + value;
		}
		return ">=12";
	}

	/**
	 * @see org.jimcat.gui.histogram.image.Dimension#getLowerLimiter(int)
	 */
	@Override
	@SuppressWarnings("unused")
	public int getLowerLimiter(int resolution) {
		return lowerLimit;
	}

	/**
	 * every mark is labeled with mp size
	 * 
	 * @see org.jimcat.gui.histogram.image.Dimension#getMarkFor(int, int)
	 */
	@Override
	@SuppressWarnings("unused")
	public ScaleMark getMarkFor(int resolution, int index) {
		if (index == 0 || index == 3 || index == values.length - 1) {
			return ScaleMark.LABEL;
		}
		if (index > 3 && (index - 2) % 5 == 0) {
			return ScaleMark.LABEL;
		}

		return ScaleMark.SMALL;
	}

	/**
	 * the name of this dimension
	 * 
	 * @see org.jimcat.gui.histogram.image.Dimension#getName()
	 */
	@Override
	public String getName() {
		return "Megapixel";
	}

	/**
	 * => no resolutions, returns null
	 * 
	 * @see org.jimcat.gui.histogram.image.Dimension#getNameFor(int)
	 */
	@Override
	@SuppressWarnings("unused")
	public String getNameFor(int resolution) {
		return null;
	}

	/**
	 * read value at given position
	 * 
	 * @see org.jimcat.gui.histogram.image.Dimension#getValueAt(int, int)
	 */
	@Override
	@SuppressWarnings("unused")
	public float getValueAt(int resolution, int index) throws IllegalArgumentException {
		return values[index] / (float) max;
	}

	/**
	 * read absoulte value at given position
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
	 * update higher limt. This will change the current view filter
	 * 
	 * @see org.jimcat.gui.histogram.image.Dimension#setHigherLimiter(int, int)
	 */
	@Override
	@SuppressWarnings("unused")
	public void setHigherLimiter(int resolution, int index) throws IllegalArgumentException {
		// shourtcut
		int newIndex = Math.min(index, values.length);
		// a limit at maximum value isn't usefull
		if (newIndex == values.length) {
			newIndex = HistogramModel.NO_LIMIT;
		}
		if (higherLimit == newIndex) {
			return;
		}
		higherLimit = newIndex;

		if (higherLimit == HistogramModel.NO_LIMIT) {
			viewControl.setHigherMegaPixelLimit(-1);
		} else {
			viewControl.setHigherMegaPixelLimit(toMegaPixel(higherLimit));
		}
	}

	/**
	 * update lower limit. This will change the current view filter
	 * 
	 * @see org.jimcat.gui.histogram.image.Dimension#setLowerLimiter(int, int)
	 */
	@Override
	@SuppressWarnings("unused")
	public void setLowerLimiter(int resolution, int index) throws IllegalArgumentException {
		int newIndex = index;
		if (newIndex == 0) {
			newIndex = HistogramModel.NO_LIMIT;
		}
		// shourtcut
		if (lowerLimit == newIndex) {
			return;
		}
		// a lower limit of 0 isn't usefull
		lowerLimit = newIndex;

		if (lowerLimit == HistogramModel.NO_LIMIT) {
			viewControl.setLowerMegaPixelLimit(-1);
		} else {
			viewControl.setLowerMegaPixelLimit(toMegaPixel(lowerLimit));
		}
	}

	/**
	 * get the number of megapixel from the given image
	 * 
	 * @param img
	 * @return the megapixel of the given image
	 */
	private float getMPFromImage(Image img) {
		return getMPFromMetadate(img.getMetadata());
	}

	/**
	 * extract megapixel value from given metadata
	 * 
	 * @param metadata
	 * @return the megapixel value for the given metadata
	 */
	private float getMPFromMetadate(ImageMetadata metadata) {
		int width = metadata.getWidth();
		int height = metadata.getHeight();
		return (width * height) / 1000000f;
	}

	/**
	 * converts the given amount of megapixels int an supported index
	 * 
	 * @param mp
	 * @return the index for the given megapixels
	 */
	private int toIndex(float mp) {
		// <0.05 <0.1 <0.5 <1 <2 <3 ... <11 <12 >=12
		if (mp < 0.05) {
			return 0;
		}
		if (mp < 0.1) {
			return 1;
		}
		if (mp < 0.5) {
			return 2;
		}
		// rest can be calculated ...
		int index = 3 + (int) mp;

		// keep uper limit in mind
		return Math.min(values.length - 1, index);
	}

	/**
	 * converts the given index into an appropriate mp value (the lower end of
	 * the given intervall)
	 * 
	 * @param index
	 * @return the megapixels for the given index
	 */
	private float toMegaPixel(int index) {
		switch (index) {
		case 0:
			return 0;
		case 1:
			return 0.05f;
		case 2:
			return 0.1f;
		case 3:
			return 0.5f;
		default:
			return index - 3;
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
			float lowerValue = control.getLowerMegaPixelLimit();
			float higherValue = control.getHigherMegaPixelLimit();

			long lowerId = HistogramModel.NO_LIMIT;
			if (lowerValue != -1) {
				lowerId = toIndex(lowerValue);
			}

			long higherId = HistogramModel.NO_LIMIT;
			if (higherValue != -1) {
				higherId = toIndex(higherValue);
			}

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
				int index = toIndex(getMPFromImage(element));

				// increment value
				values[index]++;

				// update max
				max = Math.max(max, values[index]);

				// send information
				paths.add(new HistogramModelPath(dimNumber, 0, index));
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
				int index = toIndex(getMPFromImage(element));

				// decrement value
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
				paths.add(new HistogramModelPath(dimNumber, 0, index));
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
			// set of changes
			Set<HistogramModelPath> paths = new HashSet<HistogramModelPath>();

			// work through events
			for (BeanChangeEvent<Image> event : events) {
				// check if its right modification
				if (event.getProperty() != BeanProperty.IMAGE_METADATA) {
					continue;
				}

				int newIndex = toIndex(getMPFromMetadate((ImageMetadata) event.getNewValue()));
				int oldIndex = toIndex(getMPFromMetadate((ImageMetadata) event.getOldValue()));

				if (newIndex != oldIndex) {
					values[oldIndex]--;
					values[newIndex]++;

					paths.add(new HistogramModelPath(dimNumber, 0, oldIndex));
					paths.add(new HistogramModelPath(dimNumber, 0, newIndex));
				}
			}

			if (paths.size() == 0) {
				// no changes
				return;
			}

			// find new maximum
			max = values[0];
			for (int i = 1; i < values.length; i++) {
				if (max < values[i]) {
					max = values[i];
				}
			}

			// fire event
			HistogramModelEvent event = new HistogramModelEvent(getHistogram(), paths);
			fireValueChangedEvent(event);
		}
	}
}
