package dk.frv.enav.ins.layers.ais;

import dk.frv.enav.ins.layers.common.WpCircle;

public class IntendedRouteWpCircle extends WpCircle {
	private static final long serialVersionUID = 1L;

	private IntendedRouteGraphic intendedRouteGraphic;
	private int index;

	public IntendedRouteWpCircle(IntendedRouteGraphic intendedRouteGraphic, int index, double latitude, double longitude, int offX1, int offY1, int w, int h) {
		super(latitude, longitude, offX1, offY1, w, h);
		this.index = index;
		this.intendedRouteGraphic = intendedRouteGraphic;
	}

	public int getIndex() {
		return index;
	}

	public IntendedRouteGraphic getIntendedRouteGraphic() {
		return intendedRouteGraphic;
	}
	
}
