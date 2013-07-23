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

package org.jimcat.services.rename;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jimcat.model.ExifMetadata;
import org.jimcat.model.Image;
import org.jimcat.model.ImageRating;
import org.joda.time.DateTime;

/**
 * This class is able to rename a List of imagenames according to a format
 * string
 * 
 * 
 * $Id$
 * 
 * @author Christoph
 */
public class Renamer {

	private enum Date {
		TAKEN, MODIFICATION, ADDED
	}

	private int digits = 1;

	private String configString = "";

	private String escapeCharacter = "$";

	private String unknownResultCharacter = "X";

	private Date useDate = Date.TAKEN;

	/**
	 * 
	 * This method calls the getNewName method to parse the configString to give
	 * each image a new name corresponding to the string and placeholders in
	 * configString.
	 * 
	 * @param images
	 *            a list of images to be renamed.
	 * @return a list of strings that represent the new names of the images
	 */
	public List<String> getNewNames(List<Image> images) {

		List<String> newNames = new ArrayList<String>(images.size());

		int i = 1;
		for (Image image : images) {
			newNames.add(getNewName(image, i++));
		}

		return newNames;
	}

	@SuppressWarnings("null")
	private String getNewName(Image image, int n) {
		String newName = configString;
		DateTime date = null;

		switch (useDate) {
		case MODIFICATION:
			date = image.getMetadata().getModificationDate();
			break;
		case ADDED:
			date = image.getMetadata().getDateAdded();
			break;
		case TAKEN:

			ExifMetadata exifMetadata = image.getExifMetadata();

			if (exifMetadata != null) {
				date = exifMetadata.getDateTaken();
			}
			break;
		}

		String random;

		do {
			random = String.valueOf(Math.random());
		} while (configString.contains(random));

		if (hasParameter(escapeCharacter.charAt(0))) {
			newName = newName.replace(escapeCharacter + escapeCharacter, random);
		}

		if (hasParameter('n')) {
			String number = String.format("%0" + digits + "d", new Integer(n));
			newName = newName.replace(escapeCharacter + "n", number);
		}

		if (hasParameter('w')) {
			newName = newName.replace(escapeCharacter + "w", "" + image.getMetadata().getWidth());
		}

		if (hasParameter('h')) {
			newName = newName.replace(escapeCharacter + "h", "" + image.getMetadata().getHeight());
		}

		if (hasParameter('d')) {
			if (date == null) {
				newName = newName.replace(escapeCharacter + "d", unknownResultCharacter);
			} else {
				String day = formatNumber(date.getDayOfMonth(), 2);
				newName = newName.replace(escapeCharacter + "d", day);
			}
		}

		if (hasParameter('m')) {
			if (date == null) {
				newName = newName.replace(escapeCharacter + "m", unknownResultCharacter);
			} else {
				String month = formatNumber(date.getMonthOfYear(), 2);
				newName = newName.replace(escapeCharacter + "m", month);
			}
		}

		if (hasParameter('y')) {
			if (date == null) {
				newName = newName.replace(escapeCharacter + "y", unknownResultCharacter);
			} else {
				String year = formatNumber(date.getYear(), 4);
				newName = newName.replace(escapeCharacter + "y", year);
			}
		}

		if (hasParameter('H')) {
			if (date == null) {
				newName = newName.replace(escapeCharacter + "H", unknownResultCharacter);
			} else {
				String hour = formatNumber(date.getHourOfDay(), 2);
				newName = newName.replace(escapeCharacter + "H", hour);
			}
		}

		if (hasParameter('M')) {
			if (date == null) {
				newName = newName.replace(escapeCharacter + "M", unknownResultCharacter);
			} else {
				String minute = formatNumber(date.getMinuteOfHour(), 2);
				newName = newName.replace(escapeCharacter + "M", minute);
			}
		}

		if (hasParameter('S')) {
			if (date == null) {
				newName = newName.replace(escapeCharacter + "S", unknownResultCharacter);
			} else {
				String seconds = formatNumber(date.getSecondOfMinute(), 2);
				newName = newName.replace(escapeCharacter + "S", seconds);
			}
		}

		if (hasParameter('r')) {
			newName = newName.replace(escapeCharacter + "r", ratingToString(image.getRating()));
		}

		if (hasParameter('f')) {
			if (image.getMetadata() != null && image.getMetadata().getPath() != null) {
				String fileName = removeFileType(image.getMetadata().getPath());
				newName = newName.replace(escapeCharacter + "f", fileName);
			} else {
				newName = newName.replace(escapeCharacter + "f", unknownResultCharacter);
			}
		}

		if (newName.length() == 0) {
			return newName;
		}

		// if the user is just typing dont show the last $
		// but if he wants a $ at the and (by using $$) allow it and even allow
		// $$ at the end

		int escapeCharactersAtEnd = 0;
		int index = newName.length() - 1;

		while (index >= 0 && newName.charAt(index--) == escapeCharacter.charAt(0)) {
			escapeCharactersAtEnd++;
		}

		if (escapeCharactersAtEnd % 2 == 1) {
			newName = newName.substring(0, newName.length() - 1);
		}

		if (hasParameter(escapeCharacter.charAt(0))) {
			newName = newName.replace(random, escapeCharacter);
		}

		// set at the end because the original title could contain evil control
		// sequences
		if (hasParameter('t')) {
			newName = newName.replace(escapeCharacter + "t", image.getTitle());
		}

		return newName;
	}

