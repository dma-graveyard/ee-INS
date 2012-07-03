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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

import com.bbn.openmap.MouseDelegator;
import com.bbn.openmap.gui.OMComponentPanel;

import dk.frv.enav.ins.EeINS;
import dk.frv.enav.ins.event.NavigationMouseMode;
import dk.frv.enav.ins.gui.ais.AisDialog;
import dk.frv.enav.ins.gui.msi.MsiDialog;
import dk.frv.enav.ins.gui.route.RouteManagerDialog;
import dk.frv.enav.ins.msi.IMsiUpdateListener;
import dk.frv.enav.ins.msi.MsiHandler;
import dk.frv.enav.ins.msi.MsiHandler.MsiMessageExtended;
import dk.frv.enav.ins.nogo.NogoHandler;

/**
 * The top buttons panel
 */
public class TopPanel extends OMComponentPanel implements ActionListener, IMsiUpdateListener, MouseListener {

	private static final long serialVersionUID = 1L;

	private ButtonLabel zoomInBtn = new ButtonLabel("Zoom In");
	private ButtonLabel zoomOutBtn = new ButtonLabel("Zoom Out");
	private ButtonLabel centreBtn = new ButtonLabel("Centre");
	private ToggleButtonLabel autoFollowBtn = new ToggleButtonLabel("Auto follow");
	private ButtonLabel setupBtn = new ButtonLabel("Setup");
	private ToggleButtonLabel routeBtn = new ToggleButtonLabel("R");
	private ButtonLabel routeManagerBtn = new ButtonLabel("Routes");
	private ButtonLabel msiButton = new ButtonLabel("MSI");
	private ButtonLabel aisButton = new ButtonLabel("AIS Targets");
	private ToggleButtonLabel nogoButton = new ToggleButtonLabel("Toggle NoGo");
	private ToggleButtonLabel aisBtn = new ToggleButtonLabel("AIS");
	private ToggleButtonLabel riskBtn = new ToggleButtonLabel("Risk");
	private ToggleButtonLabel encBtn = new ToggleButtonLabel("ENC");
	private ToggleButtonLabel newRouteBtn = new ToggleButtonLabel("New route");
	
	private ToggleButtonLabel lockFrames = new ToggleButtonLabel("Lock/Unlock UI");
	
	private MainFrame mainFrame;
	private MsiDialog msiDialog = null;
	private AisDialog aisDialog = null;
	
	private MouseDelegator mouseDelegator;
	private final ToggleButtonLabel tglbtnMsiFilter = new ToggleButtonLabel("MSI filter");
	private MsiHandler msiHandler;
	private NogoHandler nogoHandler;
	private BlinkingLabel msiIcon;
	private int notifyMsgId = -1;
	
	
	
	public static Font defaultFont = new Font("Arial", Font.PLAIN, 11);
	public static Color textColor = new Color(237, 237, 237);


	public TopPanel() {
		super();
		
		setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		zoomInBtn.setToolTipText("Zoom in : Shortcut Numpad +");
		zoomOutBtn.setToolTipText("Zoom out : Shortcut Numpad -");
		centreBtn.setToolTipText("Centre on ship : Shortcut C");

		autoFollowBtn.setToolTipText("Auto follow own ship");
		setupBtn.setToolTipText("Setup");
		routeBtn.setToolTipText("New route");
		routeBtn.setVisible(false);
		newRouteBtn.setToolTipText("Add a new route : Shortcut Ctrl N");
		routeManagerBtn.setToolTipText("Routes Manager : Shortcut Ctrl R");		
		msiButton.setToolTipText("Maritime Safety Information : Shortcut Ctrl M");
		aisButton.setToolTipText("Show nearby vessels : Shortcut Ctrl A");
		nogoButton.setToolTipText("Show/hide NoGo area");
		aisBtn.setToolTipText("Show/hide AIS targets");
		riskBtn.setToolTipText("Show/hide risk info");
		encBtn.setToolTipText("Show/hide ENC");
		tglbtnMsiFilter.setToolTipText("Enable/disable MSI message filtering based on position and routes");
		
		// Temporary
		boolean showRiskAndNogo = !EeINS.getSettings().getGuiSettings().isRiskNogoDisabled();

		add(zoomInBtn);
		add(zoomOutBtn);
		add(centreBtn);
		add(autoFollowBtn);
		add(setupBtn);
		add(routeBtn);
		add(newRouteBtn);
		add(routeManagerBtn);
		add(msiButton);
		add(aisButton);
		add(new JSeparator());
		add(aisBtn);		
		add(encBtn);		
		add(tglbtnMsiFilter);
		if (showRiskAndNogo) 
			add(riskBtn);
		if (showRiskAndNogo)
			add(nogoButton);	
		
		add(lockFrames);
		
		Component horizontalStrut = Box.createHorizontalStrut(5);
		JSeparator separator = new JSeparator();
		separator.setOrientation(SwingConstants.VERTICAL);
		horizontalStrut = Box.createHorizontalStrut(5);

		ImageIcon[] msiAnim = new ImageIcon[2];
		msiAnim[0] = new ImageIcon(EeINS.class.getResource("/images/msi/msi_symbol_64x20.png"));
		msiAnim[1] = new ImageIcon(EeINS.class.getResource("/images/msi/blank64x20.png"));
		msiIcon = new BlinkingLabel(400, msiAnim);

		add(horizontalStrut);
		// add(separator);
		// add(horizontalStrut);
		add(msiIcon);
		msiIcon.setVisible(false);

		msiIcon.addMouseListener(this);
		zoomInBtn.addMouseListener(this);
		
		zoomOutBtn.addMouseListener(this);
		centreBtn.addMouseListener(this);
		autoFollowBtn.addMouseListener(this);
		setupBtn.addMouseListener(this);
		routeBtn.addMouseListener(this);
		newRouteBtn.addMouseListener(this);
		routeManagerBtn.addMouseListener(this);
		msiButton.addMouseListener(this);
		aisButton.addMouseListener(this);
		nogoButton.addMouseListener(this);
		aisBtn.addMouseListener(this);
		riskBtn.addMouseListener(this);
		encBtn.addMouseListener(this);
		tglbtnMsiFilter.addMouseListener(this);
		lockFrames.addMouseListener(this);
		
		lockFrames.setSelected(true);
		nogoButton.setSelected(true);
		
		
		
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
		
	}
	
