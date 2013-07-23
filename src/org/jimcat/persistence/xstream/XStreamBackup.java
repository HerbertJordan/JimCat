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

package org.jimcat.persistence.xstream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.jimcat.model.Album;
import org.jimcat.model.ExifMetadata;
import org.jimcat.model.Image;
import org.jimcat.model.ImageMetadata;
import org.jimcat.model.ImageRating;
import org.jimcat.model.SmartList;
import org.jimcat.model.Thumbnail;
import org.jimcat.model.filter.Filter;
import org.jimcat.model.filter.HasTagsFilter;
import org.jimcat.model.filter.TagFilter;
import org.jimcat.model.filter.logical.NotFilter;
import org.jimcat.model.filter.logical.OrFilter;
import org.jimcat.model.filter.metadata.ExifMetadataFilter;
import org.jimcat.model.filter.metadata.RelativeDateFilter;
import org.jimcat.model.filter.metadata.ExifMetadataFilter.ExifMetadataProperty;
import org.jimcat.model.filter.metadata.RelativeDateFilter.ReferenceDate;
import org.jimcat.model.filter.metadata.RelativeDateFilter.TimeUnit;
import org.jimcat.model.tag.Tag;
import org.jimcat.model.tag.TagGroup;
import org.jimcat.persistence.DefaultConfig;
import org.joda.time.DateTime;

import com.thoughtworks.xstream.XStream;

/**
 * A container for a complete datastructure
 * 
 * $Id: DummyImplBackup.java 426 2007-04-29 03:01:07Z 07g1t1u3 $
 * 
 * @author Herbert
 */
public final class XStreamBackup {

	/**
	 * a boolean to indicate if this repository should use the central default
	 * or its own, if it has one
	 */
	private static final boolean USE_CENTRAL_DEFAULT = true;

	/**
	 * enable output file ziping
	 */
	private static final boolean ENABLE_ZIP = true;

	private static String userHome = System.getProperty("user.home", ".");

	private static String configDir = userHome + "/.jimcat/";

	/**
	 * a filename for XML - Backup
	 */
	private static final String BACKUP_FILE = configDir + "demo.dat";

	/**
	 * a stored instance
	 */
	private static XStreamBackup INSTANCE;

	/**
	 * the tagtree
	 */
	public TagGroup tagRoot;

	/**
	 * the set of stored images
	 */
	public Set<Image> images;

	/**
	 * the set of stored albums
	 */
	public Set<Album> albumList;

	/**
	 * the set of stored smartlists
	 */
	public Set<SmartList> smartLists;

	/**
	 * this loads the configuration from an XML File or uses the default
	 * configuration
	 * 
	 * @return an instance of xstream backup
	 */
	public static XStreamBackup getInstance() {
		if (INSTANCE != null) {
			return INSTANCE;
		}
		try {
			INSTANCE = loadFromFile();
			System.out.println("Demo data loaded from config file");
			if (INSTANCE != null) {
				return INSTANCE;
			}
		} catch (Exception e) {
			System.out.println("Error loading Demo Config File - using default setting");
		}
		// load default config
		INSTANCE = new XStreamBackup();
		if (USE_CENTRAL_DEFAULT) {
			INSTANCE.loadCentralDefaultConfig();
		} else {
			INSTANCE.makeDefaultConfig();
		}
		return INSTANCE;
	}

	/**
	 * creates and configure a new Xstream
	 * 
	 * @return a new Xstream
	 */
	private static XStream getXStream() {
		XStream stream = new XStream();
		stream.alias("DemoConfig", XStreamBackup.class);
		stream.alias("Tag", Tag.class);
		stream.alias("TagGroup", TagGroup.class);
		stream.alias("Image", Image.class);
		stream.alias("Album", Album.class);
		stream.alias("SmartList", SmartList.class);
		return stream;
	}

