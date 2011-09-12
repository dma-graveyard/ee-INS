package dk.frv.enav.ins.example;

import org.apache.log4j.Logger;

import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.omGraphics.OMCircle;
import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.frv.enav.ins.ais.AisTarget;
import dk.frv.enav.ins.ais.AisTargets;
import dk.frv.enav.ins.ais.IAisTargetListener;
import dk.frv.enav.ins.ais.VesselTarget;
import dk.frv.enav.ins.gps.GpsData;
import dk.frv.enav.ins.gps.GpsHandler;
import dk.frv.enav.ins.gps.IGpsDataListener;
import dk.frv.enav.ins.route.ActiveRoute;
import dk.frv.enav.ins.route.IRoutesUpdateListener;
import dk.frv.enav.ins.route.RouteManager;
import dk.frv.enav.ins.route.RoutesUpdateEvent;

/**
 * Example layer that registers itself as listener for GPS, AIS and route updates
 * 
 * It paints small circles to make a simple track indication after vessel AIS targets
 * 
 * @author obo
 *
 */
public class ExampleLayer extends OMGraphicHandlerLayer implements IGpsDataListener, IAisTargetListener, IRoutesUpdateListener {
	
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(ExampleLayer.class);
	
	// A handler able to read and parse GPS messages
	private GpsHandler gpsHandler = null;
	// An AIS target table
	private AisTargets aisTargets = null;
	// A manager doing the general route handling
	private RouteManager routeManager = null;
	
	// The graphics to present
	private OMGraphicList graphics = new OMGraphicList();
	
	/**
	 * Receive GPS updates in the form of GpsData messages
	 */
	@Override
	public void gpsDataUpdate(GpsData gpsData) {
		LOG.info("New GPS data: " + gpsData);		
	}

	/**
	 * Receive AIS target updates
	 */
	@Override
	public void targetUpdated(AisTarget aisTarget) {
		LOG.info("AIS target updated: " + aisTarget);
		
		if (aisTarget instanceof VesselTarget) {
			VesselTarget vesselTarget = (VesselTarget)aisTarget;
			LOG.info("Vessel " + vesselTarget.getMmsi() + " heading: " + vesselTarget.getPositionData().getTrueHeading());
			// Create small circles for last 15 position report
			double lat = vesselTarget.getPositionData().getPos().getLatitude();
			double lon = vesselTarget.getPositionData().getPos().getLongitude();
			OMCircle circle = new OMCircle(0, 0, 0, 0, 4, 4);
			circle.setLatLon(lat, lon);
			graphics.add(circle);
			
			int excess = graphics.size() - 1000;
			for (int i=0; i < excess; i++) {
				graphics.remove(0);
			}
			
			graphics.project(getProjection(), true);
						
			doPrepare();
		}
		
	}

	/**
	 * Receive updates when routes changes
	 */
	@Override
	public void routesChanged(RoutesUpdateEvent e) {
		LOG.info("Route update event: " + e);		
		if (routeManager.isRouteActive()) {
			ActiveRoute activeRoute = routeManager.getActiveRoute();
			LOG.info("TTG for next waypoint: " + activeRoute.getActiveWpTtg());
		}
	}
	
	@Override
	public synchronized OMGraphicList prepare() {
		graphics.project(getProjection());
		return graphics;
	}
	
	/**
	 * Bean context method to find other components
	 */
	@Override
	public void findAndInit(Object obj) {
		LOG.info("Hello from findAndInit obj.class: " + obj.getClass());
		if (gpsHandler == null && obj instanceof GpsHandler) {
			gpsHandler = (GpsHandler)obj;
			gpsHandler.addListener(this);
		}
		if (aisTargets == null && obj instanceof AisTargets) {
			aisTargets = (AisTargets)obj;
			aisTargets.addListener(this);
		}
		if (routeManager == null && obj instanceof RouteManager) {
			routeManager = (RouteManager)obj;
			routeManager.addListener(this);
		}
	}
	
	/**
	 * Bean context method to remove other components
	 */
	@Override
	public void findAndUndo(Object obj) {
		if (gpsHandler == obj) {
			gpsHandler.removeListener(this);
			gpsHandler = null;
		}
		if (aisTargets == obj) {
			aisTargets.removeListener(this);
			aisTargets = null;
		}
		if (obj == routeManager) {
			routeManager.removeListener(this);
			routeManager = null;
		}
	}


}
