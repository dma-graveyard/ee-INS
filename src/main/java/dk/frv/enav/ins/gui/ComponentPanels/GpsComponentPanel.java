package dk.frv.enav.ins.gui.ComponentPanels;

import java.awt.BorderLayout;

import javax.swing.border.EtchedBorder;

import com.bbn.openmap.gui.OMComponentPanel;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.enav.ins.ais.AisHandler;
import dk.frv.enav.ins.ais.VesselPositionData;
import dk.frv.enav.ins.ais.VesselTarget;
import dk.frv.enav.ins.common.text.Formatter;
import dk.frv.enav.ins.gps.GpsData;
import dk.frv.enav.ins.gps.GpsHandler;
import dk.frv.enav.ins.gps.IGpsDataListener;
import dk.frv.enav.ins.gui.SensorPanel;
import dk.frv.enav.ins.gui.Panels.GPSPanel;

public class GpsComponentPanel extends OMComponentPanel implements
		IGpsDataListener {

	private static final long serialVersionUID = 1L;
	private GpsHandler gpsHandler = null;
	private AisHandler aisHandler = null;
	
	private GpsData gpsData = null;
	
	private final GPSPanel gpsPanel = new GPSPanel();

	public GpsComponentPanel() {
		super();
		gpsPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		setBorder(null);
		
		setLayout(new BorderLayout(0, 0));
		add(gpsPanel, BorderLayout.NORTH);
	}



	public void setGpsData(GpsData gpsData) {
		synchronized (SensorPanel.class) {
			this.gpsData = gpsData;
		}
	}
	
	public GpsData getGpsData() {
		synchronized (SensorPanel.class) {
			return gpsData;
		}
	}
	
	@Override
	public void gpsDataUpdate(GpsData gpsData) {
		this.setGpsData(gpsData);
		GeoLocation pos = gpsData.getPosition();
		if (gpsData.isBadPosition() || pos == null) {
			gpsPanel.getLatLabel().setText("N/A");
			gpsPanel.getLonLabel().setText("N/A");
		} else {
			gpsPanel.getLatLabel().setText(Formatter.latToPrintable(pos.getLatitude()));
			gpsPanel.getLonLabel().setText(Formatter.lonToPrintable(pos.getLongitude()));
		}
		
		if (gpsData.isBadPosition() || gpsData.getSog() == null) {
			gpsPanel.getSogLabel().setText("N/A");
		} else {
			gpsPanel.getSogLabel().setText(Formatter.formatSpeed(gpsData.getSog()));
		}
		
		if (gpsData.isBadPosition() || gpsData.getCog() == null) {
			gpsPanel.getCogLabel().setText("N/A");
		} else {
			gpsPanel.getCogLabel().setText(Formatter.formatDegrees(gpsData.getCog(), 1));
		}
		
		Double heading = null;
		VesselTarget ownShip = null;
		
		if (aisHandler != null) {
			ownShip = aisHandler.getOwnShip();
		}
		
		if (ownShip != null) {
			VesselPositionData posData = ownShip.getPositionData();

			if (posData != null && posData.getTrueHeading() < 360) {
				heading = (double) posData.getTrueHeading();
			}

		}
		
		gpsPanel.getHdgLabel().setText(Formatter.formatDegrees(heading, 1));

		
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
