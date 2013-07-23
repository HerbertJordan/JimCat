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

package org.jimcat.gui.perspective;

import javax.swing.JPanel;

import org.jimcat.gui.ViewControl;

/**
 * A superclass of all perspectives.
 * 
 * It lists those operations a perspective must support to be used within this
 * client.
 * 
 * $Id: AbstractPerspective.java 942 2007-06-16 09:07:47Z 07g1t1u3 $
 * 
 * @author Herbert
 */
public abstract class AbstractPerspective extends JPanel {

	/**
	 * the library view behind this perspective
	 */
	private ViewControl control;

	/**
	 * is this view active
	 */
	private boolean active = true;

	/**
	 * constructor for a perspective
	 * 
	 * @param control
	 */
	public AbstractPerspective(ViewControl control) {
		this.control = control;
	}

	/**
	 * returns the ViewControl under this perspective
	 * 
	 * @return the ViewControl
	 */
	public ViewControl getViewControl() {
		return control;
	}

	/**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @param active
	 *            the active to set
	 */
	public void setActive(boolean active) {
		if (this.active != active) {
			this.active = active;
			if (active) {
				enablePerspective();
			} else {
				disablePerspective();
			}
		}
	}

	/**
	 * This methode is called when this perspective is becoming active
	 */
	protected abstract void enablePerspective();

	/**
	 * This methode is called when this perspective is becoming deactive
	 */
	protected abstract void disablePerspective();
}
