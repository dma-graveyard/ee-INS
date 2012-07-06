package dk.frv.enav.ins.gui.ComponentPanels;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.border.EtchedBorder;

import com.bbn.openmap.gui.OMComponentPanel;

import dk.frv.ais.message.AisMessage;
import dk.frv.enav.ins.ais.AisHandler;
import dk.frv.enav.ins.ais.VesselStaticData;
import dk.frv.enav.ins.ais.VesselTarget;
import dk.frv.enav.ins.common.text.Formatter;
import dk.frv.enav.ins.gps.GpsData;
import dk.frv.enav.ins.gps.GpsHandler;
import dk.frv.enav.ins.gps.IGpsDataListener;
import dk.frv.enav.ins.gui.Panels.OwnShipPanel;

public class OwnShipComponentPanel extends OMComponentPanel implements IGpsDataListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final OwnShipPanel ownShipPanel = new OwnShipPanel();
	
	private AisHandler aisHandler = null;
	private GpsHandler gpsHandler = null;
	
	public OwnShipComponentPanel(){
		super();
		
		this.setMinimumSize(new Dimension(10, 70));
		
		ownShipPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		setBorder(null);
		
		setLayout(new BorderLayout(0, 0));
		add(ownShipPanel, BorderLayout.NORTH);
	}
	

	@Override
	public void gpsDataUpdate(GpsData gpsData) {

		String ownName = null;
		String ownCallsign = null;
		Long ownMmsi = null;
		VesselTarget ownShip = null;
		
		if (aisHandler != null) {
			ownShip = aisHandler.getOwnShip();
		}
		
		if (ownShip != null) {
			VesselStaticData staticData = ownShip.getStaticData();

			ownMmsi = ownShip.getMmsi();

			if (staticData != null) {
				ownName = AisMessage.trimText(staticData.getName());
				ownCallsign = AisMessage.trimText(staticData.getCallsign());
			}

		}
		
		ownShipPanel.getNameLabel().setText("<html>" + Formatter.formatString(ownName, "N/A") + "</html>");
		ownShipPanel.getCallsignLabel().setText("<html>" + Formatter.formatString(ownCallsign, "N/A") + "</html>");
		ownShipPanel.getMmsiLabel().setText(Formatter.formatLong(ownMmsi));
	}
	
	@Override
	public void findAndInit(Object obj) {
		if (gpsHandler == null && obj instanceof GpsHandler) {
			gpsHandler = (GpsHandler)obj;
			gpsHandler.addListener(this);
		}
		if (aisHandler == null && obj instanceof AisHandler) {
			aisHandler = (AisHandler)obj;
		}
	}

}
