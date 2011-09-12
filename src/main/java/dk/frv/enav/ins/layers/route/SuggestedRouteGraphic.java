package dk.frv.enav.ins.layers.route;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.util.List;

import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMLine;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.enav.ins.ais.AisAdressedRouteSuggestion;

public class SuggestedRouteGraphic extends OMGraphicList {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<GeoLocation> routeWaypoints;
	private AisAdressedRouteSuggestion routeSuggestion;
	private Stroke stroke;

	public SuggestedRouteGraphic(AisAdressedRouteSuggestion routeSuggestion, Stroke stroke) {
		this.routeSuggestion = routeSuggestion;
		this.stroke = stroke;
		routeWaypoints = routeSuggestion.getWaypoints();
		initGraphics();
		setVague(true);
	}

	public void initGraphics() {

		// extract llpoints from geolocations
		/*
		 * int length = routeWaypoints.size() * 2; double[] llpoints = new
		 * double[length]; int llpoint = 0; for (int i = 0; i < length/2; i++) {
		 * GeoLocation geoLocation = routeWaypoints.get(i); llpoints[llpoint] =
		 * geoLocation.getLatitude(); llpoints[llpoint+1] =
		 * geoLocation.getLongitude(); llpoint += 2; } OMDecoratedSpline omds =
		 * new OMDecoratedSpline(llpoints, OMSpline.DECIMAL_DEGREES,
		 * OMSpline.LINETYPE_RHUMB); omds.setNumSegs(1); ShapeDecorator sd = new
		 * ShapeDecorator(); //sd.addDecoration(new LineShapeDecoration(5,
		 * OMColor.clear)); //sd.addDecoration(new IceAreaShapeDecoration(7, 7,
		 * IceAreaShapeDecoration.LEFT)); sd.addDecoration(new
		 * SuggestedRouteDecoration(10, 2, SuggestedRouteDecoration.LEFT));
		 * //sd.addDecoration(new TurbulanceShapeDecoration(10, 4));
		 * omds.setDecorator(sd);
		 * 
		 * add(omds);
		 */

		Stroke backgroundStroke = new BasicStroke(
				10.0f, // Width
				BasicStroke.CAP_ROUND, // End cap
				BasicStroke.JOIN_MITER, // Join style
				10.0f, // Miter limit
				null, // Dash pattern
				0.0f);

		GeoLocation prevPoint = null;
		GeoLocation nextPoint = null;
		for (GeoLocation geoLocation : routeWaypoints) {
			nextPoint = geoLocation;
			if (prevPoint != null) {
				OMLine leg = new OMLine(prevPoint.getLatitude(), prevPoint.getLongitude(), nextPoint.getLatitude(), nextPoint
						.getLongitude(), OMLine.LINETYPE_RHUMB);
				leg.setStroke(stroke);
				leg.setLinePaint(new Color(183, 68, 237, 255));
				add(leg);
				
				if (!routeSuggestion.isReplied()) {
					OMLine legBackground = new OMLine(prevPoint.getLatitude(), prevPoint.getLongitude(), nextPoint.getLatitude(),
							nextPoint.getLongitude(), OMLine.LINETYPE_RHUMB);
					legBackground.setStroke(backgroundStroke);
					legBackground.setLinePaint(new Color(42, 172, 12, 120));				
					add(legBackground);
				}
			}
			prevPoint = nextPoint;
		}
	}

	public AisAdressedRouteSuggestion getRouteSuggestion() {
		return routeSuggestion;
	}

	@Override
	public void render(Graphics gr) {
		Graphics2D image = (Graphics2D) gr;
		image.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		super.render(image);
	}
}
