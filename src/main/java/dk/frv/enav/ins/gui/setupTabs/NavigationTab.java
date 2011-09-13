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
package dk.frv.enav.ins.gui.setupTabs;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;

import dk.frv.enav.ins.settings.NavSettings;

public class NavigationTab extends JPanel {
	private static final long serialVersionUID = 1L;
	private JCheckBox checkBoxLookAhead;
	private JSpinner spinnerAutoFollowPctOffTolerance;
	private JSpinner spinnerCogVectorLength;
	private JSpinner spinnerShowMinuteMarksSelf;
	private JSpinner spinnerShowArrowScale;
	private JSpinner spinnerDefaultSpeed;
	private JSpinner spinnerDefaultTurnRad;
	private JSpinner spinnerDefaultXtd;
	private NavSettings navSettings;

	public NavigationTab() {
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Own Ship", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		spinnerCogVectorLength = new JSpinner(new SpinnerNumberModel(new Double(0), null, null, new Double(1)));
		
		JLabel label = new JLabel("COG vector length (min)");
		
		spinnerAutoFollowPctOffTolerance = new JSpinner();
		
		JLabel label_1 = new JLabel("Auto follow tolerance (%)");
		
		checkBoxLookAhead = new JCheckBox("Look ahead");
		
		spinnerShowMinuteMarksSelf = new JSpinner();
		
		JLabel label_2 = new JLabel("Scale to show minute marks (screen distance in pixels)");
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(spinnerCogVectorLength, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(label))
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(spinnerAutoFollowPctOffTolerance, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(label_1))
						.addComponent(checkBoxLookAhead)
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(spinnerShowMinuteMarksSelf, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(label_2)))
					.addContainerGap(101, Short.MAX_VALUE))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addComponent(checkBoxLookAhead)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(spinnerAutoFollowPctOffTolerance, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(label_1))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(spinnerCogVectorLength, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(label))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(label_2)
						.addComponent(spinnerShowMinuteMarksSelf, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(13, Short.MAX_VALUE))
		);
		panel.setLayout(gl_panel);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Route Settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		spinnerShowArrowScale = new JSpinner(new SpinnerNumberModel(new Float(0), null, null, new Float(1)));
		
		JLabel label_3 = new JLabel("Scale to show route arrows (map scale)");
		
		spinnerDefaultSpeed = new JSpinner(new SpinnerNumberModel(new Double(0), null, null, new Double(1)));
		
		JLabel label_4 = new JLabel("New route default speed");
		
		spinnerDefaultTurnRad = new JSpinner(new SpinnerNumberModel(new Double(0), null, null, new Double(1)));
		
		JLabel label_5 = new JLabel("New route default turn radius");
		
		spinnerDefaultXtd = new JSpinner(new SpinnerNumberModel(new Double(0), null, null, new Double(1)));
		
		JLabel label_6 = new JLabel("New route default xtd");
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGap(0, 429, Short.MAX_VALUE)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_1.createSequentialGroup()
							.addComponent(spinnerShowArrowScale, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
							.addGap(4)
							.addComponent(label_3))
						.addGroup(gl_panel_1.createSequentialGroup()
							.addComponent(spinnerDefaultSpeed, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(label_4))
						.addGroup(gl_panel_1.createSequentialGroup()
							.addComponent(spinnerDefaultTurnRad, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(label_5))
						.addGroup(gl_panel_1.createSequentialGroup()
							.addComponent(spinnerDefaultXtd, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(label_6)))
					.addContainerGap(144, Short.MAX_VALUE))
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGap(0, 139, Short.MAX_VALUE)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addComponent(spinnerShowArrowScale, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_panel_1.createSequentialGroup()
							.addGap(3)
							.addComponent(label_3)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(spinnerDefaultSpeed, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(label_4))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(spinnerDefaultTurnRad, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(label_5))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(spinnerDefaultXtd, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(label_6))
					.addContainerGap(23, Short.MAX_VALUE))
		);
		panel_1.setLayout(gl_panel_1);
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(panel_1, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
						.addComponent(panel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 129, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(36, Short.MAX_VALUE))
		);
		setLayout(groupLayout);
	}

	public void loadSettings(NavSettings navSettings) {
		this.navSettings = navSettings;
		checkBoxLookAhead.setSelected(navSettings.isLookAhead());
		spinnerAutoFollowPctOffTolerance.setValue(navSettings.getAutoFollowPctOffTollerance());
		spinnerCogVectorLength.setValue(navSettings.getCogVectorLength());
		spinnerShowMinuteMarksSelf.setValue(navSettings.getShowMinuteMarksSelf());
		
		spinnerShowArrowScale.setValue(navSettings.getShowArrowScale());
		spinnerDefaultSpeed.setValue(navSettings.getDefaultSpeed());
		spinnerDefaultTurnRad.setValue(navSettings.getDefaultTurnRad());
		spinnerDefaultXtd.setValue(navSettings.getDefaultXtd());
	}
	
	public void saveSettings() {
		navSettings.setLookAhead(checkBoxLookAhead.isSelected());
		navSettings.setAutoFollowPctOffTollerance((Integer) spinnerAutoFollowPctOffTolerance.getValue());
		navSettings.setCogVectorLength((Double) spinnerCogVectorLength.getValue());
		navSettings.setShowMinuteMarksSelf((Integer) spinnerShowMinuteMarksSelf.getValue());
		
		navSettings.setShowArrowScale((Float) spinnerShowArrowScale.getValue());
		navSettings.setDefaultSpeed((Double) spinnerDefaultSpeed.getValue());
		navSettings.setDefaultTurnRad((Double) spinnerDefaultTurnRad.getValue());
		navSettings.setDefaultXtd((Double) spinnerDefaultXtd.getValue());
	}
	
}
