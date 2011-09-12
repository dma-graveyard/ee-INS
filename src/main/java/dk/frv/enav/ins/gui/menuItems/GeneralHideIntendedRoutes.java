package dk.frv.enav.ins.gui.menuItems;

import javax.swing.JMenuItem;

import dk.frv.enav.ins.ais.AisTargets;

public class GeneralHideIntendedRoutes extends JMenuItem implements IMapMenuAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AisTargets aisTargets;

	public GeneralHideIntendedRoutes(String text) {
		super();
		setText(text);
	}
	
	@Override
	public void doAction() {
		aisTargets.hideAllIntendedRoutes();
	}
	
	public void setAisTargets(AisTargets aisTargets) {
		this.aisTargets = aisTargets;
	}

}
