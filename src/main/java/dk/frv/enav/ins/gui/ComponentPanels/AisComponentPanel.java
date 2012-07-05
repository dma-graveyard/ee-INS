package dk.frv.enav.ins.gui.ComponentPanels;

import java.awt.BorderLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.border.EtchedBorder;

import com.bbn.openmap.gui.OMComponentPanel;

import dk.frv.ais.message.AisMessage;
import dk.frv.enav.ins.ais.AisHandler;
import dk.frv.enav.ins.common.text.Formatter;
import dk.frv.enav.ins.gui.Panels.AisTargetPanel;

public class AisComponentPanel extends OMComponentPanel
		 {

	private static final long serialVersionUID = 1L;
	private AisHandler aisHandler = null;
	
	private final AisTargetPanel aisPanel = new AisTargetPanel();
	
	
	
	private JLabel nameLabel;
	private JLabel callsignLabel;
	private JLabel sogLabel;
	private JLabel cogLabel;
	private JLabel dstLabel;
	private JLabel brgLabel;
	private JCheckBox intendedRouteCheckbox;
	private JLabel intendedRouteTitelLabel;
	private JCheckBox dynamicNoGoCheckbox;
	private JLabel dynamicNogoTitelLabel;

	public AisComponentPanel() {
		super();
		aisPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		setBorder(null);
		
		setLayout(new BorderLayout(0, 0));
		add(aisPanel, BorderLayout.NORTH);
		
		
		
		nameLabel = aisPanel.getNameLabel();
		callsignLabel = aisPanel.getCallsignLabel();
		sogLabel = aisPanel.getSogLabel();
		cogLabel = aisPanel.getCogLabel();
		dstLabel = aisPanel.getDstLabel();
		brgLabel = aisPanel.getBrgLabel();
		intendedRouteCheckbox = aisPanel.getIntendedRouteCheckbox();
		intendedRouteTitelLabel = aisPanel.getIntendedRouteTitelLabel();
		dynamicNoGoCheckbox = aisPanel.getDynamicNoGoCheckbox();
		dynamicNogoTitelLabel = aisPanel.getDynamicNogoTitelLabel();
		
		intendedRouteCheckbox.setEnabled(false);
		intendedRouteTitelLabel.setEnabled(false);
		dynamicNoGoCheckbox.setEnabled(false);
		dynamicNogoTitelLabel.setEnabled(false);
		
	}


	@Override
	public void findAndInit(Object obj) {

		if (aisHandler == null && obj instanceof AisHandler) {
			aisHandler = (AisHandler)obj;
		}
	}

	public void receiveHighlight(String name, String callsign, float cog,
			double rhumbLineDistance, double rhumbLineBearing, float sog) {

		nameLabel.setText(AisMessage.trimText(name));
		callsignLabel.setText(AisMessage.trimText(callsign));
		cogLabel.setText( Float.toString(cog));
		dstLabel.setText(Formatter.formatDistNM(rhumbLineDistance));
		brgLabel.setText(Formatter.formatDegrees(rhumbLineBearing, 1));
		sogLabel.setText( Formatter.formatSpeed((double) sog));
	}

	public void receiveHighlight(float cog,
			double rhumbLineDistance, double rhumbLineBearing, float sog) {

		nameLabel.setText("N/A");
		callsignLabel.setText("N/A");
		
		cogLabel.setText( Float.toString(cog));
		dstLabel.setText(Formatter.formatDistNM(rhumbLineDistance));
		brgLabel.setText(Formatter.formatDegrees(rhumbLineBearing, 1));
		sogLabel.setText( Formatter.formatSpeed((double) sog));
		
		
	}
	
	public void resetHighLight(){
		nameLabel.setText("N/A");
		callsignLabel.setText("N/A");
		cogLabel.setText( "N/A");
		dstLabel.setText("N/A");
		brgLabel.setText("N/A");
		sogLabel.setText("N/A");
	}

}
