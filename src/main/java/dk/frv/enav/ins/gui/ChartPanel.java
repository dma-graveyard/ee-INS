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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Properties;

import javax.swing.BorderFactory;

import org.apache.log4j.Logger;

import com.bbn.openmap.BufferedLayerMapBean;
import com.bbn.openmap.Layer;
import com.bbn.openmap.LayerHandler;
import com.bbn.openmap.MapBean;
import com.bbn.openmap.MapHandler;
import com.bbn.openmap.MouseDelegator;
import com.bbn.openmap.gui.OMComponentPanel;
import com.bbn.openmap.layer.shape.ShapeLayer;
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.enav.ins.EeINS;
import dk.frv.enav.ins.event.MSIFilterMouseMode;
import dk.frv.enav.ins.event.NavigationMouseMode;
import dk.frv.enav.ins.event.RouteEditMouseMode;
import dk.frv.enav.ins.gps.GpsData;
import dk.frv.enav.ins.gps.IGpsDataListener;
import dk.frv.enav.ins.layers.EncLayerFactory;
import dk.frv.enav.ins.layers.GeneralLayer;
import dk.frv.enav.ins.layers.ais.AisLayer;
import dk.frv.enav.ins.layers.gps.GpsLayer;
import dk.frv.enav.ins.layers.msi.MsiLayer;
import dk.frv.enav.ins.layers.route.RouteLayer;
import dk.frv.enav.ins.layers.routeEdit.NewRouteContainerLayer;
import dk.frv.enav.ins.layers.routeEdit.RouteEditLayer;
import dk.frv.enav.ins.route.RoutesUpdateEvent;
import dk.frv.enav.ins.settings.MapSettings;

/**
 * The panel with chart. Initializes all layers to be shown on the map. 
 */
public class ChartPanel extends OMComponentPanel implements IGpsDataListener, MouseWheelListener {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getLogger(ChartPanel.class);
	
	private MapHandler mapHandler;
	private LayerHandler layerHandler;
	private BufferedLayerMapBean map;
	private GpsLayer gpsLayer;
	private Layer encLayer;
	private AisLayer aisLayer;
	private GeneralLayer generalLayer;
	private Layer bgLayer;
	private NavigationMouseMode mapNavMouseMode;
	private MouseDelegator mouseDelegator;
	private SensorPanel sensorPanel;
	private RouteLayer routeLayer;
	private MsiLayer msiLayer;
	private TopPanel topPanel;
	private RouteEditMouseMode routeEditMouseMode;
	private RouteEditLayer routeEditLayer;
	private NewRouteContainerLayer newRouteContainerLayer;
	public int maxScale = 5000;
	private MSIFilterMouseMode msiFilterMouseMode;
	private GpsData gpsData;

	public ChartPanel(SensorPanel sensorPanel) {
		super();
		// Set map handler
		mapHandler = EeINS.getMapHandler();
		// Set layout
		setLayout(new BorderLayout());
		// Set border
		setBorder(BorderFactory.createLineBorder(Color.GRAY));
		this.sensorPanel = sensorPanel;
		// Max scale
		this.maxScale = EeINS.getSettings().getMapSettings().getMaxScale(); 
	}

