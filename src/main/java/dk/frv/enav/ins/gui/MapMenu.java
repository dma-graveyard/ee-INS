/*
 * Copyright 2011 Danish Maritime Authority. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *   1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 *   2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY Danish Maritime Authority ``AS IS'' 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of Danish Maritime Authority.
 * 
 */
package dk.frv.enav.ins.gui;

import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.beancontext.BeanContext;
import java.beans.beancontext.BeanContextChild;
import java.beans.beancontext.BeanContextChildSupport;
import java.beans.beancontext.BeanContextMembershipEvent;
import java.beans.beancontext.BeanContextMembershipListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.bbn.openmap.LightMapHandlerChild;
import com.bbn.openmap.MapBean;
import com.bbn.openmap.MouseDelegator;

import dk.frv.enav.ins.EeINS;
import dk.frv.enav.ins.ais.AisAdressedRouteSuggestion;
import dk.frv.enav.ins.ais.AisHandler;
import dk.frv.enav.ins.ais.SarTarget;
import dk.frv.enav.ins.ais.VesselTarget;
import dk.frv.enav.ins.gps.GpsHandler;
import dk.frv.enav.ins.gui.menuitems.AisIntendedRouteToggle;
import dk.frv.enav.ins.gui.menuitems.AisTargetDetails;
import dk.frv.enav.ins.gui.menuitems.AisTargetLabelToggle;
import dk.frv.enav.ins.gui.menuitems.GeneralClearMap;
import dk.frv.enav.ins.gui.menuitems.GeneralHideIntendedRoutes;
import dk.frv.enav.ins.gui.menuitems.GeneralNewRoute;
import dk.frv.enav.ins.gui.menuitems.GeneralShowIntendedRoutes;
import dk.frv.enav.ins.gui.menuitems.IMapMenuAction;
import dk.frv.enav.ins.gui.menuitems.MonaLisaRouteRequest;
import dk.frv.enav.ins.gui.menuitems.MsiAcknowledge;
import dk.frv.enav.ins.gui.menuitems.MsiDetails;
import dk.frv.enav.ins.gui.menuitems.MsiZoomTo;
import dk.frv.enav.ins.gui.menuitems.NogoRequest;
import dk.frv.enav.ins.gui.menuitems.RouteActivateToggle;
import dk.frv.enav.ins.gui.menuitems.RouteAppendWaypoint;
import dk.frv.enav.ins.gui.menuitems.RouteCopy;
import dk.frv.enav.ins.gui.menuitems.RouteDelete;
import dk.frv.enav.ins.gui.menuitems.RouteEditEndRoute;
import dk.frv.enav.ins.gui.menuitems.RouteHide;
import dk.frv.enav.ins.gui.menuitems.RouteLegInsertWaypoint;
import dk.frv.enav.ins.gui.menuitems.RouteMetocProperties;
import dk.frv.enav.ins.gui.menuitems.RouteProperties;
import dk.frv.enav.ins.gui.menuitems.RouteRequestMetoc;
import dk.frv.enav.ins.gui.menuitems.RouteReverse;
import dk.frv.enav.ins.gui.menuitems.RouteShowMetocToggle;
import dk.frv.enav.ins.gui.menuitems.RouteWaypointActivateToggle;
import dk.frv.enav.ins.gui.menuitems.RouteWaypointDelete;
import dk.frv.enav.ins.gui.menuitems.SarTargetDetails;
import dk.frv.enav.ins.gui.menuitems.SuggestedRouteDetails;
import dk.frv.enav.ins.gui.route.RouteSuggestionDialog;
import dk.frv.enav.ins.layers.ais.AisLayer;
import dk.frv.enav.ins.layers.ais.VesselTargetGraphic;
import dk.frv.enav.ins.layers.msi.MsiDirectionalIcon;
import dk.frv.enav.ins.layers.msi.MsiLayer;
import dk.frv.enav.ins.layers.msi.MsiSymbolGraphic;
import dk.frv.enav.ins.layers.routeEdit.NewRouteContainerLayer;
import dk.frv.enav.ins.msi.MsiHandler;
import dk.frv.enav.ins.nogo.NogoHandler;
import dk.frv.enav.ins.route.Route;
import dk.frv.enav.ins.route.RouteLeg;
import dk.frv.enav.ins.route.RouteManager;

