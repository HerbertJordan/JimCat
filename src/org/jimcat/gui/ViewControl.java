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

package org.jimcat.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.DefaultListSelectionModel;
import javax.swing.ListSelectionModel;

import org.jimcat.model.Image;
import org.jimcat.model.SmartList;
import org.jimcat.model.comparator.AlbumOrderComparator;
import org.jimcat.model.comparator.ComparatorChainProxy;
import org.jimcat.model.comparator.DateAddedComparator;
import org.jimcat.model.comparator.DuplicateComparator;
import org.jimcat.model.comparator.NullComparator;
import org.jimcat.model.comparator.TitleComparator;
import org.jimcat.model.filter.AlbumFilter;
import org.jimcat.model.filter.DuplicateFilter;
import org.jimcat.model.filter.Filter;
import org.jimcat.model.filter.ImportFilter;
import org.jimcat.model.filter.RatingFilter;
import org.jimcat.model.filter.SmartListFilter;
import org.jimcat.model.filter.TagFilter;
import org.jimcat.model.filter.ImportFilter.Type;
import org.jimcat.model.filter.logical.AndFilter;
import org.jimcat.model.filter.logical.NotFilter;
import org.jimcat.model.filter.logical.OrFilter;
import org.jimcat.model.filter.metadata.MegaPixelFilter;
import org.jimcat.model.filter.metadata.PictureTakenFilter;
import org.jimcat.model.filter.metadata.TextFilter;
import org.jimcat.model.libraries.ImageLibrary;
import org.jimcat.model.libraries.LibraryView;
import org.jimcat.model.notification.BeanChangeEvent;
import org.jimcat.model.notification.BeanListener;
import org.jimcat.model.notification.BeanProperty;
import org.jimcat.model.notification.CollectionListener;

/**
 * This class represents the central view control of the JimCat GUI client.
 * 
 * It manages the current filter, the associated LibraryView and their changes.
 * 
 * $Id: ViewControl.java 999 2007-09-14 20:02:58Z cleiter $
 * 
 * @author Herbert
 */
public class ViewControl {

	/**
	 * the default sorting order
	 */
	private static final Comparator<Image> DEFAULT_SORTING;

	static {
		ComparatorChainProxy<Image> chain = new ComparatorChainProxy<Image>();
		chain.addComparator(new DateAddedComparator(), true);
		chain.addComparator(new TitleComparator());
		DEFAULT_SORTING = chain;
	}

	/**
	 * a list of filter listeners
	 */
	private List<ViewFilterListener> listeners = new CopyOnWriteArrayList<ViewFilterListener>();

	/**
	 * a list of sorting listeners
	 */
	private List<ViewSortingListener> sortingListeners = new CopyOnWriteArrayList<ViewSortingListener>();

	/**
	 * a list of freeze lock listeners
	 */
	private List<ViewFreezeListener> freezeListeners = new CopyOnWriteArrayList<ViewFreezeListener>();

	/**
	 * the view managed by this class
	 */
	private LibraryView libraryView;

	/**
	 * the selectionmodel used to determine currently selected elements
	 */
	private ListSelectionModel selectionModel;

	/**
	 * the current mode of tag combination
	 */
	private TagCombineMode combineMode = TagCombineMode.ALL;

	/**
	 * is the tag filter negated?
	 */
	private boolean negateTagFilter = false;

	/**
	 * a list of TagFilter included in this Filter
	 */
	private List<TagFilter> tagFilter = new LinkedList<TagFilter>();

	/**
	 * a smartlist included in current Filter
	 */
	private SmartList smartList;

	/**
	 * the observer reacting on smartlist filter changes
	 */
	private SmartListObserver smartListObserver = new SmartListObserver();

	/**
	 * an albumfilter included in current filter
	 */
	private AlbumFilter albumFilter;

	/**
	 * a textFilter included in current filter
	 */
	private TextFilter textFilter;

	/**
	 * the picture taken filter (after) in current filter
	 */
	private PictureTakenFilter pictureTakenAfter;

	/**
	 * the picture taken filter (after) in current filter
	 */
	private PictureTakenFilter pictureTakenBefore;

	/**
	 * the lower rating filter in current filter
	 */
	private RatingFilter lowerRatingFilter;

	/**
	 * the higher rating filter
	 */
	private RatingFilter higherRatingFilter;

