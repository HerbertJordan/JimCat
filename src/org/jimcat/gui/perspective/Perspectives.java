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

package org.jimcat.gui.perspective;

import org.jimcat.gui.SwingClient;
import org.jimcat.gui.ViewControl;
import org.jimcat.gui.perspective.boards.cards.CardPerspective;
import org.jimcat.gui.perspective.boards.thumbnail.ThumbnailPerspective;
import org.jimcat.gui.perspective.detail.DetailPerspective;

/**
 * A list of installes perspectives.
 * 
 * $Id: Perspectives.java 944 2007-06-16 17:15:22Z 07g1t1u3 $
 * 
 * @author Herbert
 */
public final class Perspectives {
	private static SwingClient client = SwingClient.getInstance();

	private static ViewControl view = client.getViewControl();

	private static Perspective detailPerspective;

	private static Perspective thumbnailPerspective;

	private static Perspective cardPerspective;

	static {

		detailPerspective = new Perspective("Details", new DetailPerspective(view));
		detailPerspective.getPerspective().setActive(false);

		thumbnailPerspective = new Perspective("Thumbnails", new ThumbnailPerspective(view));
		thumbnailPerspective.getPerspective().setActive(false);

		cardPerspective = new Perspective("Cards", new CardPerspective(view));
		cardPerspective.getPerspective().setActive(false);
	}

	/**
	 * get configured version of a detail perspective
	 * 
	 * @return a properly configured detail perspective
	 */
	public static Perspective getDetailPerspective() {
		return detailPerspective;
	}

	/**
	 * get configured version of a thumbnail perspective
	 * 
	 * @return a properly configured thumbnail perspective
	 */
	public static Perspective getThumbnailPerspective() {
		return thumbnailPerspective;
	}

	/**
	 * get configured version of a card perspective
	 * 
	 * @return a properly configured card perspective
	 */
	public static Perspective getCardPerspective() {
		return cardPerspective;
	}

	/**
	 * A wrapper joining a perspective name and an useable abstract Perspective.
	 */
	public static class Perspective {
		/**
		 * name of this perspective
		 */
		private String name;

		/**
		 * a instance of this perspective
		 */
		private AbstractPerspective perspective;

		/**
		 * internal constructor
		 * 
		 * @param name
		 * @param perspective
		 */
		private Perspective(String name, AbstractPerspective perspective) {
			this.name = name;
			this.perspective = perspective;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @return the perspective
		 */
		public AbstractPerspective getPerspective() {
			return perspective;
		}
	}

}
