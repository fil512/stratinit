package com.kenstevens.stratinit.client.gwt.widget;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.layout.HStack;

/**
 * Class that supports masking an entire {@link Canvas} and adds the possibility
 * to display a message during masking and a loading image
 * 
 * @author Mihai Ile
 * 
 */
public class ModalWindow {

	/**
	 * The canvas to be masked
	 */
	private final Canvas canvas;

	/**
	 * The modal layer
	 */
	private HStack modal;

	/**
	 * The reference to the transparent layer inside the modal HStack
	 */
	private Canvas transparent;

	/**
	 * Creates a new {@link ModalWindow} given the canvas to be masked (an
	 * {@link Canvas#addChild(Canvas)} will be called to add the masking layer
	 * above the given canvas)
	 * 
	 * @param canvas
	 *            the canvas to be masked
	 */
	public ModalWindow(Canvas canvas) {
		this.canvas = canvas;
		createModalPanel();
	}

	/**
	 * Mask the {@link Canvas} with a transparent color
	 * 
	 * @param showLoading
	 *            whether to show a box with a loading indicator above the
	 *            background
	 */
	public void show(boolean showLoading) {
		clearLabel();
		Label label = createLabel(showLoading);
		modal.addMember(label);
		modal.show();
	}

	/**
	 * Mask the {@link Canvas} with a transparent color and display a message
	 * above it
	 * 
	 * @param message
	 *            the message to display above the background
	 * @param showLoading
	 *            whether to show a box with a loading indicator above the
	 *            background
	 */
	public void show(String message, boolean showLoading) {
		clearLabel();
		Label label = createLabel(showLoading);
		label.setContents(message);
		modal.addMember(label);
		modal.show();
	}

	/**
	 * Hide the masking layer from the {@link Canvas}
	 */
	public void hide() {
		modal.hide();
	}

	private void clearLabel() {
		Canvas[] children = modal.getChildren();
		for (Canvas child : children) {
			if (child instanceof Label) {
				Label label = (Label) child;
				modal.removeChild(label);
				label.destroy();
			}
		}
	}

	private void createModalPanel() {
		modal = new HStack();
		modal.setWidth100();
		modal.setHeight100();
		modal.setDefaultLayoutAlign(Alignment.CENTER);
		modal.hide();

		transparent = new Canvas();
		transparent.setWidth100();
		transparent.setHeight100();
		transparent.setBackgroundColor("#555");
		transparent.setOpacity(30);

		modal.addChild(transparent);
		canvas.addChild(modal);
	}

	private Label createLabel(boolean showLoading) {
		Label label = new Label();
		label.setWrap(false);
		label.setPadding(5);
		label.setWidth100();
		label.setHeight(25);
		label.setBackgroundColor("#fff");
		label.setShowEdges(true);
		label.setAlign(Alignment.CENTER);
		if (showLoading) {
			label.setIcon("loading.gif");
		}
		label.setZIndex(transparent.getZIndex() + 2);
		return label;
	}

	/**
	 * Destroy the {@link ModalWindow} freeing up resources
	 */
	public void destroy() {
		modal.destroy();
	}
}