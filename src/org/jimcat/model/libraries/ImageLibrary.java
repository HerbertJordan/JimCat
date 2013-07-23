/*
 *  This file is part of jimcat.
 *
 *  jimcat is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation version 2.
 *
 *  jimcat is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with jimcat; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.jimcat.model.libraries;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jimcat.model.Image;
import org.jimcat.model.comparator.DuplicateComparator;
import org.jimcat.model.notification.BeanChangeEvent;
import org.jimcat.model.notification.BeanProperty;
import org.jimcat.persistence.RepositoryLocator;

/**
 * A central point of image management.
 * 
 * It keeps references to all persistently stored images accessable by the
 * system.
 * 
 * It consists of the basic AbstractLibrary Implementation enhanced with
 * additional some features like duplicate managment and alternative
 * containes(..) signatures
 * 
 * $Id: ImageLibrary.java 934 2007-06-15 08:40:58Z 07g1t1u2 $
 * 
 * @author Christoph
 */
public final class ImageLibrary extends AbstractLibrary<Image, ImageLibrary> {

	/**
	 * the singelton instance
	 */
	private static ImageLibrary INSTANCE;

	/**
	 * a set of images having duplicates within this library
	 */
	private Set<Image> duplicates;

	/**
	 * used to determine if the current duplicate set has to be updated
	 */
	private boolean isDuplicateSetDirty = true;

	/**
	 * constructor loading images from the repository (singelton constructor)
	 */
	private ImageLibrary() {
		super(RepositoryLocator.getImageRepository());

		// a list of duplicates -must by synchronized
		duplicates = Collections.synchronizedSet(new LinkedHashSet<Image>());
	}

	/**
	 * 
	 * @return the singelton instance of this library type
	 */
	public static ImageLibrary getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ImageLibrary();
		}
		return INSTANCE;
	}

	/**
	 * add a set of new element to this library
	 * 
	 * @see org.jimcat.model.libraries.Library#add(Set)
	 */
	@Override
	public boolean add(Set<Image> elements) {
		// adding elements may mean that the duplicate set becomes dirty
		// cause events are dispatched during adding, dirty must be set before
		isDuplicateSetDirty = true;

		// add elements
		boolean changes = super.add(elements);

		// recheck if there was a change
		if (!changes) {
			// duplicates arn't dirty
			isDuplicateSetDirty = false;
		}

		return changes;
	}

	/**
	 * remove a set of elements from this library
	 * 
	 * @see org.jimcat.model.libraries.Library#remove(org.jimcat.model.notification.ObservableBean)
	 */
	@Override
	public boolean remove(Set<Image> elements) {
		// removing elements may result in a dirty duplicate list
		// listeners are notified by the remove methode
		// => preventive
		isDuplicateSetDirty = true;

		// add elements
		boolean changes = super.remove(elements);

		// recheck if there was a change
		if (changes) {
			// then duplicate list has to be regernerated
			// => this is also dispatching "IMAGE_HAS_DUPLICATE" events
			getDuplicateSet();
		} else {
			// duplicates arn't dirty
			isDuplicateSetDirty = false;
		}

		return changes;
	}

	/**
	 * clear this library
	 * 
	 * @see org.jimcat.model.libraries.Library#removeAll()
	 */
	@Override
	public boolean removeAll() {
		if (!getContent().isEmpty()) {
			isDuplicateSetDirty = true;
			return super.removeAll();
		}
		return false;
	}

	/**
	 * checks if an image representing the given file is within this library
	 * 
	 * @param file
	 * @return true if the file is within this library
	 */
	public boolean contains(File file) {
		// check image by image
		Set<Image> content = getContent();
		synchronized (content) {
			for (Image img : getContent()) {
				if (file.equals(img.getMetadata().getPath())) {
					// found!
					return true;
				}
			}
		}
		// not found
		return false;
	}

	/**
	 * get a list of duplicates
	 * 
	 * @return a list of duplicates
	 */
	public Set<Image> getDuplicates() {
		return Collections.unmodifiableSet(getDuplicateSet());
	}

	/**
	 * check if the given image has a duplicate within this library.
	 * 
	 * @param image
	 * @return true only if the image has a diplicate within this library
	 */
	public boolean hasDuplicate(Image image) {
		// just ask duplicate set
		return getDuplicateSet().contains(image);
	}

	/**
	 * this methode will get you the most actual list of duplicates contained in
	 * this library.
	 * 
	 * @return an updated list of duplicates in this library
	 */
	private synchronized Set<Image> getDuplicateSet() {
		// check if current set is useable
		if (isDuplicateSetDirty) {
			// generate duplicate list
			// 1. get a list of all images
			List<Image> imageList = new ArrayList<Image>(getContent());

			// 2. sort them by duplicate comparator
			Comparator<Image> comparator = new DuplicateComparator();
			Collections.sort(imageList, comparator);

			// 3. find duplicates
			Set<Image> foundDuplicates = new HashSet<Image>();
			for (int i = 0; i < imageList.size() - 1; i++) {
				Image a = imageList.get(i);
				Image b = imageList.get(i + 1);
				// check if they are duplicates
				if (comparator.compare(a, b) == 0) {
					// add both (so 3 or more can be equal)
					foundDuplicates.add(a);
					foundDuplicates.add(b);
				}
			}

			// 4. update duplicate set
			// a list of updated images (virtual property has_duplicate)
			List<Image> updatedElements = new LinkedList<Image>();
			// add new images
			for (Image img : foundDuplicates) {
				if (!duplicates.contains(img)) {
					// add image
					duplicates.add(img);
					// add to changed list
					updatedElements.add(img);
				}
			}

			// remove elements wich doesn't have any duplicates any more
			for (Image img : new LinkedHashSet<Image>(duplicates)) {
				if (!foundDuplicates.contains(img)) {
					// remove image
					duplicates.remove(img);

					// if image is still part of this library
					if (contains(img)) {
						// add to changed list
						updatedElements.add(img);
					}
				}
			}

			// mark list as clean
			isDuplicateSetDirty = false;

			// 5. inform about property changes
			// build event set
			if (updatedElements.size() > 0) {
				List<BeanChangeEvent<Image>> events = new ArrayList<BeanChangeEvent<Image>>(updatedElements.size());
				for (Image img : updatedElements) {
					// send property changed message (for virtual "has
					// duplicate" property)
					events.add(new BeanChangeEvent<Image>(img, BeanProperty.IMAGE_HAS_DUPLICATE, null));
				}
				getManager().notifyUpdated(events);
			}
		}
		return duplicates;
	}

}
