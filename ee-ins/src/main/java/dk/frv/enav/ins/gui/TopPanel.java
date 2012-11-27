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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JSeparator;

import com.bbn.openmap.MouseDelegator;
import com.bbn.openmap.gui.OMComponentPanel;

import dk.frv.enav.ins.EeINS;
import dk.frv.enav.ins.event.NavigationMouseMode;
import dk.frv.enav.ins.gui.ais.AisDialog;
import dk.frv.enav.ins.gui.msi.MsiDialog;
import dk.frv.enav.ins.gui.route.RouteManagerDialog;
import dk.frv.enav.ins.layers.ais.AisLayer;
import dk.frv.enav.ins.layers.route.RouteLayer;

/**
 * The top buttons panel
 */
public class TopPanel extends OMComponentPanel implements ActionListener,
		MouseListener {

	private static final long serialVersionUID = 1L;

	private ButtonLabel zoomInBtn = new ButtonLabel(
			toolbarIcon("images/toolbar/magnifier-zoom-in.png"));
	private ButtonLabel zoomOutBtn = new ButtonLabel(
			toolbarIcon("images/toolbar/magnifier-zoom-out.png"));
	private ButtonLabel centreBtn = new ButtonLabel(
			toolbarIcon("images/toolbar/arrow-in.png"));
	private ToggleButtonLabel autoFollowBtn = new ToggleButtonLabel(
			toolbarIcon("images/toolbar/arrow-curve-000-double.png"));
	private ButtonLabel setupBtn = new ButtonLabel(
			toolbarIcon("images/toolbar/wrench.png"));
	// private ToggleButtonLabel routeBtn = new
	// ToggleButtonLabel(toolbarIcon("images/toolbar/marker--plus.png"));
	private ButtonLabel routeManagerBtn = new ButtonLabel(
			toolbarIcon("images/toolbar/marker.png"));
	private ButtonLabel msiButton = new ButtonLabel(
			toolbarIcon("images/toolbar/msi_symbol_16.png"));
	private ButtonLabel aisButton = new ButtonLabel(
			toolbarIcon("images/toolbar/radar.png"));
	private ToggleButtonLabel aisToggleName = new ToggleButtonLabel(
			toolbarIcon("images/toolbar/edit-letter-spacing.png"));
	// private ToggleButtonLabel nogoButton = new
	// ToggleButtonLabel("Toggle NoGo");
	private ToggleButtonLabel aisBtn = new ToggleButtonLabel(
			toolbarIcon("images/toolbar/board-game.png"));
	// private ToggleButtonLabel riskBtn = new ToggleButtonLabel("Risk");
	private ToggleButtonLabel encBtn = new ToggleButtonLabel(
			toolbarIcon("images/toolbar/map-medium.png"));
	private ToggleButtonLabel newRouteBtn = new ToggleButtonLabel(
			toolbarIcon("images/toolbar/marker--plus.png"));
	private ToggleButtonLabel toggleSafeHaven = new ToggleButtonLabel(
			toolbarIcon("images/toolbar/document-resize-actual.png"));

	// private final ToggleButtonLabel tglbtnMsiFilter = new ToggleButtonLabel(
	// "MSI filter");

	// private ToggleButtonLabel lockFrames = new ToggleButtonLabel(
	// "Lock/Unlock UI");

	private MainFrame mainFrame;
	private MsiDialog msiDialog = null;
	private AisDialog aisDialog = null;
	private EeINSMenuBar menuBar = null;
	private AisLayer aisLayer = null;
	private RouteLayer routeLayer = null;
	
	private MouseDelegator mouseDelegator;

	// private MsiHandler msiHandler;
	// private NogoHandler nogoHandler;
	// private BlinkingLabel msiIcon;
	// private int notifyMsgId = -1;

	private static int iconWidth = 16;
	private static int iconHeight = 16;

	public TopPanel() {
		super();

		setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));

		this.setMinimumSize(new Dimension(0, 24));
		this.setPreferredSize(new Dimension(0, 24));

		zoomInBtn.setToolTipText("Zoom in : Shortcut Numpad +");
		zoomOutBtn.setToolTipText("Zoom out : Shortcut Numpad -");
		centreBtn.setToolTipText("Centre on ship : Shortcut C");

		autoFollowBtn.setToolTipText("Auto follow own ship");
		setupBtn.setToolTipText("Setup");
		// routeBtn.setToolTipText("New route");
		// routeBtn.setVisible(false);
		newRouteBtn.setToolTipText("Add a new route : Shortcut Ctrl N");
		routeManagerBtn.setToolTipText("Routes Manager : Shortcut Ctrl R");
		msiButton
				.setToolTipText("Maritime Safety Information : Shortcut Ctrl M");
		aisButton.setToolTipText("Show nearby vessels : Shortcut Ctrl A");
		// nogoButton.setToolTipText("Show/hide NoGo area");
		aisBtn.setToolTipText("Show/hide AIS targets");
		aisToggleName.setToolTipText("Show/hide AIS Name Labels");
		// riskBtn.setToolTipText("Show/hide risk info");
		encBtn.setToolTipText("Show/hide ENC");
		// tglbtnMsiFilter
		// .setToolTipText("Enable/disable MSI message filtering based on position and routes");

		toggleSafeHaven.setToolTipText("Show/hide SafeHaven guidelines");

		// Temporary
		// boolean showRiskAndNogo = !EeINS.getSettings().getGuiSettings()
		// .isRiskNogoDisabled();

		add(zoomInBtn);
		add(zoomOutBtn);
		add(centreBtn);
		add(autoFollowBtn);
		add(setupBtn);
		// add(routeBtn);
		add(newRouteBtn);
		add(routeManagerBtn);
		add(msiButton);
		add(aisButton);
		add(new JSeparator());
		add(aisBtn);
		add(aisToggleName);
		add(encBtn);
		add(toggleSafeHaven);
		// add(tglbtnMsiFilter);
		// if (showRiskAndNogo)
		// add(riskBtn);
		// if (showRiskAndNogo)
		// add(nogoButton);

		// add(lockFrames);

		Component horizontalStrut = Box.createHorizontalStrut(5);
		// JSeparator separator = new JSeparator();
		// separator.setOrientation(SwingConstants.VERTICAL);
		horizontalStrut = Box.createHorizontalStrut(5);

		ImageIcon[] msiAnim = new ImageIcon[2];
		msiAnim[0] = new ImageIcon(
				EeINS.class.getResource("/images/msi/msi_symbol_64x20.png"));
		msiAnim[1] = new ImageIcon(
				EeINS.class.getResource("/images/msi/blank64x20.png"));
		// msiIcon = new BlinkingLabel(400, msiAnim);

		add(horizontalStrut);
		// add(separator);
		// add(horizontalStrut);
		// add(msiIcon);
		// msiIcon.setVisible(false);

		// msiIcon.addMouseListener(this);
		zoomInBtn.addMouseListener(this);

		zoomOutBtn.addMouseListener(this);
		centreBtn.addMouseListener(this);
		autoFollowBtn.addMouseListener(this);
		setupBtn.addMouseListener(this);
		// routeBtn.addMouseListener(this);
		newRouteBtn.addMouseListener(this);
		routeManagerBtn.addMouseListener(this);
		msiButton.addMouseListener(this);
		aisButton.addMouseListener(this);
		// nogoButton.addMouseListener(this);
		aisBtn.addMouseListener(this);
		// riskBtn.addMouseListener(this);
		encBtn.addMouseListener(this);
		aisToggleName.addMouseListener(this);
		// tglbtnMsiFilter.addMouseListener(this);
		// lockFrames.addMouseListener(this);

		// lockFrames.setSelected(true);
		// nogoButton.setSelected(true);

		toggleSafeHaven.addMouseListener(this);
		updateButtons();
	}

	public void updateButtons() {
		autoFollowBtn.setSelected(EeINS.getSettings().getNavSettings()
				.isAutoFollow());
		aisBtn.setSelected(EeINS.getSettings().getAisSettings().isVisible());
		encBtn.setSelected(EeINS.getSettings().getMapSettings().isEncVisible());
		// tglbtnMsiFilter.setSelected(EeINS.getSettings().getEnavSettings()
		// .isMsiFilter());
		aisToggleName.setSelected(EeINS.getSettings().getAisSettings()
				.isShowNameLabels());

	}

	public void disableAutoFollow() {
		EeINS.getSettings().getNavSettings().setAutoFollow(false);
		if (autoFollowBtn.isSelected()) {
			autoFollowBtn.setSelected(false);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {

	}

	public void newRoute() {
		if (mouseDelegator.getActiveMouseModeID() == NavigationMouseMode.modeID) {
			mainFrame.getChartPanel().editMode(true);
		} else {
			mainFrame.getChartPanel().editMode(false);
		}
	}

	public void activateNewRouteButton() {
		newRoute();
	}

	@Override
	public void findAndInit(Object obj) {
		if (obj instanceof MainFrame) {
			mainFrame = (MainFrame) obj;
		}
		if (obj instanceof MsiDialog) {
			msiDialog = (MsiDialog) obj;
		}
		if (obj instanceof MouseDelegator) {
			mouseDelegator = (MouseDelegator) obj;
		}
		// if (obj instanceof MsiHandler) {
		// msiHandler = (MsiHandler) obj;
		// }
		if (obj instanceof AisDialog) {
			aisDialog = (AisDialog) obj;
		}
		if (obj instanceof AisLayer) {
			aisLayer = (AisLayer) obj;
		}
		if (obj instanceof EeINSMenuBar) {
			menuBar = (EeINSMenuBar) obj;
		}
		if (obj instanceof RouteLayer) {
			routeLayer = (RouteLayer) obj;
		}
	}

	public MsiDialog getMsiDialog() {
		return msiDialog;
	}

	public AisDialog getAisDialog() {
		return aisDialog;
	}

	public void setEncDisabled() {
		encBtn.setEnabled(false);
		encBtn.setSelected(false);
	}

	public ToggleButtonLabel getNewRouteBtn() {
		return newRouteBtn;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// if (e.getSource() == msiIcon) {
		// if (notifyMsgId > 0) {
		// msiDialog.showMessage(notifyMsgId);
		// } else {
		// msiDialog.setVisible(true);
		// }
		// } else
		if (e.getSource() == autoFollowBtn) {
			EeINS.getSettings().getNavSettings()
					.setAutoFollow(autoFollowBtn.isSelected());
			if (autoFollowBtn.isSelected()) {
				mainFrame.getChartPanel().autoFollow();
			}
			menuBar.getAutoFollow().setSelected(
					EeINS.getSettings().getNavSettings().isAutoFollow());

		} else if (e.getSource() == centreBtn) {
			mainFrame.getChartPanel().centreOnShip();
		} else if (e.getSource() == zoomInBtn) {
			mainFrame.getChartPanel().doZoom(0.5f);
		} else if (e.getSource() == zoomOutBtn) {
			mainFrame.getChartPanel().doZoom(2f);
		} else if (e.getSource() == aisBtn) {
			EeINS.getSettings().getAisSettings()
					.setVisible(aisBtn.isSelected());
			mainFrame.getChartPanel().aisVisible(aisBtn.isSelected());

			menuBar.getAisLayer().setSelected(
					EeINS.getSettings().getAisSettings().isVisible());

			// } else if (e.getSource() == riskBtn) {
			// EeINS.getRiskHandler().toggleRiskHandler(riskBtn.isSelected());
		} else if (e.getSource() == encBtn) {
			EeINS.getSettings().getMapSettings()
					.setEncVisible(encBtn.isSelected());
			mainFrame.getChartPanel().encVisible(encBtn.isSelected());
			menuBar.getEncLayer().setSelected(
					EeINS.getSettings().getMapSettings().isEncVisible());
		} else if (e.getSource() == routeManagerBtn) {
			RouteManagerDialog routeManagerDialog = new RouteManagerDialog(
					mainFrame);
			routeManagerDialog.setVisible(true);
		} else if (e.getSource() == setupBtn) {
			SetupDialog setupDialog = new SetupDialog(mainFrame);
			setupDialog.loadSettings(EeINS.getSettings());
			setupDialog.setVisible(true);
		} else if (e.getSource() == msiButton) {
			msiDialog.setVisible(true);
		} else if (e.getSource() == aisButton) {
			aisDialog.setVisible(true);
			aisDialog.setSelection(-1, true);
		} else if (e.getSource() == newRouteBtn) {
			if (mouseDelegator.getActiveMouseModeID() == NavigationMouseMode.modeID) {
				menuBar.getNewRoute().setSelected(true);
				mainFrame.getChartPanel().editMode(true);
			} else {
				mainFrame.getChartPanel().editMode(false);
				menuBar.getNewRoute().setSelected(false);
			}
			// } else if (e.getSource() == nogoButton) {
			// nogoHandler.toggleLayer();
		} else if (e.getSource() == newRouteBtn) {
			newRoute();
		} else if (e.getSource() == aisToggleName) {
			aisLayer.toggleAllLabels();
		} else if (e.getSource() == toggleSafeHaven) {
			routeLayer.toggleSafeHaven();
		}
		// else if (e.getSource() == tglbtnMsiFilter) {
		// EeINS.getSettings().getEnavSettings()
		// .setMsiFilter(tglbtnMsiFilter.isSelected());
		// msiHandler.notifyUpdate();
		// }
		// else if (e.getSource() == lockFrames) {
		// mainFrame.getDockableComponents().toggleFrameLock();
		// }

	}

	public ButtonLabel getMsiButton() {
		return msiButton;
	}

	// public ToggleButtonLabel getNogoButton() {
	// return nogoButton;
	// }

	public ToggleButtonLabel getAisBtn() {
		return aisBtn;
	}

	//
	// public ToggleButtonLabel getRiskBtn() {
	// return riskBtn;
	// }

	public ToggleButtonLabel getEncBtn() {
		return encBtn;
	}

	// public ToggleButtonLabel getLockFrames() {
	// return lockFrames;
	// }

	// public ToggleButtonLabel getTglbtnMsiFilter() {
	// return tglbtnMsiFilter;
	// }

	public ToggleButtonLabel getAutoFollowBtn() {
		return autoFollowBtn;
	}

	public void zoomIn() {
		mainFrame.getChartPanel().doZoom(0.5f);
	}

	/**
	 * Function for resizing the icons for the toolbar
	 * 
	 * @param imgpath
	 *            path of the image
	 * @return newimage the newly created and resized image
	 */
	public ImageIcon toolbarIcon(String imgpath) {
		ImageIcon icon = new ImageIcon(imgpath);
		Image img = icon.getImage();
		Image newimg = img.getScaledInstance(iconWidth, iconHeight,
				java.awt.Image.SCALE_DEFAULT);
		ImageIcon newImage = new ImageIcon(newimg);
		return newImage;
	}

}