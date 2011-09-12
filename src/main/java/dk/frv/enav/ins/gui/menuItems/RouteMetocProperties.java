package dk.frv.enav.ins.gui.menuItems;

import javax.swing.JMenuItem;

import dk.frv.enav.ins.EeINS;
import dk.frv.enav.ins.gui.route.RouteMetocDialog;
import dk.frv.enav.ins.route.RouteManager;
import dk.frv.enav.ins.route.RoutesUpdateEvent;

public class RouteMetocProperties extends JMenuItem implements IMapMenuAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int routeIndex;
	private RouteManager routeManager;

	public RouteMetocProperties(String text) {
		super();
		setText(text);
	}
	
	@Override
	public void doAction() {
		RouteMetocDialog routeMetocDialog = new RouteMetocDialog(EeINS.getMainFrame(),routeManager, routeIndex);
		routeMetocDialog.setVisible(true);
		routeManager.notifyListeners(RoutesUpdateEvent.METOC_SETTINGS_CHANGED);
	}
	
	public void setRouteIndex(int routeIndex) {
		this.routeIndex = routeIndex;
	}
	
	public void setRouteManager(RouteManager routeManager) {
		this.routeManager = routeManager;
	}

}
