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

package org.jimcat.tests;

import org.jimcat.model.Image;
import org.jimcat.model.ImageRating;
import org.jimcat.model.notification.BeanChangeEvent;
import org.jimcat.model.notification.BeanListener;
import org.jimcat.model.notification.BeanProperty;

/**
 * 
 * 
 * $Id: TestBeanListener.java 999 2007-09-14 20:02:58Z cleiter $
 * 
 * @author csag1760
 */
public class TestBeanListener extends JimcatTestCase {

	private String result;

	public void test() {

		Image image = new Image();

		BeanListener<Image> listener = new BeanListener<Image>() {

			public void beanPropertyChanged(BeanChangeEvent<Image> event) {
				if (!event.getProperty().equals(BeanProperty.IMAGE_RATING)) {
					return;
				}

				result = "" + event.getNewValue();
			}
		};

		image.addListener(listener);

		image.setRating(ImageRating.FIVE);
		assertEquals("FIVE", result);

		image.setRating(ImageRating.FOUR);
		assertEquals("FOUR", result);

		image.removeListener(listener);
		image.setRating(ImageRating.FIVE);
		assertEquals("FOUR", result);
	}

}
