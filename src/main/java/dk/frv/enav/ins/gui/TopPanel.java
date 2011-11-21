/*
 * Copyright 2011 Danish Maritime Safety Administration. All rights reserved.
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
 * THIS SOFTWARE IS PROVIDED BY Danish Maritime Safety Administration ``AS IS'' 
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
 * either expressed or implied, of Danish Maritime Safety Administration.
 * 
 */
package dk.frv.enav.ins.gui;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

import com.bbn.openmap.MouseDelegator;
import com.bbn.openmap.gui.OMComponentPanel;

import dk.frv.enav.ins.EeINS;
import dk.frv.enav.ins.event.NavigationMouseMode;
import dk.frv.enav.ins.gui.msi.MsiDialog;
import dk.frv.enav.ins.gui.route.RouteManagerDialog;
import dk.frv.enav.ins.msi.IMsiUpdateListener;
import dk.frv.enav.ins.msi.MsiHandler;
import dk.frv.enav.ins.msi.MsiHandler.MsiMessageExtended;
import dk.frv.enav.ins.nogo.NogoHandler;
import dk.frv.enav.ins.gui.ais.*;

/**
 * The top buttons panel 
 */
public class TopPanel extends OMComponentPanel implements ActionListener, IMsiUpdateListener, MouseListener {

	private static final long serialVersionUID = 1L;

	private JButton zoomInBtn = new JButton("+");
	private JButton zoomOutBtn = new JButton("-");
	private JButton centreBtn = new JButton("Centre");
	private JToggleButton autoFollowBtn = new JToggleButton("Auto follow");
	private JButton setupBtn = new JButton("Setup");
	private JToggleButton routeBtn = new JToggleButton("R");
	private JButton routeManagerBtn = new JButton("Routes");		
	private JButton msiButton = new JButton("MSI");
	private JButton aisButton = new JButton("AIS Targets");
	private JButton nogoButton = new JButton("Toggle NoGo");
	private JToggleButton aisBtn = new JToggleButton("AIS");
	private JToggleButton encBtn = new JToggleButton("ENC");
	private JToggleButton newRouteBtn = new JToggleButton("New route");
	private MainFrame mainFrame;
	private MsiDialog msiDialog = null;
	private AisDialog aisDialog = null;
	private MouseDelegator mouseDelegator;
	private final JToggleButton tglbtnMsiFilter = new JToggleButton("MSI filter");
	private MsiHandler msiHandler;
	private NogoHandler nogoHandler;
	private BlinkingLabel msiIcon;
	private int notifyMsgId = -1;
	
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
		add(aisButton);
		add(new JSeparator());
		add(aisBtn);
		add(encBtn);
		add(tglbtnMsiFilter);
		add(nogoButton);
		
		
		Component horizontalStrut = Box.createHorizontalStrut(5);
		JSeparator separator = new JSeparator();
		separator.setOrientation(SwingConstants.VERTICAL);
		horizontalStrut = Box.createHorizontalStrut(5);
		
		
		ImageIcon[] msiAnim = new ImageIcon[2];
		msiAnim[0] = new ImageIcon(EeINS.class.getResource("/images/msi/msi_symbol_64x20.png"));
		msiAnim[1] = new ImageIcon(EeINS.class.getResource("/images/msi/blank64x20.png"));
		msiIcon = new BlinkingLabel(400, msiAnim);
		
		
		add(horizontalStrut);
		//add(separator);
		//add(horizontalStrut);
		add(msiIcon);
		msiIcon.setVisible(false);
	

		msiIcon.addMouseListener(this);
		zoomInBtn.addActionListener(this);
		zoomOutBtn.addActionListener(this);
		centreBtn.addActionListener(this);
		autoFollowBtn.addActionListener(this);
		setupBtn.addActionListener(this);
		routeBtn.addActionListener(this);
		newRouteBtn.addActionListener(this);
		routeManagerBtn.addActionListener(this);		
		msiButton.addActionListener(this);
		aisButton.addActionListener(this);
		nogoButton.addActionListener(this);
		aisBtn.addActionListener(this);
		encBtn.addActionListener(this);
		tglbtnMsiFilter.addActionListener(this);
		
		
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
		if (e.getSource() == autoFollowBtn) {
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
		} else if (e.getSource() == nogoButton) {	
			nogoButton.setSelected(nogoHandler.toggleLayer());
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
	
	public void newRoute(){
		if(mouseDelegator.getActiveMouseModeID() == NavigationMouseMode.modeID){
			mainFrame.getChartPanel().editMode(true);
		} else {
			mainFrame.getChartPanel().editMode(false);
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
		if (obj instanceof AisDialog) {
			aisDialog = (AisDialog)obj;
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
	
	public JToggleButton getNewRouteBtn() {
		return newRouteBtn;
	}

	@Override
	public void msiUpdate() {
		if (msiHandler.isPendingImportantMessages()) {
			msiIcon.setVisible(true);
			msiIcon.setBlink(true);
			String encText = "";
			
			if (EeINS.getSettings().getEnavSettings().isMsiFilter())
			{
				int firstUnAckFiltered = msiHandler.getFirstNonAcknowledgedFiltered();
				//There are no MSI to acknowledge
				if (firstUnAckFiltered != -1){
					MsiMessageExtended msiMessageFiltered = msiHandler.getFilteredMessageList().get(firstUnAckFiltered);
					notifyMsgId = msiMessageFiltered.msiMessage.getMessageId();
					encText = msiMessageFiltered.msiMessage.getEncText();
				}
				
			}else
			{
				int firstUnAck = msiHandler.getFirstNonAcknowledged();
				MsiMessageExtended msiMessage = msiHandler.getMessageList().get(firstUnAck);
				notifyMsgId = msiMessage.msiMessage.getMessageId();
				encText = msiMessage.msiMessage.getEncText();
			}
			msiIcon.setToolTipText(encText);		
		} else {
			notifyMsgId = -1;
			msiIcon.setVisible(false);
			//msiIcon.setBlink(false);
			msiIcon.setToolTipText(null); 			
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == msiIcon) {
			if (notifyMsgId > 0) {
				msiDialog.showMessage(notifyMsgId);
			} else {
				msiDialog.setVisible(true);
			}
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

}
