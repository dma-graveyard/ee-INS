package dk.frv.enav.ins.gui.ComponentPanels;

import java.awt.BorderLayout;

import javax.swing.border.EtchedBorder;

import com.bbn.openmap.gui.OMComponentPanel;
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.enav.ins.common.text.Formatter;
import dk.frv.enav.ins.event.IMapCoordListener;
import dk.frv.enav.ins.gps.GpsData;
import dk.frv.enav.ins.gps.GpsHandler;
import dk.frv.enav.ins.gps.IGpsDataListener;
import dk.frv.enav.ins.gui.SensorPanel;
import dk.frv.enav.ins.gui.Panels.CursorPanel;

public class CursorComponentPanel extends OMComponentPanel implements IGpsDataListener, IMapCoordListener {

	private static final long serialVersionUID = 1L;
	private final CursorPanel cursorPanel = new CursorPanel();
	private GpsData gpsData = null;
	
	public CursorComponentPanel(){
		super();
		
//		this.setMinimumSize(new Dimension(10, 110));
		
		cursorPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		setBorder(null);
		setLayout(new BorderLayout(0, 0)); 	
		add(cursorPanel, BorderLayout.NORTH);

	}

	/**
	 * Receive mouse location
	 */
	@Override
	public void recieveCoord(LatLonPoint llp) {
		cursorPanel.getCurLatLabel().setText(Formatter.latToPrintable(llp.getLatitude()));
		cursorPanel.getCurLonLabel().setText(Formatter.lonToPrintable(llp.getLongitude()));
		GpsData gpsData = this.getGpsData();
		if(gpsData == null || gpsData.isBadPosition() || gpsData.getPosition() == null){
			cursorPanel.getCurCursLabel().setText("N/A");
			cursorPanel.getCurDistLabel().setText("N/A");
		} else {
			GeoLocation pos = gpsData.getPosition();
			GeoLocation curPos = new GeoLocation((double) llp.getLatitude(), (double) llp.getLongitude()); 
			cursorPanel.getCurCursLabel().setText(Formatter.formatDegrees(pos.getRhumbLineBearing(curPos), 1));
			double distance = pos.getRhumbLineDistance(curPos)/1852;
			cursorPanel.getCurDistLabel().setText(Formatter.formatDistNM(distance));						
		}
	}
	
	public GpsData getGpsData() {
		synchronized (SensorPanel.class) {
			return gpsData;
		}
	}
	
	public void setGpsData(GpsData gpsData) {
		synchronized (SensorPanel.class) {
			this.gpsData = gpsData;
		}
	}
	
	@Override
	public void gpsDataUpdate(GpsData gpsData) {
		this.setGpsData(gpsData);
	}
	
	@Override
	public void findAndInit(Object obj) {
		if (obj instanceof GpsHandler) {
			((GpsHandler) obj).addListener(this);
		}
	}
}