	/**
	 * should a duplicate filter be included?
	 */
	private boolean filterDuplicates;

	/**
	 * the lower import id limit set
	 */
	private long lowerImportLimit = -1;

	/**
	 * the uper import id limit set
	 */
	private long higherImportLimit = -1;

	/**
	 * the lower megapixel limit
	 */
	private float lowerMegaPixelLimit = -1;

	/**
	 * the higher megapixel limit
	 */
	private float higherMegaPixelLimit = -1;

	/**
	 * construct for this ViewControl
	 */
	public ViewControl() {
		libraryView = new LibraryView(ImageLibrary.getInstance(), buildFilter(), DEFAULT_SORTING);
		selectionModel = new DefaultListSelectionModel();

		// install view observer
		libraryView.addListener(new ViewObserver());
	}

	/**
	 * adds a new ViewFilterListener to this controler
	 * 
	 * @param listener
	 */
	public void addViewFilterListener(ViewFilterListener listener) {
		listeners.add(listener);
	}

	/**
	 * removes a ViewFilterListener from this controler
	 * 
	 * @param listener
	 */
	public void removeViewFilterListener(ViewFilterListener listener) {
		listeners.remove(listener);
	}

	/**
	 * adds a new ViewSortingListener to this controler
	 * 
	 * @param listener
	 */
	public void addViewSortingListener(ViewSortingListener listener) {
		sortingListeners.add(listener);
	}

	/**
	 * removes a ViewSortingListener from this controler
	 * 
	 * @param listener
	 */
	public void removeViewSortingListener(ViewSortingListener listener) {
		sortingListeners.remove(listener);
	}

	/**
	 * adds a new ViewFreezeListener to this controler
	 * 
	 * @param listener
	 */
	public void addViewFreezeListener(ViewFreezeListener listener) {
		freezeListeners.add(listener);
	}

	/**
	 * removes a ViewFreezeListener from this controler
	 * 
	 * @param listener
	 */
	public void removeViewFreezeListener(ViewFreezeListener listener) {
		freezeListeners.remove(listener);
	}

	/**
	 * @return the libraryView
	 */
	public LibraryView getLibraryView() {
		return libraryView;
	}

	/**
	 * get the selection model for this view control
	 * 
	 * @return the selection model for this view control
	 */
	public ListSelectionModel getSelectionModel() {
		return selectionModel;
	}

	/**
	 * the current selection is cleared
	 */
	public void clearSelection() {
		selectionModel.clearSelection();
	}

	/**
	 * 
	 * get a set of Images currently selected
	 * 
	 * @return a set of Images currently selected
	 */
	public List<Image> getSelectedImages() {
		// build up result
		List<Image> result = new ArrayList<Image>();

		// shortcut if there is no selection
		if (selectionModel.isSelectionEmpty()) {
			return result;
		}

		// build up result set
		for (int i = selectionModel.getMinSelectionIndex(); i <= selectionModel.getMaxSelectionIndex(); i++) {
			if (selectionModel.isSelectedIndex(i)) {
				result.add(libraryView.getImage(i));
			}
		}

		// resultset compleat
		return result;
	}

	/**
	 * @return the current comperator
	 */
	public Comparator<Image> getSorting() {
		return libraryView.getComparator();
	}

	/**
	 * update current sorting order
	 * 
	 * @param comparator
	 */
	public void setSorting(Comparator<Image> comparator) {

		Comparator<Image> newOne = comparator;

		// check comparator and modify in special cases
		if (albumFilter != null) {
			// if it is an AlbumOrderComparator, revert order
			if (newOne instanceof AlbumOrderComparator) {
				AlbumOrderComparator tmp = (AlbumOrderComparator) newOne;
				tmp.setAlbum(albumFilter.getAlbum());
			} else if (newOne instanceof NullComparator) {
				newOne = new AlbumOrderComparator(albumFilter.getAlbum());
			}
		} else {
			// if it is an AlbumOrderComparator => exchange with NullComparator
			// (there is no album)
			if (newOne instanceof AlbumOrderComparator) {
				newOne = new NullComparator();
			}
		}

		Comparator<Image> current = libraryView.getComparator();
		libraryView.setComparator(newOne);
		if (!current.equals(newOne)) {
			fireSortingChanged();
		}
	}

