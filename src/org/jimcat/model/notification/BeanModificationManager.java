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

package org.jimcat.model.notification;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jimcat.services.ServiceLocator;
import org.jimcat.services.failurefeedback.FailureDescription;

/**
 * This manager is offereing an option to encapsulate several bean changes to a
 * single action.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public final class BeanModificationManager {

	/**
	 * a map to keep track of running transactions
	 */
	private static Map<Thread, BeanModification> runningTransactions;

	static {
		// init running Transactions
		runningTransactions = Collections.synchronizedMap(new HashMap<Thread, BeanModification>());
	}

	/**
	 * prevent objects of this type
	 */
	private BeanModificationManager() {
		// just to look instantiation
	}

	/**
	 * use this methode to start a new transaction
	 */
	public static void startTransaction() {
		BeanModification current = getRunningTransaction();
		if (current != null) {
			System.err.println("WARNING: there was an unfinished Transaction, initated by: ");
			current.getTransactionInitator().printStackTrace();
			System.err.println("INTERRUPTED BY: ");
			new Exception().printStackTrace();
			commitTransaction();
		}

		// create new Transaction
		current = new BeanModification();
		runningTransactions.put(Thread.currentThread(), current);
	}

	/**
	 * @return current transaction - null if there is no transaction
	 */
	public static BeanModification getRunningTransaction() {
		return runningTransactions.get(Thread.currentThread());
	}

	/**
	 * commit current transaction
	 * 
	 * @throws IllegalStateException -
	 *             if there is no current transaction
	 */
	public static void commitTransaction() throws IllegalStateException {
		BeanModification current = getRunningTransaction();
		if (current == null) {
			throw new IllegalStateException("no current transaction");
		}

		// remove and commit transaction
		runningTransactions.remove(Thread.currentThread());
		try {
			current.commit();
		} catch (Exception e) {
			String threadName = Thread.currentThread().getName();
			String msg = "Error executing transaction";
			FailureDescription description = new FailureDescription(e, threadName, msg);
			ServiceLocator.getFailureFeedbackService().reportFailure(description);
		}
	}

}
