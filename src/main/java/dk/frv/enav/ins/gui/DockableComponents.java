package dk.frv.enav.ins.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

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

public class DockableComponents {

	private static final String[] PANEL_NAMES = { "Chart", "Top", "Scale",
			"Own Ship", "GPS", "Cursor", "Active Waypoint", "Logos", "MSI", "AIS Target", "Dynamic NoGo", "NoGo" };
	private Map<String, PanelDockable> dmap;
	private CControl control;
	private DockableFactory factory;

	private TopPanel topPanel;
	private ChartPanel chartPanel;

	private ScaleComponentPanel scalePanel;
	private OwnShipComponentPanel ownShipPanel;
	private GpsComponentPanel gpsPanel;
	private CursorComponentPanel cursorPanel;
	private ActiveWaypointComponentPanel activeWaypointPanel;
	private LogoPanel logoPanel;
	private MSIComponentPanel msiPanel;
	private AisComponentPanel aisPanel;
	private DynamicNoGoComponentPanel dynamicNoGoPanel;
	private NoGoComponentPanel nogoPanel;

	private boolean locked = false;

	public DockableComponents(MainFrame mainFrame) {
		// Docks
		control = new CControl(mainFrame);
		control.setMissingStrategy(MissingCDockableStrategy.STORE);

		chartPanel = mainFrame.getChartPanel();
		topPanel = mainFrame.getTopPanel();
		scalePanel = mainFrame.getScalePanel();
		ownShipPanel = mainFrame.getOwnShipPanel();
		gpsPanel = mainFrame.getGpsPanel();
		cursorPanel = mainFrame.getCursorPanel();
		activeWaypointPanel = mainFrame.getActiveWaypointPanel();
		logoPanel = mainFrame.getLogoPanel();
		msiPanel = mainFrame.getMsiComponentPanel();
		aisPanel = mainFrame.getAisComponentPanel();
		dynamicNoGoPanel = mainFrame.getDynamicNoGoPanel();
		nogoPanel = mainFrame.getNogoPanel();

		factory = new DockableFactory(chartPanel, topPanel, scalePanel,
				ownShipPanel, gpsPanel, cursorPanel, activeWaypointPanel,
				logoPanel, msiPanel, aisPanel, dynamicNoGoPanel, nogoPanel);

		CContentArea contentArea = control.getContentArea();
		mainFrame.getContentPane().add(contentArea);

		control.addSingleDockableFactory(new PresetFilter<String>(PANEL_NAMES),
				factory);

		mainFrame.add(control.getContentArea());

		
		// Load a layout
		File layoutFile = new File(EeINS.class.getSimpleName() + ".xml");
		if (layoutFile.exists()) {
			try {
				control.readXML(layoutFile);
			} catch (IOException ex) {
				ex.printStackTrace(System.err);
			}
		} else {
			control.readXML(createLayout());
		}

		control.intern().getController().getRelocator().setDragOnlyTitel(true);

		List<SingleCDockable> mdlist = control.getRegister()
				.getSingleDockables();

		for (int i = 0; i < mdlist.size(); i++) {
			PanelDockable dockable = (PanelDockable) mdlist.get(i);
			dockable.setStackable(false);
			dockable.setMinimizable(false);
			dockable.setMaximizable(false);
		}

		control.getContentArea().setMinimumAreaSize(new Dimension(0, 0));
		
		// Frames
		BorderMod bridge = new BorderMod();
		control.getController()
				.getThemeManager()
				.publish(Priority.CLIENT, DisplayerDockBorder.KIND,
						ThemeManager.BORDER_MODIFIER_TYPE, bridge);
	}