/**
 * Right click map menu
 */
public class MapMenu extends JPopupMenu implements ActionListener, LightMapHandlerChild, BeanContextChild,
		BeanContextMembershipListener {

	private static final long serialVersionUID = 1L;
	
	private IMapMenuAction action;
	private MsiHandler msiHandler;
	
	// menu items
	private GeneralClearMap clearMap;
	private GeneralHideIntendedRoutes hideIntendedRoutes;
	private GeneralShowIntendedRoutes showIntendedRoutes;
	private GeneralNewRoute newRoute;
	private JMenu scaleMenu;
	private AisIntendedRouteToggle aisIntendedRouteToggle;
	private AisTargetDetails aisTargetDetails;
	
	private SarTargetDetails sarTargetDetails;
	private AisTargetLabelToggle aisTargetLabelToggle;
	private NogoRequest nogoRequest;
	private MsiAcknowledge msiAcknowledge;
	private MsiDetails msiDetails;
	private MsiZoomTo msiZoomTo;
	private RouteActivateToggle routeActivateToggle;
	private RouteAppendWaypoint routeAppendWaypoint;
	private RouteHide routeHide;
	private RouteCopy routeCopy;
	private RouteReverse routeReverse;	
	private RouteDelete routeDelete;
	private RouteProperties routeProperties;
	private RouteMetocProperties routeMetocProperties;
	private RouteRequestMetoc routeRequestMetoc;
	private MonaLisaRouteRequest monaLisaRouteRequest;
	private RouteShowMetocToggle routeShowMetocToggle;
	private RouteLegInsertWaypoint routeLegInsertWaypoint;
	private RouteWaypointActivateToggle routeWaypointActivateToggle;
	private RouteWaypointDelete routeWaypointDelete;
	private SuggestedRouteDetails suggestedRouteDetails;
	private RouteEditEndRoute routeEditEndRoute;
	
	// bean context
	protected String propertyPrefix = null;
	protected BeanContextChildSupport beanContextChildSupport = new BeanContextChildSupport(this);
	protected boolean isolated = false;
	private RouteManager routeManager;
	private MainFrame mainFrame;
	private GpsHandler gpsHandler;
	private Route route;
	private RouteSuggestionDialog routeSuggestionDialog;
	private MapBean mapBean;
	private Map<Integer, String> map;
	private NewRouteContainerLayer newRouteLayer;
	private AisLayer aisLayer;
	private AisHandler aisHandler;
	private NogoHandler nogoHandler;
	private MouseDelegator mouseDelegator;

	public MapMenu() {
		super();
		
		// general menu items
		clearMap = new GeneralClearMap("Clear chart");
		clearMap.addActionListener(this);
		hideIntendedRoutes = new GeneralHideIntendedRoutes("Hide all intended routes");
		hideIntendedRoutes.addActionListener(this);
		showIntendedRoutes = new GeneralShowIntendedRoutes("Show all intended routes");
		showIntendedRoutes.addActionListener(this);
		newRoute = new GeneralNewRoute("Add new route - Ctrl N");
		newRoute.addActionListener(this);
		
		nogoRequest = new NogoRequest("Request NoGo area");
		nogoRequest.addActionListener(this);
		
		scaleMenu = new JMenu("Scale");
		
		// using treemap so scale levels are always sorted
		map = new TreeMap<Integer, String>();
		
		// ais menu items
		aisTargetDetails = new AisTargetDetails("Show AIS target details");
		aisTargetDetails.addActionListener(this);
		aisIntendedRouteToggle = new AisIntendedRouteToggle();
		aisIntendedRouteToggle.addActionListener(this);
		aisTargetLabelToggle = new AisTargetLabelToggle();
		aisTargetLabelToggle.addActionListener(this);
		
			
		// SART menu items
		sarTargetDetails = new SarTargetDetails("SART details");
		sarTargetDetails.addActionListener(this);
		
		// msi menu items
		msiDetails = new MsiDetails("Show MSI details");
		msiDetails.addActionListener(this);
		msiAcknowledge = new MsiAcknowledge("Acknowledge MSI");
		msiAcknowledge.addActionListener(this);
		msiZoomTo = new MsiZoomTo("Zoom to MSI");
		msiZoomTo.addActionListener(this);
		
		// route general items
		routeActivateToggle = new RouteActivateToggle();
		routeActivateToggle.addActionListener(this);
		routeHide = new RouteHide("Hide route");
		routeHide.addActionListener(this);

		routeCopy = new RouteCopy("Copy route");
		routeCopy.addActionListener(this);
		
		routeReverse = new RouteReverse("Reverse route");
		routeReverse.addActionListener(this);
		
		routeDelete = new RouteDelete("Delete route");
		routeDelete.addActionListener(this);
		
		monaLisaRouteRequest = new MonaLisaRouteRequest("Request MonaLisa Route");
		monaLisaRouteRequest.addActionListener(this);
		routeRequestMetoc = new RouteRequestMetoc("Request METOC");
		routeRequestMetoc.addActionListener(this);
		routeShowMetocToggle = new RouteShowMetocToggle();
		routeShowMetocToggle.addActionListener(this);
		routeProperties = new RouteProperties("Route properties");		
		routeProperties.addActionListener(this);
		routeMetocProperties = new RouteMetocProperties("METOC properties");
		routeMetocProperties.addActionListener(this);
		routeAppendWaypoint = new RouteAppendWaypoint("Append waypoint");
		routeAppendWaypoint.addActionListener(this);
		
		// route leg menu
		routeLegInsertWaypoint = new RouteLegInsertWaypoint("Insert waypoint here");
		routeLegInsertWaypoint.addActionListener(this);
		
		// route waypoint menu
		routeWaypointActivateToggle = new RouteWaypointActivateToggle("Activate waypoint");
		routeWaypointActivateToggle.addActionListener(this);
		routeWaypointDelete = new RouteWaypointDelete("Delete waypoint");
		routeWaypointDelete.addActionListener(this);
		
		// suggested route menu
		suggestedRouteDetails = new SuggestedRouteDetails("Suggested route details"); 
		suggestedRouteDetails.addActionListener(this);
		
		// route edit menu
		routeEditEndRoute = new RouteEditEndRoute("End route");
		routeEditEndRoute.addActionListener(this);
		
	}
	
	/**
	 * Adds the general menu to the right-click menu. Remember to always add this first, when creating specific menus.
	 * @param alone TODO
	 */
	public void generalMenu(boolean alone){
		scaleMenu.removeAll();
		
		// clear previous map scales
		map.clear();
		// Initialize the scale levels, and give them name (this should be done from settings later...)
		map.put(5000,     "Berthing      (1 : 5.000)");
		map.put(10000,    "Harbour       (1 : 10.000)");
		map.put(70000,    "Approach      (1 : 70.000)");
		map.put(300000,   "Coastal       (1 : 300.000)");
		map.put(2000000,  "Overview      (1 : 2.000.000)");
		map.put(20000000, "Ocean         (1 : 20.000.000)");
		// put current scale level
		Integer currentScale = (int) mapBean.getScale();
		
		DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance();
		DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
		symbols.setGroupingSeparator(' ');

		map.put(currentScale, "Current scale (1 : " + formatter.format(currentScale) + ")");
		
		// Iterate through the treemap, adding the menuitems and assigning actions
		Set<Integer> keys = map.keySet();
		for (Iterator<Integer> i = keys.iterator(); i.hasNext();) {
			final Integer key = i.next();
			String value = map.get(key);
			JMenuItem menuItem = new JMenuItem(value);
			menuItem.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
			menuItem.addActionListener(new ActionListener() {
			    public void actionPerformed(ActionEvent ae) {
			        mapBean.setScale(key);
			    }
			});
			scaleMenu.add(menuItem);
		}
		
		hideIntendedRoutes.setAisHandler(aisHandler);
		showIntendedRoutes.setAisHandler(aisHandler);
		
		newRoute.setMouseDelegator(mouseDelegator);
		newRoute.setMainFrame(mainFrame);
		
		nogoRequest.setNogoHandler(nogoHandler);
		nogoRequest.setMainFrame(mainFrame);
		nogoRequest.setAisHandler(aisHandler);
		
		
		if(alone){
			removeAll();
			add(clearMap);
			add(hideIntendedRoutes);
			add(showIntendedRoutes);
			add(newRoute);
			if (!EeINS.getSettings().getGuiSettings().isRiskNogoDisabled())
				add(nogoRequest);
			add(scaleMenu);
			return;
		}
			
		addSeparator();
		add(clearMap);
		add(hideIntendedRoutes);
		add(scaleMenu);
	}
	
	/**
	 * Builds ais target menu
	 */
	public void aisMenu(VesselTargetGraphic targetGraphic, TopPanel toppanel){
		removeAll();
		aisTargetDetails.setTopPanel(toppanel);
		
		VesselTarget vesselTarget = targetGraphic.getVesselTarget();
		aisTargetDetails.setMSSI(vesselTarget.getMmsi());
		add(aisTargetDetails);
		
		
		aisIntendedRouteToggle.setVesselTargetSettings(vesselTarget.getSettings());
		aisIntendedRouteToggle.setAisLayer(aisLayer);
		aisIntendedRouteToggle.setVesselTarget(vesselTarget);
		
		if(vesselTarget.getAisRouteData() != null && vesselTarget.getAisRouteData().hasRoute()){
			aisIntendedRouteToggle.setEnabled(true);
		} else {
			aisIntendedRouteToggle.setEnabled(false);
		}
		if(vesselTarget.getSettings().isShowRoute()){
			aisIntendedRouteToggle.setText("Hide intended route");
		} else {
			aisIntendedRouteToggle.setText("Show intended route");
		}
		add(aisIntendedRouteToggle);
		
		aisTargetLabelToggle.setVesselTargetGraphic(targetGraphic);
		aisTargetLabelToggle.setAisLayer(aisLayer);
		add(aisTargetLabelToggle);
		if(targetGraphic.getShowNameLabel())
			aisTargetLabelToggle.setText("Hide AIS target label");
		else
			aisTargetLabelToggle.setText("Show AIS target label");
		
		generalMenu(false);
	}
	
	/**
	 * Options for suggested route
	 */
	public void aisSuggestedRouteMenu(VesselTarget vesselTarget) {
		removeAll();
		
		aisIntendedRouteToggle.setVesselTargetSettings(vesselTarget.getSettings());
		aisIntendedRouteToggle.setAisLayer(aisLayer);
		aisIntendedRouteToggle.setVesselTarget(vesselTarget);
		
		if(vesselTarget.getAisRouteData() != null && vesselTarget.getAisRouteData().hasRoute()){
			aisIntendedRouteToggle.setEnabled(true);
		} else {
			aisIntendedRouteToggle.setEnabled(false);
		}
		if(vesselTarget.getSettings().isShowRoute()){
			aisIntendedRouteToggle.setText("Hide intended route");
		} else {
			aisIntendedRouteToggle.setText("Show intended route");
		}
		add(aisIntendedRouteToggle);
		
		generalMenu(false);
	}
	
	/**
	 * SART menu option
	 * @param aisLayer
	 * @param sarTarget
	 */
	public void sartMenu(AisLayer aisLayer, SarTarget sarTarget) {
		removeAll();
		
		sarTargetDetails.setSarTarget(sarTarget);
		sarTargetDetails.setMainFrame(mainFrame);
		sarTargetDetails.setGpsHandler(gpsHandler);
		
		add(sarTargetDetails);
		
		generalMenu(false);		
	}
	
	/**
	 * Builds the maritime safety information menu
	 * @param topPanel Reference to the top panel to get the msi dialog
	 * @param selectedGraphic The selected graphic (containing the msi message)
	 */
	public void msiMenu(TopPanel topPanel, MsiSymbolGraphic selectedGraphic){
		removeAll();
		
		msiDetails.setTopPanel(topPanel);
		msiDetails.setMsiMessage(selectedGraphic.getMsiMessage());
		add(msiDetails);
		
		Boolean isAcknowledged = msiHandler.isAcknowledged(selectedGraphic.getMsiMessage().getMessageId());
		msiAcknowledge.setMsiHandler(msiHandler);
		msiAcknowledge.setEnabled(!isAcknowledged);
		msiAcknowledge.setMsiMessage(selectedGraphic.getMsiMessage());
		add(msiAcknowledge);

		generalMenu(false);
	}
	
	public void msiDirectionalMenu(TopPanel topPanel, MsiDirectionalIcon selectedGraphic, MsiLayer msiLayer) {
		removeAll();
		
		msiDetails.setTopPanel(topPanel);
		msiDetails.setMsiMessage(selectedGraphic.getMessage().msiMessage);
		add(msiDetails);
		
		msiZoomTo.setMsiLayer(msiLayer);
		msiZoomTo.setMsiMessageExtended(selectedGraphic.getMessage());
		add(msiZoomTo);
		
		generalMenu(false);		
	}
	
	public void generalRouteMenu(int routeIndex){		
		if(routeManager.getActiveRouteIndex() == routeIndex){
			routeActivateToggle.setText("Deactivate route");
			routeHide.setEnabled(false);
			routeDelete.setEnabled(false);
			routeAppendWaypoint.setEnabled(false);
			
			
		} else {
			routeActivateToggle.setText("Activate route");
			routeHide.setEnabled(true);
			routeDelete.setEnabled(true);
			routeAppendWaypoint.setEnabled(true);
		}
		
		routeAppendWaypoint.setRouteManager(routeManager);
		routeAppendWaypoint.setRouteIndex(routeIndex);
		add(routeAppendWaypoint);
		
		addSeparator();
		
		routeActivateToggle.setRouteManager(routeManager);
		routeActivateToggle.setRouteIndex(routeIndex);
		add(routeActivateToggle);
		
		routeHide.setRouteManager(routeManager);
		routeHide.setRouteIndex(routeIndex);
		add(routeHide);
		
		routeDelete.setRouteManager(routeManager);
		routeDelete.setRouteIndex(routeIndex);
		add(routeDelete);
		
		routeCopy.setRouteManager(routeManager);
		routeCopy.setRouteIndex(routeIndex);
		add(routeCopy);

		routeReverse.setRouteManager(routeManager);
		routeReverse.setRouteIndex(routeIndex);
		add(routeReverse);
		
		route = routeManager.getRoute(routeIndex);
		if (routeManager.isActiveRoute(routeIndex)) {
			route = routeManager.getActiveRoute();
		}

		monaLisaRouteRequest.setRouteManager(routeManager);
		monaLisaRouteRequest.setRouteIndex(routeIndex);
		monaLisaRouteRequest.setMonaLisaRouteExchange(EeINS.getMonaLisaRouteExchange());
		add(monaLisaRouteRequest);		
		
		
		routeRequestMetoc.setRouteManager(routeManager);
		routeRequestMetoc.setRouteIndex(routeIndex);
		add(routeRequestMetoc);

		if(routeManager.hasMetoc(route)){
			routeShowMetocToggle.setEnabled(true);
		} else {
			routeShowMetocToggle.setEnabled(false);
		}
		
		if(route.getRouteMetocSettings().isShowRouteMetoc() && routeManager.hasMetoc(route)){
			routeShowMetocToggle.setText("Hide METOC");
		} else {
			routeShowMetocToggle.setText("Show METOC");
		}
		
		routeShowMetocToggle.setRouteManager(routeManager);
		routeShowMetocToggle.setRouteIndex(routeIndex);
		add(routeShowMetocToggle);
		
		routeMetocProperties.setRouteManager(routeManager);
		routeMetocProperties.setRouteIndex(routeIndex);
		add(routeMetocProperties);
		
		routeProperties.setRouteManager(routeManager);
		routeProperties.setRouteIndex(routeIndex);
		add(routeProperties);
		
		generalMenu(false);
	}
	
	public void routeLegMenu(int routeIndex, RouteLeg routeLeg, Point point){
		removeAll();
		
		if(routeManager.getActiveRouteIndex() == routeIndex){
			routeLegInsertWaypoint.setEnabled(false);
		} else {
			routeLegInsertWaypoint.setEnabled(true);
		}
		
		routeLegInsertWaypoint.setMapBean(mapBean);
		routeLegInsertWaypoint.setRouteManager(routeManager);
		routeLegInsertWaypoint.setRouteLeg(routeLeg);
		routeLegInsertWaypoint.setRouteIndex(routeIndex);
		routeLegInsertWaypoint.setPoint(point);
		
		add(routeLegInsertWaypoint);
		
		generalRouteMenu(routeIndex);
		//TODO: add leg specific items
	}
	
	public void routeWaypointMenu(int routeIndex, int routeWaypointIndex){
		removeAll();
		
		routeWaypointActivateToggle.setRouteWaypointIndex(routeWaypointIndex);
		routeWaypointActivateToggle.setRouteManager(routeManager);
		
		if(routeManager.getActiveRouteIndex() == routeIndex){
			routeWaypointActivateToggle.setEnabled(true);
			routeWaypointDelete.setEnabled(false);
		} else {
			routeWaypointActivateToggle.setEnabled(false);
			routeWaypointDelete.setEnabled(true);
		}
		
		add(routeWaypointActivateToggle);
		
		routeWaypointDelete.setRouteWaypointIndex(routeWaypointIndex);
		routeWaypointDelete.setRouteIndex(routeIndex);
		routeWaypointDelete.setRouteManager(routeManager);
		add(routeWaypointDelete);
		
		generalRouteMenu(routeIndex);
	}
	
	public void suggestedRouteMenu(AisAdressedRouteSuggestion suggestedRoute){
		removeAll();
			
		suggestedRouteDetails.setSuggestedRoute(suggestedRoute);
		suggestedRouteDetails.setRouteSuggestionDialog(routeSuggestionDialog);
		add(suggestedRouteDetails);
		
		generalMenu(false);
	}
	
	public void routeEditMenu(){
		removeAll();
		routeEditEndRoute.setNewRouteLayer(newRouteLayer);
		routeEditEndRoute.setRouteManager(routeManager);
		add(routeEditEndRoute);
		
		
		generalMenu(false);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		action = (IMapMenuAction) e.getSource();
		action.doAction();
	}
	
	// Allows MapMenu to be added to the MapHandler (eg. use the find and init)
	@Override
	public void findAndInit(Object obj) {
		if(obj instanceof MsiHandler){
			msiHandler = (MsiHandler) obj;
		}
		if(obj instanceof RouteManager) {
			routeManager = (RouteManager) obj;
		}
		if(obj instanceof RouteSuggestionDialog){
			routeSuggestionDialog = (RouteSuggestionDialog) obj; 
		}
		if(obj instanceof MapBean){
			mapBean = (MapBean) obj;
		}
		if(obj instanceof NewRouteContainerLayer){
			newRouteLayer = (NewRouteContainerLayer) obj;
		}
		if(obj instanceof AisLayer){
			aisLayer = (AisLayer) obj;
		}
		if(obj instanceof AisHandler){
			aisHandler = (AisHandler) obj;
		}
		if (obj instanceof GpsHandler) {
			gpsHandler = (GpsHandler)obj;
		}
		if (obj instanceof NogoHandler) {
			nogoHandler = (NogoHandler)obj;
		}		
		if (obj instanceof MainFrame) {
			mainFrame = (MainFrame)obj;			
		}
		if (obj instanceof MouseDelegator) {
			mouseDelegator = (MouseDelegator)obj;
		}
	}
	
	public void findAndInit(Iterator<?> it) {
		while (it.hasNext()) {
			findAndInit(it.next());
		}
	}

	@Override
	public void findAndUndo(Object obj) {
	}

	@Override
	public void childrenAdded(BeanContextMembershipEvent bcme) {
		if (!isolated || bcme.getBeanContext().equals(getBeanContext())) {
			findAndInit(bcme.iterator());
		}
	}

	@Override
	public void childrenRemoved(BeanContextMembershipEvent bcme) {
		Iterator<?> it = bcme.iterator();
		while (it.hasNext()) {
			findAndUndo(it.next());
		}
	}

	@Override
	public BeanContext getBeanContext() {
		return beanContextChildSupport.getBeanContext();
	}

	@Override
	public void setBeanContext(BeanContext in_bc) throws PropertyVetoException {

		if (in_bc != null) {
			if (!isolated || beanContextChildSupport.getBeanContext() == null) {
				in_bc.addBeanContextMembershipListener(this);
				beanContextChildSupport.setBeanContext(in_bc);
				findAndInit(in_bc.iterator());
			}
		}
	}

	@Override
	public void addVetoableChangeListener(String propertyName, VetoableChangeListener in_vcl) {
		beanContextChildSupport.addVetoableChangeListener(propertyName, in_vcl);
	}

	@Override
	public void removeVetoableChangeListener(String propertyName, VetoableChangeListener in_vcl) {
		beanContextChildSupport.removeVetoableChangeListener(propertyName, in_vcl);
	}

}