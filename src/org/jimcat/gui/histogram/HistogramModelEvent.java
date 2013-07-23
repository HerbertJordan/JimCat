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

package org.jimcat.gui.histogram;

import java.util.Set;

/**
 * An HistogramModel event encapsulation.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class HistogramModelEvent {

	/**
	 * the source of this event
	 */
	private HistogramModel model;

	/**
	 * a list of changed items
	 */
	private Set<HistogramModelPath> changedElements;

	/**
	 * the newly set limit
	 */
	private HistogramModelPath limit;

	/**
	 * flag to distinguische limits
	 */
	private boolean isHigherLimit = false;

	/**
	 * a constructor requesting fields to generate an event usable for update
	 * event notifications.
	 * 
	 * @param model
	 * @param changedElements
	 */
	public HistogramModelEvent(HistogramModel model, Set<HistogramModelPath> changedElements) {
		super();
		this.model = model;
		this.changedElements = changedElements;
	}

	/**
	 * a constructor requesting fields to generate an event describing a changed
	 * limit state.
	 * 
	 * @param model -
	 *            the sourece model
	 * @param limit -
	 *            the new limit describtion
	 * @param isHigherLimit -
	 *            true if the new limit is the higher limit, false if it is the
	 *            lower
	 */
	public HistogramModelEvent(HistogramModel model, HistogramModelPath limit, boolean isHigherLimit) {
		super();
		this.model = model;
		this.limit = limit;
		this.isHigherLimit = isHigherLimit;
	}

	/**
	 * @return the changedElements
	 */
	public Set<HistogramModelPath> getChangedElements() {
		return changedElements;
	}

	/**
	 * @return the model
	 */
	public HistogramModel getModel() {
		return model;
	}

	/**
	 * get the new limit set by the limit - changed constructor
	 * 
	 * @return the new limit set by the limit - changed constructor 
	 */
	public HistogramModelPath getNewLimit() {
		return limit;
	}

	/**
	 * is the given limit a higher limit?
	 * 
	 * @return - true, if higher limit, false if lower limit
	 */
	public boolean isHigherLimit() {
		return isHigherLimit;
	}
}
