package dk.frv.enav.ins.gui.menuItems;

import javax.swing.JMenuItem;

import dk.frv.enav.ins.ais.VesselTarget;
import dk.frv.enav.ins.ais.VesselTargetSettings;
import dk.frv.enav.ins.layers.ais.AisLayer;

public class AisIntendedRouteToggle extends JMenuItem implements
		IMapMenuAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private VesselTargetSettings vesselTargetSettings;
	private VesselTarget vesselTarget;
	private AisLayer aisLayer;

	public AisIntendedRouteToggle() {
		super();
	}
	
	@Override
	public void doAction() {
		vesselTargetSettings.setShowRoute(!vesselTarget.getSettings().isShowRoute());
		aisLayer.targetUpdated(vesselTarget);
	}
	
	public void setVesselTarget(VesselTarget vesselTarget) {
		this.vesselTarget = vesselTarget;
	}
	
	public void setVesselTargetSettings(VesselTargetSettings vesselTargetSettings) {
		this.vesselTargetSettings = vesselTargetSettings;
	}

	public void setAisLayer(AisLayer aisLayer) {
		this.aisLayer = aisLayer;
	}
}
