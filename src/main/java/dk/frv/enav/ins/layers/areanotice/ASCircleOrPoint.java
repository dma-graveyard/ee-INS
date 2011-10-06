package dk.frv.enav.ins.layers.areanotice;

import java.awt.Color;

import com.bbn.openmap.omGraphics.OMCircle;
import com.bbn.openmap.proj.Length;

/**
 * Circle drawed in a given lat/lon point and radius in meters scaled with
 * scaleFactor.
 * 
 */
public class ASCircleOrPoint extends OMCircle {

	private static final long serialVersionUID = 1L;

	public ASCircleOrPoint(int scaleFactor, int precision, double latitude, double longitude, int radius) {
		super(latitude, longitude, radius, Length.METER);
		Length units = Length.METER;
		super.setLatLon(latitude, longitude);
		super.setRadius(radius * (float) Math.pow(10, (double) scaleFactor), units);
		super.setLinePaint(Color.black);
	}

}