	public JMenu createDockableMenu() {
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

	public void toggleFrameLock() {

		List<SingleCDockable> mdlist = control.getRegister()
				.getSingleDockables();

		if (!locked) {

			for (int i = 0; i < mdlist.size(); i++) {
				PanelDockable dockable = (PanelDockable) mdlist.get(i);
				dockable.setTitleShown(false);
			}
			control.getContentArea().getCenter().setResizingEnabled(false);
			control.getContentArea().getCenter().setDividerSize(0);

			locked = true;

		} else {
			for (int i = 0; i < mdlist.size(); i++) {
				PanelDockable dockable = (PanelDockable) mdlist.get(i);
				dockable.setTitleShown(true);
			}
			
			control.getContentArea().getCenter().setResizingEnabled(true);
			control.getContentArea().getCenter().setDividerSize(2);
			
			locked = false;
		}

	}

	public void saveLayout() {
		try {
			File f = new File(EeINS.class.getSimpleName() + ".xml");
			control.writeXML(f);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		control.destroy();
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
					PanelDockable dockable = dmap.get(name);
					System.out.println(dockable);
					if (dockable != null) {
						doOpen(dockable);
					} else {
						PanelDockable newDockable = (PanelDockable) factory
								.createBackup(name);
						dmap.put(newDockable.getName(), newDockable);
						newDockable.setStackable(false);
						newDockable.setMinimizable(false);
						newDockable.setMaximizable(false);
						doOpen(newDockable);
					}

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

		if (locked) {
			dockable.setTitleShown(false);
		} else {
			dockable.setTitleShown(true);
		}

		control.addDockable(dockable);

		dockable.setDefaultLocation(ExtendedMode.NORMALIZED, CLocation.base()
				.normalEast(0.5));

		dockable.setVisible(true);
	}

	private void doClose(PanelDockable dockable) {
		dockable.setVisible(false);
		control.removeDockable(dockable);
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
		PanelDockable msiDock = new PanelDockable("MSI", msiPanel);
		
//		PanelDockable aisDock = new PanelDockable("AIS Target", aisPanel);

		CGrid grid = new CGrid(aControl);
		grid.add(0, 0, 100, 3, topDock);
		grid.add(0, 3, 90, 97, chartDock);
		grid.add(90, 3, 10, 10, scaleDock);
		grid.add(90, 13, 10, 10, ownShipDock);
		grid.add(90, 23, 10, 10, gpsDock);
		grid.add(90, 33, 10, 10, cursorDock);
		grid.add(90, 43, 10, 10, activeWaypointDock);
		grid.add(90, 53, 10, 10, msiDock);
//		grid.add(90, 63, 10, 10, aisDock);
		grid.add(90, 63, 10, 37, logoDock);

		aControl.getContentArea().setMinimumAreaSize(new Dimension(0, 0));

		// Deploy the grid content
		aControl.getContentArea().deploy(grid);

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
		MSIComponentPanel msiPanel;
		AisComponentPanel aisPanel;
		DynamicNoGoComponentPanel dynamicNoGoPanel;
		NoGoComponentPanel nogoPanel;

		public DockableFactory(ChartPanel chartPanel, TopPanel topPanel,
				ScaleComponentPanel scalePanel,
				OwnShipComponentPanel ownShipPanel, GpsComponentPanel gpsPanel,
				CursorComponentPanel cursorPanel,
				ActiveWaypointComponentPanel activeWaypointPanel,
				LogoPanel logoPanel, MSIComponentPanel msiPanel, AisComponentPanel aisPanel, DynamicNoGoComponentPanel dynamicNoGoPanel, NoGoComponentPanel nogoPanel) {

			super();

			this.chartPanel = chartPanel;
			this.topPanel = topPanel;
			this.scalePanel = scalePanel;
			this.ownShipPanel = ownShipPanel;
			this.gpsPanel = gpsPanel;
			this.cursorPanel = cursorPanel;
			this.activeWaypointPanel = activeWaypointPanel;
			this.logoPanel = logoPanel;
			this.msiPanel = msiPanel;
			this.aisPanel = aisPanel;
			this.dynamicNoGoPanel = dynamicNoGoPanel;
			this.nogoPanel = nogoPanel;
			
		}

		@Override
		public SingleCDockable createBackup(String id) {
			if (id.equals("Chart")) {
				return new PanelDockable(id, chartPanel);
			}

			if (id.equals("Top")) {
				return new PanelDockable(id, topPanel);
			}
			if (id.equals("Scale")) {
				return new PanelDockable(id, scalePanel);
			}

			if (id.equals("Own Ship")) {
				return new PanelDockable(id, ownShipPanel);
			}

			if (id.equals("GPS")) {
				return new PanelDockable(id, gpsPanel);
			}

			if (id.equals("Cursor")) {
				return new PanelDockable(id, cursorPanel);
			}
			if (id.equals("Active Waypoint")) {
				return new PanelDockable(id, activeWaypointPanel);
			}
			if (id.equals("Logos")) {
				return new PanelDockable(id, logoPanel);
			}
			if (id.equals("MSI")) {
				return new PanelDockable(id, msiPanel);
			}
			if (id.equals("AIS Target")) {
				return new PanelDockable(id, aisPanel);
			}
			
			if (id.equals("Dynamic NoGo")) {
				return new PanelDockable(id, dynamicNoGoPanel);
			}
			if (id.equals("NoGo")) {
				return new PanelDockable(id, nogoPanel);
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

		public String getName() {
			return name;
		}
	}

}
