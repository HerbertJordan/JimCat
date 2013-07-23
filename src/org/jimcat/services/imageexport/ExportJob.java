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

package org.jimcat.services.imageexport;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.jimcat.model.Image;
import org.jimcat.services.jobs.Job;
import org.jimcat.services.jobs.JobManager;
import org.jimcat.services.jobs.JobUtils;

/**
 * The export job is used to export images to a directory.
 * 
 * The export job is a subclass of job. It has to configured with a
 * target-directory. Then it takes the images that shall be exported and copies
 * them from their current directory to the target-directory. Rollback is
 * supported and on rollback all previously copied images of this export job are
 * deleted.
 * 
 * 
 * $Id$
 * 
 * @author Michael
 */
public class ExportJob extends Job {
	/**
	 * The images that shall be exported
	 */
	private List<Image> imagesToExport;

	/**
	 * the names of the images to export
	 */
	private List<String> imagesToExportNames;

	/**
	 * the index for the names
	 */
	private int namesIndex;

	/**
	 * The undo list for rollback
	 */
	private List<File> undoList;

	/**
	 * the destination directory for the images
	 */
	private File destination;

	/**
	 * the index of the next image to import
	 */
	private int imageIndex = 0;

	/**
	 * 
	 * default constructor
	 */
	public ExportJob() {
		this(null);
	}

	/**
	 * 
	 * Construct a new export job with the given job manager
	 * 
	 * @param manager
	 */
	public ExportJob(JobManager manager) {
		super(manager, "Image-Export", "Exporting images ...");
		this.namesIndex = 0;
		this.imageIndex = 0;
		this.undoList = new LinkedList<File>();
	}

	/**
	 * Retruns the percentage of finished work, which means in this case how
	 * many images have been exported already.
	 * 
	 * @see org.jimcat.services.jobs.Job#getPercentage()
	 */
	@Override
	public int getPercentage() {
		// percentage of exported images
		return (int) ((imageIndex / (float) imagesToExport.size()) * 100);
	}

	/**
	 * The next rollback step method deletes the previously copied files from
	 * the file system. It does that step by step, meaning it only deletes one
	 * file when once called.
	 * 
	 * @see org.jimcat.services.jobs.Job#nextRollbackStep()
	 */
	@Override
	public boolean nextRollbackStep() {
		// if undo list is empty nothing has to be done
		if (undoList == null || undoList.isEmpty()) {
			undoList = null;
			return true;
		}
		File toDel = undoList.get(undoList.size() - 1);
		imageIndex--;
		setJobDescription("Reverting File (" + (imageIndex + 1) + "/" + imagesToExport.size() + ") ... "
		        + toDel.getName());

		// delete file
		try {
			JobUtils.deleteFile(toDel, this);
		} catch (IOException ioe) {
			// an error occured and user decided to cancel job
			// => finish step
			return false;
		}

		undoList.remove(toDel);
		return (undoList.size() == 0);
	}

	/**
	 * The nextStep method takes a image from the imagesToExport list and copies
	 * it to the destination directory. Then it puts it in the undoList, if the
	 * user wants to rollback the export.
	 * 
	 * @see org.jimcat.services.jobs.Job#nextStep()
	 */
	@Override
	public boolean nextStep() {
		if (imagesToExport.size() == 0) {
			return true;
		}
		// get the image from imagesToExport and get the file
		// from this image
		Image img = imagesToExport.get(imageIndex);
		File file = extractFile(img);
		setJobDescription("Exporting File (" + (imageIndex + 1) + "/" + imagesToExport.size() + ") ... "
		        + img.getTitle());

		try {
			// copy file
			file = copyFile(file);
			undoList.add(file);
		} catch (IOException ioe) {
			// copieing wasn't successfull - finish this element
			// set the file null to mark the failure
			file = null;
		}

		if (file != null) {
			// copying was successfull, save the last export path
			img.setLastExportPath(file.getAbsolutePath());
		}
		imageIndex++;
		return imageIndex >= imagesToExport.size();
	}

	/**
	 * The export function supports roolback, so this method returns true
	 * 
	 * @see org.jimcat.services.jobs.Job#supportsRollback()
	 */
	@Override
	public boolean supportsRollback() {
		return true;
	}

	/**
	 * In the postExecution method the description of the finished job is set
	 * according to its status.
	 * 
	 * @see org.jimcat.services.jobs.Job#postExecution()
	 */
	@Override
	public void postExecution() {
		switch (getState()) {
		case FINISHED: {
			setJobDescription("Export of " + imagesToExport.size() + " images finished successfully");
			break;
		}
		case ABORTED: {
			setJobDescription("Export aborted");
			break;
		}
		case REVERTED: {
			setJobDescription("Export reverted successfully");
			break;
		}
		default:
			break;
		}
	}

	/**
	 * @return the destination
	 */
	public File getDestination() {
		return destination;
	}

	/**
	 * @param destination
	 *            the destination to set
	 */
	public void setDestination(File destination) {
		checkConfigState();
		this.destination = destination;
	}

	/**
	 * 
	 * extract the file from an image
	 * 
	 * @param img
	 * @return the file object of the given image
	 */
	private File extractFile(Image img) {
		File file = null;
		if (img.getMetadata() != null && img.getMetadata().getPath() != null) {
			file = img.getMetadata().getPath().getAbsoluteFile();
		}
		return file;
	}

	/**
	 * @return the imagesToExport
	 */
	public List<Image> getImagesToExport() {
		return Collections.unmodifiableList(imagesToExport);
	}

	/**
	 * @param imagesToExport
	 *            the imagesToExport to set
	 */
	public void setImagesToExport(List<Image> imagesToExport) {
		checkConfigState();
		this.imagesToExport = imagesToExport;
	}

	/**
	 * 
	 * The copyFile method is used to copy a file to a given directory either
	 * giving him a new name specified in commonName, or keeping the original
	 * file name
	 * 
	 * @param source
	 * @return a file object of the copied file
	 * @throws IOException
	 *             if an error occurs the exception is caught, but whend the
	 *             user types ignore or rollback then the IOException is thrown
	 *             again to indicate a failure to the calling method
	 */
	private File copyFile(File source) throws IOException {

		// quick null check
		if (source == null) {
			throw new IOException();
		}

		// 1. determine destination file name
		File copy = null;
		String newFileName;
		if (imagesToExportNames != null && imagesToExportNames.size() > namesIndex) {
			newFileName = imagesToExportNames.get(namesIndex);
			if (!newFileName.endsWith(getFileType(source))) {
				newFileName = newFileName + getFileType(source);
			}
		} else {
			newFileName = "unknown";
		}
		copy = new File(destination, newFileName);
		copy = JobUtils.getNextFreeCopyFileName(destination, copy, false);
		namesIndex++;

		// 2. copy file to destination
		JobUtils.copyFile(source, copy, this);

		// 3. return result
		return copy;
	}

	/**
	 * 
	 * The method getFileType is used to get the file type of a image, which
	 * means cutting the suffix that follows the last point in the file name
	 * 
	 * @param file
	 * @return the file type as a string including the point before it
	 */
	private String getFileType(File file) {
		String fileName = file.getName();
		int lastPoint = fileName.lastIndexOf(".");
		if (lastPoint != -1) {
			return fileName.substring(lastPoint);
		}
		return "";
	}

	/**
	 * setImages to export names
	 * 
	 * @param imagesToExportNames
	 */
	public void setImagesToExportNames(List<String> imagesToExportNames) {
		this.imagesToExportNames = imagesToExportNames;
	}

}
