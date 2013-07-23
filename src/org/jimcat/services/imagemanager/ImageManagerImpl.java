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

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.jimcat.model.Image;
import org.jimcat.model.ImageRotation;
import org.jimcat.model.Thumbnail;
import org.jimcat.services.ServiceLocator;
import org.jimcat.services.failurefeedback.FailureDescription;

/**
 * a concreate implementation of an ImageManager - dummy implementation -
 * 
 * 
 * $Id: ImageManagerImpl.java 955 2007-06-19 11:54:02Z 07g1t1u3 $
 * 
 * @author Herbert
 */
public final class ImageManagerImpl implements ImageManager {

	/**
	 * the number of retries if there are out of memory errors
	 */
	private static final int OUT_OF_MEMORY_RETRIES = 3;

	/**
	 * The singelton instance
	 */
	private static final ImageManagerImpl INSTANCE = new ImageManagerImpl();

	/**
	 * the cache used for image caching
	 */
	private Map<ImageKey, ImageStore> cache;

	/**
	 * the preloadservice used by this image manager implementation
	 */
	private PreloadService preloadService;

	/**
	 * get singelton instance
	 * 
	 * @return an instance of ImageManager
	 */
	public static ImageManager getInstance() {
		return INSTANCE;
	}

	/**
	 * simple constructor
	 */
	private ImageManagerImpl() {
		// create cache
		cache = Collections.synchronizedMap(new LinkedHashMap<ImageKey, ImageStore>());

		// create preloadservice
		preloadService = new PreloadService(this);

		// startup preloadservice
		Thread loader = new Thread(preloadService);
		loader.setDaemon(true);
		loader.setPriority(2);
		loader.start();
	}

	/**
	 * get given image representation if availabel
	 * 
	 * @see org.jimcat.services.imagemanager.ImageManager#getImageIfAvailable(org.jimcat.model.Image,
	 *      java.awt.Dimension, org.jimcat.services.imagemanager.ImageQuality)
	 */
	public BufferedImage getImageIfAvailable(Image img, Dimension dimension, ImageQuality quality) {
		return getImageInternal(img, dimension, quality, false);
	}

	/**
	 * Get an image with the given quality.
	 * 
	 * @see org.jimcat.services.imagemanager.ImageManager#getImage(org.jimcat.model.Image,
	 *      java.awt.Dimension, org.jimcat.services.imagemanager.ImageQuality)
	 */
	public BufferedImage getImage(Image img, Dimension dimension, ImageQuality quality) {
		return getImageInternal(img, dimension, quality, true);
	}

	/**
	 * 
	 * get a image showing the image represented by the image bean. It will
	 * retrieve it as fast as possible. Nevertheless, if there is an image in
	 * good quality, it will return it.
	 * 
	 * @see org.jimcat.services.imagemanager.ImageManager#getImage(org.jimcat.model.Image,
	 *      java.awt.Dimension)
	 */
	@SuppressWarnings("unused")
	public BufferedImage getImage(Image img, Dimension dimension) {
		// get the images as fast as possible. Nevertheless, if there is an
		// version in good quality, get it
		return getImage(img, dimension, ImageQuality.getBest());
	}

	/**
	 * add the given Image to a list of images which should be preloaded
	 * 
	 * @see org.jimcat.services.imagemanager.ImageManager#preLoadImage(org.jimcat.model.Image,
	 *      java.awt.Dimension)
	 */
	public void preLoadImage(Image img, Dimension dimension) {
		// add order to preload service
		ImageKey order = new ImageKey(img, dimension);
		preloadService.addPreloadJob(order);
	}

	/**
	 * shutdown this image manager
	 * 
	 * @see org.jimcat.services.imagemanager.ImageManager#shutdown()
	 */
	public void shutdown() {
		// kill preload service
		preloadService.kill();
	}

