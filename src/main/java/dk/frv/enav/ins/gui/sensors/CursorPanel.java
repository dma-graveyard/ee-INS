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

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;

/**
 * Cursor panel in sensor panel 
 */
public class CursorPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private final JLabel lblCursor = new JLabel("Cursor");
	private final JLabel curLatTitlelabel = new JLabel("LAT");
	private final JLabel curLonTitleLabel = new JLabel("LON");
	private final JLabel curLatLabel = new JLabel("N/A");
	private final JLabel curLonLabel = new JLabel("N/A");
	private final JLabel curCursTitleLabel = new JLabel("BRG");
	private final JLabel curDistTitleLabel = new JLabel("RNG");
	private final JLabel curCursLabel = new JLabel("N/A");
	private final JLabel curDistLabel = new JLabel("N/A");
	
	public CursorPanel(){
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{10, 10, 0};
		gridBagLayout.rowHeights = new int[]{20, 16, 16, 16, 16, 10};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		lblCursor.setFont(new Font("Segoe UI", Font.BOLD, 14));
		lblCursor.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblCursor = new GridBagConstraints();
		gbc_lblCursor.anchor = GridBagConstraints.NORTH;
		gbc_lblCursor.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblCursor.insets = new Insets(0, 0, 5, 0);
		gbc_lblCursor.gridwidth = 2;
		gbc_lblCursor.gridx = 0;
		gbc_lblCursor.gridy = 0;
		add(lblCursor, gbc_lblCursor);
		curLatTitlelabel.setHorizontalAlignment(SwingConstants.LEFT);
		curLatTitlelabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		GridBagConstraints gbc_curLatTitlelabel = new GridBagConstraints();
		gbc_curLatTitlelabel.anchor = GridBagConstraints.NORTHWEST;
		gbc_curLatTitlelabel.insets = new Insets(0, 0, 5, 5);
		gbc_curLatTitlelabel.gridx = 0;
		gbc_curLatTitlelabel.gridy = 1;
		add(curLatTitlelabel, gbc_curLatTitlelabel);
		curLatLabel.setHorizontalAlignment(SwingConstants.CENTER);
		curLatLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		GridBagConstraints gbc_curLatLabel = new GridBagConstraints();
		gbc_curLatLabel.anchor = GridBagConstraints.NORTHWEST;
		gbc_curLatLabel.insets = new Insets(0, 0, 5, 0);
		gbc_curLatLabel.gridx = 1;
		gbc_curLatLabel.gridy = 1;
		add(curLatLabel, gbc_curLatLabel);
		curLonTitleLabel.setHorizontalAlignment(SwingConstants.LEFT);
		curLonTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		GridBagConstraints gbc_curLonTitleLabel = new GridBagConstraints();
		gbc_curLonTitleLabel.anchor = GridBagConstraints.NORTHWEST;
		gbc_curLonTitleLabel.insets = new Insets(0, 0, 5, 5);
		gbc_curLonTitleLabel.gridx = 0;
		gbc_curLonTitleLabel.gridy = 2;
		add(curLonTitleLabel, gbc_curLonTitleLabel);
		curLonLabel.setHorizontalAlignment(SwingConstants.CENTER);
		curLonLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		GridBagConstraints gbc_curLonLabel = new GridBagConstraints();
		gbc_curLonLabel.anchor = GridBagConstraints.NORTHWEST;
		gbc_curLonLabel.insets = new Insets(0, 0, 5, 0);
		gbc_curLonLabel.gridx = 1;
		gbc_curLonLabel.gridy = 2;
		add(curLonLabel, gbc_curLonLabel);
		curCursTitleLabel.setHorizontalAlignment(SwingConstants.LEFT);
		curCursTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		GridBagConstraints gbc_curCursTitleLabel = new GridBagConstraints();
		gbc_curCursTitleLabel.anchor = GridBagConstraints.NORTHWEST;
		gbc_curCursTitleLabel.insets = new Insets(0, 0, 5, 5);
		gbc_curCursTitleLabel.gridx = 0;
		gbc_curCursTitleLabel.gridy = 3;
		add(curCursTitleLabel, gbc_curCursTitleLabel);
		curCursLabel.setHorizontalAlignment(SwingConstants.CENTER);
		curCursLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		GridBagConstraints gbc_curCursLabel = new GridBagConstraints();
		gbc_curCursLabel.anchor = GridBagConstraints.NORTHWEST;
		gbc_curCursLabel.insets = new Insets(0, 0, 5, 0);
		gbc_curCursLabel.gridx = 1;
		gbc_curCursLabel.gridy = 3;
		add(curCursLabel, gbc_curCursLabel);
		curDistTitleLabel.setHorizontalAlignment(SwingConstants.LEFT);
		curDistTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		GridBagConstraints gbc_curDistTitleLabel = new GridBagConstraints();
		gbc_curDistTitleLabel.anchor = GridBagConstraints.NORTHWEST;
		gbc_curDistTitleLabel.insets = new Insets(0, 0, 0, 5);
		gbc_curDistTitleLabel.gridx = 0;
		gbc_curDistTitleLabel.gridy = 4;
		add(curDistTitleLabel, gbc_curDistTitleLabel);
		curDistLabel.setHorizontalAlignment(SwingConstants.CENTER);
		curDistLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		GridBagConstraints gbc_curDistLabel = new GridBagConstraints();
		gbc_curDistLabel.anchor = GridBagConstraints.NORTHWEST;
		gbc_curDistLabel.gridx = 1;
		gbc_curDistLabel.gridy = 4;
		add(curDistLabel, gbc_curDistLabel);
	}
	
	public JLabel getCurLatLabel() {
		return curLatLabel;
	}
	
	public JLabel getCurLonLabel() {
		return curLonLabel;
	}
	
	public JLabel getCurCursLabel() {
		return curCursLabel;
	}
	
	public JLabel getCurDistLabel() {
		return curDistLabel;
	}
}