	/**
	 * inform listeners about updated comparator
	 */
	private void fireSortingChanged() {
		// inform listeners
		for (ViewSortingListener listener : sortingListeners) {
			listener.sortingChanged(this);
		}
	}

	/**
	 * is this library view currently frozen?
	 * 
	 * @return true only if the view is frozen at the moment
	 */
	public boolean isFreezed() {
		return libraryView.isFreezed();
	}

	/**
	 * update freeze state
	 * 
	 * @param freeze
	 */
	public void setFreezeLock(boolean freeze) {
		if (libraryView.isFreezed() != freeze) {
			libraryView.setFreezed(freeze);
			// infrom listener
			for (ViewFreezeListener listener : freezeListeners) {
				listener.freezeStateChanged(this);
			}
		}
	}

	/**
	 * @return the albumFilter
	 */
	public AlbumFilter getAlbumFilter() {
		return albumFilter;
	}

	/**
	 * Sets the current album. This action includes a filter clearing.
	 * 
	 * @param albumFilter
	 *            the albumFilter to set
	 */
	public void setAlbumFilter(AlbumFilter albumFilter) {
		clearFilterInternal();
		this.albumFilter = albumFilter;
		// force right order
		updateFilterAndSorting(new AlbumOrderComparator(albumFilter.getAlbum()));
		fireSortingChanged();
	}

	/**
	 * @return the lowerImportLimit
	 */
	public long getLowerImportLimit() {
		return lowerImportLimit;
	}

	/**
	 * @param lowerImportLimit
	 *            the lowerImportLimit to set
	 */
	public void setLowerImportLimit(long lowerImportLimit) {
		if (this.lowerImportLimit == lowerImportLimit) {
			return;
		}
		this.lowerImportLimit = lowerImportLimit;
		updateFilterOnView();
	}

	/**
	 * @return the uperImportLimit
	 */
	public long getHigherImportLimit() {
		return higherImportLimit;
	}

	/**
	 * @param uperImportLimit
	 *            the uperImportLimit to set
	 */
	public void setHigherImportLimit(long uperImportLimit) {
		if (this.higherImportLimit == uperImportLimit) {
			return;
		}
		this.higherImportLimit = uperImportLimit;
		updateFilterOnView();
	}

	/**
	 * use this methode to establish an exact import id filter
	 * 
	 * @param id
	 */
	public void setImportIdFilter(long id) {
		this.higherImportLimit = id + 1;
		this.lowerImportLimit = id;
		updateFilterOnView();
	}

	/**
	 * @return the higherMegaPixelLimit
	 */
	public float getHigherMegaPixelLimit() {
		return higherMegaPixelLimit;
	}

	/**
	 * @param higherMegaPixelLimit
	 *            the higherMegaPixelLimit to set
	 */
	public void setHigherMegaPixelLimit(float higherMegaPixelLimit) {
		if (this.higherMegaPixelLimit == higherMegaPixelLimit) {
			return;
		}
		this.higherMegaPixelLimit = higherMegaPixelLimit;
		updateFilterOnView();
	}

	/**
	 * @return the lowerMegaPixelLimit
	 */
	public float getLowerMegaPixelLimit() {
		return lowerMegaPixelLimit;
	}

	/**
	 * @param lowerMegaPixelLimit
	 *            the lowerMegaPixelLimit to set
	 */
	public void setLowerMegaPixelLimit(float lowerMegaPixelLimit) {
		if (this.lowerMegaPixelLimit == lowerMegaPixelLimit) {
			return;
		}
		this.lowerMegaPixelLimit = lowerMegaPixelLimit;
		updateFilterOnView();
	}

	/**
	 * @return the smartList
	 */
	public SmartList getSmartList() {
		return smartList;
	}

	/**
	 * Sets the Smartlist to see this will remove the album and import filter.
	 * 
	 * @param smartList
	 *            the smartList to set
	 */
	public void setSmartList(SmartList smartList) {
		clearFilterInternal();
		// unregister from old
		if (this.smartList != null) {
			this.smartList.removeListener(smartListObserver);
		}
		// exchange
		this.smartList = smartList;
		// register to new
		if (this.smartList != null) {
			this.smartList.addListener(smartListObserver);
		}
		// update filter
		updateFilterOnView();
	}

