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
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import bibliothek.gui.DockController;
import bibliothek.gui.DockFrontend;
import bibliothek.gui.DockStation;
import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.SingleCDockable;
import bibliothek.gui.dock.station.split.SplitDockGrid;
import bibliothek.gui.dock.station.split.SplitDockProperty;
import bibliothek.gui.dock.title.AbstractDockTitle;

import com.bbn.openmap.MapHandler;

import dk.frv.enav.ins.EeINS;
import dk.frv.enav.ins.gui.ais.AisDialog;
import dk.frv.enav.ins.gui.msi.MsiDialog;

import dk.frv.enav.ins.gui.route.RouteSuggestionDialog;
import dk.frv.enav.ins.settings.GuiSettings;

/**
 * The main frame containing map and panels 
 */
public class MainFrame extends JFrame implements WindowListener {
	
	private static final String TITLE = "e-Navigation enhanced INS " + EeINS.getMinorVersion();
	
	private static final long serialVersionUID = 1L;	
	private static final Logger LOG = Logger.getLogger(MainFrame.class);
	
	protected static final int SENSOR_PANEL_WIDTH = 190;
	
	private TopPanel topPanel;
	private ChartPanel chartPanel;
	private SensorPanel sensorPanel;
	private BottomPanel bottomPanel;
	private JPanel glassPanel;
	private MsiDialog msiDialog;
	private AisDialog aisDialog;
	private RouteSuggestionDialog routeSuggestionDialog;

	private MapMenu mapMenu;
	
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
		
		//Create top menubar
		MenuBar menuBar = new MenuBar();
		this.setJMenuBar(menuBar);
		
		// Create panels
		Container pane = getContentPane();
		topPanel = new TopPanel();
		sensorPanel = new SensorPanel();
		chartPanel = new ChartPanel(sensorPanel);
		bottomPanel = new BottomPanel();
		
		
		//Docks
        DockController controller = new DockController();
        controller.setRootWindow( this );
		
		
        SplitDockStation splitDockStation = new SplitDockStation(false);
        controller.add( splitDockStation );
        
        
        ScreenDockStation screenDockStation = new ScreenDockStation( controller.getRootWindowProvider() );
        controller.add( screenDockStation );
        screenDockStation.setShowing( true );
        this.add( splitDockStation );
        
        DefaultDockable chartDock = new DefaultDockable(chartPanel );
        DefaultDockable sensorDock = new DefaultDockable(sensorPanel);
        DefaultDockable topDock = new DefaultDockable(topPanel);
        
        
        
        splitDockStation.drop( chartDock);
        splitDockStation.drop( sensorDock);
        splitDockStation.drop( topDock);
       
        
        //How to lock
//        DockStation parent = chartDock.getDockParent();
//        DockController controller2 = parent.getController();
//        parent.setController( null );
//        parent.setController( controller2 );
		
		// Add panels
//		topPanel.setPreferredSize(new Dimension(0, 30));
//		pane.add(topPanel, BorderLayout.PAGE_START);
		
//		pane.add(chartPanel, BorderLayout.CENTER);
		
		bottomPanel.setPreferredSize(new Dimension(0, 25));
		pane.add(bottomPanel, BorderLayout.PAGE_END);
		
//		sensorPanel.setPreferredSize(new Dimension(SENSOR_PANEL_WIDTH, 0));
		sensorPanel.setSize(100, 100);
//		pane.add(sensorPanel, BorderLayout.LINE_END);

		// Set up the chart panel with layers etc
		chartPanel.initChart();
		
		// Add top panel to map handler
		mapHandler.add(topPanel);
		
		// Add bottom panel to map handler
		mapHandler.add(bottomPanel);
		
		// Add chart panel to map handler
		mapHandler.add(chartPanel);
		
		// Add sensor panel to bean context
		mapHandler.add(sensorPanel);
		
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
	
	public static SingleCDockable createDockable( String title, JPanel panel ) {
        panel.setOpaque( true );
        DefaultSingleCDockable dockable = new DefaultSingleCDockable( title, title, panel );
        dockable.setCloseable( true );
        return dockable;
}
	
	
	private void initGlassPane() {
		glassPanel = (JPanel)getGlassPane();
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
	
	public SensorPanel getSensorPanel() {
		return sensorPanel;
	}
	
	public JPanel getGlassPanel() {
		return glassPanel;
	}
	
	public TopPanel getTopPanel() {
		return topPanel;
	}
	
}
