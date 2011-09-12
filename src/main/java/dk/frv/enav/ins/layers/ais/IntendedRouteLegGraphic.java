package dk.frv.enav.ins.layers.ais;

import java.awt.BasicStroke;
import java.awt.Color;

import com.bbn.openmap.omGraphics.OMLine;

import dk.frv.ais.geo.GeoLocation;

public class IntendedRouteLegGraphic extends OMLine {
	private static final long serialVersionUID = 1L;
	
	private IntendedRouteGraphic intendedRouteGraphic;
	private int index;

	public IntendedRouteLegGraphic(int index, IntendedRouteGraphic intendedRouteGraphic, boolean activeWaypoint, GeoLocation start,
			GeoLocation end, Color legColor) {
		
		super(start.getLatitude(), start.getLongitude(), end.getLatitude(), end.getLongitude(), LINETYPE_RHUMB);
		this.index = index;
		this.intendedRouteGraphic = intendedRouteGraphic;
		if(activeWaypoint){
			setStroke(new BasicStroke(2.0f, // Width
					BasicStroke.CAP_SQUARE, // End cap
					BasicStroke.JOIN_MITER, // Join style
					10.0f, // Miter limit
					new float[] { 3.0f, 10.0f }, // Dash pattern
					0.0f)); // Dash phase)
		} else {
			setStroke(new BasicStroke(3.0f, // Width
					BasicStroke.CAP_SQUARE, // End cap
					BasicStroke.JOIN_MITER, // Join style
					10.0f, // Miter limit
					new float[] { 10.0f, 8.0f }, // Dash pattern
					0.0f)); // Dash phase)
		}
		setLinePaint(legColor);		
	}

	public IntendedRouteGraphic getIntendedRouteGraphic() {
		return intendedRouteGraphic;
	}
	
	public int getIndex() {
		return index;
	}
	
}
