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

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * GPS panel in sensor panel
 */
public class NoGoPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JLabel nogoTitleLabel = new JLabel("NoGo");
	private JLabel statusTitleLabel = new JLabel("Status");
	private JLabel statusLabel = new JLabel("N/A");
	private final JLabel statLabel2 = new JLabel("N/A");
	private final JLabel statLabel1 = new JLabel("N/A");
	private final JLabel statLabel3 = new JLabel("N/A");
	private final JLabel statLabel4 = new JLabel("N/A");
	private final JLabel lblNewLabel = new JLabel("Valid From");
	private final JLabel lblNewLabel_1 = new JLabel("Valid to");
	private final JLabel lblNewLabel_2 = new JLabel("Draught");
	private final JLabel statLabel5 = new JLabel("N/A");
	private final JLabel navWarning1 = new JLabel("Do not use this for");
	private final JLabel navWarning2 = new JLabel("navigational purposes");

	public NoGoPanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 10, 10 };
		gridBagLayout.rowHeights = new int[] { 20, 16, 15, 0, 0, 0, 0, 0, 0, 0,
				10 };
		gridBagLayout.columnWeights = new double[] { 1.0, 1.0 };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);
		nogoTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		nogoTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
		GridBagConstraints gbc_nogoTitleLabel = new GridBagConstraints();
		gbc_nogoTitleLabel.anchor = GridBagConstraints.NORTH;
		gbc_nogoTitleLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_nogoTitleLabel.insets = new Insets(0, 0, 5, 0);
		gbc_nogoTitleLabel.gridwidth = 2;
		gbc_nogoTitleLabel.gridx = 0;
		gbc_nogoTitleLabel.gridy = 0;
		add(nogoTitleLabel, gbc_nogoTitleLabel);

		statusTitleLabel.setHorizontalAlignment(SwingConstants.LEFT);
		statusTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		GridBagConstraints gbc_statusTitleLabel = new GridBagConstraints();
		gbc_statusTitleLabel.anchor = GridBagConstraints.NORTHWEST;
		gbc_statusTitleLabel.insets = new Insets(0, 0, 5, 5);
		gbc_statusTitleLabel.gridx = 0;
		gbc_statusTitleLabel.gridy = 1;
		add(statusTitleLabel, gbc_statusTitleLabel);

		statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
		statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		GridBagConstraints gbc_statusLabel = new GridBagConstraints();
		gbc_statusLabel.anchor = GridBagConstraints.NORTHWEST;
		gbc_statusLabel.insets = new Insets(0, 0, 5, 0);
		gbc_statusLabel.gridx = 1;
		gbc_statusLabel.gridy = 1;
		add(statusLabel, gbc_statusLabel);

		lblNewLabel.setHorizontalAlignment(SwingConstants.LEFT);
		lblNewLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 2;
		add(lblNewLabel, gbc_lblNewLabel);

		statLabel1.setHorizontalAlignment(SwingConstants.LEFT);
		statLabel1.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		GridBagConstraints gbc_statLabel1 = new GridBagConstraints();
		gbc_statLabel1.anchor = GridBagConstraints.NORTHWEST;
		gbc_statLabel1.insets = new Insets(0, 0, 5, 0);
		gbc_statLabel1.gridx = 1;
		gbc_statLabel1.gridy = 2;
		add(statLabel1, gbc_statLabel1);

		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 3;
		add(lblNewLabel_1, gbc_lblNewLabel_1);
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.LEFT);
		lblNewLabel_1.setFont(new Font("Segoe UI", Font.PLAIN, 12));

		statLabel2.setHorizontalAlignment(SwingConstants.LEFT);
		statLabel2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		GridBagConstraints gbc_statLabel2 = new GridBagConstraints();
		gbc_statLabel2.anchor = GridBagConstraints.NORTHWEST;
		gbc_statLabel2.insets = new Insets(0, 0, 5, 0);
		gbc_statLabel2.gridx = 1;
		gbc_statLabel2.gridy = 3;
		add(statLabel2, gbc_statLabel2);

		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_2.gridx = 0;
		gbc_lblNewLabel_2.gridy = 4;
		add(lblNewLabel_2, gbc_lblNewLabel_2);
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.LEFT);
		lblNewLabel_2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		

		statLabel3.setHorizontalAlignment(SwingConstants.LEFT);
		statLabel3.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		GridBagConstraints gbc_statLabel3 = new GridBagConstraints();
		gbc_statLabel3.anchor = GridBagConstraints.NORTHWEST;
		gbc_statLabel3.insets = new Insets(0, 0, 5, 0);
		gbc_statLabel3.gridx = 1;
		gbc_statLabel3.gridy = 4;
		add(statLabel3, gbc_statLabel3);

		statLabel4.setHorizontalAlignment(SwingConstants.LEFT);
		statLabel4.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		GridBagConstraints gbc_statLabel4 = new GridBagConstraints();
		gbc_statLabel4.gridwidth = 2;
		gbc_statLabel4.insets = new Insets(0, 0, 5, 0);
		gbc_statLabel4.anchor = GridBagConstraints.NORTH;
		gbc_statLabel4.gridx = 0;
		gbc_statLabel4.gridy = 5;
		add(statLabel4, gbc_statLabel4);

		statLabel5.setHorizontalAlignment(SwingConstants.LEFT);
		statLabel5.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		GridBagConstraints gbc_statLabel5 = new GridBagConstraints();
		gbc_statLabel5.gridwidth = 2;
		gbc_statLabel5.insets = new Insets(0, 0, 5, 0);
		gbc_statLabel5.gridx = 0;
		gbc_statLabel5.gridy = 6;
		add(statLabel5, gbc_statLabel5);
		navWarning1.setForeground(Color.RED);

		navWarning1.setHorizontalAlignment(SwingConstants.LEFT);
		navWarning1.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		GridBagConstraints gbc_navWarning1 = new GridBagConstraints();
		gbc_navWarning1.gridwidth = 2;
		gbc_navWarning1.anchor = GridBagConstraints.NORTH;
		gbc_navWarning1.insets = new Insets(0, 0, 5, 0);
		gbc_navWarning1.gridx = 0;
		gbc_navWarning1.gridy = 8;
		add(navWarning1, gbc_navWarning1);

		GridBagConstraints gbc_navWarning2 = new GridBagConstraints();
		gbc_navWarning2.anchor = GridBagConstraints.NORTH;
		gbc_navWarning2.gridwidth = 2;
		gbc_navWarning2.gridx = 0;
		gbc_navWarning2.gridy = 9;
		add(navWarning2, gbc_navWarning2);
		navWarning2.setForeground(Color.RED);

		navWarning2.setHorizontalAlignment(SwingConstants.LEFT);
		navWarning2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
	}

	public JLabel getStatusTitleLabel() {
		return statusTitleLabel;
	}

	public JLabel getStatusLabel() {
		return statusLabel;
	}

	public JLabel getStatLabel2() {
		return statLabel2;
	}

	public JLabel getStatLabel1() {
		return statLabel1;
	}

	public JLabel getStatLabel3() {
		return statLabel3;
	}

	public JLabel getStatLabel4() {
		return statLabel4;
	}

	public JLabel getNavWarning1() {
		return navWarning1;
	}

	public JLabel getNavWarning2() {
		return navWarning2;
	}

	public JLabel getStatLabel5() {
		return statLabel5;
	}

	
	
	
}
