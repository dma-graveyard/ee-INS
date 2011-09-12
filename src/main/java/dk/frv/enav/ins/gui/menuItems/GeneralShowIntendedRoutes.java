package dk.frv.enav.ins.gui.menuItems;

import javax.swing.JMenuItem;

import dk.frv.enav.ins.ais.AisTargets;

public class GeneralShowIntendedRoutes extends JMenuItem implements IMapMenuAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AisTargets aisTargets;

	public GeneralShowIntendedRoutes(String text) {
		super();
		setText(text);
	}
	
	@Override
	public void doAction() {
		aisTargets.showAllIntendedRoutes();
	}
	
	public void setAisTargets(AisTargets aisTargets) {
		this.aisTargets = aisTargets;
	}

}
