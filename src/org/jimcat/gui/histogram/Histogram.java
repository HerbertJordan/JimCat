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

package org.jimcat.gui.histogram;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.jimcat.gui.histogram.HistogramModel.ScaleMark;

/**
 * The main Swing component representing a histogram model as widget.
 * 
 * $Id$
 * 
 * @author Herbert
 */
public class Histogram extends JComponent {

	// - static color setup -

	/**
	 * the color fo the components border
	 */
	private static final Color BORDER_COLOR = Color.GRAY;

	/**
	 * the color for the rest spaces
	 */
	private static final Color RESTSPACE_COLOR = new Color(250, 250, 250);

	/**
	 * the background color of the histogram
	 */
	private static final Color BACKGROUND_COLOR = Color.WHITE;

	/**
	 * the background for limited areas
	 */
	private static final Color BACKGROUND_LIMITED_COLOR = new Color(220, 220, 220);

	/**
	 * the color af a bar
	 */
	private static final Color BAR_COLOR = new Color(164, 211, 238);

	/**
	 * the color of a bar border
	 */
	private static final Color BAR_BORDER_COLOR = new Color(99, 184, 255);

	/**
	 * the color for the scale
	 */
	private static final Color SCALE_COLOR = BORDER_COLOR;

	/**
	 * the color used for the titel
	 */
	private static final Color TITEL_COLOR = new Color(235, 235, 235);

	// static dimension setup

	/**
	 * the height of the history diagram
	 */
	private static final int BAR_HEIGHT = 19;

	/**
	 * the heigth of the scale
	 */
	private static final int SCALE_HEIGHT = 15;

	/**
	 * the maximum relative height of a bar (of available space)
	 */
	private static final float MAX_RELATIVE_HEIGHT = 0.9f;

	/**
	 * space between bars
	 */
	private static final int BAR_SEPERATOR = 2;

	/**
	 * the width of the rest spaces (for left and right limiters)
	 */
	private static final int REST_SPACE_WIDTH = 10;

	/**
	 * the Font used for labeling bars
	 */
	private static final Font LABEL_FONT = new JLabel().getFont().deriveFont(5);

	/**
	 * the Font used for the titel
	 */
	private static final Font TITEL_FONT = LABEL_FONT.deriveFont(Font.BOLD | Font.ITALIC, 10);

	// dynamic values

	/**
	 * the width of a bar
	 */
	private int barWidth;

	/**
	 * the used histogram model
	 */
	private HistogramModel model;

	/**
	 * the listener observing the model
	 */
	private ModelListener modelListener;

	/**
	 * a flag to determine if the current survace isn't up to date
	 */
	private boolean dirty = true;

	/**
	 * the image buffer used for image caching
	 */
	private BufferedImage imageBuffer;

	/**
	 * current dimension visible
	 */
	private int currentDimension = 0;

	/**
	 * current resoltution visible
	 */
	private int currentResolution = 0;

	/**
	 * current index centered
	 */
	private int currentIndex = 0;

	/**
	 * a list of current indizes within the dimensions (relative to resolution
	 * 0)
	 */
	private int currentIndizes[];

	/**
	 * the left limit slider
	 */
	private HistogramSlider leftSlider;

	/**
	 * the right limit slider
	 */
	private HistogramSlider rightSlider;

	/**
	 * the installed histogram popup
	 */
	private HistogramPopup popup;

	/**
	 * create a new histogram using the given model
	 * 
	 * @param model -
	 *            the model to use
	 */
	public Histogram(HistogramModel model) {
		// add sliders
		leftSlider = new HistogramSlider();
		add(leftSlider);
		rightSlider = new HistogramSlider();
		add(rightSlider);

		// init members
		modelListener = new ModelListener();
		barWidth = 15;
		setModel(model);

		// enable tooltips by giving any value
		setToolTipText("");

		// install listener
		addComponentListener(new ResizeListener());
		addMouseListener(new MouseClickListener());
		addMouseWheelListener(new WheelListener());
		DragListener dragListener = new DragListener();
		addMouseListener(dragListener);
		addMouseMotionListener(dragListener);
		addMouseListener(new PopupListener());
	}

