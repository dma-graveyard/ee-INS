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

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import com.bbn.openmap.MapHandler;

import dk.frv.enav.ins.EeINS;
import dk.frv.enav.ins.gui.ComponentPanels.ActiveWaypointComponentPanel;
import dk.frv.enav.ins.gui.ComponentPanels.AisComponentPanel;
import dk.frv.enav.ins.gui.ComponentPanels.CursorComponentPanel;
import dk.frv.enav.ins.gui.ComponentPanels.DynamicNoGoComponentPanel;
import dk.frv.enav.ins.gui.ComponentPanels.GpsComponentPanel;
import dk.frv.enav.ins.gui.ComponentPanels.MSIComponentPanel;
import dk.frv.enav.ins.gui.ComponentPanels.NoGoComponentPanel;
import dk.frv.enav.ins.gui.ComponentPanels.OwnShipComponentPanel;
import dk.frv.enav.ins.gui.ComponentPanels.ScaleComponentPanel;
import dk.frv.enav.ins.gui.Panels.LogoPanel;
import dk.frv.enav.ins.gui.ais.AisDialog;
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

	private BottomPanel bottomPanel;

	private ScaleComponentPanel scalePanel;
	private OwnShipComponentPanel ownShipPanel;
	private GpsComponentPanel gpsPanel;
	private CursorComponentPanel cursorPanel;
	private ActiveWaypointComponentPanel activeWaypointPanel;
	private LogoPanel logoPanel;
	private MSIComponentPanel msiComponentPanel;
	private AisComponentPanel aisComponentPanel;
	private DynamicNoGoComponentPanel dynamicNoGoPanel;
	private NoGoComponentPanel nogoPanel;
	
	
	private JPanel glassPanel;
	private MsiDialog msiDialog;
	private AisDialog aisDialog;
	private RouteSuggestionDialog routeSuggestionDialog;

	private DockableComponents dockableComponents;

	private MapMenu mapMenu;
	private EeINSMenuBar menuBar;

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

		// Create panels
		Container pane = getContentPane();
		topPanel = new TopPanel();

		// Movable service panels
		scalePanel = new ScaleComponentPanel();
		ownShipPanel = new OwnShipComponentPanel();
		gpsPanel = new GpsComponentPanel();
		cursorPanel = new CursorComponentPanel();
		activeWaypointPanel = new ActiveWaypointComponentPanel();
		logoPanel = new LogoPanel();
		chartPanel = new ChartPanel(activeWaypointPanel);
		msiComponentPanel = new MSIComponentPanel();
		aisComponentPanel = new AisComponentPanel();
		dynamicNoGoPanel = new DynamicNoGoComponentPanel();
		nogoPanel = new NoGoComponentPanel();
		
		// Unmovable panels
		bottomPanel = new BottomPanel();

		// Create the dockable layouts
		dockableComponents = new DockableComponents(this);

		dockableComponents.toggleFrameLock();

		bottomPanel.setPreferredSize(new Dimension(0, 25));
		pane.add(bottomPanel, BorderLayout.PAGE_END);

		// Set up the chart panel with layers etc
		chartPanel.initChart();
		
		// Add top panel to map handler
		mapHandler.add(topPanel);

		// Add bottom panel to map handler
		mapHandler.add(bottomPanel);

		// Add chart panel to map handler
		mapHandler.add(chartPanel);

		// Add scale panel to bean context
		mapHandler.add(scalePanel);
		mapHandler.add(ownShipPanel);
		mapHandler.add(gpsPanel);
		mapHandler.add(cursorPanel);
		mapHandler.add(activeWaypointPanel);
		mapHandler.add(msiComponentPanel);
		mapHandler.add(aisComponentPanel);
		mapHandler.add(dynamicNoGoPanel);
		mapHandler.add(nogoPanel);
		
		// Create top menubar
		menuBar = new EeINSMenuBar();
		this.setJMenuBar(menuBar);
		
		// Init glass pane
		initGlassPane();

		// Add self to map map handler
		mapHandler.add(this);
		
		//Add menubar to map handler
		mapHandler.add(menuBar);

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

	private void initGlassPane() {
		glassPanel = (JPanel) getGlassPane();
		glassPanel.setLayout(null);
		glassPanel.setVisible(false);
	}

	public static Image getAppIcon() {
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
		dockableComponents.saveLayout();
		
		
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

	public ScaleComponentPanel getScalePanel() {
		return scalePanel;
	}

	public OwnShipComponentPanel getOwnShipPanel() {
		return ownShipPanel;
	}

	public GpsComponentPanel getGpsPanel() {
		return gpsPanel;
	}

	public CursorComponentPanel getCursorPanel() {
		return cursorPanel;
	}

	public LogoPanel getLogoPanel() {
		return logoPanel;
	}

	public ActiveWaypointComponentPanel getActiveWaypointPanel() {
		return activeWaypointPanel;
	}

	public DockableComponents getDockableComponents() {
		return dockableComponents;
	}

	public MSIComponentPanel getMsiComponentPanel() {
		return msiComponentPanel;
	}

	public EeINSMenuBar getEeINSMenuBar() {
		return menuBar;
	}

	public AisComponentPanel getAisComponentPanel() {
		return aisComponentPanel;
	}

	public DynamicNoGoComponentPanel getDynamicNoGoPanel() {
		return dynamicNoGoPanel;
	}

	public NoGoComponentPanel getNogoPanel() {
		return nogoPanel;
	}

	
	
	
	
	
}