	public void initChart() {
		MapSettings mapSettings = EeINS.getSettings().getMapSettings();
		Properties props = EeINS.getProperties();
		
		// Try to create ENC layer
		EncLayerFactory encLayerFactory = new EncLayerFactory(EeINS.getSettings().getMapSettings());
		encLayer = encLayerFactory.getEncLayer();

		// Create a MapBean, and add it to the MapHandler.
		map = new BufferedLayerMapBean();
		map.setDoubleBuffered(true);

		mouseDelegator = new MouseDelegator();
		mapHandler.add(mouseDelegator);

		// Add MouseMode. The MouseDelegator will find it via the
		// MapHandler.
		// Adding NavMouseMode first makes it active.
		// mapHandler.add(new NavMouseMode());
		mapNavMouseMode = new NavigationMouseMode(this);
		routeEditMouseMode = new RouteEditMouseMode();
		msiFilterMouseMode = new MSIFilterMouseMode();

		mouseDelegator.addMouseMode(mapNavMouseMode);
		mouseDelegator.addMouseMode(routeEditMouseMode);
		mouseDelegator.addMouseMode(msiFilterMouseMode);
		mouseDelegator.setActive(mapNavMouseMode);

		mapHandler.add(mapNavMouseMode);
		mapHandler.add(routeEditMouseMode);
		mapHandler.add(msiFilterMouseMode);

		mapHandler.add(sensorPanel);

		// Use the LayerHandler to manage all layers, whether they are
		// on the map or not. You can add a layer to the map by
		// setting layer.setVisible(true).
		layerHandler = new LayerHandler();
		// Get plugin layers
		createPluginLayers(props);

		// Add layer handler to map handler
		mapHandler.add(layerHandler);

		// Create the general layer
		generalLayer = new GeneralLayer();
		generalLayer.setVisible(true);
		mapHandler.add(generalLayer);

		// Create route layer
		routeLayer = new RouteLayer();
		routeLayer.setVisible(true);
		mapHandler.add(routeLayer);

		// Create route editing layer
		newRouteContainerLayer = new NewRouteContainerLayer();
		newRouteContainerLayer.setVisible(true);
		mapHandler.add(newRouteContainerLayer);
		routeEditLayer = new RouteEditLayer();
		routeEditLayer.setVisible(true);
		mapHandler.add(routeEditLayer);

		// Create MSI layer
		msiLayer = new MsiLayer();
		msiLayer.setVisible(true);
		mapHandler.add(msiLayer);

		// Create AIS layer
		aisLayer = new AisLayer();
		aisLayer.setMinRedrawInterval(EeINS.getSettings().getAisSettings().getMinRedrawInterval() * 1000);
		aisLayer.setVisible(true);
		mapHandler.add(aisLayer);

		// Create GPS layer
		gpsLayer = new GpsLayer();
		gpsLayer.setVisible(true);
		mapHandler.add(gpsLayer);
		
		// Create a esri shape layer
//		URL dbf = EeINS.class.getResource("/shape/urbanap020.dbf");
//		URL shp = EeINS.class.getResource("/shape/urbanap020.shp");
//		URL shx = EeINS.class.getResource("/shape/urbanap020.shx");
//		
//		DrawingAttributes da = new DrawingAttributes();
//		da.setFillPaint(Color.blue);
//		da.setLinePaint(Color.black);
//		
//		EsriLayer esriLayer = new EsriLayer("Drogden", dbf, shp, shx, da);
//		mapHandler.add(esriLayer);

		// Create background layer
		String layerName = "background";
		bgLayer = new ShapeLayer();
		bgLayer.setProperties(layerName, props);
		bgLayer.setAddAsBackground(true);
		bgLayer.setVisible(true);
		mapHandler.add(bgLayer);

		if (encLayer != null) {
			mapHandler.add(encLayer);
		}

		// Add map to map handler
		mapHandler.add(map);

		// Set last postion
		map.setCenter(mapSettings.getCenter());

		// Get from settings
		map.setScale(mapSettings.getScale());

		add(map);
	
		

		// Force a route layer and sensor panel update
		routeLayer.routesChanged(RoutesUpdateEvent.ROUTE_ADDED);
		sensorPanel.routesChanged(RoutesUpdateEvent.ROUTE_ADDED);
		// Force a MSI layer update
		msiLayer.doUpdate();

		// Add this class as GPS data listener
		EeINS.getGpsHandler().addListener(this);
		
		// Set ENC map settings
		encLayerFactory.setMapSettings();

		// Show AIS or not
		aisVisible(EeINS.getSettings().getAisSettings().isVisible());
		// Show ENC or not
		encVisible(EeINS.getSettings().getMapSettings().isEncVisible());
		// Maybe disable ENC
		if (encLayer == null && topPanel != null) {
			topPanel.setEncDisabled();			
		}
		
		getMap().addMouseWheelListener(this);
	}

	public void saveSettings() {
		MapSettings mapSettings = EeINS.getSettings().getMapSettings();
		mapSettings.setCenter((LatLonPoint) map.getCenter());
		mapSettings.setScale(map.getScale());
	}

	public MapBean getMap() {
		return map;
	}

	public MapHandler getMapHandler() {
		return mapHandler;
	}

	public Layer getGpsLayer() {
		return gpsLayer;
	}

