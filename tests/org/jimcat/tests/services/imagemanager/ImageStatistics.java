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

package org.jimcat.tests.services.imagemanager;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.Set;

import org.jimcat.model.Image;
import org.jimcat.persistence.RepositoryLocator;
import org.jimcat.persistence.RepositoryLocator.ConfigType;
import org.jimcat.services.imagemanager.ImageQuality;
import org.jimcat.services.imagemanager.ImageUtil;

/**
 * This class is used to generate statistical values about loaden, resolving and scaling images.
 *
 * $Id$
 * @author Herbert
 */
public class ImageStatistics {

	// a list of resolutions to test
	private static final Dimension L = new Dimension(1600,1200);
	private static final Dimension M = new Dimension(1280,1024);
	private static final Dimension S = new Dimension(1024,768);
	private static final Dimension XS = new Dimension(800,600);
	
	private static final Dimension RESOLUTIONS[] = { L, M, S, XS };
	
	private static final String SEPERATOR = ",";
	
	/**
	 * the main program will load a list of images from the 
	 * configured repository, iterate through them and print 
	 * statistical information 
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// load images
	    RepositoryLocator.setConfigType(ConfigType.CONFIG);
	    Set<Image> images = RepositoryLocator.getImageRepository().getAll();
	    RepositoryLocator.getRepositoryControl().shutdown();
	     
	    System.out.println("titel,width,height,load,resolveBest,resolveFast");
	    
	    // generate information
	    for (Image img : images) {
	    	testImage(img);
	    }
	    
	    // clean shutdown
	    RepositoryLocator.getRepositoryControl().shutdown();
    }
	
	/**
	 * test one given image
	 * 
	 * @param img
	 */
	public static void testImage(Image img) {
		
		long time = 0;
		try {
			// first - load file from disk
			time = System.nanoTime();
			byte[] content = ImageUtil.loadFile(img.getMetadata().getPath());
			long load = System.nanoTime() - time;
			
			// resolve image
			time = System.nanoTime();
			BufferedImage source = ImageUtil.loadImage(content, ImageQuality.getBest());
			long resolveBest = System.nanoTime() - time;
			
			// resolve image
			time = System.nanoTime();
			ImageUtil.loadImage(content, ImageQuality.getFastest());
			long resolveFast = System.nanoTime() - time;
			
			
			// scale image best quality
			long scaleBest[] = new long[RESOLUTIONS.length];
			for (int i=0; i<RESOLUTIONS.length; i++) {
				Dimension res = ImageUtil.getScaledDimension(img.getMetadata().getWidth(), img.getMetadata().getHeight(), RESOLUTIONS[i], false);
				time = System.nanoTime();
				ImageUtil.getScaledInstance(source, res, ImageQuality.getBest());
				scaleBest[i] = System.nanoTime() - time;
			}
			
			// scale image fastest quality
			long scaleFast[] = new long[RESOLUTIONS.length];
			for (int i=0; i<RESOLUTIONS.length; i++) {
				Dimension res = ImageUtil.getScaledDimension(img.getMetadata().getWidth(), img.getMetadata().getHeight(), RESOLUTIONS[i], false);
				time = System.nanoTime();
				ImageUtil.getScaledInstance(source, res, ImageQuality.getFastest());
				scaleFast[i] = System.nanoTime() - time;
			}
			
			// print report
			System.out.print(img.getTitle());
			System.out.print(SEPERATOR);
			
			System.out.print(img.getMetadata().getWidth());
			System.out.print(SEPERATOR);
			
			System.out.print(img.getMetadata().getHeight());
			System.out.print(SEPERATOR);
			
			System.out.print(load);
			System.out.print(SEPERATOR);
			
			System.out.print(resolveBest);
			System.out.print(SEPERATOR);
			
			System.out.print(resolveFast);
			System.out.print(SEPERATOR);
			
			for (int i=0; i<scaleBest.length; i++) {
				System.out.print(scaleBest[i]);
				System.out.print(SEPERATOR);
			}
			
			for (int i=0; i<scaleBest.length; i++) {
				System.out.print(scaleFast[i]);
				System.out.print(SEPERATOR);
			}
			
			System.out.println();
		} catch (Throwable ioe) {
			//ioe.printStackTrace();
		}
	}
	
}
