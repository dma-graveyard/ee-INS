package dk.frv.enav.ins.layers.route;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMText;

import dk.frv.enav.ins.route.Route;
import dk.frv.enav.ins.route.RouteWaypoint;

public class RouteWaypointGraphic extends OMGraphicList {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private RouteWaypoint routeWaypoint;
	private WaypointCircle circle;
	private Font font = new Font(Font.DIALOG, Font.PLAIN, 14);
	private OMText label = new OMText(0, 0, 0, 0, "", font, OMText.JUSTIFY_CENTER);	
	private Color color;
	private int width = 0;
	private int height = 0;
	
	
	/**
	 * Creates a route waypoint circle
	 * @param routeIndex TODO
	 * @param routeWaypoint RouteWaypoint object containing information about the route waypoint
	 * @param color Color of the waypoint
	 * @param width Width of the circle
	 * @param height Height of the circle 
	 */
	public RouteWaypointGraphic(Route route, int routeIndex, int wpIndex, RouteWaypoint routeWaypoint, Color color, int width, int height) {
		super();
		this.routeWaypoint = routeWaypoint;
		this.color = color;
		this.width = width;
		this.height = height;
		this.circle = new WaypointCircle(route, routeIndex, wpIndex);
		initGraphics();
	}
	
	public void initGraphics(){
		clear();
		
		double lat = routeWaypoint.getPos().getLatitude();
		double lon = routeWaypoint.getPos().getLongitude();
		
		circle.setLatLon(lat, lon);
		circle.setLinePaint(color);
		circle.setWidth(width);
		circle.setHeight(height);
		circle.setStroke(new BasicStroke(3));
		add(circle);
		
		label.setLat(lat);
		label.setLon(lon);
		label.setY(25);
		label.setLinePaint(color);
		label.setTextMatteColor(Color.WHITE);
		label.setData(routeWaypoint.getName());
		add(label);
	}
	
	@Override
	public void render(Graphics gr) {
		Graphics2D image = (Graphics2D) gr;
		image.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		super.render(image);
	}
	
}
