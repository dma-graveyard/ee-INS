package dk.frv.enav.ins.layers.areanotice;

import java.awt.Color;
import java.awt.geom.Arc2D;

import com.bbn.openmap.omGraphics.OMArc;
import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.proj.Length;

/**
 * Arc2Pie closed arc to draw Area Notice 'sector' area shapes. Modified OMArc
 * class to accept Area Notice message format and generate an arc given center
 * point, radius, and both boundary values. Both boundary angles are calculated
 * from the north (0-360 clockwise from the true North, negative values can be
 * used to specify counterclockwise rotation ). and the sector radius is defined
 * by the radius value and Scale factor The longitude and latitude values should
 * be i the decimal degree format.
 */
public class ASArc extends OMArc {

	private static final long serialVersionUID = 1L;

	public ASArc(int scaleFactor, int precision, double latitude, double longitude, int radius, int leftBound, int rightBound) {
		super(latitude, longitude, (int) radius, Length.METER, leftBound, leftBound - rightBound);
		super.setLineType(OMGraphic.LINETYPE_STRAIGHT);
		Length units = Length.METER;
		// super.setFillPaint(Color.red);
		super.setLatLon(latitude, longitude);
		super.setLineType(OMGraphic.LINETYPE_STRAIGHT);
		super.setRadius(radius * (float) Math.pow(10, (double) scaleFactor), units);
		super.setArcType(Arc2D.PIE);
		super.setRotationAngle(leftBound);
		if (leftBound > rightBound)
			super.setExtent(360 - (leftBound - rightBound));
		else
			super.setExtent(rightBound - leftBound);
		super.setLinePaint(Color.black);
	}

}