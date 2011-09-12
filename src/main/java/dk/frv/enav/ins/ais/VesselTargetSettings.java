package dk.frv.enav.ins.ais;

import java.io.Serializable;

public class VesselTargetSettings implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private boolean hide = false;
	private boolean showRoute = false;
	
	public VesselTargetSettings() {
		
	}
	
	public VesselTargetSettings(VesselTargetSettings settings) {
		this.hide = settings.hide;
		this.showRoute = settings.showRoute;
	}

	public boolean isHide() {
		return hide;
	}

	public void setHide(boolean hide) {
		this.hide = hide;
	}

	public boolean isShowRoute() {
		return showRoute;
	}

	public void setShowRoute(boolean showRoute) {
		this.showRoute = showRoute;
	}
	
}
