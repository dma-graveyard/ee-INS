package dk.frv.enav.ins.layers.route;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import com.bbn.openmap.omGraphics.OMPoly;

import dk.frv.ais.geo.GeoLocation;

public class SafeHavenPolygon extends OMPoly {

	private static final long serialVersionUID = 1L;

	public SafeHavenPolygon(double[] polyPoints, int decimalDegrees,
			int linetypeRhumb, int i) {
		super(polyPoints, decimalDegrees, linetypeRhumb);

	}

	public void setLocation(GeoLocation pos) {
		// super.set

	}

	/**
	 * @param args
	 */

	@Override
	public void render(Graphics g) {

		// Graphics2D image = (Graphics2D) g;
		// image.rotate(45);
		// AffineTransform transform = image.getTransform();
		// transform.rotate(Math.toRadians(45),0,0);
		// image.transform(transform);
		super.render(g);
	}

}
