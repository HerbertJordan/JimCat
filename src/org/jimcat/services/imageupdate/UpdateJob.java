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

package org.jimcat.services.imageupdate;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.jimcat.model.Image;
import org.jimcat.model.ImageMetadata;
import org.jimcat.model.notification.BeanModificationManager;
import org.jimcat.services.imagemanager.ImageQuality;
import org.jimcat.services.imagemanager.ImageUtil;
import org.jimcat.services.jobs.Job;
import org.joda.time.DateTime;

/**
 * The update job updates the image data, metadata, exif metadata and thumbnail
 * if the checksum or/and the modification date has changed.
 * 
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class UpdateJob extends Job {

	/**
	 * list of images to update
	 */
	private List<Image> updateList;

	/**
	 * used to remember progress
	 */
	private int currentImage;

	/**
	 * get progress information
	 * 
	 * @see org.jimcat.services.jobs.Job#getPercentage()
	 */
	@Override
	public int getPercentage() {
		// if list length is 0, return 100%
		if (updateList.size() == 0) {
			return 100;
		}
		// percentage of imported images
		return (int) ((currentImage / (float) updateList.size()) * 100);
	}

	/**
	 * rollback isn't supportied but calling this methode wouldn't hurt.
	 * 
	 * @see org.jimcat.services.jobs.Job#nextRollbackStep()
	 */
	@Override
	public boolean nextRollbackStep() {
		return true;
	}

	/**
	 * in every step one image is updated. If there are no images left the job
	 * stops. In every step the image data, metadata and thumbnail are updated.
	 * 
	 * @see org.jimcat.services.jobs.Job#nextStep()
	 */
	@Override
	public boolean nextStep() {
		// if there are no images within the filelist
		if (updateList.size() == 0) {
			return true;
		}

		// getFile
		Image image = updateList.get(currentImage);
		setJobDescription("Updateing image (" + (currentImage + 1) + "/" + updateList.size() + ") ... "
		        + image.getTitle());

		updateImage(image);

		// increment position
		currentImage++;
		return currentImage >= updateList.size();
	}

	/**
	 * update given image if necessary
	 * 
	 * @param image
	 */
	private void updateImage(Image image) {
		try {
			// 1) Load File
			File file = image.getMetadata().getPath();
			byte[] content = ImageUtil.loadFile(image.getMetadata().getPath());

			// 2) check modification date and checksum
			// cheap first => check modification date
			long localModificationDate = image.getMetadata().getModificationDate().getMillis();
			long currentModificationDate = file.lastModified();
			String checksum = ImageUtil.getChecksum(content);

			if (localModificationDate == currentModificationDate) {
				// check checksum
				if (ObjectUtils.equals(checksum, image.getMetadata().getChecksum())) {
					// no udpate necessary
					return;
				}
			}

			// 3) Build image
			ImageMetadata oldMetadata = image.getMetadata();
			long importId = oldMetadata.getImportId();
			DateTime addedDate = oldMetadata.getDateAdded();
			Image updated = ImageUtil.resolveImage(content, ImageQuality.getBest(), file, importId, addedDate);
			
			// 4) exchange values
			try {
				BeanModificationManager.startTransaction();
				// update content
				image.setThumbnail(updated.getThumbnail());
				image.setMetadata(updated.getMetadata());
				image.setExifMetadata(updated.getExifMetadata());
				// 5) flush cache
				// -- done by resolve Image
			} catch (RuntimeException e) {
				throw e;
			} finally {
				// inform listeners
				BeanModificationManager.commitTransaction();
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
			// so, update wasn't successfull, who cares
		}
	}

	/**
	 * rollback isn't supported by this job
	 * 
	 * @see org.jimcat.services.jobs.Job#supportsRollback()
	 */
	@Override
	public boolean supportsRollback() {
		return false;
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
			setJobDescription("Update of " + updateList.size() + " images finished successfully");
			break;
		}
		case ABORTED: {
			setJobDescription("Update aborted");
			break;
		}
		default:
			break;
		}
	}

	/**
	 * @return the updateList
	 */
	public List<Image> getUpdateList() {
		return Collections.unmodifiableList(updateList);
	}

	/**
	 * @param updateList
	 *            the updateList to set
	 */
	public void setUpdateList(List<Image> updateList) {
		checkState();
		this.updateList = updateList;
	}

}
