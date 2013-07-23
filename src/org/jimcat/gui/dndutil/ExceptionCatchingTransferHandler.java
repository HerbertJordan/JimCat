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

package org.jimcat.gui.dndutil;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.InputEvent;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.TransferHandler;

/**
 * This class sorrounds all methods of a transfer handler with try and catch
 * blocks and delegates them to empty methods which can be implemented by
 * subclasses.
 * 
 * By doing that the exceptions in the dnd code, which are lost when using
 * another exception handler than default, can be caught and reacted upon. If
 * inheriting from this class only the safeXXX methods should be overwritten.
 * 
 * 
 * 
 * $Id$
 * 
 * @author Michael
 */
public class ExceptionCatchingTransferHandler extends TransferHandler {

	/**
	 * The can import method sorrounded with a try - catch delegating to
	 * safeCanImport. Overwrite safeCanImport to change behaviour.
	 * 
	 * @see javax.swing.TransferHandler#canImport(javax.swing.JComponent,
	 *      java.awt.datatransfer.DataFlavor[])
	 */
	@Override
	public final boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
		try {
			return safeCanImport(comp, transferFlavors);
		} catch (Throwable t) {
			throw new RuntimeException("Exception in canImport", t);
		}
	}

	/**
	 * 
	 * Overwrite this method in your subclass to change behaviour.
	 * 
	 * @param comp
	 * @param transferFlavors
	 * @return true if data can be imported
	 */
	public boolean safeCanImport(JComponent comp, DataFlavor[] transferFlavors) {
		return super.canImport(comp, transferFlavors);
	}

	/**
	 * The createTransferable method sorrounded with a try - catch delegating to
	 * safeCreateTransferable. Overwrite safeCreateTransferable to change
	 * behaviour.
	 * 
	 * @see javax.swing.TransferHandler#createTransferable(javax.swing.JComponent)
	 */
	@Override
	protected final Transferable createTransferable(JComponent c) {
		try {
			return safeCreateTransferable(c);
		} catch (Throwable t) {
			throw new RuntimeException("Exception while creating transferable.", t);
		}
	}

	/**
	 * 
	 * Overwrite this method in your subclass to change behaviour.
	 * 
	 * @param c
	 * @return the Transferable that is created
	 */
	protected Transferable safeCreateTransferable(JComponent c) {
		return super.createTransferable(c);
	}

	/**
	 * The exportAsDrag method sorrounded with a try - catch delegating to
	 * safeExportAsDrag. Overwrite safeExportAsDrag to change behaviour.
	 * 
	 * @see javax.swing.TransferHandler#exportAsDrag(javax.swing.JComponent,
	 *      java.awt.event.InputEvent, int)
	 */
	@Override
	public final void exportAsDrag(JComponent comp, InputEvent e, int action) {
		try {
			safeExportAsDrag(comp, e, action);
		} catch (Throwable t) {
			throw new RuntimeException("Exception while export as drag.", t);
		}
	}

	/**
	 * 
	 * Overwrite this method in your subclass to change behaviour.
	 * 
	 * @param comp
	 * @param e
	 * @param action
	 * 
	 * @see javax.swing.TransferHandler#exportAsDrag(javax.swing.JComponent,
	 *      java.awt.event.InputEvent, int)
	 */
	public void safeExportAsDrag(JComponent comp, InputEvent e, int action) {
		super.exportAsDrag(comp, e, action);
	}

	/**
	 * The exportDone method sorrounded with a try - catch delegating to
	 * safeExportDone. Overwrite safeExportDone to change behaviour.
	 * 
	 * @see javax.swing.TransferHandler#exportDone(javax.swing.JComponent,
	 *      java.awt.datatransfer.Transferable, int)
	 */
	@Override
	protected final void exportDone(JComponent source, Transferable data, int action) {
		try {
			safeExportDone(source, data, action);
		} catch (Throwable t) {
			throw new RuntimeException("Exception in exportDone.", t);
		}
	}

	/**
	 * 
	 * Overwrite this method in your subclass to change behaviour.
	 * 
	 * @param source
	 * @param data
	 * @param action
	 */
	protected void safeExportDone(JComponent source, Transferable data, int action) {
		super.exportDone(source, data, action);
	}

	/**
	 * The exportToClipboard method sorrounded with a try - catch delegating to
	 * safeExportToClipboard. Overwrite safeExportToClipboard to change
	 * behaviour.
	 * 
	 * @see javax.swing.TransferHandler#exportToClipboard(javax.swing.JComponent,
	 *      java.awt.datatransfer.Clipboard, int)
	 */
	@Override
	public final void exportToClipboard(JComponent comp, Clipboard clip, int action) throws IllegalStateException {
		try {
			safeExportToClipboard(comp, clip, action);
		} catch (Throwable t) {
			throw new RuntimeException("Exception in exportToClipboard", t);
		}
	}

	/**
	 * 
	 * Overwrite this method in your subclass to change behaviour.
	 * 
	 * @param comp
	 * @param clip
	 * @param action
	 * @throws IllegalStateException
	 */
	public void safeExportToClipboard(JComponent comp, Clipboard clip, int action) throws IllegalStateException {
		super.exportToClipboard(comp, clip, action);
	}

	/**
	 * The getSourceActions method sorrounded with a try - catch delegating to
	 * safeGetSourceActions. Overwrite safeGetSourceActions to change behaviour.
	 * 
	 * @see javax.swing.TransferHandler#getSourceActions(javax.swing.JComponent)
	 */
	@Override
	public final int getSourceActions(JComponent c) {
		try {
			return safeGetSourceActions(c);
		} catch (Throwable t) {
			throw new RuntimeException("Exception in getSourceActions.", t);
		}
	}

	/**
	 * 
	 * Overwrite this method in your subclass to change behaviour.
	 * 
	 * @param c
	 * @return the source actions
	 */
	public int safeGetSourceActions(JComponent c) {
		return super.getSourceActions(c);
	}

	/**
	 * The getVisualRepresentation method sorrounded with a try - catch
	 * delegating to safeGetVisualRepresentation. Overwrite
	 * safeGetVisualRepresentation to change behaviour.
	 * 
	 * @see javax.swing.TransferHandler#getVisualRepresentation(java.awt.datatransfer.Transferable)
	 */
	@Override
	public final Icon getVisualRepresentation(Transferable t) {
		try {
			return safeGetVisualRepresentation(t);
		} catch (Throwable th) {
			throw new RuntimeException("Exception in getVisualRepresentation.", th);
		}
	}

	/**
	 * 
	 * Overwrite this method in your subclass to change behaviour.
	 * 
	 * @param t
	 * @return an icon as visual representation
	 */
	public Icon safeGetVisualRepresentation(Transferable t) {
		return super.getVisualRepresentation(t);
	}

	/**
	 * The importData method sorrounded with a try - catch delegating to
	 * safeImportData. Overwrite safeImportData to change behaviour.
	 * 
	 * @see javax.swing.TransferHandler#importData(javax.swing.JComponent,
	 *      java.awt.datatransfer.Transferable)
	 */
	@Override
	public final boolean importData(JComponent comp, Transferable t) {
		try {
			return safeImportData(comp, t);
		} catch (Throwable th) {
			throw new RuntimeException("Exception in importData.", th);
		}
	}

	/**
	 * 
	 * Overwrite this method in your subclass to change behaviour.
	 * 
	 * @param comp
	 * @param t
	 * @return true if the data could be imported successfully
	 */
	public boolean safeImportData(JComponent comp, Transferable t) {
		return super.importData(comp, t);
	}

}
