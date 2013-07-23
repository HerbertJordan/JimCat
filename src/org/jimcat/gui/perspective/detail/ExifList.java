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

package org.jimcat.gui.perspective.detail;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.jimcat.model.ExifMetadata;


/**
 * This element is responsible for showing exif metadata.
 * 
 * @see ExifMetadata
 * 
 * $Id$
 * @author Herbert
 */
public class ExifList extends JPanel {

	/**
	 * the Font used for descriptions
	 */
	private static final Font LABEL_FONT = new Font("Tahoma", Font.BOLD, 11);

	/**
	 * the format used to display the date taken TODO: make configable by
	 * Configuration property
	 */
	private static final String dateFormat = "dd.MM.yyyy";

	/**
	 * the format used to display the creation time TODO: make configable by
	 * Configuration property
	 */
	private static final String timeFormat = "hh:mm:ss";

	/**
	 * the version of this component when no information is available
	 */
	private JPanel notAvailable;

	/**
	 * the version of this component when there is information available
	 */
	private JPanel available;

	/**
	 * the metadata displayed - null if there arn't any
	 */
	private ExifMetadata metadata;

	/**
	 * the label to display the manufactor value
	 */
	private JLabel manufacturer;

	/**
	 * the label to display the model value
	 */
	private JLabel model;

	/**
	 * the label to display the taken value
	 */
	private JLabel dateTaken;

	/**
	 * the label to display the taken value
	 */
	private JLabel timeTaken;

	/**
	 * the label to display the exposure value
	 */
	private JLabel exposure;

	/**
	 * the label to display the aperture value, what ever this is
	 */
	private JLabel aperture;

	/**
	 * the label to display the flash value
	 */
	private JLabel flash;

	/**
	 * the label to display the focal value
	 */
	private JLabel focal;

	/**
	 * the label to display the iso value
	 */
	private JLabel iso;

	/**
	 * default constructor
	 */
	public ExifList() {
		initComponets();
	}

	/**
	 * build up components
	 */
	private void initComponets() {
		// ////////////////////////////////
		// not available version
		notAvailable = new JPanel();
		notAvailable.setLayout(new BorderLayout());
		notAvailable.setOpaque(false);

		// label
		JLabel info = new JLabel();
		info.setText("No Exif Data Available");
		info.setOpaque(false);
		info.setFont(LABEL_FONT);
		info.setHorizontalAlignment(SwingConstants.CENTER);
		notAvailable.add(info, BorderLayout.CENTER);

		// /////////////////////////////////
		// build up available version
		available = new JPanel();
		available.setLayout(new GridLayout(0, 1));
		available.setOpaque(false);

		// manufactorer
		available.add(getDescriptionLabel("Producer"));
		manufacturer = getInfoLabel();
		available.add(manufacturer);

		// model
		available.add(getDescriptionLabel("Model"));
		model = getInfoLabel();
		available.add(model);

		// date taken
		available.add(getDescriptionLabel("Date"));
		dateTaken = getInfoLabel();
		available.add(dateTaken);

		// time taken
		available.add(getDescriptionLabel("Time"));
		timeTaken = getInfoLabel();
		available.add(timeTaken);

		// focal
		available.add(getDescriptionLabel("Focal length"));
		focal = getInfoLabel();
		available.add(focal);

		// exposure
		available.add(getDescriptionLabel("Exposure"));
		exposure = getInfoLabel();
		available.add(exposure);

		// aperture
		available.add(getDescriptionLabel("Aperture"));
		aperture = getInfoLabel();
		available.add(aperture);

		// iso
		available.add(getDescriptionLabel("ISO"));
		iso = getInfoLabel();
		available.add(iso);

		// flash
		available.add(getDescriptionLabel("Flash"));
		flash = getInfoLabel();
		available.add(flash);

		// assemble
		setLayout(new BorderLayout());
		setOpaque(false);
		add(notAvailable, BorderLayout.CENTER);
	}

	/**
	 * used to create a description label
	 * 
	 * @param titel
	 * @return the desciption Label
	 */
	private JLabel getDescriptionLabel(String titel) {
		JLabel label = new JLabel();
		label.setText(titel);
		label.setOpaque(false);
		label.setFont(LABEL_FONT);
		return label;
	}

	/**
	 * used to create a info label
	 * 
	 * @return the info Label
	 */
	private JLabel getInfoLabel() {
		JLabel label = new JLabel();
		label.setText("");
		label.setOpaque(false);
		return label;
	}

	/**
	 * this will prepair the given string for display.
	 * 
	 * it will trimed if it is too long.
	 * 
	 * @param str
	 * @return the trimmed string
	 */
	private String trimString(String str) {
		
		final int maxStringLength = 20;
		
		if (str == null) {
			return "";
		}
		String result = str;
		if (str.length() > maxStringLength) {
			result = str.substring(0, maxStringLength - 3) + "...";
		}
		return "    " + result;
	}

	/**
	 * this method will update the currently displayed exifdata
	 * 
	 * @param data -
	 *            the new exifmetadata or null if there aren't any
	 */
	public void setExifData(ExifMetadata data) {
		// if new exifdata are null
		if (data == null) {
			// if old hasn't been exchange inlay
			if (metadata != null) {
				metadata = data;
				removeAll();
				add(notAvailable, BorderLayout.CENTER);
				revalidate();
			}

			return;
		}
		// update fields
		String str = null;

		str = data.getManufacturer();
		manufacturer.setText(trimString(str));
		manufacturer.setToolTipText(str);

		str = data.getModel();
		model.setText(trimString(str));
		model.setToolTipText(str);

		String date = "";
		String time = "";
		if (data.getDateTaken() != null) {
			date = data.getDateTaken().toString(dateFormat);
			time = data.getDateTaken().toString(timeFormat);
		}

		dateTaken.setText(trimString(date));
		timeTaken.setText(trimString(time));

		str = data.getExposure();
		exposure.setText(trimString(str + "s"));
		exposure.setToolTipText(str);

		str = data.getAperture();
		aperture.setText(trimString("f/" + str));
		aperture.setToolTipText(str);

		str = data.getFlash();
		flash.setText(trimString(str));
		flash.setToolTipText(str);

		str = data.getFocal();
		focal.setText(trimString(str + "mm"));
		focal.setToolTipText(str);

		str = data.getIso();
		iso.setText(trimString(str));
		iso.setToolTipText(str);

		if (metadata == null) {
			// swap view
			removeAll();
			add(available, BorderLayout.CENTER);
			revalidate();
		}
		// set metadata;
		metadata = data;
	}
}
