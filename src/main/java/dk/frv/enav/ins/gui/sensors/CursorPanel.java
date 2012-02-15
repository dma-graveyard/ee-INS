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
		lblCursor.setFont(new Font("Segoe UI", Font.BOLD, 14));
		lblCursor.setHorizontalAlignment(SwingConstants.CENTER);
		curLatTitlelabel.setHorizontalAlignment(SwingConstants.LEFT);
		curLatTitlelabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		curLatLabel.setHorizontalAlignment(SwingConstants.CENTER);
		curLatLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		curLonTitleLabel.setHorizontalAlignment(SwingConstants.LEFT);
		curLonTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		curLonLabel.setHorizontalAlignment(SwingConstants.CENTER);
		curLonLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		curCursTitleLabel.setHorizontalAlignment(SwingConstants.LEFT);
		curCursTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		curCursLabel.setHorizontalAlignment(SwingConstants.CENTER);
		curCursLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		curDistTitleLabel.setHorizontalAlignment(SwingConstants.LEFT);
		curDistTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		curDistLabel.setHorizontalAlignment(SwingConstants.CENTER);
		curDistLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(lblCursor, GroupLayout.DEFAULT_SIZE, 587, Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(curLatTitlelabel)
								.addComponent(curLonTitleLabel)
								.addComponent(curCursTitleLabel)
								.addComponent(curDistTitleLabel))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(curDistLabel)
								.addComponent(curCursLabel)
								.addComponent(curLonLabel)
								.addComponent(curLatLabel))))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(lblCursor)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(curLatTitlelabel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(curLonTitleLabel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(curCursTitleLabel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(curDistTitleLabel))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(curLatLabel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(curLonLabel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(curCursLabel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(curDistLabel)))
					.addContainerGap(190, Short.MAX_VALUE))
		);
		setLayout(groupLayout);
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
