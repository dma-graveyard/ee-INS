package dk.frv.enav.ins.gui.menuItems;

import javax.swing.JMenuItem;

import dk.frv.enav.ins.EeINS;
import dk.frv.enav.ins.gui.route.RoutePropertiesDialog;
import dk.frv.enav.ins.route.RouteManager;

public class RouteProperties extends JMenuItem implements IMapMenuAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int routeIndex;
	private RouteManager routeManager;

	public RouteProperties(String text) {
		super();
		setText(text);
	}
	
	@Override
	public void doAction() {
		RoutePropertiesDialog routePropertiesDialog = new RoutePropertiesDialog(EeINS.getMainFrame(), routeManager, routeIndex);
		routePropertiesDialog.setVisible(true);
	}
	
	public void setRouteIndex(int routeIndex) {
		this.routeIndex = routeIndex;
	}
	
	public void setRouteManager(RouteManager routeManager) {
		this.routeManager = routeManager;
	}

}