	/**
	 * flush content
	 * 
	 * @see org.jimcat.services.imagemanager.ImageManager#flushImage(Image,
	 *      BufferedImage)
	 */
	public void flushImage(Image image, BufferedImage sourceImage) {
		// iterate through keyset to find victems
		// lock cache
		synchronized (cache) {
			// walk through the cache set
			Iterator<ImageKey> iter = cache.keySet().iterator();
			while (iter.hasNext()) {
				ImageKey curKey = iter.next();
				// if image is equal
				if (ObjectUtils.equals(curKey.getImg(), image)) {
					// flush
					iter.remove();
				}
			}

			// add new source image
			if (sourceImage != null) {
				ImageRotation rotation = image.getRotation();
				addToCache(new ImageKey(image, null, rotation), sourceImage, ImageQuality.getBest());
			}
		}
	}

	/**
	 * get image representation for given image with requested dimension and
	 * quality. if forceLoad is false, it will return null if image is not
	 * stored within the cache
	 * 
	 * @param img -
	 *            jimcat representation of image
	 * @param dimension -
	 *            the requested image dimension
	 * @param quality -
	 *            the requested image quality
	 * @param forceLoad -
	 *            ture, it will load image anyway, false it will only use cache
	 * @return an image representation for the given image
	 */
	private BufferedImage getImageInternal(Image img, Dimension dimension, ImageQuality quality, boolean forceLoad) {
		// to overcome out of memory errors => count
		int i = 0;
		while (true) {
			try {
				// there are 3 Cache steps
				// pure source -> rotated source -> scaled result
				// all those have to be checked and maintained

				// create key (scaled result)
				int width = 0;
				int height = 0;
				ImageRotation rotation = img.getRotation();
				if (rotation == ImageRotation.ROTATION_90 || rotation == ImageRotation.ROTATION_270) {
					// transformed
					width = img.getMetadata().getHeight();
					height = img.getMetadata().getWidth();
				} else {
					// normal
					width = img.getMetadata().getWidth();
					height = img.getMetadata().getHeight();
				}

				Dimension resultDim = ImageUtil.getScaledDimension(width, height, dimension, false);
				ImageKey key = new ImageKey(img, resultDim);

				// try to load from cache
				BufferedImage result = getImageFromCache(key, quality);

				// if it is a hit => return result
				if (result != null) {
					return result;
				}

				// shortcut if it is of thumb nail quality
				if (quality == ImageQuality.THUMBNAIL) {
					result = ImageUtil.getScaledInstance(img.getThumbnail().getImage(
					        Math.max(resultDim.width, resultDim.height)), resultDim, quality);
					addToCache(key, result, quality);
					return result;
				}

				// try to load source image from cache
				Dimension sourceDim = getSourceSize(dimension);

				// first try to load correctly rotated source
				ImageKey rotatedSourceKey = new ImageKey(img, sourceDim);
				BufferedImage source = getImageFromCache(rotatedSourceKey, quality);

				if (source == null) {
					// should image realy be loaded
					if (!forceLoad) {
						// image has to be loaded from source - if not
						// requested, don't do it
						return null;
					}

					// missed -> try to get not rotated source image
					source = getSourceImage(img, sourceDim);

					// rotate source if necessary
					if (rotation != ImageRotation.ROTATION_0) {
						source = ImageUtil.rotateImage(source, rotation);
						// add source to cache - best quality possible (its the
						// original image - just rotated)
						addToCache(new ImageKey(img, sourceDim, rotation), source, ImageQuality.getBest());
					}
				}

				// scale loaded source
				result = ImageUtil.getScaledInstance(source, resultDim, quality);

				// if scaling doesn't work => there is nothing to do
				if (result == null) {
					return null;
				}

				// add to cache and return result
				addToCache(key, result, quality);
				return result;
			} catch (OutOfMemoryError oeme) {
				i++;
				if (i > OUT_OF_MEMORY_RETRIES) {
					// something is wrong (maybe to big image to load)
					// report error
					String name = Thread.currentThread().getName() + " (loading image)";
					FailureDescription desc = new FailureDescription(oeme, name,
					        "There was not enought memory to load the image");
					ServiceLocator.getFailureFeedbackService().reportFailure(desc);
					return null;
				}
			}
		}
	}

