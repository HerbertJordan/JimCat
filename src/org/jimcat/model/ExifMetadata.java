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

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.joda.time.DateTime;

/**
 * A simple immutable Databean containing all exif metadata information stored
 * with the system.
 * 
 * $Id: ExifMetadata.java 999 2007-09-14 20:02:58Z cleiter $
 * 
 * @author Herbert
 */
public class ExifMetadata {

	private Long id;

	private String manufacturer;

	private String model;

	private DateTime dateTaken;

	private String exposure;

	private String aperture;

	private String flash;

	private String focal;

	private String iso;

	/**
	 * direct constructor using all fields - fields are immutable.
	 * 
	 * @param manufacturer
	 * @param model
	 * @param dateTaken
	 * @param exposure
	 * @param aperture
	 * @param flash
	 * @param focal
	 * @param iso
	 */
	public ExifMetadata(String manufacturer, String model, DateTime dateTaken, String exposure, String aperture,
	        String flash, String focal, String iso) {
		this.manufacturer = manufacturer;
		this.model = model;
		this.dateTaken = dateTaken;
		this.exposure = exposure;
		this.aperture = aperture;
		this.flash = flash;
		this.focal = focal;
		this.iso = iso;
	}

	/**
	 * get hiberante id
	 * 
	 * @return the id assigend by hibernate
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @return the aperture
	 */
	public String getAperture() {
		return aperture;
	}

	/**
	 * @return the dateTaken
	 */
	public DateTime getDateTaken() {
		return dateTaken;
	}

	/**
	 * @return the exposure
	 */
	public String getExposure() {
		return exposure;
	}

	/**
	 * @return the flash
	 */
	public String getFlash() {
		return flash;
	}

	/**
	 * @return the focal
	 */
	public String getFocal() {
		return focal;
	}

	/**
	 * @return the iso
	 */
	public String getIso() {
		return iso;
	}

	/**
	 * @return the manufacturer
	 */
	public String getManufacturer() {
		return manufacturer;
	}

	/**
	 * @return the model
	 */
	public String getModel() {
		return model;
	}

	/**
	 * Return a String representation of the metadata.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
