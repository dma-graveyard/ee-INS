package dk.frv.enav.ins.layers.areanotice;

import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMPoly;
import com.bbn.openmap.proj.GreatCircle;
import com.bbn.openmap.proj.Length;
import com.bbn.openmap.proj.coords.LatLonPoint;

public class ASRectangle extends OMPoly {

	private static final long serialVersionUID = 1L;

	private double[] lpoints = new double[10];

	public ASRectangle(int scaleFactor, int precision, double latitude, double longitude, int eDimension, int nDimension,
			double angle, double[] array) {
		super(array, OMGraphic.DECIMAL_DEGREES, OMGraphic.LINETYPE_STRAIGHT);
		this.lpoints = array;
		double alfa = angle;

		LatLonPoint p1, p2, p3, p4, p5;
		p1 = new LatLonPoint.Double(latitude, longitude);
		p2 = new LatLonPoint.Double(GreatCircle.sphericalBetween(Length.DECIMAL_DEGREE.toRadians(p1.getLatitude()),
				Length.DECIMAL_DEGREE.toRadians(p1.getLongitude()),
				Length.METER.toRadians(eDimension * (float) Math.pow(10, (double) scaleFactor)),
				Length.DECIMAL_DEGREE.toRadians(alfa + 90)));
		p3 = new LatLonPoint.Double(GreatCircle.sphericalBetween(Length.DECIMAL_DEGREE.toRadians(p2.getLatitude()),
				Length.DECIMAL_DEGREE.toRadians(p2.getLongitude()),
				Length.METER.toRadians(nDimension * (float) Math.pow(10, (double) scaleFactor)),
				Length.DECIMAL_DEGREE.toRadians(alfa)));
		p4 = new LatLonPoint.Double(GreatCircle.sphericalBetween(Length.DECIMAL_DEGREE.toRadians(p1.getLatitude()),
				Length.DECIMAL_DEGREE.toRadians(p1.getLongitude()),
				Length.METER.toRadians(nDimension * (float) Math.pow(10, (double) scaleFactor)),
				Length.DECIMAL_DEGREE.toRadians(alfa)));
		p5 = new LatLonPoint.Double(p1);

		this.lpoints[0] = latitude;
		this.lpoints[1] = longitude;
		this.lpoints[2] = p2.getLatitude();
		this.lpoints[3] = p2.getLongitude();
		this.lpoints[4] = p3.getLatitude();
		this.lpoints[5] = p3.getLongitude();
		this.lpoints[6] = p4.getLatitude();
		this.lpoints[7] = p4.getLongitude();
		this.lpoints[8] = p5.getLatitude();
		this.lpoints[9] = p5.getLongitude();

		super.setLocation(this.lpoints, OMGraphic.DECIMAL_DEGREES);
		// super.setIsPolygon(true);
	}

}