	/**
	 * loads the configuration from a File
	 * 
	 * @return the configuration from a file
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public synchronized static XStreamBackup loadFromFile() throws FileNotFoundException, IOException {
		XStream stream = getXStream();
		File store = new File(BACKUP_FILE);

		InputStream in = null;
		XStreamBackup res = null;
		try {
			in = new FileInputStream(store);

			// test if file is zipped
			try {
				in = new GZIPInputStream(in);
			} catch (IOException ioe) {
				// so it is no GZIP format
				IOUtils.closeQuietly(in);
				in = new FileInputStream(store);
			}

			res = (XStreamBackup) stream.fromXML(in);
		} finally {
			IOUtils.closeQuietly(in);
		}

		return res;
	}

	/**
	 * saves the configuration to a file
	 * 
	 * @throws IOException
	 */
	public synchronized static void saveToFile() throws IOException {
		XStream stream = getXStream();
		File store = new File(BACKUP_FILE);
		store.createNewFile();

		OutputStream out = null;
		try {
			out = new FileOutputStream(store);
			if (ENABLE_ZIP) {
				out = new GZIPOutputStream(out);
			}
			stream.toXML(INSTANCE, out);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}

	/**
	 * a very long list of DemoDataGeneration
	 */
	private XStreamBackup() {
		// give empty image list
		images = new HashSet<Image>();
		albumList = new HashSet<Album>();
		smartLists = new HashSet<SmartList>();
	}

	/**
	 * load the default config that is defined centrally for all repositories
	 */
	private void loadCentralDefaultConfig() {

		TagGroup loadedRoot = DefaultConfig.getTagRoot();
		if (loadedRoot != null) {
			tagRoot = loadedRoot;
		} else {
			tagRoot = new TagGroup(true);
			tagRoot.setName("root");
		}

		Set<Image> loadedImages = DefaultConfig.getImages();
		if (loadedImages != null) {
			images.addAll(loadedImages);
		}

		Set<Album> loadedAlbums = DefaultConfig.getAlbums();
		if (loadedAlbums != null) {
			albumList.addAll(loadedAlbums);
		}

		Set<SmartList> loadedSmartLists = DefaultConfig.getSmartLists();
		if (loadedSmartLists != null) {
			smartLists.addAll(loadedSmartLists);
		}
	}

	/**
	 * creates default configuration
	 */
	private void makeDefaultConfig() {

		// create Tags
		tagRoot = new TagGroup(true);
		tagRoot.setName("root");

		TagGroup person = new TagGroup();
		person.setName("Person");
		tagRoot.addSubTag(person);

		TagGroup familie = new TagGroup();
		familie.setName("Familie");
		person.addSubTag(familie);

		Tag sister = new Tag();
		sister.setName("sister");
		familie.addSubTag(sister);

		Tag brother = new Tag();
		brother.setName("brother");
		familie.addSubTag(brother);

		Tag father = new Tag();
		father.setName("father");
		familie.addSubTag(father);

		Tag franz = new Tag();
		franz.setName("Franz Mustermann");
		person.addSubTag(franz);

		Tag peter = new Tag();
		peter.setName("Peter Parker");
		person.addSubTag(peter);

		Tag daisy = new Tag();
		daisy.setName("Daisy Duck");
		person.addSubTag(daisy);

		TagGroup locations = new TagGroup();
		locations.setName("Locations");
		tagRoot.addSubTag(locations);

		Tag bar = new Tag();
		bar.setName("Bar");
		locations.addSubTag(bar);

		Tag santa_monica = new Tag();
		santa_monica.setName("Santa Monica");
		locations.addSubTag(santa_monica);

		Tag salzburg = new Tag();
		salzburg.setName("Salzburg");
		locations.addSubTag(salzburg);

		TagGroup motiv = new TagGroup();
		motiv.setName("Motive");
		tagRoot.addSubTag(motiv);

		Tag birthday = new Tag();
		birthday.setName("Birthday");
		motiv.addSubTag(birthday);

		Tag holiday = new Tag();
		holiday.setName("Holiday");
		motiv.addSubTag(holiday);

		Tag christmas = new Tag();
		christmas.setName("Christmas");
		motiv.addSubTag(christmas);

		TagGroup objects = new TagGroup();
		objects.setName("Objects");
		tagRoot.addSubTag(objects);

		Tag tree = new Tag();
		tree.setName("tree");
		objects.addSubTag(tree);

		Tag rock = new Tag();
		rock.setName("rock");
		objects.addSubTag(rock);

		Tag area = new Tag();
		area.setName("Area 51s secret UFO");
		objects.addSubTag(area);

		TagGroup misc = new TagGroup();
		misc.setName("misc");
		tagRoot.addSubTag(misc);

		Tag funny = new Tag();
		funny.setName("funny");
		misc.addSubTag(funny);

		Tag light = new Tag();
		light.setName("strange light");
		misc.addSubTag(light);

		// two sets of metadaten
		DateTime createDateA = new DateTime(2007, 1, 12, 14, 23, 15, 221);
		DateTime addedDateA = new DateTime(2007, 2, 13, 18, 27, 22, 437);
		ImageMetadata metaDataA = new ImageMetadata(null, 1200, 1600, 1234567, null, 1, createDateA, addedDateA);
		ExifMetadata exifMetadataA = new ExifMetadata("Kodac", "E-40", new DateTime("2007-12-01"), ".012", "10", "yes",
		        "f.05", "ISO0815");

		DateTime createDateB = new DateTime(2006, 12, 24, 20, 23, 18, 258);
		DateTime addedDateB = new DateTime(2007, 2, 13, 18, 27, 22, 437);
		ImageMetadata metaDataB = new ImageMetadata(null, 1200, 1600, 2234567, null, 2, createDateB, addedDateB);
		ExifMetadata exifMetadataB = new ExifMetadata("Samsung", "E-41", new DateTime("2006-12-24"), ".5", "20",
		        "eye-reduction", "f.03", "DIN0815");

		// demo thumbnails and files
		URL demoImages[] = new URL[] { getClass().getResource("sunray.jpg"), getClass().getResource("ice.jpg"),
		        getClass().getResource("river.jpg"), getClass().getResource("rainbow.jpg") };

		// load thumbnails
		Thumbnail thumbs[] = new Thumbnail[demoImages.length];
		for (int i = 0; i < demoImages.length; i++) {
			try {
				thumbs[i] = new Thumbnail(ImageIO.read(demoImages[i]));
			} catch (IOException ioe) {
				System.out.println("Error loading demo image " + demoImages[i]);
				System.err.println(ioe.getMessage());
			}
		}

		// create a list of Images
		for (int i = 0; i < 20; i++) {
			Image tmp = new Image();
			tmp.setRating(ImageRating.values()[(int) (Math.random() * 6)]);
			tmp.setTitle("Image " + i);
			if (i % 2 == 0) {
				tmp.addTag(brother);
				tmp.setMetadata(metaDataA);
				tmp.setExifMetadata(exifMetadataA);
			} else {
				tmp.addTag(sister);
				tmp.setMetadata(metaDataB);
				tmp.setExifMetadata(exifMetadataB);
			}
			tmp.addTag(area);
			tmp.addTag(birthday);
			tmp.setThumbnail(thumbs[i % thumbs.length]);
			images.add(tmp);
		}

		// create albums
		Iterator<Image> iter = images.iterator();

		Album album1 = new Album();
		album1.setName("First 5 Images");
		for (int i = 0; i < 5; i++) {
			album1.addImage(iter.next());
		}

		Album album2 = new Album();
		album2.setName("Second 5 Images");
		for (int i = 0; i < 5; i++) {
			album2.addImage(iter.next());
		}

		Album album3 = new Album();
		album3.setName("Third 5 Images");
		for (int i = 0; i < 5; i++) {
			album3.addImage(iter.next());
		}

		albumList.add(album1);
		albumList.add(album2);
		albumList.add(album3);

		// Smartlists

		// create familie filter
		SmartList smartList1 = new SmartList();
		smartList1.setName("familie");

		TagFilter filterBrother = new TagFilter(brother);
		TagFilter filterSister = new TagFilter(sister);
		TagFilter filterFather = new TagFilter(father);

		Filter or1 = new OrFilter(filterBrother, filterFather);
		Filter or2 = new OrFilter(filterSister, or1);

		smartList1.setFilter(or2);

		smartLists.add(smartList1);

		// create untaged smartlist
		SmartList smartlist2 = new SmartList();
		smartlist2.setName("untagged elements");
		smartlist2.setFilter(new NotFilter(new HasTagsFilter()));

		smartLists.add(smartlist2);

		SmartList smartlist3 = new SmartList();
		smartlist3.setName("recently added");
		smartlist3.setFilter(new RelativeDateFilter(ReferenceDate.DateAdded, TimeUnit.DAYS, 7));

		smartLists.add(smartlist3);

		SmartList smartlist4 = new SmartList();
		smartlist4.setName("made by canon");
		smartlist4.setFilter(new ExifMetadataFilter(ExifMetadataProperty.MANUFACTURER, "kodac"));

		smartLists.add(smartlist4);

	}

}
