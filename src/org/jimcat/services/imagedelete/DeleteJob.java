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

package org.jimcat.services.imagedelete;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.jimcat.model.Image;
import org.jimcat.model.libraries.ImageLibrary;
import org.jimcat.services.jobs.Job;
import org.jimcat.services.jobs.JobManager;
import org.jimcat.services.jobs.JobUtils;

/**
 * This job is used to delete a set of images from the disk.
 * 
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class DeleteJob extends Job {

	/**
	 * a reference to the image library
	 */
	private static ImageLibrary library = ImageLibrary.getInstance();

	/**
	 * the set of images to be deleted
	 */
	private Set<Image> imageSet;

	/**
	 * the list internally used to iterate. => it is generated from the imageSet
	 * at startup
	 */
	private List<Image> imageList;

	/**
	 * used to iterate through list (needed by nature of job)
	 */
	private int progressPointer = 0;

	/**
	 * creates a new delete job, default values for all
	 */
	public DeleteJob() {
		this(null);
	}

	/**
	 * creates a new import job using the given JobManager
	 * 
	 * @param manager
	 */
	@SuppressWarnings("unchecked")
	public DeleteJob(JobManager manager) {
		super(manager, "Image-Delete", "deleting images from disk ...");

		// setup empty image list
		imageSet = Collections.EMPTY_SET;
	}

	/**
	 * indicates the current progress. (percentage of performed deleting
	 * operations)
	 * 
	 * @see org.jimcat.services.jobs.Job#getPercentage()
	 */
	@Override
	public int getPercentage() {
		// if size is 0, return 100%
		if (imageSet.size() == 0) {
			return 100;
		}
		return (int) ((progressPointer / (float) imageSet.size()) * 100);
	}

	/**
	 * used to generate a list of images out of the set
	 * 
	 * @see org.jimcat.services.jobs.Job#preExecution()
	 */
	@Override
	public void preExecution() {
		// just generate list
		imageList = new ArrayList<Image>(imageSet);
	}

	/**
	 * Rollback isn't supported, this should never be called
	 * 
	 * @see org.jimcat.services.jobs.Job#nextRollbackStep()
	 */
	@Override
	public boolean nextRollbackStep() {
		throw new UnsupportedOperationException("Delete Job doesn't support rollback operation");
	}

	/**
	 * do next delete step
	 * 
	 * @see org.jimcat.services.jobs.Job#nextStep()
	 */
	@Override
	public boolean nextStep() {

		// check if job is finished
		if (imageList.size() == progressPointer) {
			return true;
		}

		// get victem
		Image img = imageList.get(progressPointer);
		File victem = img.getMetadata().getPath();

		// check if victem is null
		if (victem != null) {
			// delete victem
			try {
				JobUtils.deleteFile(victem, this);
			} catch (IOException ioe) {
				// user choose to cancel job
				// => finish step
				return false;
			}
		}

		// remove from library
		library.remove(img);

		// continue
		progressPointer++;
		return imageSet.size() == progressPointer;
	}

	/**
	 * setup last message after execution
	 * 
	 * @see org.jimcat.services.jobs.Job#postExecution()
	 */
	@Override
	public void postExecution() {
		switch (getState()) {
		case FINISHED: {
			setJobDescription("Delete of " + imageSet.size() + " images finished successfully");
			break;
		}
		case ABORTED: {
			setJobDescription("Delete aborted");
			break;
		}
		default:
			break;
		}
	}

	/**
	 * returns false, delete job doesn't support rollback operations
	 * 
	 * @see org.jimcat.services.jobs.Job#supportsRollback()
	 */
	@Override
	public boolean supportsRollback() {
		return false;
	}

	/**
	 * @return the imageList
	 */
	public Set<Image> getImageSet() {
		return Collections.unmodifiableSet(imageSet);
	}

	/**
	 * @param imageSet
	 *            the imageList to set
	 */
	public void setImageSet(Set<Image> imageSet) {
		checkConfigState();
		this.imageSet = imageSet;
	}

}
