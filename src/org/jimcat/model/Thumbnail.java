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

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.imageio.ImageIO;

import org.jimcat.services.imagemanager.ImageQuality;
import org.jimcat.services.imagemanager.ImageUtil;

/**
 * A Thumbnail assigend to an image. The Thumbnail is immutable - once created
 * there will be no way of changing it.
 * 
 * 
 * $Id: Thumbnail.java 999 2007-09-14 20:02:58Z cleiter $
 * 
 * @author Herbert
 */
public class Thumbnail {

	/**
	 * The thumbnail dimension used
	 */
	public static final int MAX_THUMBNAIL_SIZE = 300;

	/**
	 * The various sizes thumbnails should be stored in.
	 */
	public static final int THUMBNAIL_SIZES[] = new int[] { 100, 150, 200, 250, MAX_THUMBNAIL_SIZE };

	@SuppressWarnings("unused")
	private Long id;

	/**
	 * image raw data - one array for each size
	 */
	private SortedMap<Integer, byte[]> data;

	/**
	 * creates a new thumb nail instance using given image the image has do be
	 * completely loaded
	 * 
	 * @param image
	 * @throws IOException
	 */
	public Thumbnail(BufferedImage image) throws IOException {
		// scale image
		int height = image.getHeight();
		int width = image.getWidth();

		// create resulting arraies
		data = new TreeMap<Integer, byte[]>();
		BufferedImage cur = image;
		for (int i = THUMBNAIL_SIZES.length - 1; i >= 0; i--) {
			int dim = THUMBNAIL_SIZES[i];

			// get scale factor - keep aspects
			float factorHeight = height / (float) dim;
			float factorWidth = width / (float) dim;
			float factor = Math.max(factorHeight, factorWidth);

			factor = Math.max(factor, 1);

			int thumbHeight = Math.max(1, (int) (height / factor));
			int thumbWidth = Math.max(1, (int) (width / factor));

			Dimension thumb = new Dimension(thumbWidth, thumbHeight);

			// scale image, use smooth, it's not time critical
			cur = ImageUtil.getScaledInstance(cur, thumb, ImageQuality.getBest());

			// generate byte array
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			try {
				ImageIO.write(cur, "JPG", out);
				data.put(dim, out.toByteArray());
			} catch (IOException ioe) {
				throw ioe;
			}
		}
	}

	/**
	 * Gets the stored thumb nail which is just bigger as the given size. 
	 * 
	 * @param size
	 *            minimum dimension of the requested image
	 * @return a thumb nail with the given size
	 */
	public BufferedImage getImage(int size) {
		if (data == null) {
			return null;
		}

		// get next higher thumb nail size
		int dim = -1;
		Iterator<Integer> iter = data.keySet().iterator();
		while (iter.hasNext() && dim < size) {
			dim = iter.next();
		}

		// construct image
		try {
			return ImageUtil.loadImage(data.get(dim), ImageQuality.getBest());
		} catch (IOException ioe) {
			// make dimension unusable
			data.remove(dim);
		}
		return null;
	}

	/**
	 * Retrieves the biggest contained thumb nail.
	 * 
	 * @return - the contained image or null, if there is no image contained
	 */
	public BufferedImage getMaxSizeImage() {
		// get biggest available thumb nail
		int max = 0;
		for (int size : data.keySet()) {
			max = size;
		}
		return getImage(max);
	}
}
