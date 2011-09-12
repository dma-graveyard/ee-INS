package dk.frv.enav.ins.gui.menuItems;

import java.awt.Point;

import javax.swing.JMenuItem;

import com.bbn.openmap.MapBean;
import com.bbn.openmap.proj.Projection;
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.enav.ins.common.math.Vector2D;
import dk.frv.enav.ins.route.Route;
import dk.frv.enav.ins.route.RouteLeg;
import dk.frv.enav.ins.route.RouteManager;
import dk.frv.enav.ins.route.RoutesUpdateEvent;

public class RouteLegInsertWaypoint extends JMenuItem implements IMapMenuAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RouteLeg routeLeg;
	private Point point;
	private MapBean mapBean;
	private RouteManager routeManager;
	private int routeIndex;

	public RouteLegInsertWaypoint(String text) {
		super();
		setText(text);
	}
	
	@Override
	public void doAction() {
		GeoLocation startWaypoint = routeLeg.getStartWp().getPos();
		GeoLocation endWaypoint = routeLeg.getEndWp().getPos();
		Projection projection = mapBean.getProjection();
		LatLonPoint newPoint = projection.inverse(point);
		
		Vector2D routeLegVector = new Vector2D(startWaypoint.getLongitude(), 
				startWaypoint.getLatitude(), 
				endWaypoint.getLongitude(), 
				endWaypoint.getLatitude());
		
		Vector2D newVector = new Vector2D(startWaypoint.getLongitude(), 
				startWaypoint.getLatitude(), 
				newPoint.getLongitude(), 
				newPoint.getLatitude());
		
		Vector2D projectedVector = routeLegVector.projection(newVector);
		
		GeoLocation newGeoLocation = new GeoLocation(projectedVector.getY2(), projectedVector.getX2());
		
		Route route = routeManager.getRoute(routeIndex);
		route.createWaypoint(routeLeg, newGeoLocation);
		routeManager.notifyListeners(RoutesUpdateEvent.ROUTE_WAYPOINT_APPENDED);
	}
	
	public void setRouteLeg(RouteLeg routeLeg) {
		this.routeLeg = routeLeg;
	}
	
	public void setPoint(Point point) {
		this.point = point;
	}
	
	public void setMapBean(MapBean mapBean) {
		this.mapBean = mapBean;
	}
	
	public void setRouteIndex(int routeIndex) {
		this.routeIndex = routeIndex;
	}
	
	public void setRouteManager(RouteManager routeManager) {
		this.routeManager = routeManager;
	}

}
