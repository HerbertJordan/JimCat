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

package org.jimcat.persistence;

import java.util.HashSet;
import java.util.Set;

import org.jimcat.model.Album;
import org.jimcat.model.Image;
import org.jimcat.model.ImageRating;
import org.jimcat.model.SmartList;
import org.jimcat.model.filter.HasTagsFilter;
import org.jimcat.model.filter.IsPartOfAlbumFilter;
import org.jimcat.model.filter.RatingFilter;
import org.jimcat.model.filter.RatingFilter.Type;
import org.jimcat.model.filter.logical.NotFilter;
import org.jimcat.model.filter.metadata.RelativeDateFilter;
import org.jimcat.model.filter.metadata.RelativeDateFilter.ReferenceDate;
import org.jimcat.model.filter.metadata.RelativeDateFilter.TimeUnit;
import org.jimcat.model.tag.Tag;
import org.jimcat.model.tag.TagGroup;

/**
 * This class containes the default configuration used when there is
 * no configuration.
 *
 * $Id$
 * @author Herbert
 */
public class DefaultConfig {

	/**
	 * the default tagtree
	 */
	private static TagGroup tagRoot;

	/**
	 * the default set of images
	 */
	private static Set<Image> images;

	/**
	 * the default set of albums
	 */
	private static Set<Album> albums;

	/**
	 * the default set of smartlists
	 */
	private static Set<SmartList> smartLists;
	
	/**
     * @return the albums
     */
    public synchronized static Set<Album> getAlbums() {
    	if (albums==null) {
    		makeDefaultConfig();
    	}
    	return albums;
    }

	/**
     * @return the images
     */
    public synchronized static Set<Image> getImages() {
    	if (images==null) {
    		makeDefaultConfig();
    	}
    	return images;
    }

	/**
     * @return the smartLists
     */
    public synchronized static Set<SmartList> getSmartLists() {
    	if (smartLists==null) {
    		makeDefaultConfig();
    	}
    	return smartLists;
    }

	/**
     * @return the tagRoot
     */
    public synchronized static TagGroup getTagRoot() {
    	if (tagRoot==null) {
    		makeDefaultConfig();
    	}
    	return tagRoot;
    }

	/**
	 * creates default configuration
	 */
	private synchronized static void makeDefaultConfig() {

		// create Tags
		TagGroup newRoot = new TagGroup(true);
		newRoot.setName("ROOT");

		TagGroup people = new TagGroup();
		people.setName("People");
		newRoot.addSubTag(people);

			TagGroup family = new TagGroup();
			family.setName("Family");
			people.addSubTag(family);
			
			Tag tmp = new Tag();
			tmp.setName("myself");
			people.addSubTag(tmp);
			
		TagGroup events = new TagGroup();
		events.setName("Events");
		newRoot.addSubTag(events);

			tmp = new Tag();
			tmp.setName("birthday");
			events.addSubTag(tmp);
			
			tmp = new Tag();
			tmp.setName("holiday");
			events.addSubTag(tmp);
			
			tmp = new Tag();
			tmp.setName("x-mas");
			events.addSubTag(tmp);
			
		TagGroup places = new TagGroup();
		places.setName("Places");
		newRoot.addSubTag(places);
		
			tmp = new Tag();
			tmp.setName("home");
			events.addSubTag(tmp);
			
		TagGroup misc = new TagGroup();
		misc.setName("Misc");
		newRoot.addSubTag(misc);

		tagRoot = newRoot;

		
		// create albums - none
		albums = new HashSet<Album>();
		
		// Smartlists

		// create favourite smartlist
		smartLists = new HashSet<SmartList>();
		
		SmartList tmps = new SmartList();
		tmps.setName("Favourite Photos");
		tmps.setFilter(new RatingFilter(Type.AT_LEAST, ImageRating.FIVE));
		smartLists.add(tmps);
		
		tmps = new SmartList();
		tmps.setName("Untagged Photos");
		tmps.setFilter(new NotFilter(new HasTagsFilter()));
		smartLists.add(tmps);
		
		tmps = new SmartList();
		tmps.setName("Part of no Album");
		tmps.setFilter(new NotFilter(new IsPartOfAlbumFilter()));
		smartLists.add(tmps);

		tmps = new SmartList();
		tmps.setName("Recently added");
		tmps.setFilter(new RelativeDateFilter(ReferenceDate.DateAdded,TimeUnit.DAYS,7));
		smartLists.add(tmps);
	}
}