	public Layer getEncLayer() {
		return encLayer;
	}

	public void centreOnShip() {
		// Get current position
		GpsData gpsData = EeINS.getGpsHandler().getCurrentData();
		if (gpsData == null) {
			return;
		}
		if (gpsData.getPosition() == null) {
			return;
		}
		map.setCenter((float) gpsData.getPosition().getLatitude(), (float) gpsData.getPosition().getLongitude());

	}

	public void doZoom(float factor) {
		float newScale = map.getScale() * factor;
		if (newScale < maxScale) {
			newScale = maxScale;
		}
		map.setScale(newScale);
		autoFollow();
	}

	public void aisVisible(boolean visible) {
		aisLayer.setVisible(visible);
	}

	public void encVisible(boolean visible) {
		if (encLayer != null) {
			encLayer.setVisible(visible);
		}
	}

	public void editMode(boolean enable) {
		if (enable) {
			mouseDelegator.setActive(routeEditMouseMode);
			routeEditLayer.setVisible(true);
			routeEditLayer.setEnabled(true);
			newRouteContainerLayer.setVisible(true);
		} else {
			mouseDelegator.setActive(mapNavMouseMode);
			routeEditLayer.setVisible(false);
			routeEditLayer.doPrepare();
			newRouteContainerLayer.setVisible(false);
			newRouteContainerLayer.getWaypoints().clear();
			newRouteContainerLayer.getRouteGraphics().clear();
			newRouteContainerLayer.doPrepare();
			EeINS.getMainFrame().getTopPanel().getNewRouteBtn().setSelected(false);
		}
	}
	
	public void autoFollow() {
		// Do auto follow
		if (!EeINS.getSettings().getNavSettings().isAutoFollow()) {
			return;
		}

		// Only do auto follow if not bad position
		if (gpsData.isBadPosition()) {
			return;
		}

		boolean lookahead = EeINS.getSettings().getNavSettings().isLookAhead();

		// Find desired location (depends on look-ahead or not)
				
		double centerX = map.getWidth() / 2.0;
		double centerY = map.getHeight() / 2.0;
		double desiredX = centerX;
		double desiredY = centerY;
		
		if (lookahead) {
			double lookAheadBorder = 100.0;
			double lookAheadMinSpd = 1.0;
			double lookAheadMaxSpd = 15.0;
			
			// Calculate a factor [0;1] from speed
			double factor = 0;
			if(gpsData.getSog() < lookAheadMinSpd) {
				factor = 0;
			} else if (gpsData.getSog() < lookAheadMaxSpd) {
				factor = gpsData.getSog() / lookAheadMaxSpd;
			} else {
				factor = 1.0;
			}
			
			double phiX = Math.cos(Math.toRadians(gpsData.getCog()) - 3 * Math.PI / 2);
			double phiY = Math.sin(Math.toRadians(gpsData.getCog()) - 3 * Math.PI / 2);
			
			double fx = factor * phiX;
			double fy = factor * phiY;
			
			desiredX = centerX + (centerX - lookAheadBorder) * fx; 
			desiredY = centerY + (centerY - lookAheadBorder) * fy;
						
		}

		// Get projected x,y of current position
		Point2D shipXY = map.getProjection().forward(gpsData.getPosition().getLatitude(), gpsData.getPosition().getLongitude());

		// Calculate how many percent the position is off for x and y
		double pctOffX = (Math.abs(desiredX - shipXY.getX()) / map.getWidth()) * 100.0;
		double pctOffY = (Math.abs(desiredY - shipXY.getY()) / map.getHeight()) * 100.0;

		//LOG.info("pctOffX: " + pctOffX + " pctOffY: " + pctOffY);

		int tollerated = EeINS.getSettings().getNavSettings().getAutoFollowPctOffTollerance();
		if (pctOffX < tollerated && pctOffY < tollerated) {
			return;
		}

		if (lookahead) {
			Point2D forwardCenter = map.getProjection().inverse(centerX - desiredX + shipXY.getX(), centerY - desiredY + shipXY.getY());
			map.setCenter((float) forwardCenter.getY(), (float) forwardCenter.getX());
		} else {
			map.setCenter((float) gpsData.getPosition().getLatitude(), (float) gpsData.getPosition().getLongitude());
		}

	}