	/**
	 * @return the textFilter
	 */
	public TextFilter getTextFilter() {
		return textFilter;
	}

	/**
	 * @param textFilter
	 *            the textFilter to set
	 */
	public void setTextFilter(TextFilter textFilter) {
		this.textFilter = textFilter;
		updateFilterOnView();
	}

	/**
	 * @return the pictureTakenAfter
	 */
	public PictureTakenFilter getPictureTakenAfter() {
		return pictureTakenAfter;
	}

	/**
	 * @param pictureTakenAfter
	 *            the pictureTakenAfter to set
	 */
	public void setPictureTakenAfter(PictureTakenFilter pictureTakenAfter) {
		this.pictureTakenAfter = pictureTakenAfter;
		updateFilterOnView();
	}

	/**
	 * @return the pictureTakenBefore
	 */
	public PictureTakenFilter getPictureTakenBefore() {
		return pictureTakenBefore;
	}

	/**
	 * @param pictureTakenBefore
	 *            the pictureTakenBefore to set
	 */
	public void setPictureTakenBefore(PictureTakenFilter pictureTakenBefore) {
		this.pictureTakenBefore = pictureTakenBefore;
		updateFilterOnView();
	}

	/**
	 * @return the higherRatingFilter
	 */
	public RatingFilter getHigherRatingFilter() {
		return higherRatingFilter;
	}

	/**
	 * @param higherRatingFilter
	 *            the higherRatingFilter to set
	 */
	public void setHigherRatingFilter(RatingFilter higherRatingFilter) {
		this.higherRatingFilter = higherRatingFilter;
		updateFilterOnView();
	}

	/**
	 * @return the lowerRatingFilter
	 */
	public RatingFilter getLowerRatingFilter() {
		return lowerRatingFilter;
	}

	/**
	 * @param lowerRatingFilter
	 *            the lowerRatingFilter to set
	 */
	public void setLowerRatingFilter(RatingFilter lowerRatingFilter) {
		this.lowerRatingFilter = lowerRatingFilter;
		updateFilterOnView();
	}

	/**
	 * @return the combineMode
	 */
	public TagCombineMode getCombineMode() {
		return combineMode;
	}

	/**
	 * @param combineMode
	 *            the combineMode to set
	 */
	public void setCombineMode(TagCombineMode combineMode) {
		TagCombineMode oldValue = this.combineMode;
		this.combineMode = combineMode;
		if (oldValue != combineMode) {
			updateFilterOnView();
		}
	}

	/**
	 * @return the negateTagFilter
	 */
	public boolean isNegateTagFilter() {
		return negateTagFilter;
	}

	/**
	 * @param negateTagFilter
	 *            the negateTagFilter to set
	 */
	public void setNegateTagFilter(boolean negateTagFilter) {
		boolean oldValue = this.negateTagFilter;
		this.negateTagFilter = negateTagFilter;
		if (oldValue != negateTagFilter) {
			updateFilterOnView();
		}
	}

	/**
	 * @return the useDuplicateFilter
	 */
	public boolean isFilterDuplicates() {
		return filterDuplicates;
	}

	/**
	 * @param useDuplicateFilter
	 *            the useDuplicateFilter to set
	 */
	public void setFilterDuplicates(boolean useDuplicateFilter) {
		boolean oldValue = this.filterDuplicates;
		this.filterDuplicates = useDuplicateFilter;
		if (oldValue != filterDuplicates) {
			if (filterDuplicates) {
				// change sorting so equal images will be next to each other
				libraryView.setComparator(new DuplicateComparator());
			}
			updateFilterOnView();
		}
	}

	/**
	 * appends a new tagFilter
	 * 
	 * @param filter
	 */
	public void addTagFilter(TagFilter filter) {
		if (!tagFilter.contains(filter)) {
			tagFilter.add(filter);
			updateFilterOnView();
		}
	}

	/**
	 * remoes a tagFilter from the current filterSetup
	 * 
	 * @param filter
	 */
	public void removeTagFilter(TagFilter filter) {
		if (tagFilter.contains(filter)) {
			tagFilter.remove(filter);
			updateFilterOnView();
		}
	}

	/**
	 * get a list of currently enabled TagFilter
	 * 
	 * @return a list of current tag filter
	 */
	public List<TagFilter> getTagFilterList() {
		return Collections.unmodifiableList(tagFilter);
	}

