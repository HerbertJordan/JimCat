package org.jimcat.model.libraries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jimcat.model.Image;
import org.jimcat.model.comparator.NullComparator;
import org.jimcat.model.filter.Filter;
import org.jimcat.model.notification.BeanChangeEvent;
import org.jimcat.model.notification.CollectionListener;
import org.jimcat.model.notification.CollectionListenerManager;
import org.jimcat.model.notification.ObservableCollection;

/**
 * A view of the ImageLibrary. Images are filtered and sorted.
 * 
 * You can create a new instance of this class, it will contain all images that
 * are currently in the library. You can set filters and sort the images using a
 * comparator. Operations like add and remove are not supported because an
 * ImageCollection is just a view, use ImageLibrary.
 * 
 * 
 * $Id: LibraryView.java 942 2007-06-16 09:07:47Z 07g1t1u3 $
 * 
 * @author Christoph
 */
public class LibraryView implements ObservableCollection<Image, LibraryView>, CollectionListener<Image, ImageLibrary> {

	/**
	 * the current content of this view (Hash)
	 */
	private HashSet<Image> content;

	/**
	 * the matched images as array, correct sorting
	 */
	private Image contentArray[] = new Image[0];

	/**
	 * used to determine if contentArray is dirty
	 */
	private boolean arrayDirty = true;

	/**
	 * the collection listener manager used by this view (which is just another
	 * kind of collection)
	 */
	private CollectionListenerManager<Image, LibraryView> listenerManager;

	/**
	 * currently installed fitler null = no filter, all images contained
	 */
	private Filter filter;

	/**
	 * a flag indicating that filtering is currently in process
	 */
	private boolean isFiltering;

	/**
	 * a comparator sorting current content
	 * 
	 * null - random order
	 */
	private SaveComparator comparator;

	/**
	 * the library used to get content
	 */
	private ImageLibrary library;

	/**
	 * if this value is true, no bean changes will be respected any more.
	 * 
	 * if it is reset to false, all changes will take effect at once.
	 */
	private boolean freezed = false;

	/**
	 * a list of changes happening while state is freezed
	 */
	private List<BeanChangeEvent<Image>> holdBackChanges;

	/**
	 * small constructor creating an unfiltered, unsorted list fed by the given
	 * library
	 * 
	 * @param library
	 */
	public LibraryView(ImageLibrary library) {
		this(library, null, new NullComparator());
	}

	/**
	 * more sophisticated constructor to configure new view. Use it this one if
	 * you would like to create a view containing only a view elements
	 * 
	 * @param library -
	 *            the library fedding this view
	 * @param filter -
	 *            the filter to use
	 * @param comparator -
	 *            a comparator to use
	 */
	public LibraryView(ImageLibrary library, Filter filter, Comparator<Image> comparator) {

		this.comparator = new SaveComparator(comparator);
		this.library = library;

		listenerManager = new CollectionListenerManager<Image, LibraryView>(this);
		holdBackChanges = new LinkedList<BeanChangeEvent<Image>>();

		library.addListener(this);

		setFilter(filter);
		filter();
	}

	/**
	 * A user should call this methode if this view isn't needed any more.
	 * 
	 * Without calling this methode, a View will exist for good. After calling
	 * this, the actual contend can still be used, but it will not be
	 * automatically updated any longer
	 */
	public void dispose() {
		// just cut connection to library
		// this is to remove constructor-generated referenzes
		// so that objects of this class may be finalized somewhen
		library.removeListener(this);
	}

	/**
	 * this will return the list of currently contained images. The list is
	 * unmodifiabale by the reciver. Nevertheless, it may be changed by this
	 * view. If you like to have a "untouchabel" list of items use the
	 * getSnaphot() methode.
	 * 
	 * @see LibraryView#getSnapshot()
	 * @see Collections#unmodifiableList(List)
	 * 
	 * @return an unmodifiable mutating list of currently matched images
	 */
	public List<Image> getImages() {
		return Collections.unmodifiableList(Arrays.asList(getContentArray()));
	}

	/**
	 * Get a snapshot. The sorting order will be preserved.
	 * 
	 * @return a snapshot of the currently maching images.
	 */
	public List<Image> getSnapshot() {
		return new ArrayList<Image>(Arrays.asList(getContentArray()));
	}

	/**
	 * get the image with the given index
	 * 
	 * @param index
	 * @return the image with the given index
	 */
	public Image getImage(int index) {
		return getContentArray()[index];
	}

	/**
	 * get index of given image
	 * 
	 * @param img
	 * @return index of given image
	 * 
	 * @see List#indexOf(Object)
	 */
	public int indexOf(Image img) {
		if (getContent().contains(img)) {
			return Arrays.binarySearch(getContentArray(), img, comparator);
		}
		return -1;
	}