	/**
	 * Receive GPS update
	 */
	@Override
	public void gpsDataUpdate(GpsData gpsData) {
		this.gpsData = gpsData;
		autoFollow();
	}

	/**
	 * Given a set of points scale and center so that all points are contained
	 * in the view
	 * 
	 * @param waypoints
	 */
	public void zoomTo(List<GeoLocation> waypoints) {
		if (waypoints.size() == 0) {
			return;
		}

		// Disable auto follow
		EeINS.getSettings().getNavSettings().setAutoFollow(false);
		topPanel.updateButtons();

		if (waypoints.size() == 1) {
			map.setCenter(waypoints.get(0).getLatitude(), waypoints.get(0).getLongitude());
			return;
		}

		// Find bounding box
		double maxLat = -91;
		double minLat = 91;
		double maxLon = -181;
		double minLon = 181;
		for (GeoLocation pos : waypoints) {
			if (pos.getLatitude() > maxLat) {
				maxLat = pos.getLatitude();
			}
			if (pos.getLatitude() < minLat) {
				minLat = pos.getLatitude();
			}
			if (pos.getLongitude() > maxLon) {
				maxLon = pos.getLongitude();
			}
			if (pos.getLongitude() < minLon) {
				minLon = pos.getLongitude();
			}
		}

		double centerLat = (maxLat + minLat) / 2.0;
		double centerLon = (maxLon + minLon) / 2.0;
		map.setCenter(centerLat, centerLon);

		// LatLonPoint minlatlon = new LatLonPoint.Double(maxLat, minLon); //
		// upper left corner
		// LatLonPoint maxlatlon = new LatLonPoint.Double(minLat, maxLon); //
		// lower right corner
		//		
		// Point2D pixelminlatlon = map.getProjection().forward(minlatlon);
		// Point2D pixelmaxlatlon = map.getProjection().forward(maxlatlon);
		//		
		// float newScale = ProjMath.getScaleFromProjected(pixelminlatlon,
		// pixelmaxlatlon, map.getProjection());

		/*
		 * System.out.println("Scale: " + newScale + "\n");
		 * System.out.println("geomin: " + minlatlon + "\n");
		 * System.out.println("geomax: " + maxlatlon + "\n");
		 * System.out.println("pixelmin: " + pixelminlatlon + "\n");
		 * System.out.println("pixelmax: " + pixelmaxlatlon);
		 */

		// map.setScale(newScale*5);
	}
	
	/**
	 * Called when projection has been changed by user
	 */
	public void manualProjChange() {
		topPanel.disableAutoFollow();
	}

	public MouseDelegator getMouseDelegator() {
		return mouseDelegator;
	}

	private void createPluginLayers(Properties props) {
		String layersValue = props.getProperty("eeins.plugin_layers");
		if (layersValue == null)
			return;
		String[] layerNames = layersValue.split(" ");
		for (String layerName : layerNames) {
			String classProperty = layerName + ".class";
			String className = props.getProperty(classProperty);
			if (className == null) {
				LOG.error("Failed to locate property " + classProperty);
				continue;
			}
			try {
				// Create it if you do...
				Object obj = java.beans.Beans.instantiate(null, className);
				if (obj instanceof Layer) {
					Layer l = (Layer) obj;
					// All layers have a setProperties method, and
					// should intialize themselves with proper
					// settings here. If a property is not set, a
					// default should be used, or a big, graceful
					// complaint should be issued.
					l.setProperties(layerName, props);
					l.setVisible(true);
					layerHandler.addLayer(l);
				}
			} catch (java.lang.ClassNotFoundException e) {
				LOG.error("Layer class not found: \"" + className + "\"");
			} catch (java.io.IOException e) {
				LOG.error("IO Exception instantiating class \"" + className + "\"");
			}
		}
	}
	
	public int getMaxScale() {
		return maxScale;
	}

	@Override
	public void findAndInit(Object obj) {
		if (obj instanceof TopPanel) {
			topPanel = (TopPanel) obj;
			// Maybe no S52 layer
			if (encLayer == null) {
				topPanel.setEncDisabled();
			}
		}
	}

	/**
	 * Call auto follow when zooming
	 */
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		autoFollow();
	}
}
