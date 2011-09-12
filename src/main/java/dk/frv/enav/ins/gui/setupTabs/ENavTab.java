package dk.frv.enav.ins.gui.setupTabs;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;

import dk.frv.enav.ins.settings.EnavSettings;

public class ENavTab extends JPanel {
	private static final long serialVersionUID = 1L;
	private JTextField textFieldServerPort;
	private JTextField textFieldServerName;
	private JTextField textFieldConnectionTimeout;
	private JTextField textFieldReadTimeout;
	private JSpinner spinnerActiveRouteMetocPoll;
	private JSpinner spinnerMetocTimeDiffTolerance;
	private JSpinner spinnerMsiPollInterval;
	private JSpinner spinnerMsiTextboxesVisibleAtScale;
	private JSpinner spinnerMetocTtl;
	private EnavSettings enavSettings;
	private JSpinner spinnerMsiRelevanceGpsUpdateRange;
	private JSpinner spinnerMsiVisibilityFromOwnShipRange;
	private JSpinner spinnerMsiVisibilityFromNewWaypoint;
	
	
	public ENavTab() {
		
		JPanel MetocPanel = new JPanel();
		MetocPanel.setBorder(new TitledBorder(null, "METOC Settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		spinnerMetocTtl = new JSpinner();
		
		JLabel label = new JLabel("METOC validity duration (min)");
		
		spinnerActiveRouteMetocPoll = new JSpinner();
		
		JLabel label_1 = new JLabel("Active route METOC poll interval (min)");
		
		spinnerMetocTimeDiffTolerance = new JSpinner();
		
		JLabel label_2 = new JLabel("METOC time difference tolerance (min)");
		GroupLayout gl_MetocPanel = new GroupLayout(MetocPanel);
		gl_MetocPanel.setHorizontalGroup(
			gl_MetocPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_MetocPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_MetocPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_MetocPanel.createSequentialGroup()
							.addComponent(spinnerMetocTtl, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(label))
						.addGroup(gl_MetocPanel.createSequentialGroup()
							.addComponent(spinnerActiveRouteMetocPoll, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(label_1))
						.addGroup(gl_MetocPanel.createSequentialGroup()
							.addComponent(spinnerMetocTimeDiffTolerance, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(label_2)))
					.addContainerGap(168, Short.MAX_VALUE))
		);
		gl_MetocPanel.setVerticalGroup(
			gl_MetocPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_MetocPanel.createSequentialGroup()
					.addGroup(gl_MetocPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(spinnerMetocTtl, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(label))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_MetocPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(spinnerActiveRouteMetocPoll, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(label_1))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_MetocPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(spinnerMetocTimeDiffTolerance, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(label_2))
					.addContainerGap(16, Short.MAX_VALUE))
		);
		MetocPanel.setLayout(gl_MetocPanel);
		
		JPanel HttpPanel = new JPanel();
		HttpPanel.setBorder(new TitledBorder(null, "HTTP Settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JLabel label_3 = new JLabel("Server name:");
		
		JLabel label_4 = new JLabel("Server port:");
		
		JLabel label_5 = new JLabel("Connection timeout:");
		
		JLabel label_6 = new JLabel("Read timeout:");
		
		textFieldServerPort = new JTextField();
		
		textFieldServerName = new JTextField();
		textFieldServerName.setColumns(10);
		
		textFieldConnectionTimeout = new JTextField();
		
		textFieldReadTimeout = new JTextField();
		GroupLayout gl_HttpPanel = new GroupLayout(HttpPanel);
		gl_HttpPanel.setHorizontalGroup(
			gl_HttpPanel.createParallelGroup(Alignment.LEADING)
				.addGap(0, 429, Short.MAX_VALUE)
				.addGroup(gl_HttpPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_HttpPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(label_3)
						.addComponent(label_4)
						.addComponent(label_5)
						.addComponent(label_6))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_HttpPanel.createParallelGroup(Alignment.LEADING, false)
						.addComponent(textFieldServerPort, GroupLayout.PREFERRED_SIZE, 288, GroupLayout.PREFERRED_SIZE)
						.addComponent(textFieldServerName, GroupLayout.PREFERRED_SIZE, 288, GroupLayout.PREFERRED_SIZE)
						.addComponent(textFieldConnectionTimeout, GroupLayout.PREFERRED_SIZE, 288, GroupLayout.PREFERRED_SIZE)
						.addComponent(textFieldReadTimeout, GroupLayout.PREFERRED_SIZE, 288, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		gl_HttpPanel.setVerticalGroup(
			gl_HttpPanel.createParallelGroup(Alignment.LEADING)
				.addGap(0, 138, Short.MAX_VALUE)
				.addGroup(gl_HttpPanel.createSequentialGroup()
					.addGroup(gl_HttpPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(label_3)
						.addComponent(textFieldServerName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_HttpPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(textFieldServerPort, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(label_4))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_HttpPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(textFieldConnectionTimeout, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(label_5))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_HttpPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(textFieldReadTimeout, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(label_6))
					.addContainerGap(14, Short.MAX_VALUE))
		);
		HttpPanel.setLayout(gl_HttpPanel);
		
		JPanel MsiPanel = new JPanel();
		MsiPanel.setBorder(new TitledBorder(null, "MSI Settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		spinnerMsiPollInterval = new JSpinner();
		
		JLabel label_7 = new JLabel("MSI poll interval (sec)");
		
		spinnerMsiTextboxesVisibleAtScale = new JSpinner();
		
		JLabel label_8 = new JLabel("MSI textbox visibility scale (map scale)");
		
		spinnerMsiRelevanceGpsUpdateRange = new JSpinner(new SpinnerNumberModel(new Double(0), null, null, new Double(1)));
		
		spinnerMsiVisibilityFromOwnShipRange = new JSpinner(new SpinnerNumberModel(new Double(0), null, null, new Double(1)));
		
		JLabel lblRangeBetweenGps = new JLabel("GPS position interval before MSI visibility is calcualted");
		
		JLabel lblRelevancyRangeFrom = new JLabel("MSI visibility range from own ship");
		
		spinnerMsiVisibilityFromNewWaypoint = new JSpinner(new SpinnerNumberModel(new Double(0), null, null, new Double(1)));
		
		JLabel lblMsiVisibilityRange = new JLabel("MSI visibility range from new waypoint at route creation");
		GroupLayout gl_MsiPanel = new GroupLayout(MsiPanel);
		gl_MsiPanel.setHorizontalGroup(
			gl_MsiPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_MsiPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_MsiPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_MsiPanel.createSequentialGroup()
							.addComponent(spinnerMsiPollInterval, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(label_7, GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE))
						.addGroup(gl_MsiPanel.createSequentialGroup()
							.addComponent(spinnerMsiTextboxesVisibleAtScale, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(label_8, GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE))
						.addGroup(gl_MsiPanel.createSequentialGroup()
							.addComponent(spinnerMsiRelevanceGpsUpdateRange, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblRangeBetweenGps, GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE))
						.addGroup(gl_MsiPanel.createSequentialGroup()
							.addComponent(spinnerMsiVisibilityFromOwnShipRange, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblRelevancyRangeFrom, GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE))
						.addGroup(gl_MsiPanel.createSequentialGroup()
							.addComponent(spinnerMsiVisibilityFromNewWaypoint, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblMsiVisibilityRange, GroupLayout.PREFERRED_SIZE, 328, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		gl_MsiPanel.setVerticalGroup(
			gl_MsiPanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_MsiPanel.createSequentialGroup()
					.addGroup(gl_MsiPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(spinnerMsiPollInterval, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(label_7))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_MsiPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(spinnerMsiTextboxesVisibleAtScale, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(label_8))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_MsiPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(spinnerMsiRelevanceGpsUpdateRange, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblRangeBetweenGps))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_MsiPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(spinnerMsiVisibilityFromOwnShipRange, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblRelevancyRangeFrom))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_MsiPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(spinnerMsiVisibilityFromNewWaypoint, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblMsiVisibilityRange))
					.addContainerGap(27, Short.MAX_VALUE))
		);
		MsiPanel.setLayout(gl_MsiPanel);
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(MsiPanel, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 434, Short.MAX_VALUE)
						.addComponent(MetocPanel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 434, Short.MAX_VALUE)
						.addComponent(HttpPanel, GroupLayout.PREFERRED_SIZE, 434, Short.MAX_VALUE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(MetocPanel, GroupLayout.PREFERRED_SIZE, 108, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(HttpPanel, GroupLayout.PREFERRED_SIZE, 135, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(MsiPanel, GroupLayout.PREFERRED_SIZE, 178, GroupLayout.PREFERRED_SIZE)
					.addGap(75))
		);
		setLayout(groupLayout);
	}

	public void loadSettings(EnavSettings enavSettings) {
		this.enavSettings = enavSettings;
		spinnerMetocTtl.setValue(enavSettings.getMetocTtl());
		spinnerActiveRouteMetocPoll.setValue(enavSettings.getActiveRouteMetocPollInterval());
		spinnerMetocTimeDiffTolerance.setValue(enavSettings.getMetocTimeDiffTolerance());
		
		//TODO: make proper input text fields with filter
		textFieldServerName.setText(enavSettings.getServerName());
//		textFieldServerPort.setText(enavSettings.getHttpPort());
//		textFieldConnectionTimeout.setText(enavSettings.getConnectTimeout());
//		textFieldReadTimeout.setText(enavSettings.getReadTimeout());
		
		spinnerMsiPollInterval.setValue(enavSettings.getMsiPollInterval());
		spinnerMsiTextboxesVisibleAtScale.setValue(enavSettings.getMsiTextboxesVisibleAtScale());
		
		spinnerMsiRelevanceGpsUpdateRange.setValue(enavSettings.getMsiRelevanceGpsUpdateRange());
		spinnerMsiVisibilityFromOwnShipRange.setValue(enavSettings.getMsiRelevanceFromOwnShipRange());
		spinnerMsiVisibilityFromNewWaypoint.setValue(enavSettings.getMsiVisibilityFromNewWaypoint());
	}
	
	public void saveSettings() {
		enavSettings.setMetocTtl((Integer) spinnerMetocTtl.getValue());
		enavSettings.setActiveRouteMetocPollInterval((Integer) spinnerActiveRouteMetocPoll.getValue());
		enavSettings.setMetocTimeDiffTolerance((Integer) spinnerMetocTimeDiffTolerance.getValue());
		
		enavSettings.setServerName(textFieldServerName.getText());
		//TODO: get rest of settings from text fields when they have filter on
		
		enavSettings.setMsiPollInterval((Integer) spinnerMsiPollInterval.getValue());
		enavSettings.setMsiTextboxesVisibleAtScale((Integer) spinnerMsiTextboxesVisibleAtScale.getValue());
		
		enavSettings.setMsiRelevanceGpsUpdateRange((Double) spinnerMsiRelevanceGpsUpdateRange.getValue());
		enavSettings.setMsiRelevanceFromOwnShipRange((Double) spinnerMsiVisibilityFromOwnShipRange.getValue());
		enavSettings.setMsiVisibilityFromNewWaypoint((Double) spinnerMsiVisibilityFromNewWaypoint.getValue());
	}
}
