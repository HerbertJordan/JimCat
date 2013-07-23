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

import org.jimcat.persistence.db4o.Db4oAlbumRepository;
import org.jimcat.persistence.db4o.Db4oImageRepository;
import org.jimcat.persistence.db4o.Db4oRepositoryControl;
import org.jimcat.persistence.db4o.Db4oSmartListRepository;
import org.jimcat.persistence.db4o.Db4oTagRepository;
import org.jimcat.persistence.mock.MockImageRepository;
import org.jimcat.persistence.mock.MockTagRepository;
import org.jimcat.persistence.xstream.XStreamAlbumRepository;
import org.jimcat.persistence.xstream.XStreamImageRepository;
import org.jimcat.persistence.xstream.XStreamRepositoryControl;
import org.jimcat.persistence.xstream.XStreamSmartListRepository;
import org.jimcat.persistence.xstream.XStreamTagRepository;
import org.jimcat.services.configuration.Configuration;

/**
 * The RepositoryLocator is an abstract factory that gives you all the
 * repository of the family that is defined by the config.
 * 
 * 
 * $Id: RepositoryLocator.java 999 2007-09-14 20:02:58Z cleiter $
 * 
 * @author Christoph
 */
public final class RepositoryLocator {

	/**
	 * an enumeration of supported configuration setups
	 */
	public enum ConfigType {
		XSTREAM, MOCK, DB4O, CONFIG
	}

	private static RepositoryControl repositoryControl;

	private static AlbumRepository albumRepository;

	private static ImageRepository imageRepository;

	private static SmartListRepository smartListRepository;

	private static TagRepository tagRepository;

	private static ConfigType type;

	/**
	 * 
	 * @return the RepositoryControl
	 */
	public static RepositoryControl getRepositoryControl() {
		return repositoryControl;
	}

	/**
	 * 
	 * set the tag repository
	 * 
	 * @param tr
	 */
	public static void setTagRepository(TagRepository tr) {
		tagRepository = tr;
	}

	/**
	 * 
	 * @return true if there are dependencies
	 */
	public static boolean hasDependencies() {
		return type != null;
	}

	/**
	 * 
	 * reset all dependencies
	 */
	public static void resetDependencies() {

		if (type == null) {
			throw new IllegalStateException("Call setConfigType first.");
		}

		if (type == ConfigType.CONFIG) {
			String s = Configuration.getString("persistence", "");

			if (s.equalsIgnoreCase("xstream")) {
				type = ConfigType.XSTREAM;
			} else if (s.equalsIgnoreCase("mock")) {
				type = ConfigType.MOCK;
			} else if (s.equalsIgnoreCase("db4o")) {
				type = ConfigType.DB4O;
			} else {
				type = ConfigType.DB4O; // default mode
			}
		}

		switch (type) {
		case MOCK:
			tagRepository = new MockTagRepository();
			imageRepository = new MockImageRepository();
			break;
		case XSTREAM:
			tagRepository = new XStreamTagRepository();
			smartListRepository = new XStreamSmartListRepository();
			imageRepository = new XStreamImageRepository();
			albumRepository = new XStreamAlbumRepository();
			repositoryControl = new XStreamRepositoryControl();
			break;
		case DB4O:
			tagRepository = new Db4oTagRepository();
			smartListRepository = new Db4oSmartListRepository();
			imageRepository = new Db4oImageRepository();
			albumRepository = new Db4oAlbumRepository();
			repositoryControl = new Db4oRepositoryControl();
			break;
		case CONFIG:
			throw new IllegalStateException("Internal Java Error");
		}

		// load default configuration if necessary
		if (repositoryControl != null && !repositoryControl.configurationExists()) {
			tagRepository.save(DefaultConfig.getTagRoot());
			albumRepository.save(DefaultConfig.getAlbums());
			smartListRepository.save(DefaultConfig.getSmartLists());
			imageRepository.save(DefaultConfig.getImages());
		}
	}

	/**
	 * 
	 * set the configuration type
	 * 
	 * @param t
	 */
	public static void setConfigType(ConfigType t) {
		if (type != null) {
			throw new IllegalStateException("You can't call setConfigType() twice");
		}

		type = t;

		resetDependencies();
	}

	private static void check() {
		if (type == null) {
			throw new IllegalStateException("Call setConfigType first");
		}
	}

	/**
	 * 
	 * @return the AlbumRepository
	 */
	public static AlbumRepository getAlbumRepository() {
		check();
		return albumRepository;
	}

	/**
	 * 
	 * @return the ImageRepository
	 */
	public static ImageRepository getImageRepository() {
		check();
		return imageRepository;
	}

	/**
	 * 
	 * @return the SmartListRepository
	 */
	public static SmartListRepository getSmartListRepository() {
		check();
		return smartListRepository;
	}

	/**
	 * 
	 * @return the TagRepository
	 */
	public static TagRepository getTagRepository() {
		check();
		return tagRepository;
	}
}
