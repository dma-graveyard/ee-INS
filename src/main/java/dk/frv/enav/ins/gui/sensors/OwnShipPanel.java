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
 * Own ship panel in sensor panel
 */
public class OwnShipPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel ownShipTitleLabel = new JLabel("Own Ship");
	private JLabel nameTitleLabel = new JLabel("Name");
	private JLabel nameLabel = new JLabel("N/A");
	private JLabel CallsignTitleLabel = new JLabel("Callsign");
	private JLabel callsignLabel = new JLabel("N/A");
	private JLabel mmsiTitleLabel = new JLabel("MMSI");
	private JLabel mmsiLabel = new JLabel("N/A");
	
	public OwnShipPanel() {
		
		ownShipTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		ownShipTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

		nameTitleLabel.setHorizontalAlignment(SwingConstants.LEFT);
		nameTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		
		nameLabel.setHorizontalAlignment(SwingConstants.LEFT);
		nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		
		CallsignTitleLabel.setHorizontalAlignment(SwingConstants.LEFT);
		CallsignTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
				
		callsignLabel.setHorizontalAlignment(SwingConstants.LEFT);
		callsignLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
				
		mmsiTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		mmsiTitleLabel.setHorizontalAlignment(SwingConstants.LEFT);

		mmsiLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		mmsiLabel.setHorizontalAlignment(SwingConstants.LEFT);
		
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(ownShipTitleLabel, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(mmsiTitleLabel)
								.addComponent(CallsignTitleLabel)
								.addComponent(nameTitleLabel))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(nameLabel)
								.addComponent(callsignLabel)
								.addComponent(mmsiLabel))))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(ownShipTitleLabel)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(nameTitleLabel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(CallsignTitleLabel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(mmsiTitleLabel)
							.addGap(44))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(nameLabel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(callsignLabel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(mmsiLabel)
							.addGap(44)))
					.addContainerGap(170, Short.MAX_VALUE))
		);
		setLayout(groupLayout);
	}
	
	public JLabel getNameLabel() {
		return nameLabel;
	}
	
	public JLabel getCallsignLabel() {
		return callsignLabel;
	}
	
	public JLabel getMmsiLabel() {
		return mmsiLabel;
	}
}
