package dk.frv.enav.ins.common.graphics;

import java.awt.geom.Point2D;

import javax.swing.ImageIcon;

import com.bbn.openmap.omGraphics.OMRaster;
import com.bbn.openmap.proj.Projection;

public class CenterRaster extends OMRaster {
	private static final long serialVersionUID = 1L;

	private Point2D center = null;
	private float radius = 5;

	public CenterRaster(double lat, double lon, int i, int j, ImageIcon imageIcon) {
		super(lat, lon, -(i / 2), -(j / 2), imageIcon);
	}
	
	public CenterRaster(int x, int y, ImageIcon imageIcon) {
		super(x-(imageIcon.getIconWidth() / 2), y-(imageIcon.getIconHeight() / 2), imageIcon);
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	@Override
	public boolean generate(Projection proj) {
		center = proj.forward(getLat(), getLon());
		return super.generate(proj);
	}

	@Override
	public float distance(double mx, double my) {
		if (center == null || getNeedToRegenerate() || shape == null) {
			return Float.MAX_VALUE;
		}

		float dist = (float) Math.sqrt(Math.pow(mx - center.getX(), 2) + Math.pow(my - center.getY(), 2)) - radius;
		if (dist < 0) {
			dist = 0;
		}

		return dist;
	}

	@Override
	public boolean shouldRenderFill() {
		return false;
	}

}
