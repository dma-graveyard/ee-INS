/*
 * Copyright 2011 Danish Maritime Authority. All rights reserved.
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
 * THIS SOFTWARE IS PROVIDED BY Danish Maritime Authority ``AS IS'' 
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
package dk.frv.enav.ins.gui.sensors;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

/**
 * Scale panel in sensor panel
 */
public class ScalePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private final JLabel scaleLabel = new JLabel("Scale N/A");
	private final JLabel timeLabel = new JLabel("N/A");
	
	public ScalePanel(){
		super();
		setBorder(new LineBorder(Color.GRAY));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{10, 0};
		gridBagLayout.rowHeights = new int[]{20, 20, 10};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_timeLabel = new GridBagConstraints();
		gbc_timeLabel.anchor = GridBagConstraints.NORTH;
		gbc_timeLabel.insets = new Insets(0, 0, 5, 0);
		gbc_timeLabel.gridx = 0;
		gbc_timeLabel.gridy = 0;
		add(timeLabel, gbc_timeLabel);
		scaleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		scaleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		GridBagConstraints gbc_scaleLabel = new GridBagConstraints();
		gbc_scaleLabel.anchor = GridBagConstraints.NORTH;
		gbc_scaleLabel.gridx = 0;
		gbc_scaleLabel.gridy = 1;
		add(scaleLabel, gbc_scaleLabel);
	}
	
	public JLabel getTimeLabel() {
		return timeLabel;
	}
	
	public JLabel getScaleLabel() {
		return scaleLabel;
	}
	
}
