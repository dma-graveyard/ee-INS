package dk.frv.enav.ins.gui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;

import com.bbn.openmap.MouseDelegator;
import com.bbn.openmap.gui.OMComponentPanel;

import dk.frv.enav.ins.EeINS;
import dk.frv.enav.ins.event.NavigationMouseMode;
import dk.frv.enav.ins.gui.msi.MsiDialog;
import dk.frv.enav.ins.gui.route.RouteManagerDialog;
import dk.frv.enav.ins.msi.MsiHandler;

public class TopPanel extends OMComponentPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	private JButton zoomInBtn = new JButton("+");
	private JButton zoomOutBtn = new JButton("-");
	private JButton centreBtn = new JButton("Centre");
	private JToggleButton autoFollowBtn = new JToggleButton("Auto follow");
	private JButton setupBtn = new JButton("Setup");
	private JToggleButton routeBtn = new JToggleButton("R");
	private JButton routeManagerBtn = new JButton("Routes");		
	private JButton msiButton = new JButton("MSI");
	private JToggleButton aisBtn = new JToggleButton("AIS");
	private JToggleButton encBtn = new JToggleButton("ENC");
	private JToggleButton newRouteBtn = new JToggleButton("New route");
	private MainFrame mainFrame;
	private MsiDialog msiDialog = null;
	private MouseDelegator mouseDelegator;
	private final JToggleButton tglbtnMsiFilter = new JToggleButton("MSI filter");

	private MsiHandler msiHandler;
	
	public TopPanel() {
		super();		
		setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		zoomInBtn.setToolTipText("Zoom in");
		zoomOutBtn.setToolTipText("Zoom out");
		centreBtn.setToolTipText("Centre on ship");
		autoFollowBtn.setToolTipText("Auto follow own ship");
		setupBtn.setToolTipText("Setup");
		routeBtn.setToolTipText("New route");
		routeBtn.setVisible(false);
		newRouteBtn.setToolTipText("Add a new route");
		routeManagerBtn.setToolTipText("Routes Manager");		
		msiButton.setToolTipText("Maritime Safety Information");
		aisBtn.setToolTipText("Show/hide AIS targets");
		encBtn.setToolTipText("Show/hide ENC");
		tglbtnMsiFilter.setToolTipText("Enable/disable MSI message filtering based on position and routes");

		add(zoomInBtn);
		add(zoomOutBtn);
		add(centreBtn);
		add(autoFollowBtn);
		add(setupBtn);
		add(routeBtn);
		add(newRouteBtn);
		add(routeManagerBtn);		
		add(msiButton);
		add(new JSeparator());
		add(aisBtn);
		add(encBtn);
		add(tglbtnMsiFilter);

		zoomInBtn.addActionListener(this);
		zoomOutBtn.addActionListener(this);
		centreBtn.addActionListener(this);
		autoFollowBtn.addActionListener(this);
		setupBtn.addActionListener(this);
		routeBtn.addActionListener(this);
		newRouteBtn.addActionListener(this);
		routeManagerBtn.addActionListener(this);		
		msiButton.addActionListener(this);
		aisBtn.addActionListener(this);
		encBtn.addActionListener(this);
		tglbtnMsiFilter.addActionListener(this);
		
		updateButtons();
	}
	
	public void updateButtons() {
		autoFollowBtn.setSelected(EeINS.getSettings().getNavSettings().isAutoFollow());
		aisBtn.setSelected(EeINS.getSettings().getAisSettings().isVisible());
		encBtn.setSelected(EeINS.getSettings().getMapSettings().isEncVisible());
		tglbtnMsiFilter.setSelected(EeINS.getSettings().getEnavSettings().isMsiFilter());
	}
	
	public void disableAutoFollow() {
		EeINS.getSettings().getNavSettings().setAutoFollow(false);
		if (autoFollowBtn.isSelected()) {
			autoFollowBtn.setSelected(false);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == autoFollowBtn) {
			EeINS.getSettings().getNavSettings().setAutoFollow(autoFollowBtn.isSelected());
			if (autoFollowBtn.isSelected()) {
				mainFrame.getChartPanel().centreOnShip();
			}
		} else if (e.getSource() == centreBtn) {
			mainFrame.getChartPanel().centreOnShip();
		} else if (e.getSource() == zoomInBtn) {
			mainFrame.getChartPanel().doZoom(0.5f);
		} else if (e.getSource() == zoomOutBtn) {
			mainFrame.getChartPanel().doZoom(2f);
		} else if (e.getSource() == aisBtn) {
			EeINS.getSettings().getAisSettings().setVisible(aisBtn.isSelected());
			mainFrame.getChartPanel().aisVisible(aisBtn.isSelected());
		} else if (e.getSource() == encBtn) {
			EeINS.getSettings().getMapSettings().setEncVisible(encBtn.isSelected());
			mainFrame.getChartPanel().encVisible(encBtn.isSelected());
		} else if (e.getSource() == routeManagerBtn) {
			RouteManagerDialog routeManagerDialog = new RouteManagerDialog(mainFrame);
			routeManagerDialog.setVisible(true);
		} else if (e.getSource() == setupBtn) {
			SetupDialog setupDialog = new SetupDialog(mainFrame);
			setupDialog.loadSettings(EeINS.getSettings());
			setupDialog.setVisible(true);
		} else if (e.getSource() == msiButton) {			
			msiDialog.setVisible(true);
		} else if (e.getSource() == newRouteBtn) {
			if(mouseDelegator.getActiveMouseModeID() == NavigationMouseMode.modeID){
				mainFrame.getChartPanel().editMode(true);
			} else {
				mainFrame.getChartPanel().editMode(false);
			}
		} else if (e.getSource() == tglbtnMsiFilter) {
			EeINS.getSettings().getEnavSettings().setMsiFilter(tglbtnMsiFilter.isSelected());
			msiHandler.notifyUpdate();
		}
	}
	
	@Override
	public void findAndInit(Object obj) {
		if (obj instanceof MainFrame) {
			mainFrame = (MainFrame)obj;
		}
		if (obj instanceof MsiDialog) {
			msiDialog = (MsiDialog)obj;
		}
		if (obj instanceof MouseDelegator) {
			mouseDelegator = (MouseDelegator) obj;
		}
		if (obj instanceof MsiHandler) {
			msiHandler = (MsiHandler) obj;
		}
	}
	
	public MsiDialog getMsiDialog() {
		return msiDialog;
	}
	
	public void setEncDisabled() {
		encBtn.setEnabled(false);
		encBtn.setSelected(false);
	}
	
	public JToggleButton getNewRouteBtn() {
		return newRouteBtn;
	}

}
