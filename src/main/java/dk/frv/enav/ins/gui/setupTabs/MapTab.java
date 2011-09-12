package dk.frv.enav.ins.gui.setupTabs;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.TitledBorder;

import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.frv.enav.ins.settings.MapSettings;
import javax.swing.SpinnerNumberModel;

public class MapTab extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private JSpinner spinnerDefaultMapScale;
	private JSpinner spinnerMaximumScale;
	private JSpinner spinnerLatitude;
	private JSpinner spinnerLongitude;
	private JSpinner spinnerShallowContour;
	private JSpinner spinnerSafetyDepth;
	private JCheckBox chckbxUseENC;
	private JSpinner spinnerSafetyContour;
	private JSpinner spinnerDeepContour;
	private JCheckBox chckbxShowText;
	private JCheckBox chckbxShallowPattern;
	private JCheckBox chckbxSimplePointSymbols;
	private JCheckBox chckbxPlainAreas;
	private JCheckBox chckbxTwoShades;
	private MapSettings mapSettings;
	
	/**
	 * Create the panel.
	 */
	public MapTab() {
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "S52 Layer", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "General", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(panel_1, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE)
						.addComponent(panel, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 159, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(113, Short.MAX_VALUE))
		);
		
		spinnerShallowContour = new JSpinner();
		
		spinnerSafetyDepth = new JSpinner();
		
		spinnerSafetyContour = new JSpinner();
		
		spinnerDeepContour = new JSpinner();
		
		chckbxShowText = new JCheckBox("Show text");
		
		chckbxShallowPattern = new JCheckBox("Shallow pattern");
		
		chckbxSimplePointSymbols = new JCheckBox("Simple point symbols");
		
		chckbxPlainAreas = new JCheckBox("Plain areas");
		
		chckbxTwoShades = new JCheckBox("Two shades");
		
		JLabel lblNewLabel = new JLabel("Shallow contour");
		
		JLabel lblSafetyDepth = new JLabel("Safety depth");
		
		JLabel lblSafetyContour = new JLabel("Safety contour");
		
		JLabel lblDeepContour = new JLabel("Deep contour");
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(chckbxSimplePointSymbols)
						.addComponent(chckbxShallowPattern)
						.addComponent(chckbxShowText)
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(spinnerShallowContour, GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblNewLabel))
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(spinnerSafetyDepth, GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblSafetyDepth))
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(spinnerSafetyContour, GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblSafetyContour))
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(spinnerDeepContour, GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblDeepContour)))
					.addGap(54)
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(chckbxTwoShades)
						.addComponent(chckbxPlainAreas))
					.addContainerGap(148, Short.MAX_VALUE))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(spinnerShallowContour, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(spinnerSafetyDepth, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblSafetyDepth))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(spinnerSafetyContour, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblSafetyContour))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(spinnerDeepContour, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblDeepContour))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(chckbxPlainAreas)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(chckbxTwoShades))
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(chckbxShowText)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(chckbxShallowPattern)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(chckbxSimplePointSymbols)))
					.addContainerGap(9, Short.MAX_VALUE))
		);
		panel.setLayout(gl_panel);
		
		spinnerDefaultMapScale = new JSpinner();
		spinnerDefaultMapScale.setModel(new SpinnerNumberModel(new Float(0), null, null, new Float(1)));
		
		spinnerMaximumScale = new JSpinner();
		spinnerMaximumScale.setModel(new SpinnerNumberModel(new Integer(0), null, null, new Integer(1)));
		
		spinnerLatitude = new JSpinner();
		spinnerLatitude.setModel(new SpinnerNumberModel(new Double(0), null, null, new Double(1)));
		
		spinnerLongitude = new JSpinner();
		spinnerLongitude.setModel(new SpinnerNumberModel(new Double(0), null, null, new Double(1)));
		
		chckbxUseENC = new JCheckBox("Use ENC");
		
		JLabel lblStartupMapCenter = new JLabel("Default map center");
		
		JLabel lblLatitude = new JLabel("Latitude");
		
		JLabel lblLongitude = new JLabel("Longitude");
		
		JLabel lblDefaultMapScale = new JLabel("Default map scale");
		
		JLabel lblMaximumZoomLevel = new JLabel("Maximum scale");
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addComponent(chckbxUseENC)
						.addGroup(gl_panel_1.createSequentialGroup()
							.addComponent(spinnerDefaultMapScale, GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblDefaultMapScale))
						.addGroup(gl_panel_1.createSequentialGroup()
							.addComponent(spinnerMaximumScale, GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblMaximumZoomLevel))
						.addComponent(lblStartupMapCenter)
						.addGroup(gl_panel_1.createSequentialGroup()
							.addComponent(lblLatitude)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(spinnerLatitude, GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE)
							.addGap(29)
							.addComponent(lblLongitude)
							.addGap(5)
							.addComponent(spinnerLongitude, GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(138, Short.MAX_VALUE))
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(spinnerDefaultMapScale, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblDefaultMapScale))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(spinnerMaximumScale, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblMaximumZoomLevel))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblStartupMapCenter)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblLatitude)
						.addComponent(spinnerLatitude, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblLongitude)
						.addComponent(spinnerLongitude, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(chckbxUseENC)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		panel_1.setLayout(gl_panel_1);
		setLayout(groupLayout);
	}
	
	public void loadSettings(MapSettings mapSettings) {
		this.mapSettings = mapSettings;
		spinnerDefaultMapScale.setValue(mapSettings.getScale());
		spinnerMaximumScale.setValue(mapSettings.getMaxScale());
		Float latitude = (Float) mapSettings.getCenter().getLatitude();
		Float longitude = (Float) mapSettings.getCenter().getLongitude();
		spinnerLatitude.setValue(latitude.doubleValue());
		spinnerLongitude.setValue(longitude.doubleValue());
		chckbxUseENC.setSelected(mapSettings.isUseEnc());
		
		spinnerShallowContour.setValue(mapSettings.getS52ShallowContour());
		spinnerSafetyDepth.setValue(mapSettings.getS52SafetyDepth());
		spinnerSafetyContour.setValue(mapSettings.getS52SafetyContour());
		spinnerDeepContour.setValue(mapSettings.getS52DeepContour());
		chckbxShowText.setSelected(mapSettings.isS52ShowText());
		chckbxShallowPattern.setSelected(mapSettings.isS52ShallowPattern());
		chckbxSimplePointSymbols.setSelected(mapSettings.isUseSimplePointSymbols());
		chckbxPlainAreas.setSelected(mapSettings.isUsePlainAreas());
		chckbxTwoShades.setSelected(mapSettings.isS52TwoShades());
	}
	
	public void saveSettings() {
		mapSettings.setScale((Float) spinnerDefaultMapScale.getValue());
		mapSettings.setMaxScale((Integer) spinnerMaximumScale.getValue());
		LatLonPoint center = new LatLonPoint.Double((Double) spinnerLatitude.getValue(), (Double) spinnerLongitude.getValue());
		mapSettings.setCenter(center);
		mapSettings.setUseEnc(chckbxUseENC.isSelected());
		
		mapSettings.setS52ShallowContour((Integer) spinnerShallowContour.getValue());
		mapSettings.setS52SafetyDepth((Integer) spinnerSafetyDepth.getValue());
		mapSettings.setS52SafetyContour((Integer) spinnerSafetyContour.getValue());
		mapSettings.setS52ShowText(chckbxShowText.isSelected());
		mapSettings.setS52ShallowPattern(chckbxShallowPattern.isSelected());
		mapSettings.setUseSimplePointSymbols(chckbxSimplePointSymbols.isSelected());
		mapSettings.setUsePlainAreas(chckbxPlainAreas.isSelected());
		mapSettings.setS52TwoShades(chckbxTwoShades.isSelected());
	}
	
}
