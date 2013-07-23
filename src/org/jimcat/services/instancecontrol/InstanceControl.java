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

package org.jimcat.services.instancecontrol;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;

import org.jimcat.services.configuration.Configuration;


/**
 * This module is trying to ensure that there is only a single instance of
 * Jimcat running on this system.
 * 
 * This is done through a none shareable resource, a TCP port.
 * 
 * This also allowes the surpressed system to send a message to the surpressing
 * application.
 * 
 * $Id: InstanceControl.java 935 2007-06-15 09:21:09Z 07g1t1u2 $
 * 
 * @author Herbert
 */
public final class InstanceControl {

	/**
	 * the only instance of this class (singelton)
	 */
	private final static InstanceControl INSTANCE = new InstanceControl();

	/**
	 * the servesocket used to ensure singel instance of application
	 */
	private ServerSocket socket;

	/**
	 * a listener for incomming connections
	 */
	private SocketListener socketListener;

	/**
	 * a list of listeners who want to be informed on action
	 */
	private List<InstanceListener> listeners = new CopyOnWriteArrayList<InstanceListener>();

	/**
	 * determines if this is the first instance allowed to run
	 */
	private boolean onlyInstance;

	/**
	 * private constructor
	 */
	private InstanceControl() {
		int port = Configuration.getInt("instance.controlport", 12445);
		try {
			// try and get Resource
			socket = new ServerSocket(port);

			// so we are the first instance
			onlyInstance = true;

			// add a callhandler
			socketListener = new SocketListener(socket);
			new Thread(socketListener).start();

		} catch (IOException e) {
			// there is another instance of jimcat
			onlyInstance = false;

			// inform other instance
			try {
				// just open a socket and close it again
				Socket client = new Socket("localhost", port);
				client.close();
			} catch (IOException ioe) {
				// ignore
			}
		}
	}

	/**
	 * returns the only instance of this class (singelton)
	 * 
	 * @return an instance of InstanceControl
	 */
	public static InstanceControl getInstance() {
		return INSTANCE;
	}

	/**
	 * Use this methode on startup to check if this is the first instance. If it
	 * is false, please stop startup.
	 * 
	 * @return the firstInstance
	 */
	public boolean isOnlyInstance() {
		return onlyInstance;
	}

	/**
	 * this will shut down the InstanceControl and release the unshareable
	 * resource (socket)
	 * 
	 * It can't be aquired again.
	 */
	public void shutdown() {
		if (socketListener != null) {
			socketListener.kill();
		}
	}

	/**
	 * adds a new instancelistener to this modul
	 * 
	 * @param listener -
	 *            the new listener
	 */
	public void addInstanceListener(InstanceListener listener) {
		listeners.add(listener);
	}

	/**
	 * remoes a instancelistener from this modul
	 * 
	 * @param listener -
	 *            a listener
	 */
	public void removeInstanceListener(InstanceListener listener) {
		listeners.remove(listener);
	}

	/**
	 * notifies all listerners about surpressed instance
	 */
	private void notifyListeners() {
		for (InstanceListener listener : listeners) {
			listener.otherInstanceSurpressed();
		}
	}

	/**
	 * Internal class for socket operations
	 * 
	 * $Id: InstanceControl.java 935 2007-06-15 09:21:09Z 07g1t1u2 $
	 * 
	 * @author Herbert
	 */
	private class SocketListener implements Runnable {

		/**
		 * determines the threads runningstate
		 */
		boolean alive = true;

		/**
		 * the associated ServerSocket
		 */
		ServerSocket mySocket = null;

		/**
		 * to wait until finished
		 */
		private Semaphore finished = new Semaphore(0);

		/**
		 * internal constructor requireing a serversocket
		 * @param socket
		 */
		public SocketListener(ServerSocket socket) {
			this.mySocket = socket;
		}

		/**
		 * runs as long as this thread is alive after all, it will close the
		 * serversocket
		 * 
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			alive = true;
			// wait for other instances and informe listeners
			while (alive) {
				try {
					Socket client = mySocket.accept();
					client.close();
				} catch (IOException ioe) {
					// ignore
				}
				if (alive) {
					notifyListeners();
				}
			}

			// shutdown serversocket after killing this runnable
			try {
				mySocket.close();
			} catch (IOException ioe) {
				// ignore
			}

			// inform about finishing
			finished.release();
		}

		/**
		 * this will stop this SocketListener
		 */
		public void kill() {
			if (!alive) {
				return;
			}

			int port = Configuration.getInt("instance.controlport", 12445);
			alive = false;

			try {
				// open a socket to resume execution of runnable
				Socket client = new Socket("localhost", port);
				client.close();
			} catch (IOException ioe) {
				// ignore
			}

			// wait until finished
			finished.acquireUninterruptibly();
		}
	}
}