	private String ratingToString(ImageRating rating) {
		switch (rating) {
		case NONE:
			return "0";
		case ONE:
			return "1";
		case TWO:
			return "2";
		case THREE:
			return "3";
		case FOUR:
			return "4";
		case FIVE:
			return "5";
		default:
			throw new IllegalStateException("Unknown rating");
		}
	}

	/**
	 * 
	 * use the date taken
	 */
	public void useDateTaken() {
		useDate = Date.TAKEN;
	}

	/**
	 * 
	 * use date added
	 */
	public void useDateAdded() {
		useDate = Date.ADDED;
	}

	/**
	 * 
	 * use modification date
	 */
	public void useModificationDate() {
		useDate = Date.MODIFICATION;
	}

	private String formatNumber(int n, int numDigits) {
		return String.format("%0" + numDigits + "d", new Integer(n));
	}

	private boolean hasParameter(char c) {
		return configString.contains(escapeCharacter + c);
	}

	/**
	 * set the configString which determines the new names
	 * 
	 * @param configString
	 */
	public void setConfigString(String configString) {
		this.configString = configString;
	}

	/**
	 * 
	 * set the number of digits used for replacing number placeholder
	 * 
	 * @param digits
	 */
	public void setDigits(int digits) {
		this.digits = digits;
	}

	/**
	 * 
	 * The method getFileType is used to get the file type of a image, which
	 * means cutting the suffix that follows the last point in the file name
	 * 
	 * @param file
	 * @return the file type as a string including the point before it
	 */
	public String getFileType(File file) {
		String fileName = file.getName();
		int lastPoint = fileName.lastIndexOf(".");
		if (lastPoint != -1) {
			return fileName.substring(lastPoint);
		}
		return "";
	}

	/**
	 * 
	 * calls getName for a file and removes filetype (f.e. .JPG)
	 * 
	 * @param file
	 * @return the file name without file type
	 */
	public String removeFileType(File file) {
		String fileName = file.getName();
		int lastPoint = fileName.lastIndexOf(".");
		if (lastPoint != -1) {
			return fileName.substring(0, lastPoint);
		}
		return "";
	}

	/**
	 * @return the escapeCharacter
	 */
	public String getEscapeCharacter() {
		return escapeCharacter;
	}

	/**
	 * @return the unknownResultCharacter
	 */
	public String getUnknownResultCharacter() {
		return unknownResultCharacter;
	}

}
