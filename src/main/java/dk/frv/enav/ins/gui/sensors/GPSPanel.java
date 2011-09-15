/*
 * Copyright 2011 Danish Maritime Safety Administration. All rights reserved.
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
 * either expressed or implied, of Danish Maritime Safety Administration.
 * 
 */
package dk.frv.enav.ins.gui.sensors;

import java.awt.Font;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;

/**
 * GPS panel in sensor panel
 */
public class GPSPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private JLabel gpsTitleLabel = new JLabel("GPS");
	private JLabel latTitleLabel = new JLabel("LAT");
	private JLabel latLabel = new JLabel("N/A");
	private JLabel lonTitleLabel = new JLabel("LON");
	private JLabel lonLabel = new JLabel("N/A");
	private JLabel sogTitleLabel = new JLabel("SOG");
	private JLabel sogLabel = new JLabel("N/A");
	private JLabel cogLabel = new JLabel("N/A");
	private JLabel cogTitleLabel = new JLabel("COG");
	private JLabel hdgLabel = new JLabel("N/A");
	private JLabel hdgTitleLabel = new JLabel("HDG");
	
	public GPSPanel() {
		
		gpsTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		gpsTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

		latTitleLabel.setHorizontalAlignment(SwingConstants.LEFT);
		latTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		
		latLabel.setHorizontalAlignment(SwingConstants.CENTER);
		latLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		
		lonTitleLabel.setHorizontalAlignment(SwingConstants.LEFT);
		lonTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
				
		lonLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lonLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
				
		sogTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		sogTitleLabel.setHorizontalAlignment(SwingConstants.LEFT);

		sogLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		sogLabel.setHorizontalAlignment(SwingConstants.RIGHT);
				
		cogTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		cogTitleLabel.setHorizontalAlignment(SwingConstants.LEFT);
		
		cogLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		cogLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		
		hdgTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		hdgTitleLabel.setHorizontalAlignment(SwingConstants.LEFT);
		
		hdgLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		hdgLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(gpsTitleLabel, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(hdgTitleLabel)
								.addComponent(cogTitleLabel)
								.addComponent(sogTitleLabel)
								.addComponent(lonTitleLabel)
								.addComponent(latTitleLabel))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(hdgLabel)
								.addComponent(cogLabel)
								.addComponent(latLabel)
								.addComponent(lonLabel)
								.addComponent(sogLabel))))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(gpsTitleLabel)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(latTitleLabel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lonTitleLabel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(sogTitleLabel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(cogTitleLabel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(hdgTitleLabel))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(latLabel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lonLabel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(sogLabel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(cogLabel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(hdgLabel)))
					.addContainerGap(190, Short.MAX_VALUE))
		);
		setLayout(groupLayout);
	}
	
	public JLabel getLatLabel() {
		return latLabel;
	}
	
	public JLabel getLonLabel() {
		return lonLabel;
	}
	
	public JLabel getSogLabel() {
		return sogLabel;
	}
	
	public JLabel getCogLabel() {
		return cogLabel;
	}
	
	public JLabel getHdgLabel() {
		return hdgLabel;
	}
}