	/**
	 * exchange filter.
	 * 
	 * This result in a content update. If filter is null, it is treated like
	 * there would be no filter.
	 * 
	 * @param newFilter
	 */
	public void setFilter(Filter newFilter) {
		// check if there is any change
		if (filter == newFilter) {
			return;
		}

		// exchange
		filter = newFilter;

		// regenerate list
		filter();
	}

	/**
	 * Update filter and sorting at once, so common operations has only been
	 * done once
	 * 
	 * @param newFilter
	 * @param newSorting
	 */
	public void updateView(Filter newFilter, Comparator<Image> newSorting) {
		// update filter
		boolean filterRequired = (filter != newFilter);
		filter = newFilter;

		// update comparator
		boolean sortingRequired = (comparator.comparator != newSorting);
		comparator = new SaveComparator(newSorting);

		// update content
		if (filterRequired) {
			// will also sort
			filter();
		} else if (sortingRequired) {
			// just sort
			sort();
		}
	}

	/**
	 * perform filtering + sorting
	 */
	private void filter() {
		// perform filter operation
		isFiltering = true;
		updateContent();
		isFiltering = false;

		// notify listeners
		listenerManager.notifyExchange();
	}

	/**
	 * this methode is synchronizing filter / sorting and library base
	 */
	private void updateContent() {
		content = new HashSet<Image>(library.size());

		if (filter == null) {
			content.addAll(library.getAll());
		} else {
			// try to reduce set of possible members
			Set<Image> base = filter.possibleMembers();
			if (base == null) {
				base = library.getAll();
			}
			for (Image image : base) {
				// there may be images added during match processing
				// (duplicates)
				// => contains check important
				if (!content.contains(image) && filter.matches(image)) {
					content.add(image);
				}
			}
		}

		// invalidate other content lists
		arrayDirty = true;
	}

	/**
	 * perform sorting
	 * 
	 * this will notify listeners about a basement exchange
	 */
	private void sort() {
		// sorting will be lacy evaluated
		arrayDirty = true;

		// inform listeners about changes - this also affects filter
		listenerManager.notifyExchange();
	}

	/**
	 * get a clean hash set of contained images
	 * 
	 * @return a clean hash set of contained images
	 */
	private Set<Image> getContent() {
		return content;
	}

	/**
	 * get a clean array-version of matched images
	 * 
	 * @return a clean array-version of matched images
	 */
	private Image[] getContentArray() {
		if (arrayDirty) {
			contentArray = getContent().toArray(new Image[0]);
			Arrays.sort(contentArray, comparator);
			arrayDirty = false;
		}
		return contentArray;
	}

	/**
	 * @return the comparator
	 */
	public Comparator<Image> getComparator() {
		return comparator.comparator;
	}

	/**
	 * @param comparator
	 */
	public void setComparator(Comparator<Image> comparator) {
		this.comparator = new SaveComparator(comparator);
		sort();
	}

	/**
	 * check if this view is currently freezed
	 * 
	 * @return true if freezed, false else
	 */
	public boolean isFreezed() {
		return freezed;
	}

	/**
	 * set freezed state of this view.
	 * 
	 * @param freezed
	 */
	public void setFreezed(boolean freezed) {
		if (this.freezed && !freezed) {
			this.freezed = freezed;
			// freeze lock is removed
			// fake update event
			elementsUpdated(library, holdBackChanges);
			holdBackChanges.clear();
		}
		this.freezed = freezed;
	}

	/**
	 * is the given image within this view
	 * 
	 * @param image
	 * @return true if the given image is within this view
	 */
	public boolean contains(Image image) {
		return getContent().contains(image);
	}

	/**
	 * how many images are within this view
	 * 
	 * @return the number of images in this view
	 */
	public int size() {
		return getContent().size();
	}

	/**
	 * add a new listener to this view
	 * 
	 * @see org.jimcat.model.notification.ObservableCollection#addListener(org.jimcat.model.notification.CollectionListener)
	 */
	public void addListener(CollectionListener<Image, LibraryView> listener) {
		listenerManager.addListener(listener);
	}

	/**
	 * remove a listener from this view
	 * 
	 * @see org.jimcat.model.notification.ObservableCollection#removeListener(org.jimcat.model.notification.CollectionListener)
	 */
	public void removeListener(CollectionListener<Image, LibraryView> listener) {
		listenerManager.removeListener(listener);
	}

	/**
	 * If there is a deep structural exchange
	 * @param collection 
	 * 
	 * @see org.jimcat.model.notification.CollectionListener#basementChanged(org.jimcat.model.notification.ObservableCollection)
	 */
	@SuppressWarnings("unused")
	public void basementChanged(ImageLibrary collection) {
		// revalidate all images
		filter();
	}

