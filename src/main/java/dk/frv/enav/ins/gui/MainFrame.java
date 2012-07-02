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
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import bibliothek.gui.dock.common.CContentArea;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.MissingCDockableStrategy;
import bibliothek.gui.dock.common.SingleCDockable;
import bibliothek.gui.dock.common.SingleCDockableFactory;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.displayer.DisplayerDockBorder;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.util.Priority;
import bibliothek.util.filter.PresetFilter;
import bibliothek.util.xml.XElement;

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

	// Docks
	// private List<DefaultSingleCDockable> dockables = new
	// ArrayList<DefaultSingleCDockable>();

	private static final String[] PANEL_NAMES = { "Chart", "Top", "Scale",
			"Own Ship", "GPS", "Cursor", "Active Waypoints", "Logos" };
	private Map<String, PanelDockable> dmap;

	private CControl control;
    private DockableFactory factory;

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
		MenuBar menuBar = new MenuBar();
		this.setJMenuBar(menuBar);

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

		bottomPanel = new BottomPanel();

		chartPanel.initChart();

		// Docks
		control = new CControl(this);
		control.setMissingStrategy( MissingCDockableStrategy.STORE );
		
		factory = new DockableFactory(chartPanel, topPanel, scalePanel,
				ownShipPanel, gpsPanel, cursorPanel,
				activeWaypointPanel, logoPanel);
		
		CContentArea contentArea = control.getContentArea();
		pane.add(contentArea);
		control.addSingleDockableFactory(new PresetFilter<String>(PANEL_NAMES),
				factory);

		add(control.getContentArea());

		//Load a layout
		File layoutFile = new File(EeINS.class.getSimpleName() + ".xml");
		if (layoutFile.exists()) {
			try {
				control.readXML(layoutFile);
			} catch (IOException ex) {
				ex.printStackTrace(System.err);
			}
		} else {
			System.out.println("No layout file found - creating standard");
			control.readXML(createLayout());
		}

		menuBar.add(createDockableMenu());

		// Frames
		BorderMod bridge = new BorderMod();
		control.getController()
				.getThemeManager()
				.publish(Priority.CLIENT, DisplayerDockBorder.KIND,
						ThemeManager.BORDER_MODIFIER_TYPE, bridge);

		toggleFrameLock();

		
		
		bottomPanel.setPreferredSize(new Dimension(0, 25));
		pane.add(bottomPanel, BorderLayout.PAGE_END);

		// sensorPanel.setPreferredSize(new Dimension(SENSOR_PANEL_WIDTH, 0));
		// sensorPanel.setSize(100, 100);
		// pane.add(sensorPanel, BorderLayout.LINE_END);

		// Set up the chart panel with layers etc

		// Add top panel to map handler
		mapHandler.add(topPanel);

		// Add bottom panel to map handler
		mapHandler.add(bottomPanel);

		// Add chart panel to map handler
		mapHandler.add(chartPanel);

		// Add sensor panel to bean context
		// mapHandler.add(sensorPanel);

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

	public void toggleFrameLock() {

		List<SingleCDockable> mdlist = control.getRegister()
				.getSingleDockables();

		if (((PanelDockable) mdlist.get(0)).isTitleShown()) {

			for (int i = 0; i < mdlist.size(); i++) {
				PanelDockable dockable = (PanelDockable) mdlist.get(i);
				dockable.setTitleShown(false);
			}
			control.getContentArea().getCenter().setResizingEnabled(false);
			control.getContentArea().getCenter().setDividerSize(0);

		} else {
			for (int i = 0; i < mdlist.size(); i++) {
				PanelDockable dockable = (PanelDockable) mdlist.get(i);
				dockable.setTitleShown(true);
			}

			control.getContentArea().getCenter().setResizingEnabled(true);
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
		try {
            File f = new File(EeINS.class.getSimpleName() + ".xml");
            control.writeXML(f);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        control.destroy();
		
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

	private JMenu createDockableMenu() {
		JMenu menu = new JMenu("Dockables");
		List<SingleCDockable> mdlist = control.getRegister()
				.getSingleDockables();
		dmap = new HashMap<String, PanelDockable>();
		for (int i = 0; i < mdlist.size(); i++) {
			PanelDockable dockable = (PanelDockable) mdlist.get(i);
			dmap.put(dockable.getName(), dockable);
		}

		for (String name : PANEL_NAMES) {
			JMenuItem m = createDockableMenuItem(name, dmap.get(name));
			menu.add(m);
		}

		return menu;
	}

	private JMenuItem createDockableMenuItem(final String name,
			PanelDockable dockable) {
		JCheckBoxMenuItem m = new JCheckBoxMenuItem(name);
		m.setSelected(dockable != null && dockable.isVisible());
		m.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBoxMenuItem m = (JCheckBoxMenuItem) e.getSource();
				if (m.isSelected()) {
					System.out.println("Select again? " + m.getText());
					PanelDockable dockable = dmap.get(name);
					doOpen(dockable);
				} else {
					PanelDockable dockable = getMyDockableByName(name);
					if (dockable != null)
						doClose(dockable);
				}
			}
		});
		return m;
	}

	private PanelDockable getMyDockableByName(String name) {
		return (PanelDockable) control.getSingleDockable(name);
	}

	private void doOpen(PanelDockable dockable) {
		control.addDockable(dockable);

		dockable.setDefaultLocation(ExtendedMode.NORMALIZED, CLocation.base()
				.normalEast(0.5));

		dockable.setVisible(true);
	}

	private void doClose(PanelDockable dockable) {
		// saveDockableLocation(dockable);
		dockable.setVisible(false);
		control.remove(dockable);
	}

	// If no layout file is present, create the basic layout!
	private XElement createLayout() {
		System.out.println("Create layout?");
		CControl aControl = new CControl();

		PanelDockable chartDock = new PanelDockable("Chart", chartPanel);
		PanelDockable topDock = new PanelDockable("Top", topPanel);
		PanelDockable scaleDock = new PanelDockable("Scale", scalePanel);
		PanelDockable ownShipDock = new PanelDockable("Own Ship", ownShipPanel);
		PanelDockable gpsDock = new PanelDockable("GPS", gpsPanel);
		PanelDockable cursorDock = new PanelDockable("Cursor", cursorPanel);
		PanelDockable activeWaypointDock = new PanelDockable("Active Waypoint",
				activeWaypointPanel);
		PanelDockable logoDock = new PanelDockable("Logos", logoPanel);

		CGrid grid = new CGrid(aControl);
		grid.add(0, 0, 100, 3, topDock);
		grid.add(0, 3, 90, 97, chartDock);
		grid.add(90, 3, 10, 10, scaleDock);
		grid.add(90, 13, 10, 10, ownShipDock);
		grid.add(90, 23, 10, 10, gpsDock);
		grid.add(90, 33, 10, 10, cursorDock);
		grid.add(90, 43, 10, 10, activeWaypointDock);
		grid.add(90, 53, 10, 47, logoDock);

		aControl.getContentArea().setMinimumAreaSize(new Dimension(0, 0));

		// Deploy the grid content
		aControl.getContentArea().deploy(grid);
		control.intern().getController().getRelocator().setDragOnlyTitel(true);

		List<SingleCDockable> mdlist = control.getRegister()
				.getSingleDockables();

		for (int i = 0; i < mdlist.size(); i++) {
			PanelDockable dockable = (PanelDockable) mdlist.get(i);
			dockable.setStackable(false);
			dockable.setMinimizable(false);
			dockable.setMaximizable(false);
		}

		XElement root = new XElement("root");
		aControl.writeXML(root);
		aControl.destroy();
		return root;
	}

	// Create the dockables from a xml file
	private static class DockableFactory implements SingleCDockableFactory {

		ChartPanel chartPanel;
		TopPanel topPanel;
		ScaleComponentPanel scalePanel;
		OwnShipComponentPanel ownShipPanel;
		GpsComponentPanel gpsPanel;
		CursorComponentPanel cursorPanel;
		ActiveWaypointComponentPanel activeWaypointPanel;
		LogoPanel logoPanel;

		public DockableFactory(ChartPanel chartPanel, TopPanel topPanel,
				ScaleComponentPanel scalePanel,
				OwnShipComponentPanel ownShipPanel, GpsComponentPanel gpsPanel,
				CursorComponentPanel cursorPanel,
				ActiveWaypointComponentPanel activeWaypointPanel,
				LogoPanel logoPanel) {

			super();
			this.chartPanel = chartPanel;
			this.topPanel = topPanel;
			this.scalePanel = scalePanel;
			this.ownShipPanel = ownShipPanel;
			this.gpsPanel = gpsPanel;
			this.cursorPanel = cursorPanel;
			this.activeWaypointPanel = activeWaypointPanel;
			this.logoPanel = logoPanel;
		}

		@Override
		public SingleCDockable createBackup(String id) {

			if (id == "Chart") {
				return new PanelDockable(id, chartPanel);
			}

			if (id == "Top") {
				return new PanelDockable(id, topPanel);
			}
			if (id == "Scale") {
				return new PanelDockable(id, scalePanel);
			}

			if (id == "Own Ship") {
				return new PanelDockable(id, ownShipPanel);
			}

			if (id == "GPS") {
				return new PanelDockable(id, gpsPanel);
			}

			if (id == "Cursor") {
				return new PanelDockable(id, cursorPanel);
			}

			if (id == "Active Waypoint") {
				return new PanelDockable(id, activeWaypointPanel);
			}
			if (id == "Logos") {
				return new PanelDockable(id, logoPanel);
			}

			return new PanelDockable(id, new JPanel());

		}
	}

	private static class PanelDockable extends DefaultSingleCDockable {

		private final String name;

		public PanelDockable(String name, JPanel panel) {
			super(name);
			this.name = name;
			setTitleText(name);

			add(panel);

		}

		// @Override
		// protected DefaultCommonDockable createCommonDockable() {
		// return new MyCommonDockable(this, getClose());
		// }

		public String getName() {
			return name;
		}
	}
}
