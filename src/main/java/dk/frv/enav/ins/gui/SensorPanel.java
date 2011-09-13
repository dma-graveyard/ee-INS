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
package dk.frv.enav.ins.gui;

import java.util.Date;
import java.util.Locale;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EtchedBorder;

import com.bbn.openmap.event.ProjectionEvent;
import com.bbn.openmap.event.ProjectionListener;
import com.bbn.openmap.gui.OMComponentPanel;
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.ais.message.AisMessage;
import dk.frv.enav.ins.EeINS;
import dk.frv.enav.ins.ais.AisTargets;
import dk.frv.enav.ins.ais.VesselPositionData;
import dk.frv.enav.ins.ais.VesselStaticData;
import dk.frv.enav.ins.ais.VesselTarget;
import dk.frv.enav.ins.common.text.Formatter;
import dk.frv.enav.ins.event.IMapCoordListener;
import dk.frv.enav.ins.gps.GnssTime;
import dk.frv.enav.ins.gps.GpsData;
import dk.frv.enav.ins.gps.GpsHandler;
import dk.frv.enav.ins.gps.IGpsDataListener;
import dk.frv.enav.ins.gui.sensors.ActiveWaypointPanel;
import dk.frv.enav.ins.gui.sensors.CursorPanel;
import dk.frv.enav.ins.gui.sensors.GPSPanel;
import dk.frv.enav.ins.gui.sensors.OwnShipPanel;
import dk.frv.enav.ins.gui.sensors.ScalePanel;
import dk.frv.enav.ins.msi.MsiHandler;
import dk.frv.enav.ins.route.IRoutesUpdateListener;
import dk.frv.enav.ins.route.RouteManager;
import dk.frv.enav.ins.route.RoutesUpdateEvent;

public class SensorPanel extends OMComponentPanel implements IGpsDataListener, Runnable, ProjectionListener, IMapCoordListener, IRoutesUpdateListener {
	
	private static final long serialVersionUID = 1L;
	
	private GpsHandler gpsHandler = null;
	private AisTargets aisTargets = null;
	private MsiHandler msiHandler = null;
	
	private GpsData gpsData = null;
	private GnssTime gnssTime = null;
	private ChartPanel chartPanel;
	private RouteManager routeManager;
	private final ScalePanel scalePanel = new ScalePanel();	
	private final OwnShipPanel ownShipPanel = new OwnShipPanel();
	private final GPSPanel gpsPanel = new GPSPanel();
	private final CursorPanel cursorPanel = new CursorPanel();
	private final ActiveWaypointPanel activeWaypointPanel;
	private final JLabel euBalticLogo = new JLabel("");
	private final JLabel efficienseaLogo = new JLabel("");
	
	public SensorPanel() {
		super();
		activeWaypointPanel = new ActiveWaypointPanel();
		activeWaypointPanel.setVisible(false);
		activeWaypointPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		cursorPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		gpsPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		scalePanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		ownShipPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		setBorder(null);
		
		efficienseaLogo.setIcon(new ImageIcon(EeINS.class.getResource("/images/sensorPanel/efficiensea.png")));
		euBalticLogo.setIcon(new ImageIcon(EeINS.class.getResource("/images/sensorPanel/euBaltic.png")));
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(ownShipPanel, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
						.addComponent(scalePanel, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
						.addComponent(gpsPanel, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
						.addComponent(cursorPanel, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
						.addComponent(activeWaypointPanel, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
						.addComponent(euBalticLogo, Alignment.TRAILING)
						.addComponent(efficienseaLogo, Alignment.TRAILING))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(scalePanel, GroupLayout.PREFERRED_SIZE, 53, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(ownShipPanel, GroupLayout.PREFERRED_SIZE, 110, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(gpsPanel, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(cursorPanel, GroupLayout.PREFERRED_SIZE, 118, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(activeWaypointPanel, GroupLayout.PREFERRED_SIZE, 185, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 172, Short.MAX_VALUE)
					.addComponent(efficienseaLogo)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(euBalticLogo)
					.addContainerGap())
		);
		setLayout(groupLayout);
		(new Thread(this)).start();
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
		String ownName = null;
		String ownCallsign = null;
		Long ownMmsi = null;
		VesselTarget ownShip = null;
		
		if (aisTargets != null) {
			ownShip = aisTargets.getOwnShip();
		}
		
		if (ownShip != null) {
			VesselPositionData posData = ownShip.getPositionData();
			VesselStaticData staticData = ownShip.getStaticData();

			ownMmsi = ownShip.getMmsi();
			if (posData != null && posData.getTrueHeading() < 360) {
				heading = (double) posData.getTrueHeading();
			}

			if (staticData != null) {
				ownName = AisMessage.trimText(staticData.getName());
				ownCallsign = AisMessage.trimText(staticData.getCallsign());
			}

		}
		
		gpsPanel.getHdgLabel().setText(Formatter.formatDegrees(heading, 1));
		ownShipPanel.getNameLabel().setText("<html>" + Formatter.formatString(ownName, "N/A") + "</html>");
		ownShipPanel.getCallsignLabel().setText("<html>" + Formatter.formatString(ownCallsign, "N/A") + "</html>");
		ownShipPanel.getMmsiLabel().setText(Formatter.formatLong(ownMmsi));
		
		activeWaypointPanel.updateActiveNavData();
		
	}
	
	@Override
	public void run() {
		while (true) {
			if (gnssTime != null) {
				Date now = gnssTime.getDate();
				scalePanel.getTimeLabel().setText(Formatter.formatLongDateTime(now));
			}
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) { }
		}
		
	}
	
	public void initPanel(ChartPanel chartPanel) {
		// Add gps panel as position listener
		EeINS.getGpsHandler().addListener(this);
		
		// Start time panel thread
		this.chartPanel = chartPanel;
		new Thread(this).start();
		
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
	public void projectionChanged(ProjectionEvent arg0) {
		setScale(chartPanel.getMap().getProjection().getScale());
	}
	
	public void setScale(float scale){
		scalePanel.getScaleLabel().setText("Scale: " + String.format(Locale.US, "%3.0f", scale));
	}

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

	@Override
	public void routesChanged(RoutesUpdateEvent e) {
		if(routeManager.isRouteActive()){
			activeWaypointPanel.setVisible(true);
			activeWaypointPanel.updateActiveNavData();
		} else if (activeWaypointPanel.isVisible()) {
			activeWaypointPanel.setVisible(false);
		}
	}
	
	@Override
	public void findAndInit(Object obj) {
		if (gnssTime == null && obj instanceof GnssTime) {
			gnssTime = (GnssTime)obj;
		}
		if (obj instanceof ChartPanel) {
			chartPanel = (ChartPanel)obj;
			chartPanel.getMap().addProjectionListener(this);
			return;
		}
		if (obj instanceof RouteManager) {
			routeManager = (RouteManager)obj;
			activeWaypointPanel.setRouteManager(routeManager);
			routeManager.addListener(this);
			return;
		}
		if (gpsHandler == null && obj instanceof GpsHandler) {
			gpsHandler = (GpsHandler)obj;
			gpsHandler.addListener(this);
		}
		if (aisTargets == null && obj instanceof AisTargets) {
			aisTargets = (AisTargets)obj;
		}
		if (msiHandler == null && obj instanceof MsiHandler) {
			msiHandler = (MsiHandler)obj;
		}
	}
	
	@Override
	public void findAndUndo(Object obj) {
		if (obj instanceof GnssTime) {
			System.out.println("Removed GPS time");
			gnssTime = null;
			return;
		}
		if (aisTargets == obj) {
			aisTargets = null;
		}
	}
}
