package dk.frv.enav.ins.gui.ComponentPanels;

import java.awt.BorderLayout;

import javax.swing.border.EtchedBorder;

import com.bbn.openmap.event.ProjectionEvent;
import com.bbn.openmap.event.ProjectionListener;
import com.bbn.openmap.gui.OMComponentPanel;
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.frv.enav.ins.event.IMapCoordListener;
import dk.frv.enav.ins.gps.GpsData;
import dk.frv.enav.ins.gps.GpsHandler;
import dk.frv.enav.ins.gps.IGpsDataListener;
import dk.frv.enav.ins.gui.Panels.ActiveWaypointPanel;
import dk.frv.enav.ins.route.IRoutesUpdateListener;
import dk.frv.enav.ins.route.RouteManager;
import dk.frv.enav.ins.route.RoutesUpdateEvent;

public class ActiveWaypointComponentPanel extends OMComponentPanel implements IGpsDataListener, Runnable, ProjectionListener, IMapCoordListener, IRoutesUpdateListener {

	private static final long serialVersionUID = 1L;
	private final ActiveWaypointPanel activeWaypointPanel;
	private RouteManager routeManager;
	
	public ActiveWaypointComponentPanel(){
		super();
		
//		this.setMinimumSize(new Dimension(10, 165));
		
		activeWaypointPanel = new ActiveWaypointPanel();
//		activeWaypointPanel.setVisible(false);
		activeWaypointPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		setBorder(null);
		
		setLayout(new BorderLayout(0, 0));
		add(activeWaypointPanel, BorderLayout.NORTH);
	}
	
	/**
	 * Receive route update
	 */
	@Override
	public void routesChanged(RoutesUpdateEvent e) {
		activeWaypointPanel.updateActiveNavData();
//		if(routeManager.isRouteActive()){
//			activeWaypointPanel.setVisible(true);
//			activeWaypointPanel.updateActiveNavData();
//		} else if (activeWaypointPanel.isVisible()) {
//			activeWaypointPanel.setVisible(false);
//		}
	}
	@Override
	public void recieveCoord(LatLonPoint llp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void projectionChanged(ProjectionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Receive GPS update
	 */
	@Override
	public void gpsDataUpdate(GpsData gpsData) {
		activeWaypointPanel.updateActiveNavData();
	}
	
	@Override
	public void findAndInit(Object obj) {
		if (obj instanceof RouteManager) {
			routeManager = (RouteManager)obj;
			activeWaypointPanel.setRouteManager(routeManager);
			routeManager.addListener(this);
			return;
		}
		if (obj instanceof GpsHandler) {
			((GpsHandler)obj).addListener(this);
		}
	}
	

}
