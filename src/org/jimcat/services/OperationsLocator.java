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

package org.jimcat.services;

import org.jimcat.services.operations.AlbumOperationsImpl;
import org.jimcat.services.operations.ImageOperationsImpl;
import org.jimcat.services.operations.JobOperationsImpl;
import org.jimcat.services.operations.SmartListOperationsImpl;
import org.jimcat.services.operations.SystemOperationsImpl;
import org.jimcat.services.operations.TagOperationsImpl;

/**
 * A single point to confugre running modules.
 * 
 * It serves as a static library for all kind of Operations interfaces. Throught
 * the system only implementations provided by this locator should be used.
 * 
 * $Id: OperationsLocator.java 934 2007-06-15 08:40:58Z 07g1t1u2 $
 * 
 * @author Herbert
 */
public final class OperationsLocator {

	/**
	 * the used SystemOperations implementation
	 */
	private static final SystemOperations systemOperations = new SystemOperationsImpl();

	/**
	 * the used JobOperations implementation
	 */
	private static final JobOperations jobOperations = new JobOperationsImpl();

	/**
	 * the used AlbumOperations implementation
	 */
	private static final AlbumOperations albumOperations = new AlbumOperationsImpl();

	/**
	 * the used ImageOperations implementation
	 */
	private static final ImageOperations imageOperations = new ImageOperationsImpl();

	/**
	 * the used TagOperations implementation
	 */
	private static final TagOperations tagOperations = new TagOperationsImpl();

	/**
	 * the used SmartListOperations implementation
	 */
	private static final SmartListOperations smartListOperations = new SmartListOperationsImpl();

	/**
	 * private construtor to prevent instanziation
	 */
	private OperationsLocator() {
		// to seale
	}

	/**
	 * provieds access to the only systemOperations Implementation this
	 * application should use.
	 * 
	 * @return the system operations
	 */
	public static SystemOperations getSystemOperations() {
		return systemOperations;
	}

	/**
	 * provieds access to the only JobOperations Implementation this application
	 * should use.
	 * 
	 * @return the job operations
	 */
	public static JobOperations getJobOperations() {
		return jobOperations;
	}

	/**
	 * provieds access to the only AlbemOperations Implementation this
	 * application should use.
	 * 
	 * @return the album operations
	 */
	public static AlbumOperations getAlbumOperations() {
		return albumOperations;
	}

	/**
	 * provieds access to the only ImageOperations Implementation this
	 * application should use.
	 * 
	 * @return the image operations
	 */
	public static ImageOperations getImageOperations() {
		return imageOperations;
	}

	/**
	 * provieds access to the only TagOperations Implementation this application
	 * should use.
	 * 
	 * @return the tag operations
	 */
	public static TagOperations getTagOperations() {
		return tagOperations;
	}

	/**
	 * provieds access to the only SmartListOperations Implementation this
	 * application should use.
	 * 
	 * @return the smartlist operations
	 */
	public static SmartListOperations getSmartListOperations() {
		return smartListOperations;
	}

}
