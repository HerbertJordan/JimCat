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

package org.jimcat.services.imageimport;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.jimcat.model.Album;
import org.jimcat.model.Image;
import org.jimcat.model.ImageRating;
import org.jimcat.model.comparator.NullComparator;
import org.jimcat.model.filter.Filter;
import org.jimcat.model.filter.ImportFilter;
import org.jimcat.model.filter.ImportFilter.Type;
import org.jimcat.model.libraries.ImageLibrary;
import org.jimcat.model.libraries.LibraryView;
import org.jimcat.model.tag.Tag;
import org.jimcat.services.configuration.Configuration;
import org.jimcat.services.imagemanager.ImageQuality;
import org.jimcat.services.imagemanager.ImageUtil;
import org.jimcat.services.jobs.Job;
import org.jimcat.services.jobs.JobFailureDescription;
import org.jimcat.services.jobs.JobFailureOption;
import org.jimcat.services.jobs.JobManager;
import org.jimcat.services.jobs.JobUtils;
import org.joda.time.DateTime;

/**
 * This class represents an import job.
 * 
 * It should be used to import images to the system.
 * 
 * $Id: ImportJob.java 934 2007-06-15 08:40:58Z 07g1t1u2 $
 * 
 * @author Herbert
 */
public class ImportJob extends Job {

	/**
	 * a small enumeration to control process flow
	 */
	enum ImportState {
		PREPARING, RUNNING;
	}

	/**
	 * the maximal import id used up to now
	 */
	private static long MAX_IMPORT_ID = -1;

	/**
	 * lock for id-access
	 */
	private static Object idLock = new Object();

	/**
	 * a reference to the image library
	 */
	private static ImageLibrary library = ImageLibrary.getInstance();

	/**
	 * helps filtering interesting elements
	 */
	private static ImportFileNameFilter fileNameFilter = new ImportFileNameFilter();

	/**
	 * an id for this import
	 */
	private long importId = getNewImportID();

	/**
	 * the date when this import Job was started
	 */
	private DateTime addedDate;

	/**
	 * list of files / directories to import
	 */
	private List<File> files;

	/**
	 * should the import include subdirectories if there are any?
	 */
	private boolean recursive = false;

	/**
	 * should the read images be copied to a backup directory
	 */
	private boolean copyImages = false;

	/**
	 * the destination directory for copied images
	 */
	private File destination;

	/**
	 * a list of Tags which will be attached to all imported images
	 */
	private List<Tag> defaultTags = Collections.emptyList();

	/**
	 * the album assigned by default
	 */
	private Album defaultAlbum;

	/**
	 * the current state of execution
	 */
	private ImportState state = ImportState.PREPARING;

	/**
	 * the index of the next image to import
	 */
	private int fileIndex = 0;

	/**
	 * used for import statistics
	 */
	private int imported = 0;

	/**
	 * used for counting allready existing images
	 */
	private int allreadyContained = 0;

	/**
	 * number of images not imported caused by error
	 */
	private int ignored = 0;

	/**
	 * switch for supporting ignore all option
	 */
	private boolean ignoreAll = false;

	/**
	 * images which have to be undone
	 */
	private List<Image> undoList = null;

	/**
	 * creates a new import job, default values for all
	 */
	public ImportJob() {
		this(null);
	}

	/**
	 * creates a new import job using the given JobManager
	 * 
	 * @param manager
	 */
	public ImportJob(JobManager manager) {
		super(manager, "Image-Import", "Adding images to library ...");

		// set up default destination direcotry
		String destPath = Configuration.getConfigBaseDirectory() + "import/" + importId + "/";
		destPath = Configuration.getString("backupdirectory", destPath);
		destination = new File(destPath);
	}

	/**
	 * gerate a timestamp on import start
	 * 
	 * @see org.jimcat.services.jobs.Job#preExecution()
	 */
	@Override
	public void preExecution() {
		// gerate TimeStamp when import starts
		addedDate = new DateTime();
	}

	/**
	 * Returns the percentage of finished work. While the Job is receiving list
	 * of images, the percentage will be constantly 0. When importing images the
	 * percentage will be the part of allready imported images.
	 * 
	 * @see org.jimcat.services.jobs.Job#getPercentage()
	 */
	@Override
	public int getPercentage() {
		// if the job is still prepairing, progress is unknown
		if (state == ImportState.PREPARING) {
			return 0;
		}
		// if list length is 0, return 100%
		if (files.size() == 0) {
			return 100;
		}
		// percentage of imported images
		return (int) ((fileIndex / (float) files.size()) * 100);
	}

