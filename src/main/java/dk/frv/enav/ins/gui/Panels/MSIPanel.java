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
package dk.frv.enav.ins.gui.Panels;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import dk.frv.enav.ins.EeINS;
import dk.frv.enav.ins.gui.BlinkingLabel;

/**
 * GPS panel in sensor panel
 */
public class MSIPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JLabel msiTitleLabel = new JLabel("MSI");
	private JLabel msgTitleLabel = new JLabel("Unread Messages:");
	private JLabel msgLabel = new JLabel("N/A");
	private JLabel filterTitleLabel = new JLabel("Filter:");
	private final JLabel filter = new JLabel("On");
	
	private BlinkingLabel msiIcon;

	public MSIPanel() {

		ImageIcon[] msiAnim = new ImageIcon[2];
		msiAnim[0] = new ImageIcon(EeINS.class.getResource("/images/msi/msi_symbol_128x20.png"));
		msiAnim[1] = new ImageIcon(EeINS.class.getResource("/images/msi/blank128x20.png"));
		msiIcon = new BlinkingLabel(400, msiAnim);
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 10, 10, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0,
				Double.MIN_VALUE };
		setLayout(gridBagLayout);
		msiTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		msiTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
		GridBagConstraints gbc_msiTitleLabel = new GridBagConstraints();
		gbc_msiTitleLabel.anchor = GridBagConstraints.NORTH;
		gbc_msiTitleLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_msiTitleLabel.insets = new Insets(0, 0, 5, 0);
		gbc_msiTitleLabel.gridwidth = 2;
		gbc_msiTitleLabel.gridx = 0;
		gbc_msiTitleLabel.gridy = 0;
		add(msiTitleLabel, gbc_msiTitleLabel);

		msgTitleLabel.setHorizontalAlignment(SwingConstants.LEFT);
		msgTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		GridBagConstraints gbc_msgTitleLabel = new GridBagConstraints();
		gbc_msgTitleLabel.anchor = GridBagConstraints.NORTHWEST;
		gbc_msgTitleLabel.insets = new Insets(0, 0, 5, 5);
		gbc_msgTitleLabel.gridx = 0;
		gbc_msgTitleLabel.gridy = 1;
		add(msgTitleLabel, gbc_msgTitleLabel);

		msgLabel.setHorizontalAlignment(SwingConstants.CENTER);
		msgLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		GridBagConstraints gbc_msgLabel = new GridBagConstraints();
		gbc_msgLabel.anchor = GridBagConstraints.NORTHWEST;
		gbc_msgLabel.insets = new Insets(0, 0, 5, 0);
		gbc_msgLabel.gridx = 1;
		gbc_msgLabel.gridy = 1;
		add(msgLabel, gbc_msgLabel);

		filterTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		filterTitleLabel.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_filterTitleLabel = new GridBagConstraints();
		gbc_filterTitleLabel.anchor = GridBagConstraints.NORTHWEST;
		gbc_filterTitleLabel.insets = new Insets(0, 0, 5, 5);
		gbc_filterTitleLabel.gridx = 0;
		gbc_filterTitleLabel.gridy = 2;
		add(filterTitleLabel, gbc_filterTitleLabel);

		GridBagConstraints gbc_filterBtn = new GridBagConstraints();
		gbc_filterBtn.insets = new Insets(0, 0, 5, 0);
		gbc_filterBtn.anchor = GridBagConstraints.NORTHWEST;
		gbc_filterBtn.gridx = 1;
		gbc_filterBtn.gridy = 2;
		add(filter, gbc_filterBtn);

		GridBagConstraints gbc_msiBlinking = new GridBagConstraints();
		gbc_msiBlinking.gridwidth = 2;
		gbc_msiBlinking.gridx = 0;
		gbc_msiBlinking.gridy = 4;
		add(msiIcon, gbc_msiBlinking);
	}

	public JLabel getMsiTitleLabel() {
		return msiTitleLabel;
	}

	public JLabel getMsgTitleLabel() {
		return msgTitleLabel;
	}

	public JLabel getMsgLabel() {
		return msgLabel;
	}

	public JLabel getFilterTitleLabel() {
		return filterTitleLabel;
	}

	public JLabel getFilter() {
		return filter;
	}

	public BlinkingLabel getMsiIcon() {
		return msiIcon;
	}

	
	
}