	/**
	 * this will clear the current filtersetting followed by a filter update
	 */
	public void clearFilter() {
		clearFilterInternal();
		updateFilterAndSorting(DEFAULT_SORTING);
		fireSortingChanged();
	}

	/**
	 * check if all images are visible
	 * 
	 * @return if all images are visible at the moment
	 */
	public boolean allVisible() {
		return buildFilter() == null;
	}

	/**
	 * this will determine if reordering is possible
	 * 
	 * @return true if manual reordering is possible
	 */
	public boolean isReorderingPossible() {
		// there is no sorting allowed
		// except AlbumOrderComparator
		Comparator<Image> comparator = getSorting();
		if (!(comparator instanceof AlbumOrderComparator)) {
			return false;
		}

		// there must be an album filter installed
		if (albumFilter == null) {
			return false;
		}

		// there is no other filtering allowed
		if (tagFilter.size() > 0) {
			return false;
		}
		if (smartList != null) {
			return false;
		}
		if (textFilter != null) {
			return false;
		}
		if (pictureTakenAfter != null) {
			return false;
		}
		if (pictureTakenBefore != null) {
			return false;
		}
		if (lowerRatingFilter != null) {
			return false;
		}
		if (higherRatingFilter != null) {
			return false;
		}
		if (filterDuplicates) {
			return false;
		}
		if (higherImportLimit != -1 || lowerImportLimit != -1) {
			return false;
		}
		if (higherMegaPixelLimit != -1 || lowerImportLimit != -1) {
			return false;
		}
		// in this case, reordering is allowed
		return true;
	}

	/**
	 * clear filter - without update
	 */
	private void clearFilterInternal() {
		tagFilter = new LinkedList<TagFilter>();
		smartList = null;
		albumFilter = null;
		textFilter = null;
		pictureTakenAfter = null;
		pictureTakenBefore = null;
		lowerRatingFilter = null;
		higherRatingFilter = null;
		higherImportLimit = -1;
		lowerImportLimit = -1;
		higherMegaPixelLimit = -1;
		lowerMegaPixelLimit = -1;
		filterDuplicates = false;
		negateTagFilter = false;
	}

	/**
	 * this methode will update the Filter within the libraryView
	 */
	private void updateFilterOnView() {
		updateFilterAndSorting(libraryView.getComparator());
	}

	/**
	 * this methode will update the Filter within the libraryView
	 * 
	 * @param newComparator
	 */
	private void updateFilterAndSorting(Comparator<Image> newComparator) {
		Filter filter = buildFilter();
		setFreezeLock(false);
		libraryView.updateView(filter, newComparator);
		notifyListeners();
	}

	/**
	 * internal methode to build filter hierarchie
	 * 
	 * @return the root of the internal filter hierarchie
	 */
	public Filter buildFilter() {
		// Build up Filter
		Filter result = null;

		// start with albumFilter
		result = AndFilter.create(albumFilter, result);

		// import Filter
		result = AndFilter.create(buildImportFilter(), result);

		// smartListFilter
		if (smartList != null) {
			result = AndFilter.create(new SmartListFilter(smartList), result);
		}

		// textFilter
		result = AndFilter.create(textFilter, result);

		// after filter
		result = AndFilter.create(pictureTakenAfter, result);

		// before filter
		result = AndFilter.create(pictureTakenBefore, result);

		// lower rating filter
		result = AndFilter.create(lowerRatingFilter, result);

		// higher rating filter
		result = AndFilter.create(higherRatingFilter, result);

		// higher megapixel filter
		if (higherMegaPixelLimit != -1) {
			Filter filter = new MegaPixelFilter(org.jimcat.model.filter.metadata.MegaPixelFilter.Type.SMALLER_THEN,
			        higherMegaPixelLimit);
			result = AndFilter.create(filter, result);
		}

		// higher megapixel filter
		if (lowerMegaPixelLimit != -1) {
			Filter filter = new MegaPixelFilter(org.jimcat.model.filter.metadata.MegaPixelFilter.Type.BIGGER_OR_EQUAL,
			        lowerMegaPixelLimit);
			result = AndFilter.create(filter, result);
		}

		// duplicate Filter
		if (filterDuplicates) {
			result = AndFilter.create(new DuplicateFilter(), result);
		}

		// add tag filter
		if (tagFilter.size() > 0) {
			result = AndFilter.create(buildTagFilter(), result);
		}

		return result;
	}