	/**
	 * do import.
	 * 
	 * the import is devided into two sections. In the first part, the ImportJob
	 * has to generate a list of files found in the source directory.
	 * 
	 * In the second part, the job reads in images and add them to the database.
	 * 
	 * @see org.jimcat.services.jobs.Job#nextStep()
	 */
	@Override
	public boolean nextStep() {
		if (state == ImportState.PREPARING) {
			if (nextPreparingStep()) {
				state = ImportState.RUNNING;
				fileIndex = 0;

				// remove directories from file list
				List<File> directory = new LinkedList<File>();
				for (File file : files) {
					if (file.isDirectory()) {
						directory.add(file);
					}
				}
				files.removeAll(directory);
			}
			return false;
		}

		// do import
		int i = 0;
		while (true) {
			// retry several times to avoid OutOfMemory Errors
			try {
				return doNextImportStep();
			} catch (OutOfMemoryError error) {
				i++;
				if (i > 3) {
					// something is wrong (maybe image to load is too big)
					throw error;
				}
			}
		}
	}

	/**
	 * this will execute the next preparing step
	 * 
	 * @return - true if this has been the last step, false else
	 */
	private boolean nextPreparingStep() {
		// get subtree of current file
		File element = files.get(fileIndex);

		// if it is a directory follow it
		out: {
			if (!element.isDirectory()) {
				break out;
			}

			// print information
			setJobDescription("Searching ... " + element.getName());

			// add content recursively
			File[] content = element.listFiles(fileNameFilter);

			if (content == null) {
				break out;
			}

			for (File f : content) {
				// to avoid duplicates
				if (files.contains(f)) {
					break out;
				}

				if (f.isDirectory() && !recursive) {
					continue;
				}

				files.add(f);
			}
		}

		// increment position
		fileIndex++;
		return fileIndex >= files.size();
	}

	/**
	 * this will presume the import work
	 * 
	 * @return - true if finished, false else
	 */
	private boolean doNextImportStep() {
		// if there are no images within the filelist
		if (files.size() == 0) {
			return true;
		}

		// getFile
		File file = files.get(fileIndex);
		setJobDescription("Loading File (" + (fileIndex + 1) + "/" + files.size() + ") ... " + file.getName());

		// counted already included images
		boolean alreadyIncluded = library.contains(file);
		if (alreadyIncluded && !isCopyImages()) {
			allreadyContained++;
		}

		// read in file
		// check if file is already loaded into the library
		// do not ignore images if you have to copy them
		if (!file.isDirectory() && (!alreadyIncluded || isCopyImages())) {

			// copy this image to a backup location
			if (isCopyImages()) {
				try {
					// copy file
					file = copyFile(file);
				} catch (IOException ioe) {
					// copieing wasn't successfull - finish this element
					return false;
				}
			}

			// create new Image Object
			Image img = new Image();

			// set titel and default rating
			String title = file.getName();
			title = title.substring(0, title.lastIndexOf('.'));
			img.setTitle(title);
			img.setRating(ImageRating.NONE);

			// prepair JobFailure
			JobFailureDescription desc = new JobFailureDescription();
			desc.setDescription("Error importing file " + file.getName() + ".\n"
			        + "Please make sure the file is accessable.");

			// create a list of options
			List<JobFailureOption> options = new LinkedList<JobFailureOption>();
			options.add(JobFailureOption.Retry);
			options.add(JobFailureOption.Ignore);
			options.add(JobFailureOption.IgnoreAll);
			options.add(JobFailureOption.Rollback);
			options.add(JobFailureOption.Cancel);
			desc.setOptions(options);

			// select default option
			desc.setRespond(JobFailureOption.Retry);

			// load detailed infos
			boolean loaded = false;
			do {
				try {
					Image tmp = ImageUtil.resolveImage(file, ImageQuality.getBest(), importId, addedDate);

					// add Thumbnail to local
					img.setThumbnail(tmp.getThumbnail());

					// add ImageMetaData to local
					img.setMetadata(tmp.getMetadata());

					// add exif data to local
					img.setExifMetadata(tmp.getExifMetadata());

					// add to library
					library.add(img);

					// add default tags
					for (Tag t : defaultTags) {
						img.addTag(t);
					}

					// add to default album
					if (defaultAlbum != null) {
						img.addToAlbum(defaultAlbum);
					}

					imported++;
					loaded = true;
				} catch (Exception ioe) {
					// whatever happens => set cause
					desc.setCause(ioe);
				}
				if (!loaded) {
					if (ignoreAll) {
						ignored++;
						loaded = true;
					} else {
						// request failer handling
						requestFailureHandling(desc);

						// handle reaction
						switch (desc.getRespond()) {
						case Retry:
							loaded = false;
							break;
						case Ignore:
							ignored++;
							loaded = true;
							break;
						case IgnoreAll:
							ignored++;
							loaded = true;
							ignoreAll = true;
							break;
						case Rollback:
							rollback();
							return false;
						case Cancel:
							cancel();
							return false;
						}
					}
				}
			} while (!loaded);
		}

		// increment position
		fileIndex++;
		return fileIndex >= files.size();
	}

