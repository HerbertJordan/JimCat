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

package org.jimcat.tests.services.instancecontrol;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.jimcat.services.configuration.Configuration;
import org.jimcat.services.instancecontrol.InstanceControl;
import org.jimcat.services.instancecontrol.InstanceListener;

/**
 * This testcase should test the instancecontrol modul. This test can only be
 * successfull if there is no running Jimcat application.
 * 
 * $Id: TestInstanceControl.java 554 2007-05-09 22:20:37Z 07g1t1u3 $
 * 
 * @author Herbert
 */
public class TestInstanceControl extends TestCase {

	private static final int WAITING_TIME = 200;

	/**
	 * used to synchronize asynchron listener handling
	 */
	private Semaphore mux = new Semaphore(0);

	/**
	 * to simulate side-effects
	 */
	private int value = 0;

	/**
	 * a instanceListener for testing purposes
	 */
	private InstanceListener listener = new InstanceListener() {
		/**
		 * zust for simulation purposes
		 * 
		 * @see org.jimcat.services.instancecontrol.InstanceListener#otherInstanceSurpressed()
		 */
		public void otherInstanceSurpressed() {
			value = 1;
			mux.release();
		}
	};

	/**
	 * test the instancecontrol and its listeners
	 */
	public void testInstanceControl() {

		InstanceControl control = InstanceControl.getInstance();
		boolean isOnlyInstance = control.isOnlyInstance();

		if (!isOnlyInstance) {
			fail("WARNING: Instance Control tests couldn't be performed, other instance is running");
		}

		// register listener and simpulate other instance
		control.addInstanceListener(listener);
		assertFalse("Other instance should fail", simulateOtherInstance());

		try {
			boolean fired = mux.tryAcquire(WAITING_TIME, TimeUnit.MILLISECONDS);
			assertTrue("InstanceListener should been fired", fired);
		} catch (InterruptedException ie) {
			// ignore
		}
		assertEquals("Value should have canched", value, 1);

		// unregister instance
		value = 0;
		control.removeInstanceListener(listener);
		assertFalse("Other instance should fail", simulateOtherInstance());

		try {
			boolean fired = mux.tryAcquire(WAITING_TIME, TimeUnit.MILLISECONDS);
			assertFalse("InstanceListener shouldn't been fired", fired);
		} catch (InterruptedException ie) {
			// ignore
		}
		assertEquals("Value shouldn't have canched", value, 0);

		// shutdown Control
		control.shutdown();
		assertTrue("Other instance should be possible", simulateOtherInstance());
	}

	/**
	 * this will simulate a other instance
	 * 
	 * @return - true if another instance could have been instanciated false
	 *         else
	 */
	private boolean simulateOtherInstance() {
		int port = Configuration.getInt("instance.controlport", 12445);
		boolean result = true;

		try {
			// First try to aquire unshareable resource
			ServerSocket socket = new ServerSocket(port);
			// if it is working, anohter instance could have been formed
			// => close socket quitly and return
			try {
				socket.close();
			} catch (IOException ioe) {
				// ignore those exceptions
			}
		} catch (IOException ioe) {
			// There is another instance
			result = false;
			// inform it about atempt
			try {
				Socket client = new Socket("localhost", port);
				client.close();
			} catch (IOException ioe2) {
				// ignore
			}
		}
		return result;
	}

}