	/**
	 * build tag filter filter part
	 * 
	 * @return the root of the tag filter tree
	 */
	public Filter buildTagFilter() {
		Filter result = null;

		// neutral element for OR
		if (combineMode == TagCombineMode.ANY) {
			result = new NotFilter(result);
		}

		// tag filter
		for (TagFilter filter : tagFilter) {
			if (combineMode == TagCombineMode.ALL) {
				result = AndFilter.create(filter, result);
			} else {
				result = OrFilter.create(filter, result);
			}
		}

		// negate filter if requested
		if (negateTagFilter) {
			result = new NotFilter(result);
		}
		return result;
	}

	/**
	 * build up import filter combinations
	 * 
	 * @return the root of the import filter tree
	 */
	private Filter buildImportFilter() {
		// case 1) no filter
		if (higherImportLimit == -1 && lowerImportLimit == -1) {
			return null;
		}
		// case 2) exact filter
		if (higherImportLimit == lowerImportLimit + 1) {
			return new ImportFilter(Type.EXACT, lowerImportLimit);
		}
		Filter upper = null;
		if (higherImportLimit != -1) {
			upper = new ImportFilter(Type.UP_TO, higherImportLimit);
		}
		Filter lower = null;
		if (lowerImportLimit != -1) {
			lower = new ImportFilter(Type.AT_LEAST, lowerImportLimit);
		}
		// return and conection
		return AndFilter.create(upper, lower);
	}

	/**
	 * inform listeners about changes
	 */
	private void notifyListeners() {
		for (ViewFilterListener listener : listeners) {
			listener.filterChanges(this);
		}
	}

	/**
	 * react on smartlist changes (of current smartlist)
	 */
	private class SmartListObserver implements BeanListener<SmartList> {

		/**
		 * react on a changed filter
		 * 
		 * @see org.jimcat.model.notification.BeanListener#beanPropertyChanged(org.jimcat.model.notification.BeanChangeEvent)
		 */
		public void beanPropertyChanged(BeanChangeEvent<SmartList> event) {
			SmartList list = event.getSource();
			if (list == smartList) {
				if (event.getProperty() == BeanProperty.SMARTLIST_FILTER) {
					updateFilterOnView();
				}
			} else {
				list.removeListener(this);
			}
		}
	}

	/**
	 * a listener observing the libraryview - on changes, the selection will be
	 * cleared.
	 */
	private class ViewObserver implements CollectionListener<Image, LibraryView> {

		/**
		 * react -> clearSelection
		 * 
		 * @param collection
		 * 
		 * @see org.jimcat.model.notification.CollectionListener#basementChanged(org.jimcat.model.notification.ObservableCollection)
		 */
		@SuppressWarnings("unused")
		public void basementChanged(LibraryView collection) {
			clearSelection();
		}

		/**
		 * react -> clearSelection
		 * 
		 * @param collection
		 * @param elements
		 * 
		 * @see org.jimcat.model.notification.CollectionListener#elementsAdded(org.jimcat.model.notification.ObservableCollection,
		 *      java.util.Set)
		 */
		@SuppressWarnings("unused")
		public void elementsAdded(LibraryView collection, Set<Image> elements) {
			if (collection.getComparator() != DEFAULT_SORTING) {
				clearSelection();
			}
		}

		/**
		 * react -> clearSelection
		 * 
		 * @param collection
		 * @param elements
		 * 
		 * @see org.jimcat.model.notification.CollectionListener#elementsRemoved(org.jimcat.model.notification.ObservableCollection,
		 *      java.util.Set)
		 */
		@SuppressWarnings("unused")
		public void elementsRemoved(LibraryView collection, Set<Image> elements) {
			clearSelection();
		}

		/**
		 * react -> do nothing
		 * 
		 * @param collection
		 * @param events
		 * 
		 * @see org.jimcat.model.notification.CollectionListener#elementsUpdated(org.jimcat.model.notification.ObservableCollection,
		 *      java.util.List)
		 */
		@SuppressWarnings("unused")
		public void elementsUpdated(LibraryView collection, List<BeanChangeEvent<Image>> events) {
			// nothing to do
		}

	}
}
