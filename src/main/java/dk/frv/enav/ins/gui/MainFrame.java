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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import bibliothek.gui.dock.common.CContentArea;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.displayer.DisplayerDockBorder;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.util.Priority;

import com.bbn.openmap.MapHandler;

import dk.frv.enav.ins.EeINS;
import dk.frv.enav.ins.gui.ais.AisDialog;
import dk.frv.enav.ins.gui.mainFramePanels.ActiveWaypointComponentPanel;
import dk.frv.enav.ins.gui.mainFramePanels.CursorComponentPanel;
import dk.frv.enav.ins.gui.mainFramePanels.GpsComponentPanel;
import dk.frv.enav.ins.gui.mainFramePanels.LogoPanel;
import dk.frv.enav.ins.gui.mainFramePanels.OwnShipComponentPanel;
import dk.frv.enav.ins.gui.mainFramePanels.ScaleComponentPanel;
import dk.frv.enav.ins.gui.msi.MsiDialog;
import dk.frv.enav.ins.gui.route.RouteSuggestionDialog;
import dk.frv.enav.ins.settings.GuiSettings;

/**
 * The main frame containing map and panels
 */
public class MainFrame extends JFrame implements WindowListener {

	private static final String TITLE = "e-Navigation enhanced INS "
			+ EeINS.getMinorVersion();

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getLogger(MainFrame.class);

	protected static final int SENSOR_PANEL_WIDTH = 190;

	private TopPanel topPanel;
	private ChartPanel chartPanel;
	
//	private SensorPanel sensorPanel;
	
	private BottomPanel bottomPanel;
	
	private ScaleComponentPanel scalePanel;
	private OwnShipComponentPanel ownShipPanel;
	private GpsComponentPanel gpsPanel;
	private CursorComponentPanel cursorPanel;
	private ActiveWaypointComponentPanel activeWaypointPanel;
	private LogoPanel logoPanel;
	
	private JPanel glassPanel;
	private MsiDialog msiDialog;
	private AisDialog aisDialog;
	private RouteSuggestionDialog routeSuggestionDialog;

	private MapMenu mapMenu;

	//Docks
	private List<DefaultSingleCDockable> dockables = new ArrayList<DefaultSingleCDockable>();
	

	
	CControl control;
	
	
	public MainFrame() {
		super();
		initGUI();
	}

