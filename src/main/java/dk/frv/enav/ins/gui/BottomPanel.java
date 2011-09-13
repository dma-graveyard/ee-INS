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

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.MatteBorder;

import com.bbn.openmap.gui.OMComponentPanel;

import dk.frv.enav.ins.EeINS;
import dk.frv.enav.ins.ais.AisHandler;
import dk.frv.enav.ins.gps.GpsHandler;
import dk.frv.enav.ins.gui.msi.MsiDialog;
import dk.frv.enav.ins.msi.IMsiUpdateListener;
import dk.frv.enav.ins.msi.MsiHandler;
import dk.frv.enav.ins.msi.MsiHandler.MsiMessageExtended;
import dk.frv.enav.ins.services.shore.ShoreServices;
import dk.frv.enav.ins.status.IStatusComponent;

public class BottomPanel extends OMComponentPanel implements IMsiUpdateListener, MouseListener, Runnable {

	private static final long serialVersionUID = 1L;
	private MsiHandler msiHandler;
	private MsiDialog msiDialog;
	private ShoreServices shoreServices;
	private AisHandler aisHandler;
	private GpsHandler gpsHandler;
	private StatusLabel gpsStatus;
	private StatusLabel aisStatus;
	private StatusLabel shoreServiceStatus;
	private BlinkingLabel msiIcon;
	private int notifyMsgId = -1;
	private JToolBar toolBar;
	private List<IStatusComponent> statusComponents = new ArrayList<IStatusComponent>();
	
	public BottomPanel() {
		super();
		setBorder(new CompoundBorder(new MatteBorder(1, 0, 0, 0, new Color(255, 255, 255)), new MatteBorder(1, 0, 0, 0, new Color(192, 192, 192))));
		FlowLayout flowLayout = (FlowLayout) getLayout();
		flowLayout.setVgap(2);
		flowLayout.setHgap(3);
		flowLayout.setAlignment(FlowLayout.RIGHT);
		
		toolBar = new JToolBar();
		toolBar.setFloatable(false);
		add(toolBar);
		
		gpsStatus = new StatusLabel("GPS");
		addToolbarComponent(gpsStatus);
		
		aisStatus = new StatusLabel("AIS");
		addToolbarComponent(aisStatus);
		
		shoreServiceStatus = new StatusLabel("Shore services");
		addToolbarComponent(shoreServiceStatus);
		
		ImageIcon[] msiAnim = new ImageIcon[2];
		msiAnim[0] = new ImageIcon(EeINS.class.getResource("/images/msi/msi_symbol_16.png"));
		msiAnim[1] = new ImageIcon(EeINS.class.getResource("/images/msi/blank.png"));
		msiIcon = new BlinkingLabel(400, msiAnim);
		addToolbarComponent(msiIcon);		
		
		new Thread(this).start();
	}
	
	private void addToolbarComponent(Component component) {
		Component horizontalStrut = Box.createHorizontalStrut(5);
		toolBar.add(horizontalStrut);
		JSeparator separator = new JSeparator();
		separator.setOrientation(SwingConstants.VERTICAL);
		toolBar.add(separator);		
		horizontalStrut = Box.createHorizontalStrut(5);
		toolBar.add(horizontalStrut);
		toolBar.add(component);
		component.addMouseListener(this);
	}

	@Override
	public void findAndInit(Object obj) {
		if (obj instanceof MsiHandler) {
			msiHandler = (MsiHandler)obj;
			msiHandler.addListener(this);
		} else if (obj instanceof MsiDialog) {
			msiDialog = (MsiDialog)obj;
		} else if (obj instanceof AisHandler) {
			aisHandler = (AisHandler)obj;
			statusComponents.add(aisHandler);
		} else if (obj instanceof GpsHandler) {
			gpsHandler = (GpsHandler)obj;
			statusComponents.add(gpsHandler);
		} else if (obj instanceof ShoreServices) {
			shoreServices = (ShoreServices)obj;
			statusComponents.add(shoreServices);
		}
		
	}

	@Override
	public void msiUpdate() {
		if (msiHandler.isPendingImportantMessages()) {
			msiIcon.setBlink(true);
			int firstUnAck = msiHandler.getFirstNonAcknowledged();
			MsiMessageExtended msiMessage = msiHandler.getMessageList().get(firstUnAck);
			notifyMsgId = msiMessage.msiMessage.getMessageId();
			msiIcon.setToolTipText(msiMessage.msiMessage.getEncText());
		} else {
			notifyMsgId = -1;
			msiIcon.setBlink(false);
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
		} else if (e.getSource() instanceof StatusLabel) {
			StatusDialog statusDialog = new StatusDialog();
			statusDialog.showStatus(statusComponents);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}
	
	@Override
	public void run() {
		while (true) {
			updateStatus();
			EeINS.sleep(3000);
		}
	}
	
	private void updateStatus() {
		if (gpsHandler != null) {
			gpsStatus.updateStatus(gpsHandler);
		}
		if (aisHandler != null) {
			aisStatus.updateStatus(aisHandler);
		}
		if (shoreServices != null) {
			shoreServiceStatus.updateStatus(shoreServices);
		}
	}

}