	/**
	 * paint this component
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics componentGraphics) {
		Point diagramPos = new Point(0, 5);
		if (dirty) {
			Dimension size = getSize();
			int width = size.width - 1;
			int height = getSize().height - 5;

			// create new image buffer
			imageBuffer = new BufferedImage(width + 1, height + 1, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = imageBuffer.createGraphics();

			// draw background
			paintDiagramBackground(g);

			// paint bars
			paintBars(g);

			// paint scale
			paintScale(g);

			// draw Border
			g.setColor(BORDER_COLOR);
			g.drawRect(0, 0, width, BAR_HEIGHT);

			// draw resting position
			g.setColor(RESTSPACE_COLOR);
			g.fillRect(0, 0, REST_SPACE_WIDTH, BAR_HEIGHT);
			g.fillRect(width - REST_SPACE_WIDTH, 0, REST_SPACE_WIDTH, BAR_HEIGHT);
			g.setColor(BORDER_COLOR);
			g.drawRect(0, 0, REST_SPACE_WIDTH, BAR_HEIGHT);
			g.drawRect(width - REST_SPACE_WIDTH, 0, REST_SPACE_WIDTH, BAR_HEIGHT);

			g.dispose();
			dirty = false;
		}
		componentGraphics.drawImage(imageBuffer, diagramPos.x, diagramPos.y, null);
	}

	/**
	 * paint backbround for diagramm
	 * 
	 * @param g -
	 *            graphics to use for painting
	 */
	private void paintDiagramBackground(Graphics2D g) {
		// get component width
		int width = getSize().width;

		// overall background
		g.setColor(BACKGROUND_COLOR);
		g.fillRect(0, 0, width, BAR_HEIGHT);

		// limit background
		g.setColor(BACKGROUND_LIMITED_COLOR);

		// add titel
		g.setColor(TITEL_COLOR);
		g.setFont(TITEL_FONT);
		g.drawString(model.getNameFor(currentDimension), REST_SPACE_WIDTH + 5, 10);

		// left limit background
		int left = model.getLowerLimiter(currentDimension, currentResolution);
		if (left != HistogramModel.NO_LIMIT) {
			int leftLimit = getPositionForIndex(left);
			g.fillRect(0, 0, leftLimit, BAR_HEIGHT);
		}

		// right limit background
		int right = model.getHigherLimiter(currentDimension, currentResolution);
		if (right != HistogramModel.NO_LIMIT) {
			int rightLimit = getPositionForIndex(right);
			g.fillRect(rightLimit - 1, 0, width - rightLimit, BAR_HEIGHT);
		}
	}

	/**
	 * paint bars into the diagram
	 * 
	 * @param g -
	 *            graphic used for painting
	 */
	private void paintBars(Graphics2D g) {

		// get visible range
		Range range = getVisibleRange();

		// draw bars
		for (int i = range.start; i < range.end; i++) {
			// set bar color
			g.setColor(BAR_COLOR);

			// draw bar
			float value = model.getValueAt(currentDimension, currentResolution, i);
			int barHeigh = (int) (value * BAR_HEIGHT * MAX_RELATIVE_HEIGHT);

			// make very small values visible
			if (value > 0) {
				barHeigh = Math.max(barHeigh, 1);
			}

			int leftSite = getPositionForIndex(i);
			g.fillRect(leftSite, BAR_HEIGHT - barHeigh, barWidth, barHeigh);

			// set bar border color
			g.setColor(BAR_BORDER_COLOR);
			// draw border
			g.drawRect(leftSite, BAR_HEIGHT - barHeigh, barWidth, barHeigh);
		}
	}

	/**
	 * paint scale under diagram
	 * 
	 * @param g
	 */
	private void paintScale(Graphics2D g) {

		// get visible range
		Range range = getVisibleRange();

		// draw bars
		for (int i = range.start; i <= range.end; i++) {
			ScaleMark mark = model.getMarkFor(currentDimension, currentResolution, i);
			switch (mark) {
			case NONE:
				break;
			case SMALL:
				paintSmallMark(g, i);
				break;
			case BIG:
				paintBigMark(g, i);
				break;
			case LABEL:
				paintLabel(g, i);
				break;
			}
		}
	}

	/**
	 * paintes a small mark into the given graphics element at the specified
	 * index
	 * 
	 * @param g -
	 *            target graphic
	 * @param index -
	 *            the target index
	 */
	private void paintSmallMark(Graphics2D g, int index) {
		paintMark(g, index, 2);
	}

	/**
	 * paintes a big mark into the given graphics element at the specified index
	 * 
	 * @param g -
	 *            target graphic
	 * @param index -
	 *            the target index
	 */
	private void paintBigMark(Graphics2D g, int index) {
		paintMark(g, index, 5);
	}

