package dk.frv.enav.ins.gui.menuItems;

import javax.swing.JMenuItem;

import dk.frv.enav.ins.layers.ais.AisLayer;
import dk.frv.enav.ins.layers.ais.VesselTargetGraphic;

public class AisTargetLabelToggle extends JMenuItem implements IMapMenuAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private VesselTargetGraphic vesselTargetGraphic;
	private AisLayer aisLayer;

	public AisTargetLabelToggle() {
		super();
	}
	
	@Override
	public void doAction() {
		vesselTargetGraphic.setShowNameLabel(!vesselTargetGraphic.getShowNameLabel());
		aisLayer.targetUpdated(vesselTargetGraphic.getVesselTarget());
	}
	
	public void setVesselTargetGraphic(VesselTargetGraphic vesselTargetGraphic) {
		this.vesselTargetGraphic = vesselTargetGraphic;
	}

	public void setAisLayer(AisLayer aisLayer) {
		this.aisLayer = aisLayer;
	}

}
