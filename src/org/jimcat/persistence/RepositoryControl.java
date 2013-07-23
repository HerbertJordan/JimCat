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

/**
 * An interface for general persistence control commands
 * 
 * $Id: RepositoryControl.java 934 2007-06-15 08:40:58Z 07g1t1u2 $
 * 
 * @author Herbert
 */
public interface RepositoryControl {

	/**
	 * this methoded is called on Repository installation within the Repository
	 * locator.
	 * 
	 * It should only return false if there is no current configuration. In that
	 * case it is assumed that all lists are empty. Therefore the default
	 * configuration will be installed.
	 * 
	 * @return if the configuration exists
	 */
	public boolean configurationExists();

	/**
	 * Calling this methode should force the persistencylayer to shut down. No
	 * further calls will be made after this.
	 */
	public void shutdown();
}
