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

package org.jimcat.services.imagemanager;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This runnable will perform an aynchron preload action.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class PreloadService implements Runnable {

	/**
	 * used to kill this service
	 */
	private static final ImageKey POISEND_ORDER = new ImageKey(null, null, null);

	/**
	 * the maximum size of the order queue
	 */
	private static final int MAX_QUEUE_SIZE = 3;

	/**
	 * the manager used to preload images
	 */
	private ImageManager manager;

	/**
	 * the queue used to recive orders
	 */
	private BlockingQueue<ImageKey> orders;

	/**
	 * constructor requiring imagemanager to perform action
	 * 
	 * @param manager -
	 *            the image manager which should be used
	 */
	public PreloadService(ImageManager manager) {
		this.manager = manager;

		// create blocking order Queue
		orders = new LinkedBlockingQueue<ImageKey>();
	}

	/**
	 * assigne a new order to this service
	 * 
	 * @param order
	 */
	public void addPreloadJob(ImageKey order) {
		// just add order to queue
		if (order != null) {
			// remove oldest elements until MAX_QUEUE_SIZE is reached
			while (orders.size() >= MAX_QUEUE_SIZE) {
				orders.poll();
			}
			orders.offer(order);
		}
	}

	/**
	 * kill this service
	 */
	public void kill() {
		addPreloadJob(POISEND_ORDER);
	}

	/**
	 * this methode will perform the preload action
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		// "endless loop" until this thread got killed
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
		ImageKey order = getNextOrder();
		while (order != POISEND_ORDER) {
			// load image
			manager.getImage(order.getImg(), order.getDim());

			// get next order
			order = getNextOrder();
		}
	}

	/**
	 * get the next element from the order queue block until there is an order.
	 * 
	 * @return - the next order
	 */
	private ImageKey getNextOrder() {
		ImageKey order = null;
		// retry until there is an order
		while (order == null) {
			try {
				// get next order, blocks if there is no
				order = orders.take();
			} catch (InterruptedException ie) {
				// just retry
			}
		}
		return order;
	}

}