	/**
	 * used to get a source image to scale. The image will not be rotated
	 * 
	 * @param img -
	 *            the image representation
	 * @param dimension -
	 *            a target dimension
	 * @return the source images
	 */
	private BufferedImage getSourceImage(Image img, Dimension dimension) {

		// be sure to be the only thread loading this image
		// no one else should look this element
		Object lock = null;
		if (img.getMetadata() != null && img.getMetadata().getPath() != null) {
			// normal behaviour
			lock = img.getMetadata().getPath();
		} else {
			// to avoid nullpointexception
			lock = new Object();
		}

		synchronized (lock) {
			// the resulting image
			BufferedImage res = null;
			ImageQuality sourceQuality = ImageQuality.getBest();

			// first check cache
			ImageKey sourceKey = new ImageKey(img, dimension, ImageRotation.ROTATION_0);
			res = getImageFromCache(sourceKey, sourceQuality);

			// if still null
			if (res == null) {
				// load a image, depending on requested size
				Thumbnail thumbnail = img.getThumbnail();

				// if there is a thumbnail (should be) and it is smaller than
				// requested
				// => use the thumbnail
				int thumbNailSize = Thumbnail.MAX_THUMBNAIL_SIZE;
				if (dimension != null && dimension.height <= thumbNailSize && dimension.width <= thumbNailSize) {
					res = thumbnail.getImage(Math.max(dimension.width, dimension.height));
				} else {
					// otherwise load source image from disk
					try {
						res = ImageUtil.loadImage(img.getMetadata().getPath(), sourceQuality);
					} catch (Exception e) {
						// load wasn't possible => use thumbnail (better than
						// nothing)
						res = thumbnail.getMaxSizeImage();
					}
				}

				// add to pure source to cache
				addToCache(sourceKey, res, sourceQuality);
			}

			// return result
			return res;
		}
	}

	/**
	 * get the image from the cache.
	 * 
	 * returnes null if there is no corresponding value.
	 * 
	 * @param key -
	 *            the hashkey identifieing the image
	 * @param quality -
	 *            the minium quality requested
	 * @return - the image
	 */
	private BufferedImage getImageFromCache(ImageKey key, ImageQuality quality) {

		// clear old invalid references (randomly, not allways)
		if (Math.random() < .1) {
			cleanCache();
		}

		// get image from cache
		ImageStore store = cache.get(key);
		// check if result is valid
		if (store != null) {
			return store.getImage(quality);
		}

		// no such element in cache
		return null;
	}

	/**
	 * this will remove useless WeakReference - Objects from the cache
	 */
	private void cleanCache() {
		// lock cache
		synchronized (cache) {
			// walk through the cache set
			Iterator<ImageKey> iter = cache.keySet().iterator();
			while (iter.hasNext()) {
				ImageKey curKey = iter.next();
				// check if there is still something behind the weak reference
				ImageStore store = cache.get(curKey);
				if (store == null || store.isEmpty()) {
					// remove mapping (goes through to map)
					iter.remove();
				}
			}
		}
	}

	/**
	 * add a new value to the image cache
	 * 
	 * @param key -
	 *            the image cache key to reference
	 * @param img -
	 *            the image to add
	 * @param quality -
	 *            the quality of this image
	 */
	private void addToCache(ImageKey key, BufferedImage img, ImageQuality quality) {

		// get store
		ImageStore store = cache.get(key);
		// if there is no such store, create it
		if (store == null) {
			store = new ImageStore();
			cache.put(key, store);
		}

		// add image to store
		store.addImage(img, quality);
	}

	/**
	 * determines the required size of the source file to load image.
	 * 
	 * It may return the thumb nail dimension, if it is big enough or null, if
	 * the original image is required.
	 * 
	 * @param dimension
	 * @return the source size
	 */
	private Dimension getSourceSize(Dimension dimension) {
		for (int size : Thumbnail.THUMBNAIL_SIZES) {
			if (dimension.height <= size && dimension.width <= size) {
				// Thumb nail is big enough
				return new Dimension(size, size);
			}
		}
		// original image is required
		return null;
	}

}
