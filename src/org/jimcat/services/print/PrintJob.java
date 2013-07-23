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

package org.jimcat.services.print;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;

import org.jimcat.services.jobs.Job;
import org.jimcat.services.jobs.JobFailureDescription;
import org.jimcat.services.jobs.JobFailureOption;
import org.jimcat.services.jobs.JobManager;

/**
 * This class represents a print job
 * 
 * For a given Layout (number of rows an columns as well as width and height of
 * a single image, all images of the current view are printed out on a choseable
 * site. Width and height are used to construct equal rectangles which are
 * placed on the page according to the number of rows and columns. Each
 * rectanlge contains an image, which uses the whole width or height of the
 * rectangle depending on its format. The space between the rectangle is
 * calculated from the size of the rectangles and their number. The print job
 * takes the images displayed in the current view and creates the pages that are
 * needed to print the given layout. After creation of all pages the printing is
 * initialised.
 * 
 * 
 * 
 * @author Steve
 */
public class PrintJob extends Job {

	/**
	 * Format of the pages that will be printed
	 */
	private PageFormat pageFormat = null;

	/**
	 * Book element that contains the pages with all images and is printed thus
	 * finishing the print job
	 */
	private Book book = null;

	/**
	 * the printer job used to print document
	 */
	private PrinterJob printerJob;

	/**
	 * the document to print
	 */
	private PrintDocument printDocument;

	/**
	 * counting prepaired pages
	 */
	private int currentPage = 1;

	/**
	 * used to calculate percentage
	 */
	private int renderedPages = 0;

	/**
	 * 
	 * Creates a new import job from the parameters using the given JobManager
	 * 
	 * @param manager
	 *            the job manager for this job
	 * @param printDocument
	 *            the document used to layout the images that shall be printed
	 * @param pF
	 *            the format secified by the output printer holding information
	 *            about size, borders and layout of the page
	 */
	public PrintJob(JobManager manager, PrintDocument printDocument, PageFormat pF) {
		super(manager, "Image-Printing", "Printing images ...");
		this.printDocument = printDocument;
		book = new Book();
		pageFormat = pF;
	}

	/**
	 * Returns the percentage of finished work which is calculated as percentage
	 * of current pages of the Book element to print compared to the final
	 * number of pages.
	 * 
	 * @see org.jimcat.services.jobs.Job#getPercentage()
	 */
	@Override
	public int getPercentage() {
		return (renderedPages * 100) / printDocument.getPageCount();
	}

	/**
	 * get printer job if pagecount > 0, else just finish job, because no images
	 * have been selected for printing.
	 * 
	 * @see org.jimcat.services.jobs.Job#preExecution()
	 */
	@Override
	public void preExecution() {
		if (printDocument.getPageCount() == 0) {
			finishedJob();
		}
		printerJob = PrinterJob.getPrinterJob();

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
			if (printDocument.getPageCount() == 0) {
				setJobDescription("No images have been selected for printing.");
			} else {
				setJobDescription("Printing of " + currentPage + " pages finished successfully");
			}
			break;
		}
		case ABORTED: {
			setJobDescription("Print aborted");
			break;
		}
		default:
			break;
		}
	}

	/**
	 * The PrintJob does not support Rollback.
	 * 
	 * @see org.jimcat.services.jobs.Job#nextRollbackStep()
	 */
	@Override
	public boolean nextRollbackStep() {
		return true;
	}

	/**
	 * In a single step of the PrintJob all Images that should be printed on one
	 * page are colelcted and given to he Printable as a List. The Printable is
	 * then appended to the Book which is printed in the final step.
	 * 
	 * @see org.jimcat.services.jobs.Job#nextStep()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean nextStep() {
		setJobDescription("JimCat is preparing page " + currentPage + " of " + printDocument.getPageCount()
		        + " for print.");
		JPanel tempPage = printDocument.drawPage(currentPage);

		book.append(new PrintablePage(tempPage, currentPage), pageFormat);

		if (currentPage < printDocument.getPageCount()) {
			currentPage++;
			return false;
		}

		printerJob.setPageable(book);

		// prepair JobFailure description
		JobFailureDescription desc = new JobFailureDescription();
		desc.setDescription("Error when sending print job to system. "
		        + "Please make sure the selected printer is connected and working.");

		// create a list of options
		List<JobFailureOption> options = new LinkedList<JobFailureOption>();
		options.add(JobFailureOption.Retry);
		options.add(JobFailureOption.Cancel);
		desc.setOptions(options);

		// select default option
		desc.setRespond(JobFailureOption.Retry);

		while (printerJob.printDialog()) {
			try {
				printerJob.print();
				return true;
			} catch (Exception e) {
				// if job is canceled => game over
				if (getState().isFinal()) {
					return false;
				}
				// request failer handling
				requestFailureHandling(desc);
				// handle reaction
				switch (desc.getRespond()) {
				case Retry:
					break;
				case Cancel:
					cancel();
					return false;
				default:
					// cannot happen
					break;
				}
			}
		}
		// cancel job if not finished
		if (!getState().isFinal()) {
			cancel();
		}
		return false;
	}

	/**
	 * This type of job supportes a rollback
	 * 
	 * @see org.jimcat.services.jobs.Job#supportsRollback()
	 */
	@Override
	public boolean supportsRollback() {
		return false;
	}

	/**
	 * inform job about a rendered page
	 * 
	 * @param index
	 *            the rendering index
	 */
	private synchronized void setRenderingIndex(int index) {
		int oldValue = renderedPages;
		renderedPages = index;
		if (oldValue != index) {
			fireProgressChangedEvent();
		}
	}

	/**
	 * private class to wrap a printable page
	 */
	private class PrintablePage implements Printable {

		/**
		 * the page layout to print
		 */ 
		private JPanel page = null;

		/**
		 * the page number of this page
		 */
		private int pageNumber = 0;

		/**
		 * create a new Page to print from a panel
		 * 
		 * @param panel
		 *            the page panel
		 * @param pN
		 *            the page number
		 * 
		 */
		public PrintablePage(JPanel panel, int pN) {
			page = panel;
			pageNumber = pN;
		}

		/**
		 * Draw a graphic using the given panel
		 * 
		 * @see java.awt.print.Printable#print(java.awt.Graphics,
		 *      java.awt.print.PageFormat, int)
		 */
		public int print(Graphics g, PageFormat format, int pageIndex) throws PrinterException {

			// check job state (also support suspend)
			if (checkState().isFinal()) {
				// job abourted
				printerJob.cancel();
				return PAGE_EXISTS;
			}

			// inform user
			setJobDescription("Rendering page " + (pageNumber) + " of " + printDocument.getPageCount() + " ... ");

			int x = (int) Math.round(format.getImageableX());
			int y = (int) Math.round(format.getImageableY());
			int width = (int) Math.round(format.getImageableWidth());
			int height = (int) Math.round(format.getImageableHeight());

			if (pageNumber != (pageIndex + 1)) {
				return Printable.NO_SUCH_PAGE;
			}

			// config page
			page.setSize(new Dimension(width, height));
			PrintDocument.doLayout(page);

			// move origin
			g.translate(x, y);

			// print page
			page.print(g);

			// send notification
			setRenderingIndex(pageNumber);

			return PAGE_EXISTS;
		}
	}

}