	private void initGUI() {
		MapHandler mapHandler = EeINS.getMapHandler();
		// Get settings
		GuiSettings guiSettings = EeINS.getSettings().getGuiSettings();

		setTitle(TITLE);
		// Set location and size
		if (guiSettings.isMaximized()) {
			setExtendedState(getExtendedState() | MAXIMIZED_BOTH);
		} else {
			setLocation(guiSettings.getAppLocation());
			setSize(guiSettings.getAppDimensions());
		}
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setIconImage(getAppIcon());
		addWindowListener(this);

		// Create top menubar
//		MenuBar menuBar = new MenuBar();
//		this.setJMenuBar(menuBar);

		// Create panels
		Container pane = getContentPane();
		
		topPanel = new TopPanel();
		
//		sensorPanel = new SensorPanel();
		
		
		//Movable service panels
		scalePanel = new ScaleComponentPanel();
		ownShipPanel = new OwnShipComponentPanel();
		gpsPanel = new GpsComponentPanel();
		cursorPanel = new CursorComponentPanel();
		activeWaypointPanel = new ActiveWaypointComponentPanel();
		logoPanel = new  LogoPanel();
		
		//Add route panel instead!
		/**TO DO
		 * 
		 */
		chartPanel = new ChartPanel(activeWaypointPanel);
		
		bottomPanel = new BottomPanel();

		// Docks
		control = new CControl( this );
		CContentArea contentArea = control.getContentArea();
		pane.add( contentArea );
		

		//Frames
		BorderMod bridge = new BorderMod();
		control.getController().getThemeManager().publish(Priority.CLIENT,
				DisplayerDockBorder.KIND, ThemeManager.BORDER_MODIFIER_TYPE,
				bridge);
		

		// Dockables
		DefaultSingleCDockable chartDock = new DefaultSingleCDockable("Chart", chartPanel);
		
		DefaultSingleCDockable topDock = new DefaultSingleCDockable("Top", topPanel);
//		DefaultSingleCDockable sensorDock = new DefaultSingleCDockable("Sensor", sensorPanel);
		DefaultSingleCDockable scaleDock = new DefaultSingleCDockable("Scale", scalePanel);
		DefaultSingleCDockable ownShipDock = new DefaultSingleCDockable("Own Ship", ownShipPanel);
		DefaultSingleCDockable gpsDock = new DefaultSingleCDockable("GPS", gpsPanel);
		DefaultSingleCDockable cursorDock = new DefaultSingleCDockable("Cursor", cursorPanel);
		DefaultSingleCDockable activeWaypointDock = new DefaultSingleCDockable("Active Waypoint", activeWaypointPanel);
		DefaultSingleCDockable logoDock =  new DefaultSingleCDockable("Logos", logoPanel);
		
		
		dockables.add(chartDock);
		dockables.add(topDock);
//		dockables.add(sensorDock);
		dockables.add(scaleDock);
		dockables.add(ownShipDock);
		dockables.add(gpsDock);
		dockables.add(cursorDock);
		dockables.add(activeWaypointDock);
		dockables.add(logoDock);
		
		chartDock.setTitleText("Chart Panel");
		
		for (int i = 0; i < dockables.size(); i++) {
			dockables.get(i).setStackable(false);
			dockables.get(i).setMinimizable(false);
			dockables.get(i).setMaximizable(false);
		}
		

		CGrid grid = new CGrid(control);
		grid.add(0, 0, 100, 3, topDock);
		grid.add(0, 3, 90, 97, chartDock);
		grid.add(90, 3, 10, 10, scaleDock);
		grid.add(90, 13, 10, 10, ownShipDock);
		grid.add(90, 23, 10, 10, gpsDock);
		grid.add(90, 33, 10, 10, cursorDock);
		grid.add(90, 43, 10, 10, activeWaypointDock);
		grid.add(90, 53, 10, 47, logoDock);
		
//		grid.add(80, 53, 10, 47, sensorDock);
		
		contentArea.setMinimumAreaSize(new Dimension(0, 0));
		
		//Deploy the grid content
		contentArea.deploy( grid );
		control.intern().getController().getRelocator().setDragOnlyTitel( true );
		
		toggleFrameLock();
		
		

		bottomPanel.setPreferredSize(new Dimension(0, 25));
		pane.add(bottomPanel, BorderLayout.PAGE_END);

		// sensorPanel.setPreferredSize(new Dimension(SENSOR_PANEL_WIDTH, 0));
//		sensorPanel.setSize(100, 100);
		// pane.add(sensorPanel, BorderLayout.LINE_END);

		// Set up the chart panel with layers etc
		chartPanel.initChart();

		// Add top panel to map handler
		mapHandler.add(topPanel);

		// Add bottom panel to map handler
		mapHandler.add(bottomPanel);

		// Add chart panel to map handler
		mapHandler.add(chartPanel);

		// Add sensor panel to bean context
//		mapHandler.add(sensorPanel);
		
		
		// Add scale panel to bean context		
		mapHandler.add(scalePanel);
		mapHandler.add(ownShipPanel);
		mapHandler.add(gpsPanel);
		mapHandler.add(cursorPanel);
		mapHandler.add(activeWaypointPanel);
		
		// Init glass pane
		initGlassPane();

		// Add self to map map handler
		mapHandler.add(this);

		// Init MSI dialog
		msiDialog = new MsiDialog(this);
		mapHandler.add(msiDialog);

		// Init MSI dialog
		aisDialog = new AisDialog(this);
		mapHandler.add(aisDialog);

		// Init Route suggestion dialog
		routeSuggestionDialog = new RouteSuggestionDialog(this);
		mapHandler.add(routeSuggestionDialog);

		// Init the map right click menu
		mapMenu = new MapMenu();
		mapHandler.add(mapMenu);
	}

	
	public void toggleFrameLock(){
		
	
		
		if (dockables.get(0).isTitleShown()){
			//Lock
			for (int i = 0; i < dockables.size(); i++) {
				dockables.get(i).setTitleShown(false);
			}
	
			control.getContentArea().getCenter().setResizingEnabled( false );
			control.getContentArea().getCenter().setDividerSize(0);
			
			
		}else{
			for (int i = 0; i < dockables.size(); i++) {
				dockables.get(i).setTitleShown(true);
			}
	
			control.getContentArea().getCenter().setResizingEnabled( true );
			control.getContentArea().getCenter().setDividerSize(2);
		}
	}

	private void initGlassPane() {
		glassPanel = (JPanel) getGlassPane();
		glassPanel.setLayout(null);
		glassPanel.setVisible(false);
	}

	private static Image getAppIcon() {
		java.net.URL imgURL = EeINS.class.getResource("/images/appicon.png");
		if (imgURL != null) {
			return new ImageIcon(imgURL).getImage();
		}
		LOG.error("Could not find app icon");
		return null;
	}

	@Override
	public void windowActivated(WindowEvent we) {
	}

	@Override
	public void windowClosed(WindowEvent we) {
	}

	@Override
	public void windowClosing(WindowEvent we) {
		// Close routine
		EeINS.closeApp();
	}

	public void saveSettings() {
		// Save gui settings
		GuiSettings guiSettings = EeINS.getSettings().getGuiSettings();
		guiSettings.setMaximized((getExtendedState() & MAXIMIZED_BOTH) > 0);
		guiSettings.setAppLocation(getLocation());
		guiSettings.setAppDimensions(getSize());
		// Save map settings
		chartPanel.saveSettings();
	}

	@Override
	public void windowDeactivated(WindowEvent we) {
	}

	@Override
	public void windowDeiconified(WindowEvent we) {
	}

	@Override
	public void windowIconified(WindowEvent we) {
	}

	@Override
	public void windowOpened(WindowEvent we) {
	}

	public ChartPanel getChartPanel() {
		return chartPanel;
	}

	public JPanel getGlassPanel() {
		return glassPanel;
	}

	public TopPanel getTopPanel() {
		return topPanel;
	}

}
