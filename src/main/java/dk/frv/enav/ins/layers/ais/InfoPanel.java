/*
 * Copyright 2012 Danish Maritime Authority. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *   1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 *   2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY Danish Maritime Safety Administration ``AS IS'' 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of Danish Maritime Authority.
 * 
 */
package dk.frv.enav.ins.layers.ais;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

/**
 * Abstract base class for panels to be shown on the map in the glass pane
 */
public abstract class InfoPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private JLabel textLabel;
	private JLabel imageLabel;

	/**
	 * Constructor
	 */
	public InfoPanel() {
		super();
		FlowLayout flowLayout = new FlowLayout();
		setLayout(flowLayout);
		flowLayout.setVgap(0);
		flowLayout.setHgap(0);
//		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, new Color(30, 30, 30), new Color(45, 45, 45)));
		textLabel = new JLabel();
		add(textLabel);
		setVisible(false);
		textLabel.setFont(new Font("Arial", Font.PLAIN, 11));
		textLabel.setBackground(new Color(83, 83, 83));
		textLabel.setForeground(new Color(237, 237, 237));
		setBackground(new Color(83, 83, 83));
	}

	/**
	 * Constructor with an image
	 * @param image
	 */
	public InfoPanel(ImageIcon image) {
		super();
		imageLabel =  new JLabel(image);
		FlowLayout flowLayout = new FlowLayout();
		setLayout(flowLayout);
		flowLayout.setVgap(0);
		flowLayout.setHgap(0);
		this.setBackground(null);
		this.setBorder(null);
		this.setOpaque(false);
		add(imageLabel);
		setVisible(false);
	}

	/**
	 * Resize and display label
	 */
	public void resizeAndShow() {
		validate();
		Dimension d = textLabel.getSize();
		this.setSize(d.width + 6, d.height + 4);
		setVisible(true);
	}

	/**
	 * Set position of element
	 * @param x location
	 * @param y location
	 */
	public void setPos(int x, int y) {
		Rectangle rect = getBounds();
		setBounds(x, y, (int) rect.getWidth(), (int) rect.getHeight());
	}

	/**
	 * Show the image
	 */
	public void showImage(){
		validate();
		Dimension d = imageLabel.getSize();
		this.setSize(d.width, d.height);
		setVisible(true);
	}

	/**
	 * Show text
	 * @param text
	 */
	public void showText(String text) {
		textLabel.setText(text);
		resizeAndShow();
	}

}
