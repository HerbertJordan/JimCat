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

package org.jimcat.model;

/**
 * An enumeration of possible ImageRotation values.
 * 
 * Names are rotation angle in degree clockwise.
 * 
 * $Id: ImageRotation.java 934 2007-06-15 08:40:58Z 07g1t1u2 $
 * 
 * @author Herbert
 */
public enum ImageRotation {
	ROTATION_0(0), ROTATION_90(Math.PI/2.0), ROTATION_180(Math.PI), ROTATION_270(Math.PI*3.0/2.0);

	static {
		// init neighbors
		ROTATION_0.setClockwiseNeighbor(ROTATION_90);
		ROTATION_0.setCounterClockwiseNeighbor(ROTATION_270);

		ROTATION_90.setClockwiseNeighbor(ROTATION_180);
		ROTATION_90.setCounterClockwiseNeighbor(ROTATION_0);

		ROTATION_180.setClockwiseNeighbor(ROTATION_270);
		ROTATION_180.setCounterClockwiseNeighbor(ROTATION_90);

		ROTATION_270.setClockwiseNeighbor(ROTATION_0);
		ROTATION_270.setCounterClockwiseNeighbor(ROTATION_180);
	}

	/**
	 * value if you would turn it clockwise
	 */
	private ImageRotation clockWiseNeighbor;

	/**
	 * value if you would turn it counterclockwise
	 */
	private ImageRotation counterClockWiseNeighbor;

	/**
	 * the angle of rotation
	 */
	private double angle;

	/**
	 * this constructor is requesting an angle of rotation the constructed item
	 * will represent
	 * 
	 * @param angle
	 */
	private ImageRotation(double angle) {
		this.angle = angle;
	}

	/**
	 * sets the clockwise Neighbor
	 * 
	 * @param neighbor
	 */
	private void setClockwiseNeighbor(ImageRotation neighbor) {
		clockWiseNeighbor = neighbor;
	}

	/**
	 * sets the counterclockwise Neighbor
	 * 
	 * @param neighbor
	 */
	private void setCounterClockwiseNeighbor(ImageRotation neighbor) {
		counterClockWiseNeighbor = neighbor;
	}

	/**
	 * returns its clockwise neighbor
	 * 
	 * @return the next rotation in clockwise direction
	 */
	public ImageRotation getClockwiseNeighbor() {
		return clockWiseNeighbor;
	}

	/**
	 * returns its counterclockwise Neighbor
	 * 
	 * @return the next rotation in counter clockwise direction
	 */
	public ImageRotation getCounterClockwiseNeighbor() {
		return counterClockWiseNeighbor;
	}

	/**
	 * @return the angle
	 */
	public double getAngle() {
		return angle;
	}

}
