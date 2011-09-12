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
import dk.frv.enav.ins.gui.msi.MsiDialog;
import dk.frv.enav.ins.gui.route.RouteSuggestionDialog;
import dk.frv.enav.ins.settings.GuiSettings;

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
		
		// Create panels
		Container pane = getContentPane();
		topPanel = new TopPanel();
		sensorPanel = new SensorPanel();
		chartPanel = new ChartPanel(sensorPanel);
		bottomPanel = new BottomPanel();
		
		// Add panels
		topPanel.setPreferredSize(new Dimension(0, 30));
		pane.add(topPanel, BorderLayout.PAGE_START);
		
		pane.add(chartPanel, BorderLayout.CENTER);
		
		bottomPanel.setPreferredSize(new Dimension(0, 25));
		pane.add(bottomPanel, BorderLayout.PAGE_END);
		
		sensorPanel.setPreferredSize(new Dimension(SENSOR_PANEL_WIDTH, 0));
		pane.add(sensorPanel, BorderLayout.LINE_END);

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
		
		// Init Route suggestion dialog
		routeSuggestionDialog = new RouteSuggestionDialog(this);
		mapHandler.add(routeSuggestionDialog);				
		
		// Init the map right click menu
		mapMenu = new MapMenu();
        mapHandler.add(mapMenu);
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