	/**
	 * this will revert the next part of work privouslly done by this job.
	 * 
	 * @see org.jimcat.services.jobs.Job#nextRollbackStep()
	 */
	@Override
	public boolean nextRollbackStep() {
		// if there was no import, just finish
		if (state == ImportState.PREPARING) {
			return true;
		}
		// create undolist if it is first step
		if (undoList == null) {
			// create a libraryview containing added images
			Filter filter = new ImportFilter(Type.EXACT, importId);
			Comparator<Image> comparator = new NullComparator();
			LibraryView view = new LibraryView(ImageLibrary.getInstance(), filter, comparator);

			// get list of images
			undoList = new LinkedList<Image>(view.getImages());

			// dispose view, its not needed any more
			view.dispose();

			// if there is nothing to do, finish job
			if (undoList.size() == 0) {
				return true;
			}
		}

		// if there is nothing to do in special
		if (!isCopyImages()) {
			// delete all images at once
			library.remove(new HashSet<Image>(undoList));
			return true;
		}

		// get last image
		Image img = undoList.get(undoList.size() - 1);

		// inform user about progress
		fileIndex--;
		setJobDescription("Reverting File (" + (fileIndex + 1) + "/" + files.size() + ") ... " + img.getTitle());

		// if this job is copieing files, undo it too
		if (isCopyImages()) {
			// remove file
			File file = img.getMetadata().getPath();
			try {
				JobUtils.deleteFile(file, this);
			} catch (IOException ioe) {
				// something went wrong, user choose to cancel job
				// finish step
				return false;
			}
		}

		// remove image from library
		library.remove(img);

		// image removed
		undoList.remove(img);

		// remove directory when finished
		if (undoList.size() == 0 && isCopyImages() && destination.exists()) {
			try {
				FileUtils.deleteDirectory(destination);
			} catch (IOException ioe) {
				// cant do anything about it
			}
		}

		return undoList.size() == 0;
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
			setJobDescription(imported + " images successfully imported, " + allreadyContained + " already contained, "
			        + ignored + " ignored");
			break;
		}
		case ABORTED: {
			setJobDescription("Import aborted");
			break;
		}
		case REVERTED: {
			setJobDescription("Import reverted successfully");
			break;
		}
		default:
			break;
		}
	}

	/**
	 * This type of job supportes a rollback
	 * 
	 * @see org.jimcat.services.jobs.Job#supportsRollback()
	 */
	@Override
	public boolean supportsRollback() {
		return true;
	}

	/**
	 * @return the copyImages
	 */
	public boolean isCopyImages() {
		return copyImages;
	}

	/**
	 * @param copyImages
	 *            the copyImages to set
	 */
	public void setCopyImages(boolean copyImages) {
		checkConfigState();
		this.copyImages = copyImages;
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
	 * @return the files
	 */
	public List<File> getFiles() {
		return Collections.unmodifiableList(files);
	}

	/**
	 * @param fileList
	 *            the files to set
	 */
	public void setFiles(List<File> fileList) {
		checkConfigState();

		// check if all files contained are allowed
		files = new LinkedList<File>();

		// filter input
		for (File f : fileList) {
			if (fileNameFilter.accept(f.getParentFile(), f.getName())) {
				files.add(f);
			}
		}
	}

	/**
	 * @return is recursive
	 */
	public boolean isRecursive() {
		return recursive;
	}

	/**
	 * @param recursive
	 *            the recursive to set
	 */
	public void setRecursive(boolean recursive) {
		checkConfigState();
		this.recursive = recursive;
	}

	/**
	 * @return the importId
	 */
	public long getImportId() {
		return importId;
	}

	/**
	 * @return the defaultTags
	 */
	public List<Tag> getDefaultTags() {
		return defaultTags;
	}

	/**
	 * @param defaultTags
	 *            the defaultTags to set
	 */
	public void setDefaultTags(List<Tag> defaultTags) {
		checkConfigState();
		this.defaultTags = defaultTags;
	}

	/**
	 * @return the defaultAlbum
	 */
	public Album getDefaultAlbum() {
		return defaultAlbum;
	}

	/**
	 * @param defaultAlbum
	 *            the defaultAlbum to set
	 */
	public void setDefaultAlbum(Album defaultAlbum) {
		checkConfigState();
		this.defaultAlbum = defaultAlbum;
	}

	/**
	 * this methode will copy the given file to the backup destination.
	 * 
	 * @param source -
	 *            the file to copy
	 * @return - the file - representation of the copy or null if copy wasn'
	 *         successfull
	 * @throws IOException -
	 *             if copien wasn't successfull
	 */
	private File copyFile(File source) throws IOException {
		// 1. get destination file name
		File copy = JobUtils.getNextFreeCopyFileName(destination, source, true);

		// 2. copy to destination
		JobUtils.copyFile(source, copy, this);

		// 3. return copy
		return copy;
	}

	/**
	 * this will generate a new import ID
	 * 
	 * @return the new import ID
	 */
	private static long getNewImportID() {
		synchronized (idLock) {
			// generate new id
			// if MAX_IMPORT_ID isn't up do date
			if (MAX_IMPORT_ID == -1) {
				// find maximal value
				long max = -1;
				for (Image img : library.getAll()) {
					long tmp = img.getMetadata().getImportId();
					if (max < tmp) {
						max = tmp;
					}
				}
				MAX_IMPORT_ID = max + 1;
			}
			// return next value
			return MAX_IMPORT_ID++;
		}
	}

}
