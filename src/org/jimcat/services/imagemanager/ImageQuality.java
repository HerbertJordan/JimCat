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

import java.awt.RenderingHints;

/**
 * An enumeration of image Quality. This is used by the ImageManager to choose
 * rendering options.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public enum ImageQuality {
	// must be order from best to worst
	BEST(RenderingHints.VALUE_INTERPOLATION_BICUBIC, true), 
	FASTEST(RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR, false),
	THUMBNAIL(RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR, false);
	
	/**
	 * use this if you would like to get te best quality possible without
	 * needing to now all qualities.
	 * 
	 * @return the best image quality
	 */
	public static ImageQuality getBest() {
		return BEST;
	}

	/**
	 * use this if you would like to get te fastes quality possible without
	 * needing to now all qualities.
	 * 
	 * @return the fastest image quality
	 */
	public static ImageQuality getFastest() {
		return FASTEST;
	}

	/**
	 * the rendering hind used by the Graphics2D to scale images
	 */
	private Object hint;

	/**
	 * should the scaling process been split up into several steps?
	 */
	private boolean intermediateSteps;

	/**
	 * a private constructor requesting fields
	 * 
	 * @param hint
	 * @param intermediateSteps
	 */
	private ImageQuality(Object hint, boolean intermediateSteps) {
		this.hint = hint;
		this.intermediateSteps = intermediateSteps;
	}

	/**
	 * Get the rendering hind for graphics 2d to support this quality level
	 * 
	 * @return the hint
	 */
	public Object getHint() {
		return hint;
	}

	/**
	 * does this quality level require intermediate rendering steps
	 * 
	 * @return the immediateSteps
	 */
	public boolean requiresIntermediateSteps() {
		return intermediateSteps;
	}

}
