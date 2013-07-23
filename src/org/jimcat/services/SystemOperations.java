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

import org.jimcat.services.failurefeedback.FailureFeedbackListener;
import org.jimcat.services.instancecontrol.InstanceListener;

/**
 * A general interface for all kind of system operations provided by the service
 * layer.
 * 
 * $Id: SystemOperations.java 450 2007-04-30 12:47:58Z 07g1t1u3 $
 * 
 * @author Herbert
 */
public interface SystemOperations {

	/**
	 * call this methode to initiate a systemshutdown this will clearelly finish
	 * up any active structure
	 * 
	 * @throws IllegalStateException -
	 *             if the current program state doesn't allow shutdown
	 */
	public void shutdown() throws IllegalStateException;

	/**
	 * call this methode to access the systems configuration modul - the
	 * configuration is useing string-pairs
	 * 
	 * @param key -
	 *            a key
	 * @return value - the corresponding value or null if there is no such key
	 */
	public String getProperty(String key);

	/**
	 * use this methode to save a key/value pair in the system. This paire will
	 * be persistant. If there is another keypair with the same key it will be
	 * overriden.
	 * 
	 * @param key -
	 *            a key
	 * @param value -
	 *            the new value
	 */
	public void setProperty(String key, String value);

	/**
	 * this should add a new InstanceListener to the systems instancecontrol
	 * 
	 * @param listener -
	 *            a listener
	 */
	public void addInstanceListener(InstanceListener listener);

	/**
	 * this should remove an InstanceListener to the systems instancecontrol
	 * 
	 * @param listener -
	 *            a listener
	 */
	public void removeInstanceListener(InstanceListener listener);

	/**
	 * add a new failure feedback listener to the installed failure feedback
	 * service
	 * 
	 * @param listener
	 */
	public void addFailureFeedbackListener(FailureFeedbackListener listener);

	/**
	 * remove failure feedback listener from the installed failure feedback
	 * service
	 * 
	 * @param listener
	 */
	public void removeFailureFeedbackListener(FailureFeedbackListener listener);
}
