package dk.frv.enav.ins.layers.common;

import com.bbn.openmap.omGraphics.OMCircle;

public class WpCircle extends OMCircle {
	private static final long serialVersionUID = 1L;
	
	public WpCircle() {
		super(0, 0, 0, 0, 10, 10);
	}
	
	public WpCircle(double latitude, double longitude, int offX1, int offY1, int w, int h) {
		super(latitude, longitude, offX1, offY1, w, h);
	}

	@Override
	public float distance(double x, double y) {
		float distance = Float.POSITIVE_INFINITY;

        if (getNeedToRegenerate() || shape == null) {
            return distance;
        }
        
		float dist = (float)Math.sqrt(Math.pow(x-x1, 2) + Math.pow(y-y1, 2));
		
		double rad = getWidth() / 2.0; 
		
		if (dist <= rad) {
			dist = 0;
		} else {
			dist -= rad;
		}
		
		return dist;
	}

}