	/**
	 * react on new elements added to the library
	 * @param collection 
	 * @param elements 
	 * 
	 * @see org.jimcat.model.notification.CollectionListener#elementsAdded(org.jimcat.model.notification.ObservableCollection,
	 *      java.util.Set)
	 */
	@SuppressWarnings("unused")
	public void elementsAdded(ImageLibrary collection, Set<Image> elements) {
		// the list of added images
		Set<Image> newbies = new HashSet<Image>();

		// add images
		Set<Image> matching = getContent();
		for (Image img : elements) {
			if (filter == null || filter.matches(img)) {
				if (matching.add(img)) {
					newbies.add(img);
					arrayDirty = true;
				}
			}
		}

		// notify listener
		if (newbies.size() > 0) {
			listenerManager.notifyAdded(newbies);
		}
	}

	/**
	 * reacts on elements removed from the library
	 * 
	 * This methode is invoked when ther is any change of an image within a
	 * collection
	 * @param collection 
	 * @param elements 
	 * 
	 * @see org.jimcat.model.notification.CollectionListener#elementsRemoved(org.jimcat.model.notification.ObservableCollection,
	 *      java.util.Set)
	 */
	@SuppressWarnings("unused")
	public void elementsRemoved(ImageLibrary collection, Set<Image> elements) {
		// list of removed items
		Set<Image> victems = new HashSet<Image>();

		// iterate through elements
		Set<Image> matching = getContent();
		for (Image img : elements) {
			if (matching.remove(img)) {
				victems.add(img);
				arrayDirty = true;
			}
		}

		// inform listeners
		if (victems.size() > 0) {
			listenerManager.notifyRemoved(victems);
		}
	}

	/**
	 * react on a list of changes
	 * @param collection 
	 * @param events 
	 * 
	 * @see org.jimcat.model.notification.CollectionListener#elementsUpdated(org.jimcat.model.notification.ObservableCollection,
	 *      java.util.List)
	 */
	@SuppressWarnings("unused")
	public void elementsUpdated(ImageLibrary collection, List<BeanChangeEvent<Image>> events) {

		// the current state
		Set<Image> matching = getContent();

		// check if currently freezed
		if (isFreezed()) {
			// remember change event
			holdBackChanges.addAll(events);

			// inform listeners about changes anyway
			List<BeanChangeEvent<Image>> changes = new LinkedList<BeanChangeEvent<Image>>();
			for (BeanChangeEvent<Image> change : events) {
				Image img = change.getSource();
				if (matching.contains(img)) {
					changes.add(change);
				}
			}
			listenerManager.notifyUpdated(changes);
			return;
		}

		// list of results
		List<BeanChangeEvent<Image>> changes = new LinkedList<BeanChangeEvent<Image>>();
		Set<Image> added = new HashSet<Image>();
		Set<Image> removed = new HashSet<Image>();

		// iterate through events
		for (BeanChangeEvent<Image> event : events) {
			// check if the element is valid for this view
			// does it match the filter criteria
			Image element = event.getSource();
			if (filter == null || filter.matches(element)) {
				// if it isn't already in the list
				if (matching.add(element)) {
					added.add(element);
					arrayDirty = true;
				} else {
					// just inform listeners about update
					changes.add(event);
				}
			} else {
				// remove it anyway and inform
				if (content.remove(element)) {
					removed.add(element);
					arrayDirty = true;
				}
			}
		}

		// inform listeners
		if (!isFiltering) {
			listenerManager.notifyRemoved(removed);
			listenerManager.notifyAdded(added);
			listenerManager.notifyUpdated(changes);
		}
	}

	/**
	 * private class to wrapp a save comparator around any comparator
	 */
	private static class SaveComparator implements Comparator<Image> {

		/**
		 * the wrapped comparator
		 */
		private Comparator<Image> comparator;

		/**
		 * create a new comparator wrappen given comparator
		 * 
		 * @param comp
		 */
		private SaveComparator(Comparator<Image> comp) {
			this.comparator = comp;
		}

		/**
		 * compare to images so that compareTo(a,b) == 0 when a.equals(b)==true
		 * @param o1 
		 * @param o2 
		 * @return the result of the compare method as specified in java.util.Comparator
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(Image o1, Image o2) {
			// speedup
			if (o1 == o2) {
				return 0;
			}

			// use comparator - natural order if required
			if (comparator != null) {
				int res = comparator.compare(o1, o2);
				if (res != 0) {
					return res;
				}
			}
			return o1.compareTo(o2);
		}

	}

}