	public void newRoute(){
		if(mouseDelegator.getActiveMouseModeID() == NavigationMouseMode.modeID){
			mainFrame.getChartPanel().editMode(true);
		} else {
			mainFrame.getChartPanel().editMode(false);
		}
	}

	public void activateNewRouteButton(){
		newRouteBtn.doClick();
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
		if (obj instanceof MsiHandler) {
			msiHandler = (MsiHandler) obj;
		}
		if (obj instanceof AisDialog) {
			aisDialog = (AisDialog) obj;
		}
		if (obj instanceof NogoHandler) {
			nogoHandler = (NogoHandler)obj;
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
	public void msiUpdate() {
		if (msiHandler.isPendingImportantMessages()) {
			msiIcon.setVisible(true);
			msiIcon.setBlink(true);
			String encText = "";

			if (EeINS.getSettings().getEnavSettings().isMsiFilter()) {
				int firstUnAckFiltered = msiHandler.getFirstNonAcknowledgedFiltered();
				// There are no MSI to acknowledge
				if (firstUnAckFiltered != -1) {
					MsiMessageExtended msiMessageFiltered = msiHandler.getFilteredMessageList().get(firstUnAckFiltered);
					notifyMsgId = msiMessageFiltered.msiMessage.getMessageId();
					encText = msiMessageFiltered.msiMessage.getEncText();
				}

			} else {
				int firstUnAck = msiHandler.getFirstNonAcknowledged();
				MsiMessageExtended msiMessage = msiHandler.getMessageList().get(firstUnAck);
				notifyMsgId = msiMessage.msiMessage.getMessageId();
				encText = msiMessage.msiMessage.getEncText();
			}
			msiIcon.setToolTipText(encText);
		} else {
			notifyMsgId = -1;
			msiIcon.setVisible(false);
			// msiIcon.setBlink(false);
			msiIcon.setToolTipText(null);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
		System.out.println("mouseEvent");
		
		if (e.getSource() == msiIcon) {
			if (notifyMsgId > 0) {
				msiDialog.showMessage(notifyMsgId);
			} else {
				msiDialog.setVisible(true);
			}
		} else if (e.getSource() == autoFollowBtn) {
			EeINS.getSettings().getNavSettings().setAutoFollow(autoFollowBtn.isSelected());
			if (autoFollowBtn.isSelected()) {
				mainFrame.getChartPanel().autoFollow();
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
		} else if (e.getSource() == riskBtn) {
			EeINS.getRiskHandler().toggleRiskHandler(riskBtn.isSelected());
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
		} else if (e.getSource() == aisButton) {
			aisDialog.setVisible(true);
			aisDialog.setSelection(-1, true);
		} else if (e.getSource() == newRouteBtn) {
			if (mouseDelegator.getActiveMouseModeID() == NavigationMouseMode.modeID) {
				mainFrame.getChartPanel().editMode(true);
			} else {
				mainFrame.getChartPanel().editMode(false);
			}
		} else if (e.getSource() == nogoButton) {	
			System.out.println("ello");
			nogoHandler.toggleLayer();
		} else if (e.getSource() == newRouteBtn) {
			newRoute();
		} else if (e.getSource() == tglbtnMsiFilter) {
			EeINS.getSettings().getEnavSettings().setMsiFilter(tglbtnMsiFilter.isSelected());
			msiHandler.notifyUpdate();
		}else if (e.getSource() == lockFrames) {
			mainFrame.getDockableComponents().toggleFrameLock();
		}
		
		
		
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
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public ButtonLabel getMsiButton() {
		return msiButton;
	}

	public ToggleButtonLabel getNogoButton() {
		return nogoButton;
	}

	public ToggleButtonLabel getAisBtn() {
		return aisBtn;
	}

	public ToggleButtonLabel getRiskBtn() {
		return riskBtn;
	}

	public ToggleButtonLabel getEncBtn() {
		return encBtn;
	}

	public ToggleButtonLabel getLockFrames() {
		return lockFrames;
	}

	public ToggleButtonLabel getTglbtnMsiFilter() {
		return tglbtnMsiFilter;
	}

	public ToggleButtonLabel getAutoFollowBtn() {
		return autoFollowBtn;
	}

	

	
	public static void styleButton(final JLabel label){

		label.setPreferredSize(new Dimension(80, 25));
//		generalSettings.setSize(new Dimension(76, 30));
		label.setFont(defaultFont);
		label.setForeground(textColor);
		//label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		label.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(45, 45, 45)));
		label.setBackground(new Color(128, 128, 128));
		label.setOpaque(true);
		
		label.setHorizontalAlignment(JLabel.CENTER);

		label.addMouseListener(new MouseAdapter() {  
		    public void mousePressed(MouseEvent e) {
		    	label.setBackground(new Color(168, 168, 168));
		    }
			
		    public void mouseReleased(MouseEvent e) {
		    	label.setBackground(new Color(128, 128, 128));
		    }
		});
	}
	
}