	/**
	 * paintes a mark at the given place with the given length
	 * 
	 * @param g
	 * @param index
	 * @param length
	 */
	private void paintMark(Graphics2D g, int index, int length) {
		g.setColor(SCALE_COLOR);

		int posX = getPositionForIndex(index) - 1;
		g.drawLine(posX, BAR_HEIGHT, posX, BAR_HEIGHT + length);
	}

	/**
	 * paintes a label at the given position
	 * 
	 * @param g
	 * @param index
	 */
	private void paintLabel(Graphics2D g, int index) {
		// paint mark
		paintMark(g, index, 5);

		// do not add a label for last bar
		if (index >= model.getBucketCount(currentDimension, currentResolution)) {
			return;
		}

		// add label
		String label = model.getLabelForMark(currentDimension, currentResolution, index);

		g.setFont(LABEL_FONT);
		int labelWidth = (int) Math.round(LABEL_FONT.getStringBounds(label, g.getFontRenderContext()).getWidth());

		int posX = getPositionForIndex(index) + (barWidth + BAR_SEPERATOR - labelWidth) / 2;

		g.drawString(label, posX, BAR_HEIGHT + SCALE_HEIGHT);
	}

	/**
	 * this component wants to have a certain height
	 * 
	 * @see javax.swing.JComponent#getPreferredSize()
	 */
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(20, BAR_HEIGHT + SCALE_HEIGHT + 10);
	}

	/**
	 * get tooltip for the given mouse event
	 * 
	 * @see javax.swing.JComponent#getToolTipText(java.awt.event.MouseEvent)
	 */
	@Override
	public String getToolTipText(MouseEvent event) {
		int images = getCountAt(event);

		if (images == -1) {
			// no tooltip
			return null;
		}

		if (images == 0) {
			return "No Images";
		} else if (images == 1) {
			return "1 Image";
		}

		return images + " Images";
	}

	/**
	 * get absolute value at given position
	 * 
	 * @param event
	 * @return teh absolute value of the bar at given position or -1 if there is
	 *         no such bar
	 */
	private int getCountAt(MouseEvent event) {
		// get bar pointed at
		int x = event.getPoint().x;
		int index = getBarIndexAt(x);

		if (index < 0 || index >= model.getBucketCount(currentDimension, currentResolution)) {
			return -1;
		}

		return model.getAbsoluteValueAt(currentDimension, currentResolution, index);
	}

	/**
	 * center on the given element
	 * 
	 * @param path -
	 *            a path describing the element
	 * @throws IllegalArgumentException -
	 *             if path isn't supported by model
	 */
	public void centerElement(HistogramModelPath path) throws IllegalArgumentException {
		// check path
		int dim = path.getDimension();
		int res = path.getResolution();
		int index = path.getIndex();

		// check if element exists => throws Illegal Argument exception
		model.getValueAt(dim, res, index);

		// change perspective
		currentDimension = dim;
		currentResolution = res;
		currentIndex = index;

		refresh();
	}

	/**
	 * switch to the given dimension / resolution
	 * 
	 * @param dimension -
	 *            the new dimension
	 * @param resolution -
	 *            the new resolution
	 */
	public void showResolution(int dimension, int resolution) {
		// limit new values
		int newDim = Math.max(Math.min(dimension, model.getCountDimension()), 0);
		int newRes = Math.max(Math.min(resolution, model.getCountResolutions(newDim)), 0);

		// check new dimension
		if (currentDimension != newDim || currentResolution != newRes) {
			// save current index
			currentIndizes[currentDimension] = model.convertIndex(currentDimension, currentResolution, 0, currentIndex);

			// change dimension
			currentDimension = newDim;

			// update current resolution
			currentResolution = newRes;

			// restore current index
			currentIndex = model.convertIndex(newDim, 0, newRes, currentIndizes[newDim]);

			// refresch
			refresh();
		}
	}

	/**
	 * moves view several step (as far as possible)
	 * 
	 * @param steps -
	 *            amoung of steps
	 */
	public void moveCenter(int steps) {
		if (steps != 0) {
			int newPos = currentIndex + steps;
			currentIndex = Math.max(Math.min(newPos, model.getBucketCount(currentDimension, currentResolution) - 1), 0);
			refresh();
		}
	}

	/**
	 * increase resolution by given steps (there it can also decrease it)
	 * 
	 * @param steps
	 */
	private void increaseResolution(int steps) {
		if (steps != 0) {
			int newRes = currentResolution + steps;
			newRes = Math.max(Math.min(newRes, model.getCountResolutions(currentDimension) - 1), 0);

			// translate index
			currentIndex = model.convertIndex(currentDimension, currentResolution, newRes, currentIndex);

			// assign new resolution
			currentResolution = newRes;
			refresh();
		}
	}

	/**
	 * @return the model
	 */
	public HistogramModel getModel() {
		return model;
	}

	/**
	 * exchange the model used by this component
	 * 
	 * @param newModel -
	 *            the new model
	 * @throws IllegalArgumentException -
	 *             if the new Model is null
	 */
	public void setModel(HistogramModel newModel) throws IllegalArgumentException {
		if (newModel == null) {
			throw new IllegalArgumentException("model must not be null");
		}
		// shortcut
		if (newModel == model) {
			return;
		}

		// exchange model
		if (model != null) {
			model.removeHistogramModelListener(modelListener);
		}
		this.model = newModel;
		model.addHistogramModelListener(modelListener);

		// exchange popup
		popup = new HistogramPopup(this, model);

		// flush current indizes
		currentIndizes = new int[model.getCountDimension()];
		// center by default
		for (int i = 0; i < currentIndizes.length; i++) {
			currentIndizes[i] = model.getInitialIndex(i);
		}

		// center initial element
		try {
			centerElement(model.getInitialPath());
		} catch (IllegalArgumentException e) {
			currentDimension = 0;
			currentResolution = 0;
			currentIndex = currentIndizes[0];
		}

		refresh();
	}

	/**
	 * @return the barWidth
	 */
	public int getBarWidth() {
		return barWidth;
	}

	/**
	 * @param barWidth
	 *            the barWidth to set
	 */
	public void setBarWidth(int barWidth) {
		if (this.barWidth != barWidth) {
			this.barWidth = barWidth;
			refresh();
		}
	}

	/**
	 * increase / decrease barWidth by given steps
	 * 
	 * @param steps
	 */
	private void increaseBarWidth(int steps) {
		if (steps == 0) {
			return;
		}
		setBarWidth(Math.max(getBarWidth() + steps, 15));
	}

	/**
	 * update current slider positions
	 */
	private void updateSliderPositions() {
		int x = 0;
		// place left limiter
		int left = model.getLowerLimiter(currentDimension, currentResolution);
		if (left != HistogramModel.NO_LIMIT) {
			x = Math.max(getPositionForIndex(left), REST_SPACE_WIDTH + 1);
			x -= leftSlider.getSize().width / 2 + 2;
		} else {
			x = (REST_SPACE_WIDTH - leftSlider.getSize().width) / 2;
		}
		leftSlider.setLocation(x + 1, 4);

		// place right limiter
		int right = model.getHigherLimiter(currentDimension, currentResolution);
		if (right != HistogramModel.NO_LIMIT) {
			x = Math.min(getPositionForIndex(right), getSize().width - REST_SPACE_WIDTH);
			x -= rightSlider.getSize().width / 2;
		} else {
			int sliderWidth = rightSlider.getSize().width;
			x = getSize().width - sliderWidth - (REST_SPACE_WIDTH - sliderWidth) / 2;
		}
		rightSlider.setLocation(x - 1, 4);
	}

	/**
	 * refresh this component
	 */
	private void refresh() {
		dirty = true;
		updateSliderPositions();
		repaint();
	}

	/**
	 * calculate the index shown at given x-position (rounded to next border)
	 * 
	 * @param posX
	 * @return the index for the given position
	 */
	private int getIndexForPosition(int posX) {
		// get bar increment (interval for each bar)
		int barIncrement = barWidth + BAR_SEPERATOR;

		// calculate center point
		int center = (getSize().width - barIncrement) / 2;

		// calculate index
		return currentIndex + Math.round((posX - center) / (float) barIncrement);
	}

	/**
	 * get the index of the bar shown at given x position
	 * 
	 * @param posX
	 * @return teh index of the bar shown at the given position
	 */
	private int getBarIndexAt(int posX) {
		int barIncrement = barWidth + BAR_SEPERATOR;
		return getIndexForPosition(posX - barIncrement / 2);
	}

	/**
	 * calculate the left side of the bar showing given position
	 * 
	 * @param index -
	 *            a index relative to current resolution
	 * @return - the x position where the corresponding bar is placed
	 */
	private int getPositionForIndex(int index) {
		// get bar increment (interval for each bar)
		int barIncrement = barWidth + BAR_SEPERATOR;

		// calculate center point
		int center = (getSize().width - barIncrement) / 2;

		// calculate position
		return center - barIncrement * (currentIndex - index);

	}

	/**
	 * get visible index range
	 * 
	 * @return - a range of currently visible indizes
	 */
	private Range getVisibleRange() {
		// get width
		int width = getSize().width;
		// get bar increment (interval for each bar)
		int barIncrement = barWidth + BAR_SEPERATOR;
		// count possible bar range
		int barCount = (int) Math.ceil(width / (float) barIncrement);

		Range result = new Range();
		// start and end indizes painting scale for
		result.start = Math.max(0, currentIndex - barCount / 2);
		int countBucket = model.getBucketCount(currentDimension, currentResolution);
		result.end = Math.min(countBucket, currentIndex + barCount / 2 + 1);

		return result;
	}

	/**
	 * a private class listening to model changes
	 */
	private class ModelListener implements HistogramModelListener {

		/**
		 * react on a limit change
		 * 
		 * @see org.jimcat.gui.histogram.HistogramModelListener#limitChanged(org.jimcat.gui.histogram.HistogramModelEvent)
		 */
		public void limitChanged(HistogramModelEvent event) {
			// if limit is of current dimension => refresh view
			HistogramModelPath path = event.getNewLimit();
			if (path.getDimension() == currentDimension) {
				refresh();
			}
		}

		/**
		 * there was a big change in the model => refresh anyway
		 * 
		 * @see org.jimcat.gui.histogram.HistogramModelListener#structureChanged(org.jimcat.gui.histogram.HistogramModel)
		 */
		@SuppressWarnings("unused")
		public void structureChanged(HistogramModel source) {
			// check if current centered element is still supported
			if (currentDimension > model.getCountDimension()) {
				currentDimension = 0;
			}
			if (currentResolution > model.getCountResolutions(currentDimension)) {
				currentResolution = 0;
			}
			if (currentIndex > model.getBucketCount(currentDimension, currentResolution)) {
				currentIndex = 0;
			}

			// refresh view
			refresh();
		}

		/**
		 * if one of the values is in current resolution => refresh
		 * 
		 * @see org.jimcat.gui.histogram.HistogramModelListener#valueChanged(org.jimcat.gui.histogram.HistogramModelEvent)
		 */
		public void valueChanged(HistogramModelEvent event) {
			// if a change is within current view => refresh view
			Set<HistogramModelPath> elements = event.getChangedElements();
			for (HistogramModelPath path : elements) {
				if (path.getDimension() == currentDimension && path.getResolution() == currentResolution) {
					refresh();
					return;
				}
			}
		}
	}

	/**
	 * a private listener racting on resize events
	 */
	private class ResizeListener extends ComponentAdapter {
		/**
		 * refresh view if a resize action happens
		 * 
		 * @see java.awt.event.ComponentAdapter#componentResized(java.awt.event.ComponentEvent)
		 */
		@Override
		@SuppressWarnings("unused")
		public void componentResized(ComponentEvent e) {
			refresh();
		}
	}

	/**
	 * private class to react on direct clicks on bars
	 */
	private class MouseClickListener extends MouseAdapter {
		/**
		 * the limiter will be moved to the bar at given index
		 * 
		 * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseClicked(MouseEvent e) {

			// left double click removes selection
			if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
				resetLimits();
				return;
			}

			// outside selectable area
			if (getCountAt(e) == -1) {
				resetLimits();
				return;
			}

			// if there are no images it makes no sense to select this item
			if (getCountAt(e) == 0) {
				return;
			}

			// get x value
			int x = e.getPoint().x;

			// get index for x value
			int index = getBarIndexAt(x);

			// set limiter
			model.setLowerLimiter(currentDimension, currentResolution, index);
			model.setHigherLimiter(currentDimension, currentResolution, index + 1);

			refresh();
		}

		/**
		 * reset limitations
		 */
		private void resetLimits() {
			model.setLowerLimiter(currentDimension, currentResolution, HistogramModel.NO_LIMIT);
			model.setHigherLimiter(currentDimension, currentResolution, HistogramModel.NO_LIMIT);
			refresh();
		}
	}

	/**
	 * private class adding mouse wheel support
	 */
	private class WheelListener implements MouseWheelListener {

		/**
		 * @see java.awt.event.MouseWheelListener#mouseWheelMoved(java.awt.event.MouseWheelEvent)
		 */
		public void mouseWheelMoved(MouseWheelEvent e) {
			if (e.isControlDown()) {
				// increment / decrement resolution
				increaseResolution(e.getWheelRotation());
			} else if (e.isShiftDown()) {
				// increment / decrement bar width
				increaseBarWidth(e.getWheelRotation());
			} else {
				// move bars left / right
				moveCenter(e.getWheelRotation());
			}
		}
	}

	/**
	 * a private class realizing limiter dragging
	 */
	private class DragListener extends MouseMotionAdapter implements MouseListener {

		/**
		 * the slider currently dragged
		 */
		private HistogramSlider current;

		/**
		 * the point dragging was started on
		 */
		private Point dragPoint = new Point();

		/**
		 * Shifts the limiter of the histogramm.
		 * 
		 * @see java.awt.event.MouseMotionAdapter#mouseDragged(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseDragged(MouseEvent e) {
			if (current != null) {
				// calculate parameter
				int locX = e.getPoint().x - dragPoint.x;
				int locY = current.getLocation().y;
				int rightBorder = getSize().width - current.getSize().width;
				int leftBorder = 0;

				// prevent crossover
				if (current == leftSlider) {
					rightBorder = rightSlider.getLocation().x;
				} else {
					leftBorder = leftSlider.getLocation().x;
				}

				// prevent exiting borders
				locX = Math.min(Math.max(locX, leftBorder), rightBorder);

				// set new location
				current.setLocation(locX, locY);
			}
		}

		/**
		 * initate sliding
		 * 
		 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
		 */
		public void mousePressed(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				// choose sliding component
				Point point = e.getPoint();
				Rectangle rectangle = null;
				if (leftSlider.getBounds().contains(point)) {
					current = leftSlider;
					rectangle = leftSlider.getBounds();
				} else if (rightSlider.getBounds().contains(point)) {
					current = rightSlider;
					rectangle = rightSlider.getBounds();
				} else {
					current = null;
				}

				// update drag point
				if (rectangle != null) {
					dragPoint.x = point.x - rectangle.x;
					dragPoint.y = point.y - rectangle.y;
				}
			}
		}

		/**
		 * mouse released (drop) + snap in
		 * 
		 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
		 */
		@SuppressWarnings("unused")
		public void mouseReleased(MouseEvent e) {
			// snap in ...
			if (current == null) {
				return;
			}

			// the position of the slider at drop
			int posX = current.getLocation().x + current.getSize().width / 2;

			// get index from position
			int newLimit = getIndexForPosition(posX);
			if (posX < REST_SPACE_WIDTH || posX > getSize().width - REST_SPACE_WIDTH) {
				newLimit = HistogramModel.NO_LIMIT;
			} else if (newLimit < 0 || newLimit > model.getBucketCount(currentDimension, currentResolution)) {
				newLimit = HistogramModel.NO_LIMIT;
			}

			if (current == leftSlider) {
				model.setLowerLimiter(currentDimension, currentResolution, newLimit);
			} else if (current == rightSlider) {
				model.setHigherLimiter(currentDimension, currentResolution, newLimit);
			}
			current = null;
			refresh();
		}

		/**
		 * ignored
		 * 
		 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
		 */
		@SuppressWarnings("unused")
		public void mouseClicked(MouseEvent e) {
			// do nothing
		}

		/**
		 * ignored
		 * 
		 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
		 */
		@SuppressWarnings("unused")
		public void mouseEntered(MouseEvent e) {
			// do nothing
		}

		/**
		 * ignored
		 * 
		 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
		 */
		@SuppressWarnings("unused")
		public void mouseExited(MouseEvent e) {
			// do nothings
		}
	}

	/**
	 * responsible for showing popup menu
	 */
	private class PopupListener extends MouseAdapter {
		/**
		 * call checkForPopup
		 * 
		 * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseReleased(MouseEvent e) {
			checkForPopup(e);
		}

		/**
		 * call checkForPopup
		 * 
		 * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
		 */
		@Override
		public void mousePressed(MouseEvent e) {
			checkForPopup(e);
		}

		/**
		 * 
		 * Check if event is a poput trigger and show popup
		 * 
		 * @param e
		 */
		private void checkForPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				// Show popup
				popup.show(Histogram.this, e.getX(), e.getY());
			}
		}
	}

	/**
	 * used to transmit visible ranges
	 */
	private class Range {
		/**
		 * starting point
		 */
		public int start;

		/**
		 * endpoint
		 */
		public int end;
	}
}
