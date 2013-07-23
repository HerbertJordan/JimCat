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

/**
 * A interface for histogram model listeners.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public interface HistogramModelListener {

	/**
	 * this methode is called if any value within the Histogram Model has
	 * changed.
	 * 
	 * The event should containe the list of changed nodes.
	 * 
	 * @param event -
	 *            describing the event
	 */
	public void valueChanged(HistogramModelEvent event);

	/**
	 * this methode is called if a big structural change happend.
	 * 
	 * There may be new or fewer dimensions / resolutions. The Listener should
	 * revalidate all depending data.
	 * 
	 * @param model -
	 *            the source of this event
	 */
	public void structureChanged(HistogramModel model);

	/**
	 * this methode should be called if a new limiter is established within this
	 * model.
	 * 
	 * The event is providing the new limit.
	 * 
	 * @param event
	 */
	public void limitChanged(HistogramModelEvent event);

}
